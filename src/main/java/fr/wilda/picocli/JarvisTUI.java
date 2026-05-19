package fr.wilda.picocli;

import static dev.tamboui.toolkit.Toolkit.*;

import dev.tamboui.layout.Constraint;
import dev.tamboui.layout.Flex;
import dev.tamboui.style.Color;
import dev.tamboui.style.Overflow;
import dev.tamboui.toolkit.app.ToolkitRunner;
import dev.tamboui.toolkit.element.Element;
import dev.tamboui.toolkit.elements.ListElement;
import dev.tamboui.toolkit.elements.Row;
import dev.tamboui.toolkit.event.EventResult;
import dev.tamboui.tui.TuiConfig;
import dev.tamboui.tui.event.KeyEvent;
import dev.tamboui.widgets.input.TextInputState;
import fr.wilda.picocli.sdk.ai.AIEndpointService;
import fr.wilda.picocli.sdk.ai.mcp.TuiToolApproval;
import fr.wilda.picocli.sdk.ai.tool.DocumentLoader;
import io.smallrye.mutiny.Multi;
import jakarta.inject.Inject;
import picocli.CommandLine;

import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Function;

@CommandLine.Command(name = "tui", description = "Start the Jarvis TUI", mixinStandardHelpOptions = true)
public class JarvisTUI implements Callable<Integer> {

  @Inject
  AIEndpointService aiEndpointService;

  @Inject
  DocumentLoader documentLoader;

  @Inject
  TuiToolApproval tuiToolApproval;

  /// Available modes with their display labels.
  enum Mode {
    MENU(""),
    CHAT("Chat Bot"),
    RAG("RAG"),
    MCP("MCP"),
    MANUAL_WORKFLOW("Manual Workflow"),
    WORKFLOW("Workflow"),
    AGENT("YOLO Agent");

    private final String title;

    Mode(String title) {
      this.title = title;
    }

    @Override
    public String toString() {
      return title;
    }
  }

  // --- Menu items ---
  private static final List<String> MENU_ITEMS = List.of(
      "Chat bot",
      "RAG demo",
      "MCP demo",
      "Agent with human workflow demo",
      "Agent with developed workflow demo",
      "YOLO mode demo"
  );

  // --- State ---
  private Mode currentMode = Mode.MENU;
  private final ListElement<?> menuList = list(MENU_ITEMS.toArray(new String[0]))
      .highlightColor(Color.CYAN)
      .highlightSymbol("▶ ")
      .autoScroll();
  private final TextInputState inputState = new TextInputState();
  private String response = "";
  private String logs = "";
  private boolean processing = false;
  private boolean ragDocumentsLoaded = false;
  private ToolkitRunner runner;

  @Override
  public Integer call() throws Exception {
    var config = TuiConfig.builder()
        .tickRate(Duration.ofMillis(100))
        .build();

    var logRedirector = new TuiLogRedirector();
    logRedirector.install();
    tuiToolApproval.enableTuiMode();

    try (var runner = ToolkitRunner.create(config)) {
      this.runner = runner;
      runner.run(this::render);
      return 0;
    } finally {
      tuiToolApproval.disableTuiMode();
      logRedirector.uninstall();
    }
  }

  // ========== Rendering ==========

  private Element render() {
    return switch (currentMode) {
      case MENU -> menuView();
      case RAG -> ragDocumentsLoaded ? chatView() : ragPathView();
      default -> chatView();
    };
  }

  // --- Menu view ---

  private Element menuView() {
    return column(
        panel(
            text("PicoCLI + TamboUI Demo").bold().cyan()
        ).rounded().borderColor(Color.CYAN).length(3),

        panel("Jarvis", menuList)
            .rounded().borderColor(Color.GREEN).fill()
            .id("menu").focusable()
            .focusedBorderColor(Color.CYAN)
            .onKeyEvent(this::handleMenuKey),

        helpBar(
            "↑/↓", "Navigate",
            "Enter", "Select",
            "q/Ctrl+C", "Quit"
        )
    );
  }

  private EventResult handleMenuKey(KeyEvent event) {
    if (event.isConfirm() || event.isSelect()) {
      var selected = menuList.selected();
      if (selected >= 0 && selected < MENU_ITEMS.size()) {
        currentMode = Mode.values()[selected + 1]; // skip MENU
        inputState.clear();
        response = "";
        return EventResult.HANDLED;
      }
    }
    return EventResult.UNHANDLED;
  }

  // --- Chat view ---

  private Element chatView() {
    return column(
        chatHeader(),

        panel("Question",
            textInput(inputState)
                .placeholder(processing ? "Waiting for response..." : "Ask a question...")
                .id("chat-input")
                .onSubmit(this::submitQuestion)
        ).rounded().borderColor(Color.YELLOW).focusedBorderColor(Color.CYAN).length(3),

        panel("Response", text(buildResponseText()).overflow(Overflow.WRAP_WORD))
            .rounded().borderColor(Color.GREEN).fill(2)
            .id("chat-response").focusable()
            .onKeyEvent(this::handleChatKey),

        panel("Logs", textLines(logs.isEmpty() ? "No logs yet." : logs))
            .rounded().borderColor(Color.DARK_GRAY).fill()
            .id("chat-logs"),

        chatFooter()
    );
  }

  private EventResult handleChatKey(KeyEvent event) {
    if (tuiToolApproval.hasPendingApproval()) {
      if (event.isConfirm() || event.isChar('y') || event.isChar('Y')) {
        tuiToolApproval.approve();
        return EventResult.HANDLED;
      }
      if (event.isCancel() || event.isChar('n') || event.isChar('N')) {
        tuiToolApproval.reject();
        return EventResult.HANDLED;
      }
      return EventResult.HANDLED;
    }

    if (event.isCancel()) {
      switchToMenuView();
      return EventResult.HANDLED;
    }
    return EventResult.UNHANDLED;
  }

  // --- RAG path input view ---

  private Element ragPathView() {
    var infoText = response.isEmpty()
        ? "Enter the path to the documents you want to load for RAG.\n"
            + "Leave empty to use the default path from configuration.\n"
            + "Press Enter to load."
        : response;

    return column(
        chatHeader(),

        textInput(inputState)
            .placeholder("Enter path to documents (empty for default)...")
            .id("rag-input")
            .onSubmit(this::submitRagPath)
            .length(3),

        panel("Info", text(infoText))
            .rounded().borderColor(Color.GREEN).fill()
            .id("rag-info").focusable()
            .onKeyEvent(event -> {
              if (event.isCancel()) {
                switchToMenuView();
                return EventResult.HANDLED;
              }
              return EventResult.UNHANDLED;
            }),

        helpBar("Enter", "Load", "Esc", "Back", "Ctrl+C", "Quit")
    );
  }

  // ========== Shared UI components ==========

  /// Builds the chat/RAG header showing the current demo mode.
  private Element chatHeader() {
    return panel(
        row(
            text("🤖 Jarvis").bold().cyan().fit(),
            text(" - ").white().fit(),
            text(currentMode.toString()).bold().yellow().fit()
        ).flex(Flex.CENTER)
    ).rounded().borderColor(Color.CYAN).length(3);
  }

  /// Builds the chat footer, adapting to MCP tool approval state.
  private Element chatFooter() {
    if (tuiToolApproval.hasPendingApproval()) {
      return row(
          text(" ⚠️ Tool: ").bold().yellow().fit(),
          text(tuiToolApproval.pendingToolName() + "  ").white().fit(),
          text("Enter/y").bold().green().fit(),
          text(" Approve  ").dim().fit(),
          text("Esc/n").bold().red().fit(),
          text(" Reject").dim().fit()
      ).flex(Flex.START).length(1);
    }
    return helpBar("Enter", "Send", "Esc", "Back", "Ctrl+C", "Quit");
  }

  /// Creates a key-binding help bar from alternating key/description pairs.
  private Row helpBar(String... keysAndDescriptions) {
    var elements = new Element[keysAndDescriptions.length];
    for (int i = 0; i < keysAndDescriptions.length; i++) {
      if (i % 2 == 0) {
        elements[i] = text(" " + keysAndDescriptions[i]).bold().yellow().fit();
      } else {
        elements[i] = text(" " + keysAndDescriptions[i] + "  ").dim().fit();
      }
    }
    return row(elements).flex(Flex.START).length(1);
  }

  // ========== View switching ==========

  private void switchToMenuView() {
    currentMode = Mode.MENU;
    inputState.clear();
    response = "";
    logs = "";
    ragDocumentsLoaded = false;
  }

  // ========== Question submission ==========

  private void submitQuestion() {
    var question = inputState.text().trim();
    if (question.isEmpty() || processing) {
      return;
    }
    response = "";
    inputState.clear();
    processing = true;

    switch (currentMode) {
      case CHAT, RAG -> streamResponse(aiEndpointService::askAQuestion, question);
      case MCP -> streamResponse(aiEndpointService::askAQuestionAboutOVHcloud, question);
      default -> {
        logs += "[ " + currentMode.name() + " mode ] This demo will be wired in a next step...\n";
        processing = false;
      }
    }
  }

  private void submitRagPath() {
    var path = inputState.text().trim();
    inputState.clear();

    try {
      if (path.isEmpty()) {
        logs += "📜 Loading RAG documents from default path...\n";
        documentLoader.loadDocument(null);
      } else {
        logs += "📜 Loading RAG documents from: " + path + "\n";
        documentLoader.loadDocument(Path.of(path));
      }
      ragDocumentsLoaded = true;
      logs += "✅ Documents loaded! You can now ask questions.\n";
    } catch (Exception e) {
      logs += "⚠️ Error loading documents: " + e.getMessage() + "\n";
    }
    // RAG docs loaded — render() will now show chatView()
  }

  /// Streams a Multi response from the AI service reactively.
  private void streamResponse(Function<String, Multi<String>> serviceCall, String question) {
    serviceCall.apply(question)
        .subscribe().with(
            token -> runner.runOnRenderThread(() -> response += token),
            error -> runner.runOnRenderThread(() -> {
              logs += "⚠️ Error: " + error.getMessage() + "\n";
              processing = false;
            }),
            () -> runner.runOnRenderThread(() -> processing = false)
        );
  }

  // ========== Helpers ==========

  /// Converts a multiline string into a column of text elements with word wrapping.
  private Element textLines(String content) {
    var lines = content.split("\n", -1);
    var elements = new Element[lines.length];
    for (int i = 0; i < lines.length; i++) {
      elements[i] = text(lines[i]).overflow(Overflow.WRAP_WORD);
    }
    return column(elements);
  }

  private String buildResponseText() {
    if (processing && response.isEmpty()) {
      return "🤔 Thinking...";
    } else if (response.isEmpty()) {
      return "Type your question above and press Enter...";
    }
    return response;
  }

  /// Redirects JUL log output to the TUI response buffer instead of the terminal.
  private class TuiLogRedirector {
    private final java.util.logging.Logger rootLogger = java.util.logging.Logger.getLogger("");
    private java.util.logging.Handler[] originalHandlers;

    private final java.util.logging.Handler tuiHandler = new java.util.logging.Handler() {
      @Override
      public void publish(java.util.logging.LogRecord logRecord) {
        if (logRecord.getMessage() != null && currentMode != Mode.MENU) {
          runner.runOnRenderThread(() -> logs += logRecord.getMessage() + "\n");
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
}
