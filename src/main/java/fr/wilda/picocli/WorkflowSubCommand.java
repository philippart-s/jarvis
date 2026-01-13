package fr.wilda.picocli;

import fr.wilda.picocli.sdk.ai.McpToolsException;
import fr.wilda.picocli.sdk.ai.agent.workflow.JarvisWorkflow;
import io.quarkiverse.langchain4j.runtime.aiservice.ChatEvent;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Multi;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.inject.Inject;
import picocli.AutoComplete.GenerateCompletion;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@ActivateRequestContext
@Command(name = "workflow",
    description = "Mode workflow agentique - Orchestration explicite des étapes (Classification → Routage → Exécution)",
    mixinStandardHelpOptions = true,
    subcommands = {GenerateCompletion.class})
public class WorkflowSubCommand implements Callable<Integer> {

  @Parameters(paramLabel = "<question>", description = "Question à poser à Jarvis mode workflow agentique", defaultValue = "")
  String question;

  @CommandLine.Option(names = {"-i", "--interactive"})
  boolean interactive;

  @Inject
  JarvisWorkflow jarvisWorkflow;

  @Override
  public Integer call() throws Exception {
    // workflow "pourquoi le ciel est bleu?"
    // workflow "donne moi le détail de mon compte ovhcloud"
    // workflow "en te basant sur les documents en ta procession donne moi le programme du Mars JUG"

    Log.info("━".repeat(50));
    Log.info("🔄 Mode Workflow Agentique");
    Log.info("━".repeat(50));

    if (!interactive) {
      processResponse(jarvisWorkflow.executeJarvisWorkflow(question));
    } else {
      while (true) {
        Log.info("💬> ");
        Scanner scanner = new Scanner(System.in);
        var prompt = scanner.nextLine();
        if (prompt.equals("exit")) {
          break;
        } else {
          processResponse(jarvisWorkflow.executeJarvisWorkflow(prompt));
        }
      }
    }
    return 0;
  }

  private void processResponse(Multi<String> response) {
    response.subscribe()
        .asStream()
        .forEach(token -> {
          try {
            TimeUnit.MILLISECONDS.sleep(150);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          Log.info(token);
        });
  }
}

