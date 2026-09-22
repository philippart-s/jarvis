package fr.wilda.picocli;

import dev.tamboui.layout.Flex;
import dev.tamboui.markdown.MarkdownView;
import dev.tamboui.style.Color;
import dev.tamboui.style.Overflow;
import dev.tamboui.toolkit.app.ToolkitRunner;
import dev.tamboui.toolkit.element.Element;
import dev.tamboui.toolkit.elements.ListElement;
import dev.tamboui.toolkit.elements.Row;
import dev.tamboui.toolkit.event.EventResult;
import dev.tamboui.tui.TuiConfig;
import dev.tamboui.tui.event.KeyEvent;
import dev.tamboui.widgets.input.TextAreaState;
import dev.tamboui.widgets.input.TextInputState;
import fr.wilda.picocli.sdk.ai.AIEndpointService;
import fr.wilda.picocli.sdk.ai.agent.AutonomousAgent;
import fr.wilda.picocli.sdk.ai.agent.common.ClassifierAgent;
import fr.wilda.picocli.sdk.ai.agent.common.JarvisAgent;
import fr.wilda.picocli.sdk.ai.agent.common.OVHcloudAgent;
import fr.wilda.picocli.sdk.ai.agent.common.RagAgent;
import fr.wilda.picocli.sdk.ai.agent.workflow.JarvisWorkflow;
import fr.wilda.picocli.sdk.ai.mcp.ToolApproval;
import fr.wilda.picocli.sdk.ai.tool.DocumentLoader;
import io.quarkus.arc.Arc;
import io.smallrye.mutiny.Multi;
import jakarta.inject.Inject;
import picocli.CommandLine;

import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Function;

import static dev.tamboui.toolkit.Toolkit.*;
import static dev.tamboui.toolkit.markdown.MarkdownElement.markdown;

@CommandLine.Command(name = "tui", description = "Start the Jarvis TUI", mixinStandardHelpOptions = true)
public class JarvisTUI implements Callable<Integer> {

  @Inject
  AIEndpointService aiEndpointService;

  @Inject
  DocumentLoader documentLoader;

  @Inject
  ToolApproval tuiToolApproval;

  @Inject
  TuiLoggingController tuiLoggingController;

  @Inject
  ClassifierAgent classifierAgent;

  @Inject
  RagAgent ragAgent;

  @Inject
  JarvisAgent jarvisAgent;

  @Inject
  OVHcloudAgent ovhcloudAgent;

  @Inject
  JarvisWorkflow jarvisWorkflow;

  @Inject
  AutonomousAgent autonomousAgent;

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
      "Manual Workflow demo",
      "Workflow demo",
      "YOLO Agent demo"
  );

  /// Upper bound on retained log lines. The logs panel is a rolling view, and an
  /// unbounded list would keep growing for the whole session.
  private static final int MAX_LOG_LINES = 500;

  // --- State ---
  private Mode currentMode = Mode.MENU;
  private final ListElement<?> menuList = list(MENU_ITEMS.toArray(new String[0]))
      .highlightColor(Color.CYAN)
      .highlightSymbol("▶ ")
      .autoScroll();
  private final ListElement<?> logList = list()
      .scrollbar()
      .autoScroll()
      .displayOnly()
      .highlightColor(Color.CYAN);
  final TextInputState inputState = new TextInputState();
  private String response = "";
  /// Log lines, and a revision counter so the render loop only pushes them into
  /// the list element when they actually changed.
  private final List<String> logLines = new ArrayList<>();
  private int logRevision = 0;
  private int renderedLogRevision = -1;
  private TextAreaState ragInfoState;
  private String ragInfoText = "";
  private boolean processing = false;
  private boolean ragDocumentsLoaded = false;
  private ToolkitRunner runner;
  private int scroll, logScroll = 0;

  @Override
  public Integer call() throws Exception {
    var config = TuiConfig.builder()
        .tickRate(Duration.ofMillis(100))
        .build();

    tuiToolApproval.enableTuiMode();

    try (var runner = ToolkitRunner.create(config)) {
      this.runner = runner;
      tuiLoggingController.enable(this::log);
      runner.run(this::render);
      return 0;
    } finally {
      tuiToolApproval.disableTuiMode();
      tuiLoggingController.disable();
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
            text("🤖 Jarvis TUI 🤖").bold()
                .cyan()
        ).rounded()
            .borderColor(Color.CYAN)
            .length(3),

        panel("Jarvis", menuList)
            .rounded()
            .borderColor(Color.GREEN)
            .fill()
            .id("menu")
            .focusable()
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
    if (renderedLogRevision != logRevision) {
      logList.items(logLines.isEmpty() ? List.of("No logs yet.") : List.copyOf(logLines));
      renderedLogRevision = logRevision;
    }
    logList.selected(Math.min(logScroll, Math.max(0, logLines.size() - 1)));

    var view = column(
        chatHeader(),

        panel("Question",
            textInput(inputState)
                .placeholder(processing ? "Waiting for response..." : "Ask a question...")
                .id("chat-input")
                .onSubmit(this::submitQuestion)
        ).rounded()
            .borderColor(Color.YELLOW)
            .focusedBorderColor(Color.CYAN)
            .length(3),

        panel("Response",
            markdown(buildResponseText())
                .scroll(scroll)
                .overflow(Overflow.WRAP_WORD))
            .rounded()
            .borderColor(Color.GREEN)
            .fill(2)
            .id("chat-response")
            .focusable()
            .focusedBorderColor(Color.CYAN)
            .onKeyEvent(this::handleChatKey),

        panel("Logs", logList)
            .onKeyEvent(this::handleChatKey)
            .focusable()
            .rounded()
            .borderColor(Color.DARK_GRAY)
            .fill()
            .id("chat-logs"),
        chatFooter()
    );

    if (tuiToolApproval.hasPendingApproval()) {
      runner.focusManager()
          .setFocus("approval-dialog");
      return stack(
          view,
          dialog("⚠️  Tool Approval",
              text("Tool: " + tuiToolApproval.pendingToolName()).bold()
                  .cyan(),
              text(""),
              text("Do you want to allow this tool execution?"),
              text(""),
              text("[Enter] Approve    [Esc] Reject").dim()
          ).rounded()
              .borderColor(Color.YELLOW)
              .width(60)
              .id("approval-dialog")
              .focusable()
              .onConfirm(tuiToolApproval::approve)
              .onCancel(tuiToolApproval::reject)
      );
    }

    return view;
  }

  private EventResult handleChatKey(KeyEvent event) {
    if (event.isCancel()) {
      switchToMenuView();
      return EventResult.HANDLED;
    }
    if (event.isPageDown()) {
      logScroll = Math.min(logScroll + 1, Math.max(0, logLines.size() - 1));
      return EventResult.HANDLED;
    }
    if (event.isPageUp()) {
      logScroll = Math.max(0, logScroll - 1);
      return EventResult.HANDLED;
    }
    if (event.isDown()) {
      scroll = Math.min(scroll + 1, maxResponseScroll());
      return EventResult.HANDLED;
    }
    if (event.isUp()) {
      scroll = Math.max(0, scroll - 1);
      return EventResult.HANDLED;
    }

    return EventResult.UNHANDLED;
  }

  // --- RAG path input view ---

  private Element ragPathView() {
    var infoText = response.isEmpty()
        ? """
        Enter the path to the documents you want to load for RAG.
        Leave empty to use the default path from configuration.
        Press Enter to load.
        """
        : response;

    return column(
        chatHeader(),

        textInput(inputState)
            .placeholder("Enter path to documents (empty for default)...")
            .id("rag-input")
            .onSubmit(this::submitRagPath)
            .length(3),

        panel("Info", textArea(ragInfoState(infoText)).wrapWord())
            .rounded()
            .borderColor(Color.GREEN)
            .fill()
            .id("rag-info")
            .focusable()
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
            text("🤖 Jarvis").bold()
                .cyan()
                .fit(),
            text(" - ").white()
                .fit(),
            text(currentMode.toString()).bold()
                .yellow()
                .fit()
        ).flex(Flex.CENTER)
    ).rounded()
        .borderColor(Color.CYAN)
        .length(3);
  }

  /// Builds the chat footer.
  private Row chatFooter() {
    return helpBar("Enter", "Send", "Esc", "Back", "Ctrl+C", "Quit", "Page up/Page down", "Scroll Logs", "↑/↓", "Scroll Response");
  }

  /// Creates a key-binding help bar from alternating key/description pairs.
  private Row helpBar(String... keysAndDescriptions) {
    var elements = new Element[keysAndDescriptions.length];
    for (int i = 0; i < keysAndDescriptions.length; i++) {
      if (i % 2 == 0) {
        elements[i] = text(" " + keysAndDescriptions[i]).bold()
            .yellow()
            .fit();
      } else {
        elements[i] = text(" " + keysAndDescriptions[i] + "  ").dim()
            .fit();
      }
    }
    return row(elements).flex(Flex.START)
        .length(1);
  }

  // ========== View switching ==========

  private void switchToMenuView() {
    currentMode = Mode.MENU;
    inputState.clear();
    response = "";
    logLines.clear();
    logRevision++;
    scroll = 0;
    logScroll = 0;
    ragDocumentsLoaded = false;
  }

  // ========== Question submission ==========

  private void submitQuestion() {
    var question = inputState.text()
        .trim();
    if (question.isEmpty() || processing) {
      return;
    }
    response = "";
    scroll = 0;
    inputState.clear();
    processing = true;

    switch (currentMode) {
      case CHAT, RAG -> streamResponse(aiEndpointService::askAQuestion, question);
      case MCP -> streamResponse(aiEndpointService::askAQuestionAboutOVHcloud, question);
      case MANUAL_WORKFLOW -> executeManualWorkflow(question);
      case WORKFLOW -> executeWorkflow(question);
      case AGENT -> executeAgent(question);
      default -> {
        log("[ " + currentMode.name() + " mode ] This demo will be wired in a next step...");
        processing = false;
      }
    }
  }

  private void executeManualWorkflow(String question) {
    Thread.startVirtualThread(() -> {
      var requestContext = Arc.container()
          .requestContext();
      requestContext.activate();
      try {
        log("🔍 Classifying question...");
        var subCommand = classifierAgent.classify(question);
        log(switch (subCommand) {
          case MCP -> "☁️ MCP Agent selected ☁️";
          case RAG -> "📜 RAG Agent selected 📜";
          case CHAT -> "💬 Chat Agent selected 💬";
        });

        var agentResponse = switch (subCommand) {
          case MCP -> ovhcloudAgent.askAQuestion(question);
          case RAG -> {
            ragAgent.askAQuestionEvent(question);
            yield "";
          }
          case CHAT -> "";
        };

        log("🤖 Calling Jarvis agent... with question=\"" + question + "\" and agentResponse=\"" + agentResponse + "\"");
        jarvisAgent.askAQuestion(question, agentResponse)
            .subscribe()
            .with(
                token -> onUi(() -> response += token),
                error -> onUi(() -> {
                  log("⚠️ Error: " + error.getMessage());
                  processing = false;
                }),
                () -> onUi(() -> processing = false)
            );
      } catch (Exception e) {
        onUi(() -> {
          log("⚠️ Workflow error: " + e.getMessage());
          processing = false;
        });
      } finally {
        requestContext.terminate();
      }
    });
  }

  private void executeWorkflow(String question) {
    Thread.startVirtualThread(() -> {
      var requestContext = Arc.container()
          .requestContext();
      requestContext.activate();
      try {
        log("🐣 Executing workflow...");
        jarvisWorkflow.executeJarvisWorkflow(question)
            .subscribe()
            .with(
                token -> onUi(() -> response += token),
                error -> onUi(() -> {
                  log("⚠️ Error: " + error.getMessage());
                  processing = false;
                }),
                () -> onUi(() -> processing = false)
            );
      } catch (Exception e) {
        onUi(() -> {
          log("⚠️ Workflow error: " + e.getMessage());
          processing = false;
        });
      } finally {
        requestContext.terminate();
      }
    });
  }

  private void executeAgent(String question) {
    Thread.startVirtualThread(() -> {
      try {
        log("⚠️ YOLO mode activated...");
        var result = autonomousAgent.ask(question);
        onUi(() -> {
          response = result;
          processing = false;
        });
      } catch (Exception e) {
        onUi(() -> {
          log("⚠️ Agent error: " + e.getMessage());
          processing = false;
        });
      }
    });
  }

  private void submitRagPath() {
    var path = inputState.text()
        .trim();
    inputState.clear();

    try {
      if (path.isEmpty()) {
        log("📜 Loading RAG documents from default path...");
        documentLoader.loadDocument(null);
      } else {
        log("📜 Loading RAG documents from: " + path);
        documentLoader.loadDocument(Path.of(path));
      }
      ragDocumentsLoaded = true;
      log("✅ Documents loaded! You can now ask questions.");
    } catch (Exception e) {
      log("⚠️ Error loading documents: " + e.getMessage());
    }
  }

  /// Streams a Multi response from the AI service reactively.
  private void streamResponse(Function<String, Multi<String>> serviceCall, String question) {
    Thread.startVirtualThread(() -> serviceCall.apply(question)
        .subscribe()
        .with(
            token -> onUi(() -> response += token),
            error -> onUi(() -> {
              log("⚠️ Error: " + error.getMessage());
              processing = false;
            }),
            () -> onUi(() -> processing = false)
        ));
  }

  // ========== Helpers ==========

  /// Applies a UI state mutation on the render thread.
  /// Runs inline when already on it, so this is safe to call from any thread —
  /// agents and Mutiny subscriptions run on virtual threads and must not touch
  /// the fields the render loop reads.
  private void onUi(Runnable mutation) {
    runner.runOnRenderThread(mutation);
  }

  /// Appends a message to the logs panel, one entry per line.
  private void log(String message) {
    onUi(() -> appendLog(message));
  }

  private void appendLog(String message) {
    if (message == null || message.isBlank()) {
      return;
    }
    for (var line : message.stripTrailing().split("\n")) {
      logLines.add(line);
    }
    if (logLines.size() > MAX_LOG_LINES) {
      logLines.subList(0, logLines.size() - MAX_LOG_LINES).clear();
    }
    logRevision++;
  }

  /// Upper bound for the response scroll offset, measured against the area the
  /// response panel occupied in the last frame.
  ///
  /// MarkdownView clamps the scroll it is handed, so the display is already
  /// correct without this; what it avoids is the local counter running away, which
  /// would leave Up looking dead until it has been pressed as many times as Down
  /// was. The two extra rows absorb any difference between this measurement and
  /// the styled view actually rendered — over-estimating costs a couple of
  /// harmless key presses, under-estimating would block the last rows.
  private int maxResponseScroll() {
    var area = runner.elementRegistry()
        .getArea("chat-response");
    if (area == null) {
      return Integer.MAX_VALUE;
    }
    // The panel draws a rounded border, so the content is inset by one cell.
    var width = Math.max(1, area.width() - 2);
    var height = Math.max(1, area.height() - 2);
    var totalRows = MarkdownView.builder()
        .source(buildResponseText())
        .overflow(Overflow.WRAP_WORD)
        .build()
        .computeHeight(width);
    return Math.max(0, totalRows - height + 2);
  }

  /// Reuses the RAG info text area state across frames; rebuilding it on every
  /// render would drop the cursor and reparse the text ten times a second.
  private TextAreaState ragInfoState(String infoText) {
    if (ragInfoState == null || !ragInfoText.equals(infoText)) {
      ragInfoState = new TextAreaState(infoText);
      ragInfoText = infoText;
    }
    return ragInfoState;
  }

  private String buildResponseText() {
    if (processing && response.isEmpty()) {
      return "🤔 Thinking...";
    } else if (response.isEmpty()) {
      return "Type your question above and press Enter...";
    }
    return response;
  }

}
