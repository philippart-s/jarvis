package fr.wilda.picocli;

import fr.wilda.picocli.sdk.ai.McpToolsException;
import fr.wilda.picocli.sdk.ai.agent.workflow.JarvisWorkflow;
import io.quarkiverse.langchain4j.runtime.aiservice.ChatEvent;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import picocli.AutoComplete.GenerateCompletion;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.util.Scanner;
import java.util.concurrent.Callable;

@Command(name = "workflow",
    description = "Mode workflow agentique - Orchestration explicite des étapes (Classification → Routage → Exécution)",
    mixinStandardHelpOptions = true,
    subcommands = {GenerateCompletion.class})
public class WorkflowSubCommand implements Callable<Integer> {
  @Parameters(paramLabel = "<question>", description = "Question à poser à Jarvis mode workflow agentique")
  String question;

  @Inject
  JarvisWorkflow jarvisWorkflow;

  @Override
  public Integer call() throws Exception {
    Log.info("━".repeat(50));
    Log.info("\n🔄 Mode Workflow Agentique\n");
    Log.info("━".repeat(50) + "\n");

    jarvisWorkflow.executeJarvisWorkflow(question).onItem()
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

