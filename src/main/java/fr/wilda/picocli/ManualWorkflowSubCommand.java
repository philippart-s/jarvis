package fr.wilda.picocli;

import fr.wilda.picocli.sdk.ai.agent.common.ClassifierAgent;
import fr.wilda.picocli.sdk.ai.agent.common.JarvisAgent;
import fr.wilda.picocli.sdk.ai.agent.common.OVHcloudAgent;
import fr.wilda.picocli.sdk.ai.agent.common.RagAgent;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.inject.Inject;
import picocli.AutoComplete.GenerateCompletion;
import picocli.CommandLine.Command;

import java.util.Scanner;
import java.util.concurrent.Callable;

@ActivateRequestContext
@Command(name = "manual-workflow",
    description = "Mode workflow agentique - Orchestration explicite des étapes (Classification → Routage → Exécution)",
    mixinStandardHelpOptions = true,
    subcommands = {GenerateCompletion.class})
public class ManualWorkflowSubCommand extends BaseCommand implements Callable<Integer> {

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
    // manual-workflow "pourquoi le ciel est bleu?"
    // manual-workflow "donne moi le détail de mon compte ovhcloud"
    // manual-workflow "en te basant sur les documents en ta procession donne-moi le programme du Mars JUG de janvier 2026"

    welcomeMessage();

    if (!interactive) {
      if (!question.isEmpty()) {
        workflow(question);
      }
    } else {
      while (true) {
        Log.info("💬> ");
        Scanner scanner = new Scanner(System.in);
        var prompt = scanner.nextLine();
        if (prompt.equals("exit")) {
          break;
        } else {
          workflow(prompt);
        }
      }
    }
    return 0;
  }

  private void workflow(String input) {
    var agentResponse = "";
    var agentToCall = classifierAgent.classify(input);
    switch (agentToCall) {
      case MCP -> {
        Log.infof("☁️ MCP Agent selected ☁️%n");
        agentResponse = ovhcloudAgent.askAQuestion(input);
      }
      case RAG -> {
        Log.infof("📜 RAG Agent selected 📜%n");
        ragAgent.askAQuestionEvent(input);

      }
      case CHAT -> Log.infof("💬 Chat Agent selected 💬%n");
      default -> {

      }
    }

    processResponse(jarvisAgent.askAQuestion(input, agentResponse));

  }
}

