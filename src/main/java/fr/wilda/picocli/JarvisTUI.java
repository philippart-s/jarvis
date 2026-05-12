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
import dev.tamboui.tui.TuiRunner;
import dev.tamboui.tui.event.Event;
import dev.tamboui.tui.event.KeyCode;
import dev.tamboui.tui.event.KeyEvent;
import dev.tamboui.tui.event.ResizeEvent;
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
import jakarta.inject.Inject;
import picocli.CommandLine;

import java.util.List;

@CommandLine.Command(name = "tui", description = "Start the Jarvis TUI", mixinStandardHelpOptions = true)
public class JarvisTUI extends TuiCommand {

  @Inject
  AIEndpointService aiEndpointService;

  /// Available demo modes, mapped to the list items.
  enum DemoMode {
    CHAT, RAG, MCP, MANUAL_WORKFLOW, WORKFLOW, AGENT
  }

  /// Current view: either the menu or the chat interface.
  enum ViewState {
    MENU, CHAT
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
  private final StringBuilder responseBuffer = new StringBuilder();
  private int responseScroll = 0;
  private volatile boolean processing = false;

  @Override
  protected void runTui(TuiRunner runner) throws Exception {
    // Disable all console log handlers to prevent log messages from corrupting
    // the TUI rendering. Quarkus/JBoss LogManager captures System.out at boot,
    // so redirecting streams is not sufficient — we must remove the handlers.
    var rootLogger = java.util.logging.Logger.getLogger("");
    var originalHandlers = rootLogger.getHandlers();
    for (var handler : originalHandlers) {
      rootLogger.removeHandler(handler);
    }

    try {
      runner.run(this::handleEvent, this::render);
    } finally {
      // Restore handlers when TUI exits
      for (var handler : originalHandlers) {
        rootLogger.addHandler(handler);
      }
    }
  }

  // ========== Event handling ==========

  private boolean handleEvent(Event event, TuiRunner runner) {
    return switch (event) {
      case KeyEvent k when k.isQuit() && viewState == ViewState.MENU -> {
        runner.quit();
        yield false;
      }
      case KeyEvent k when k.isCtrlC() -> {
        runner.quit();
        yield false;
      }
      case KeyEvent k -> switch (viewState) {
        case MENU -> handleMenuKey(k);
        case CHAT -> handleChatKey(k);
      };
      case ResizeEvent _ -> true;
      default -> false;
    };
  }

  // --- Menu key handling ---

  private boolean handleMenuKey(KeyEvent k) {
    if (k.isUp()) {
      listState.selectPrevious();
      return true;
    }
    if (k.isDown()) {
      listState.selectNext(items.size());
      return true;
    }
    if (k.isHome()) {
      listState.selectFirst();
      return true;
    }
    if (k.isEnd()) {
      listState.select(items.size() - 1);
      return true;
    }
    if (k.isConfirm()) {
      var selected = listState.selected();
      if (selected != null) {
        currentDemo = DemoMode.values()[selected];
        switchToChatView();
      }
      return true;
    }
    return false;
  }

  // --- Chat key handling ---

  private boolean handleChatKey(KeyEvent k) {
    if (k.isCancel()) {
      switchToMenuView();
      return true;
    }
    if (k.isConfirm()) {
      submitQuestion();
      return true;
    }

    // Scrolling the response area
    if (k.isUp() || k.isPageUp()) {
      if (responseScroll > 0) {
        responseScroll--;
      }
      return true;
    }
    if (k.isDown() || k.isPageDown()) {
      responseScroll++;
      return true;
    }

    // Text input handling
    if (k.isDeleteBackward()) {
      inputState.deleteBackward();
      return true;
    }
    if (k.isDeleteForward()) {
      inputState.deleteForward();
      return true;
    }
    if (k.isLeft()) {
      inputState.moveCursorLeft();
      return true;
    }
    if (k.isRight()) {
      inputState.moveCursorRight();
      return true;
    }
    if (k.isHome()) {
      inputState.moveCursorToStart();
      return true;
    }
    if (k.isEnd()) {
      inputState.moveCursorToEnd();
      return true;
    }
    if (k.code() == KeyCode.CHAR && !k.hasCtrl() && !k.hasAlt()) {
      inputState.insert(k.character());
      return true;
    }

    return false;
  }

  // ========== View switching ==========

  private void switchToChatView() {
    viewState = ViewState.CHAT;
    inputState.clear();
    responseBuffer.setLength(0);
    responseScroll = 0;
  }

  private void switchToMenuView() {
    viewState = ViewState.MENU;
    inputState.clear();
    responseBuffer.setLength(0);
    responseScroll = 0;
  }

  // ========== Question submission ==========

  private void submitQuestion() {
    var question = inputState.text().trim();
    if (question.isEmpty() || processing) {
      return;
    }
    responseBuffer.setLength(0);
    responseScroll = 0;
    inputState.clear();
    processing = true;

    switch (currentDemo) {
      case CHAT -> streamChat(question);
      default -> {
        responseBuffer.append("[ ").append(currentDemo.name()).append(" mode ]\n\n");
        responseBuffer.append("This demo will be wired in a next step...");
        processing = false;
      }
    }
  }

  /// Streams the response from the AI service on a virtual thread.
  /// Each token is appended to the response buffer so the TUI renders it progressively.
  private void streamChat(String question) {
    Thread.startVirtualThread(() -> {
      try {
        responseBuffer.append("🤖 Calling AI service...\n\n");
        aiEndpointService.askAQuestion(question)
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
    renderInfo(frame, layout.get(2));
    renderMenuFooter(frame, layout.get(3));
  }

  private void renderMenuHeader(Frame frame, Rect area) {
    var header = Block.builder()
        .borders(Borders.ALL)
        .borderType(BorderType.ROUNDED)
        .borderStyle(Style.EMPTY.fg(Color.CYAN))
        .title(Title.from(
            Line.from(
                Span.raw(" PicoCLI ").bold().cyan(),
                Span.raw("+ ").white(),
                Span.raw("TamboUI ").bold().yellow(),
                Span.raw("Demo ").white()
            )
        ).centered())
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
    var helpLine = Line.from(
        Span.raw(" ↑/↓").bold().yellow(),
        Span.raw(" Navigate  ").dim(),
        Span.raw("Enter").bold().yellow(),
        Span.raw(" Select  ").dim(),
        Span.raw("q/Ctrl+C").bold().yellow(),
        Span.raw(" Quit  ").dim(),
        Span.raw("--help").bold().yellow(),
        Span.raw(" CLI help").dim()
    );

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
    var demoLabel = switch (currentDemo) {
      case CHAT -> "Chat Bot";
      case RAG -> "RAG Demo";
      case MCP -> "MCP Demo";
      case MANUAL_WORKFLOW -> "Manual Workflow Demo";
      case WORKFLOW -> "Workflow Demo";
      case AGENT -> "YOLO Agent Demo";
    };

    var header = Block.builder()
        .borders(Borders.ALL)
        .borderType(BorderType.ROUNDED)
        .borderStyle(Style.EMPTY.fg(Color.CYAN))
        .title(Title.from(
            Line.from(
                Span.raw(" 🤖 Jarvis ").bold().cyan(),
                Span.raw("- ").white(),
                Span.raw(demoLabel + " ").bold().yellow()
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
        .style(Style.EMPTY.fg(Color.WHITE))
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
    var helpLine = Line.from(
        Span.raw(" Enter").bold().yellow(),
        Span.raw(" Send  ").dim(),
        Span.raw("↑/↓").bold().yellow(),
        Span.raw(" Scroll  ").dim(),
        Span.raw("Esc").bold().yellow(),
        Span.raw(" Back  ").dim(),
        Span.raw("Ctrl+C").bold().yellow(),
        Span.raw(" Quit").dim()
    );

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
