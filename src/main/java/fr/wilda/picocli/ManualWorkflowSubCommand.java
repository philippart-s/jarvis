package fr.wilda.picocli;

import fr.wilda.picocli.sdk.ai.McpToolsException;
import fr.wilda.picocli.sdk.ai.agent.manual.ClassifierAgent;
import fr.wilda.picocli.sdk.ai.agent.manual.JarvisAgent;
import fr.wilda.picocli.sdk.ai.agent.manual.OVHcloudAgent;
import fr.wilda.picocli.sdk.ai.agent.manual.RagAgent;
import io.quarkiverse.langchain4j.runtime.aiservice.ChatEvent;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.inject.Inject;
import picocli.AutoComplete.GenerateCompletion;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.util.Scanner;
import java.util.concurrent.Callable;

@ActivateRequestContext
@Command(name = "manual-workflow",
    description = "Mode workflow agentique - Orchestration explicite des étapes (Classification → Routage → Exécution)",
    mixinStandardHelpOptions = true,
    subcommands = {GenerateCompletion.class})
public class ManualWorkflowSubCommand implements Callable<Integer> {
  @Parameters(paramLabel = "<question>", description = "Question à poser à Jarvis mode workflow agentique")
  String question;


  @Inject
  ClassifierAgent classifierAgent;

  @Inject
  RagAgent ragAgent;

  @Inject
  JarvisAgent jarvisAgent;

  @Inject
  OVHcloudAgent ovhcloudAgent;

  @Override
  public Integer call() throws Exception {
    String agentResponse = "";

    Log.info("━".repeat(50));
    Log.info("🔄 Mode Workflow Manuel");
    Log.info("━".repeat(50));

    Log.info("🔀 Choose the right agent given the user prompt.");

    var agentToCall = classifierAgent.classify(question);
    switch (agentToCall) {
      case MCP -> {
        Log.info("☁️ MCP Agent selected ☁️");
        agentResponse = ovhcloudAgent.askAQuestion(question);
      }
      case RAG -> {
        Log.info("📜 RAG Agent selected 📜");
        ragAgent.askAQuestionEvent(question);

      }
      case CHAT -> Log.info("Chat Agent selected");
      default -> {

      }
    }
      Log.info("🤖 Call Jarvis Agent after agents 🤖");
      jarvisAgent.askAQuestion(question, agentResponse).onItem()
          .invoke(event -> {
            switch (event) {
              case ChatEvent.PartialResponseEvent e -> {
                Log.info(e.getChunk());
              }
              case ChatEvent.BeforeToolExecutionEvent e -> {
                Log.info(String.format("⚠️ Please valid the tool usage: %s ⚠️%n", e.getRequest().name()));
                Log.info("Please type 'ok' to confirm the use of the tool: ");
                Scanner scanner = new Scanner(System.in);
                if (scanner.next()
                    .equals("ok")) {
                  Log.info(String.format("🔧 Using tool: %s", e.getRequest().name()));
                } else {
                  throw new McpToolsException();
                }
              }
              default -> {
              }
            }
          })
          .collect()
          .asList()
          .await()
          .indefinitely();



    return 0;
  }
}

