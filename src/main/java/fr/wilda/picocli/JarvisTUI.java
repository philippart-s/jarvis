package fr.wilda.picocli;

import dev.tamboui.layout.Constraint;
import dev.tamboui.layout.Layout;
import dev.tamboui.layout.Rect;
import dev.tamboui.picocli.TuiCommand;
import dev.tamboui.style.Color;
import dev.tamboui.style.Style;
import dev.tamboui.terminal.Frame;
import dev.tamboui.text.Line;
import dev.tamboui.text.Span;
import dev.tamboui.text.Text;
import dev.tamboui.tui.TuiConfig;
import dev.tamboui.tui.TuiRunner;
import dev.tamboui.tui.bindings.ActionHandler;
import dev.tamboui.tui.bindings.Actions;
import dev.tamboui.tui.bindings.Bindings;
import dev.tamboui.tui.bindings.BindingSets;
import dev.tamboui.tui.bindings.KeyTrigger;
import dev.tamboui.tui.event.Event;
import dev.tamboui.tui.event.KeyCode;
import dev.tamboui.tui.event.KeyEvent;
import dev.tamboui.tui.event.ResizeEvent;
import dev.tamboui.tui.event.TickEvent;
import dev.tamboui.widgets.block.Block;
import dev.tamboui.widgets.block.BorderType;
import dev.tamboui.widgets.block.Borders;
import dev.tamboui.widgets.block.Title;
import dev.tamboui.widgets.input.TextInput;
import dev.tamboui.widgets.input.TextInputState;
import dev.tamboui.widgets.list.ListItem;
import dev.tamboui.widgets.list.ListState;
import dev.tamboui.widgets.list.ListWidget;
import dev.tamboui.widgets.paragraph.Paragraph;
import dev.tamboui.style.Overflow;
import fr.wilda.picocli.sdk.ai.AIEndpointService;
import fr.wilda.picocli.sdk.ai.mcp.TuiToolApproval;
import fr.wilda.picocli.sdk.ai.tool.DocumentLoader;
import jakarta.inject.Inject;
import picocli.CommandLine;

import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.function.Function;

import io.smallrye.mutiny.Multi;

@CommandLine.Command(name = "tui", description = "Start the Jarvis TUI", mixinStandardHelpOptions = true)
public class JarvisTUI extends TuiCommand {

  @Inject
  AIEndpointService aiEndpointService;

  @Inject
  DocumentLoader documentLoader;

  @Inject
  TuiToolApproval tuiToolApproval;

  /// Available demo modes, mapped to the list items.
  /// The label is used in the chat header to display the current mode.
  enum DemoMode {
    CHAT("Chat Bot"),
    RAG("RAG Demo"),
    MCP("MCP Demo"),
    MANUAL_WORKFLOW("Manual Workflow Demo"),
    WORKFLOW("Workflow Demo"),
    AGENT("YOLO Agent Demo");

    private final String label;

    DemoMode(String label) {
      this.label = label;
    }

    String label() {
      return label;
    }
  }

  /// Current view: either the menu, the chat interface, or the RAG path input.
  enum ViewState {
    MENU, CHAT, RAG_PATH_INPUT
  }

  // --- Menu state ---
  private final ListState listState = new ListState();
  private final List<String> items = List.of(
      "💬  Chat bot 💬",
      "🧮  RAG demo 🧮",
      "🧩  MCP demo 🧩",
      "👤  Agent with human workflow demo 👤",
      "🏗️   Agent with developed workflow demo 🏗️",
      "🤖  YOLO mode demo 🤖"
  );

  // --- View state ---
  private ViewState viewState = ViewState.MENU;
  private DemoMode currentDemo = DemoMode.CHAT;

  // --- Chat state ---
  private final TextInputState inputState = new TextInputState();
  private final StringBuffer responseBuffer = new StringBuffer();
  private int responseScroll = 0;
  private volatile boolean processing = false;
  private boolean ragDocumentsLoaded = false;

  @Override
  protected TuiConfig createConfig() {
    // Enable periodic tick events so the TUI re-renders automatically
    // when the response buffer is updated by background threads.
    return TuiConfig.builder()
        .tickRate(Duration.ofMillis(100))
        .build();
  }

  @Override
  protected void runTui(TuiRunner runner) throws Exception {
    var logRedirector = new TuiLogRedirector();
    logRedirector.install();

    tuiToolApproval.enableTuiMode();

    try {
      runner.run(this::handleEvent, this::render);
    } finally {
      tuiToolApproval.disableTuiMode();
      logRedirector.uninstall();
    }
  }

  /// Redirects JUL log output to the TUI response buffer instead of the terminal.
  /// This prevents log messages from services (RagTool, DocumentRetriever, etc.)
  /// from corrupting the TUI display.
  private class TuiLogRedirector {
    private final java.util.logging.Logger rootLogger = java.util.logging.Logger.getLogger("");
    private java.util.logging.Handler[] originalHandlers;

    private final java.util.logging.Handler tuiHandler = new java.util.logging.Handler() {
      @Override
      public void publish(java.util.logging.LogRecord record) {
        if (record.getMessage() != null && viewState == ViewState.CHAT) {
          responseBuffer.append(record.getMessage());
        }
      }

      @Override
      public void flush() {}

      @Override
      public void close() {}
    };

    void install() {
      originalHandlers = rootLogger.getHandlers();
      for (var handler : originalHandlers) {
        rootLogger.removeHandler(handler);
      }
      rootLogger.addHandler(tuiHandler);
    }

    void uninstall() {
      rootLogger.removeHandler(tuiHandler);
      for (var handler : originalHandlers) {
        rootLogger.addHandler(handler);
      }
    }
  }

  // --- Action handlers ---
  // Menu uses standard bindings (q quits). Input views rebind quit to Ctrl+C only
  // so the user can type 'q' freely in text fields.

  /// Bindings for input views: standard bindings with quit restricted to Ctrl+C only.
  private static final Bindings INPUT_BINDINGS = BindingSets.standard()
      .toBuilder()
      .rebind(KeyTrigger.ctrl('c'), Actions.QUIT)
      .build();

  private final ActionHandler menuActions = new ActionHandler(BindingSets.standard())
      .on(Actions.MOVE_UP, _ -> listState.selectPrevious())
      .on(Actions.MOVE_DOWN, _ -> listState.selectNext(items.size()))
      .on(Actions.HOME, _ -> listState.selectFirst())
      .on(Actions.END, _ -> listState.select(items.size() - 1))
      .on(Actions.SELECT, _ -> selectMenuItem())
      .on(Actions.CONFIRM, _ -> selectMenuItem());

  /// Action handler for text input (shared between chat and RAG path views).
  /// Handles cursor movement and deletion. Confirm/cancel are handled
  /// per-view before dispatching to this handler.
  private final ActionHandler textInputActions = new ActionHandler(INPUT_BINDINGS)
      .on(Actions.DELETE_BACKWARD, _ -> inputState.deleteBackward())
      .on(Actions.DELETE_FORWARD, _ -> inputState.deleteForward())
      .on(Actions.MOVE_LEFT, _ -> inputState.moveCursorLeft())
      .on(Actions.MOVE_RIGHT, _ -> inputState.moveCursorRight())
      .on(Actions.HOME, _ -> inputState.moveCursorToStart())
      .on(Actions.END, _ -> inputState.moveCursorToEnd());

  // ========== Event handling ==========

  private boolean handleEvent(Event event, TuiRunner runner) {
    return switch (event) {
      case KeyEvent k when k.isCtrlC() -> {
        runner.quit();
        yield false;
      }
      case KeyEvent k -> switch (viewState) {
        case MENU -> handleMenuEvent(k, runner);
        case CHAT -> handleChatEvent(k);
        case RAG_PATH_INPUT -> handleRagPathEvent(k);
      };
      case TickEvent _ -> viewState == ViewState.CHAT || viewState == ViewState.RAG_PATH_INPUT;
      case ResizeEvent _ -> true;
      default -> false;
    };
  }

  // --- Menu event handling ---

  private boolean handleMenuEvent(KeyEvent k, TuiRunner runner) {
    if (k.isQuit()) {
      runner.quit();
      return false;
    }
    return menuActions.dispatch(k);
  }

  // --- Chat event handling ---

  private boolean handleChatEvent(KeyEvent k) {
    // MCP tool approval takes priority over all other input
    if (tuiToolApproval.hasPendingApproval()) {
      return handleToolApproval(k);
    }

    if (k.isCancel()) {
      switchToMenuView();
      return true;
    }
    if (k.isConfirm()) {
      submitQuestion();
      return true;
    }

    // Scrolling the response area (Up/Down/PageUp/PageDown)
    if (k.isUp() || k.isPageUp()) {
      if (responseScroll > 0) responseScroll--;
      return true;
    }
    if (k.isDown() || k.isPageDown()) {
      responseScroll++;
      return true;
    }

    // Text input: try action handler first, then fall back to character insertion
    if (textInputActions.dispatch(k)) return true;
    return insertCharIfApplicable(k);
  }

  // --- RAG path input event handling ---

  private boolean handleRagPathEvent(KeyEvent k) {
    if (k.isCancel()) {
      switchToMenuView();
      return true;
    }
    if (k.isConfirm()) {
      submitRagPath();
      return true;
    }

    if (textInputActions.dispatch(k)) return true;
    return insertCharIfApplicable(k);
  }

  /// Handles the MCP tool approval dialog: Enter/y approves, Esc/n rejects.
  private boolean handleToolApproval(KeyEvent k) {
    if (k.isConfirm() || k.isChar('y') || k.isChar('Y')) {
      tuiToolApproval.approve();
      return true;
    }
    if (k.isCancel() || k.isChar('n') || k.isChar('N')) {
      tuiToolApproval.reject();
      return true;
    }
    return false;
  }

  /// Inserts a typed character into the text input if the key is a plain character
  /// (no Ctrl/Alt modifiers). Returns false for non-character keys.
  private boolean insertCharIfApplicable(KeyEvent k) {
    if (k.code() == KeyCode.CHAR && !k.hasCtrl() && !k.hasAlt()) {
      inputState.insert(k.character());
      return true;
    }
    return false;
  }

  private void submitRagPath() {
    var path = inputState.text().trim();
    inputState.clear();
    processing = true;

    Thread.startVirtualThread(() -> {
      try {
        if (path.isEmpty()) {
          responseBuffer.append("📜 Loading RAG documents from default path...\n\n");
          documentLoader.loadDocument(null);
        } else {
          responseBuffer.append("📜 Loading RAG documents from: ").append(path).append("\n\n");
          documentLoader.loadDocument(Path.of(path));
        }
        ragDocumentsLoaded = true;
        responseBuffer.append("✅ Documents loaded! You can now ask questions.\n\n");
      } catch (Exception e) {
        responseBuffer.append("⚠️ Error loading documents: ").append(e.getMessage()).append("\n\n");
      } finally {
        processing = false;
        viewState = ViewState.CHAT;
      }
    });
  }

  // ========== View switching ==========

  private void selectMenuItem() {
    var selected = listState.selected();
    if (selected != null) {
      currentDemo = DemoMode.values()[selected];
      switchToChatView();
    }
  }

  private void switchToChatView() {
    if (currentDemo == DemoMode.RAG && !ragDocumentsLoaded) {
      viewState = ViewState.RAG_PATH_INPUT;
    } else {
      viewState = ViewState.CHAT;
    }
    inputState.clear();
    responseBuffer.setLength(0);
    responseScroll = 0;
  }

  private void switchToMenuView() {
    viewState = ViewState.MENU;
    inputState.clear();
    responseBuffer.setLength(0);
    responseScroll = 0;
    ragDocumentsLoaded = false;
  }

  // ========== Question submission ==========

  private void submitQuestion() {
    var question = inputState.text().trim();
    if (question.isEmpty() || processing) {
      return;
    }
    // Add a separator between conversations
    if (!responseBuffer.isEmpty()) {
      responseBuffer.append("\n\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
    }
    responseBuffer.append("💬 ").append(question).append("\n\n");
    responseScroll = 0;
    inputState.clear();
    processing = true;

    switch (currentDemo) {
      case CHAT, RAG -> streamResponse(aiEndpointService::askAQuestion, question);
      case MCP -> streamResponse(aiEndpointService::askAQuestionAboutOVHcloud, question);
      default -> {
        responseBuffer.append("[ ").append(currentDemo.name()).append(" mode ]\n\n");
        responseBuffer.append("This demo will be wired in a next step...");
        processing = false;
      }
    }
  }

  /// Streams a Multi response from the AI service on a virtual thread.
  /// Each token is appended to the response buffer so the TUI renders it progressively.
  private void streamResponse(Function<String, Multi<String>> serviceCall, String question) {
    Thread.startVirtualThread(() -> {
      try {
        serviceCall.apply(question)
            .subscribe()
            .asStream()
            .forEach(responseBuffer::append);
      } catch (Exception e) {
        responseBuffer.append("\n\n⚠️ Error: ").append(e.getMessage());
      } finally {
        processing = false;
      }
    });
  }

  // ========== Rendering ==========

  private void render(Frame frame) {
    switch (viewState) {
      case MENU -> renderMenuView(frame);
      case CHAT -> renderChatView(frame);
      case RAG_PATH_INPUT -> renderRagPathView(frame);
    }
  }

  // --- Menu view (existing) ---

  private void renderMenuView(Frame frame) {
    var area = frame.area();
    var layout = Layout.vertical()
        .constraints(
            Constraint.length(3),
            Constraint.fill(),
            Constraint.length(5),
            Constraint.length(3)
        )
        .split(area);

    renderMenuHeader(frame, layout.get(0));
    renderList(frame, layout.get(1));
    //renderInfo(frame, layout.get(2));
    renderMenuFooter(frame, layout.get(3));
  }

  private void renderMenuHeader(Frame frame, Rect area) {
    var header = Block.builder()
        .borders(Borders.ALL)
        .borderType(BorderType.ROUNDED)
        .borderStyle(Style.EMPTY.fg(Color.CYAN))
        .title(Title.from(
            Line.from(Span.raw(" 🤖  Jarvis TUI 🤖  ").bold().cyan())).centered())
        .build();
    frame.renderWidget(header, area);
  }

  private void renderList(Frame frame, Rect area) {
    var listItems = items.stream()
        .map(s -> ListItem.from(s).toSizedWidget())
        .toList();

    var list = ListWidget.builder()
        .items(listItems)
        .block(Block.builder()
            .borders(Borders.ALL)
            .borderType(BorderType.ROUNDED)
            .borderStyle(Style.EMPTY.fg(Color.GREEN))
            .title("Jarvis")
            .titleBottom(Title.from("↑/↓ navigate, Enter select").right())
            .build())
        .highlightStyle(Style.EMPTY.bg(Color.GREEN).fg(Color.WHITE).bold())
        .highlightSymbol("▶ ")
        .build();

    frame.renderStatefulWidget(list, area, listState);
  }

  private void renderInfo(Frame frame, Rect area) {
    var selected = listState.selected();
    var selectedText = selected != null && selected < items.size()
        ? items.get(selected)
        : "None";

    var content = Text.from(
        Line.from(Span.raw("Title: ").bold(), Span.raw("Jarvis").cyan()),
        Line.from(Span.raw("Items: ").bold(), Span.raw(String.valueOf(items.size())).yellow()),
        Line.from(Span.raw("Selected: ").bold(), Span.raw(selectedText).green())
    );

    var info = Paragraph.builder()
        .text(content)
        .block(Block.builder()
            .borders(Borders.ALL)
            .borderType(BorderType.ROUNDED)
            .borderStyle(Style.EMPTY.fg(Color.MAGENTA))
            .title("CLI Options")
            .build())
        .build();
    frame.renderWidget(info, area);
  }

  private void renderMenuFooter(Frame frame, Rect area) {
    renderSimpleFooter(frame, area, Line.from(
        Span.raw(" ↑/↓").bold().yellow(),
        Span.raw(" Navigate  ").dim(),
        Span.raw("Enter").bold().yellow(),
        Span.raw(" Select  ").dim(),
        Span.raw("q/Ctrl+C").bold().yellow(),
        Span.raw(" Quit  ").dim(),
        Span.raw("--help").bold().yellow(),
        Span.raw(" CLI help").dim()
    ));
  }

  // --- RAG path input view ---

  private void renderRagPathView(Frame frame) {
    var area = frame.area();
    var layout = Layout.vertical()
        .constraints(
            Constraint.length(3),   // header
            Constraint.length(3),   // text input
            Constraint.fill(),      // info area
            Constraint.length(3)    // footer
        )
        .split(area);

    renderChatHeader(frame, layout.get(0));

    // Path text input
    var textInput = TextInput.builder()
        .placeholder("Enter path to documents (empty for default)...")
        .placeholderStyle(Style.EMPTY.dim().italic())
        .style(Style.EMPTY.fg(Color.BLACK))
        .cursorStyle(Style.EMPTY.reversed())
        .block(Block.builder()
            .borders(Borders.ALL)
            .borderType(BorderType.ROUNDED)
            .borderStyle(Style.EMPTY.fg(Color.YELLOW))
            .title(Title.from(Span.raw("Document Path").bold().yellow()))
            .build())
        .build();
    textInput.renderWithCursor(layout.get(1), frame.buffer(), inputState, frame);

    // Info / status area
    var infoText = responseBuffer.isEmpty()
        ? "Enter the path to the documents you want to load for RAG.\nLeave empty to use the default path from configuration.\nPress Enter to load."
        : responseBuffer.toString();

    var info = Paragraph.builder()
        .text(Text.from(infoText))
        .overflow(Overflow.WRAP_WORD)
        .block(Block.builder()
            .borders(Borders.ALL)
            .borderType(BorderType.ROUNDED)
            .borderStyle(Style.EMPTY.fg(Color.GREEN))
            .title("Info")
            .build())
        .build();
    frame.renderWidget(info, layout.get(2));

    // Footer
    renderSimpleFooter(frame, layout.get(3), Line.from(
        Span.raw(" Enter").bold().yellow(),
        Span.raw(" Load  ").dim(),
        Span.raw("Esc").bold().yellow(),
        Span.raw(" Back  ").dim(),
        Span.raw("Ctrl+C").bold().yellow(),
        Span.raw(" Quit").dim()
    ));
  }

  // --- Chat view ---

  private void renderChatView(Frame frame) {
    var area = frame.area();
    var layout = Layout.vertical()
        .constraints(
            Constraint.length(3),   // header
            Constraint.length(3),   // text input
            Constraint.fill(),      // response area
            Constraint.length(3)    // footer
        )
        .split(area);

    renderChatHeader(frame, layout.get(0));
    renderTextInput(frame, layout.get(1));
    renderResponseArea(frame, layout.get(2));
    renderChatFooter(frame, layout.get(3));
  }

  private void renderChatHeader(Frame frame, Rect area) {
    var header = Block.builder()
        .borders(Borders.ALL)
        .borderType(BorderType.ROUNDED)
        .borderStyle(Style.EMPTY.fg(Color.CYAN))
        .title(Title.from(
            Line.from(
                Span.raw(" 🤖 Jarvis ").bold().cyan(),
                Span.raw("- ").white(),
                Span.raw(currentDemo.label() + " ").bold().yellow()
            )
        ).centered())
        .build();
    frame.renderWidget(header, area);
  }

  private void renderResponseArea(Frame frame, Rect area) {
    String responseText;
    if (processing && responseBuffer.isEmpty()) {
      responseText = "🤔 Thinking...";
    } else if (responseBuffer.isEmpty()) {
      responseText = "Type your question above and press Enter...";
    } else {
      responseText = responseBuffer.toString();
    }

    var paragraph = Paragraph.builder()
        .text(Text.from(responseText))
        .overflow(Overflow.WRAP_WORD)
        .scroll(responseScroll)
        .block(Block.builder()
            .borders(Borders.ALL)
            .borderType(BorderType.ROUNDED)
            .borderStyle(Style.EMPTY.fg(Color.GREEN))
            .title("Response")
            .titleBottom(Title.from("↑/↓ scroll").right())
            .build())
        .build();
    frame.renderWidget(paragraph, area);
  }

  private void renderTextInput(Frame frame, Rect area) {
    var textInput = TextInput.builder()
        .placeholder(processing ? "Waiting for response..." : "Ask a question...")
        .placeholderStyle(Style.EMPTY.dim().italic())
        .style(Style.EMPTY.fg(Color.BLACK))
        .cursorStyle(Style.EMPTY.reversed())
        .block(Block.builder()
            .borders(Borders.ALL)
            .borderType(BorderType.ROUNDED)
            .borderStyle(Style.EMPTY.fg(Color.YELLOW))
            .title(Title.from(Span.raw("Question").bold().yellow()))
            .build())
        .build();

    textInput.renderWithCursor(area, frame.buffer(), inputState, frame);
  }

  private void renderChatFooter(Frame frame, Rect area) {
    Line helpLine;
    if (tuiToolApproval.hasPendingApproval()) {
      helpLine = Line.from(
          Span.raw(" ⚠️ Tool: ").bold().yellow(),
          Span.raw(tuiToolApproval.pendingToolName() + "  ").white(),
          Span.raw("Enter/y").bold().green(),
          Span.raw(" Approve  ").dim(),
          Span.raw("Esc/n").bold().red(),
          Span.raw(" Reject").dim()
      );
    } else {
      helpLine = Line.from(
          Span.raw(" Enter").bold().yellow(),
          Span.raw(" Send  ").dim(),
          Span.raw("↑/↓").bold().yellow(),
          Span.raw(" Scroll  ").dim(),
          Span.raw("Esc").bold().yellow(),
          Span.raw(" Back  ").dim(),
          Span.raw("Ctrl+C").bold().yellow(),
          Span.raw(" Quit").dim()
      );
    }

    var footer = Paragraph.builder()
        .text(Text.from(helpLine))
        .block(Block.builder()
            .borders(Borders.ALL)
            .borderType(BorderType.ROUNDED)
            .borderStyle(tuiToolApproval.hasPendingApproval()
                ? Style.EMPTY.fg(Color.YELLOW)
                : Style.EMPTY.fg(Color.DARK_GRAY))
            .build())
        .build();
    frame.renderWidget(footer, area);
  }

  /// Renders a simple footer with a dark gray border and the given help line.
  /// Used by the menu view and RAG path input view.
  private void renderSimpleFooter(Frame frame, Rect area, Line helpLine) {
    var footer = Paragraph.builder()
        .text(Text.from(helpLine))
        .block(Block.builder()
            .borders(Borders.ALL)
            .borderType(BorderType.ROUNDED)
            .borderStyle(Style.EMPTY.fg(Color.DARK_GRAY))
            .build())
        .build();
    frame.renderWidget(footer, area);
  }
}
