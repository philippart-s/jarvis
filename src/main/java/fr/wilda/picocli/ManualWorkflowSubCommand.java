package fr.wilda.picocli;

import fr.wilda.picocli.sdk.ai.agent.common.ClassifierAgent;
import fr.wilda.picocli.sdk.ai.agent.common.JarvisAgent;
import fr.wilda.picocli.sdk.ai.agent.common.OVHcloudAgent;
import fr.wilda.picocli.sdk.ai.agent.common.RagAgent;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.inject.Inject;
import picocli.AutoComplete.GenerateCompletion;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.Scanner;
import java.util.concurrent.Callable;

@ActivateRequestContext
@Command(name = "manual-workflow",
    description = "👤 Agentic manual workflow 🤖",
    mixinStandardHelpOptions = true,
    subcommands = {GenerateCompletion.class})
public class ManualWorkflowSubCommand implements Callable<Integer> {

  @CommandLine.Mixin
  AgentBaseCommand agentBaseCommand;

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
    // Display common welcom message
    agentBaseCommand.welcomeMessage();

    if (!agentBaseCommand.interactive) {
      if (!agentBaseCommand.question.isEmpty()) {
        workflow(agentBaseCommand.question);
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
        Log.info("☁️ MCP Agent selected ☁️\n");
        agentResponse = ovhcloudAgent.askAQuestion(input);
      }
      case RAG -> {
        Log.info("📜 RAG Agent selected 📜\n");
        ragAgent.askAQuestionEvent(input);

      }
      case CHAT -> Log.info("💬 Chat Agent selected 💬\n");
      default -> {

      }
    }

    agentBaseCommand.processResponse(jarvisAgent.askAQuestion(input, agentResponse));
  }
}

