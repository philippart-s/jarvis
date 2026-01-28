package fr.wilda.picocli;

import fr.wilda.picocli.sdk.ai.agent.workflow.JarvisWorkflow;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.inject.Inject;
import picocli.AutoComplete.GenerateCompletion;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.Scanner;
import java.util.concurrent.Callable;

@ActivateRequestContext
@Command(name = "workflow",
    description = "🐣 Agentic semi autonomous workflow 🤖",
    mixinStandardHelpOptions = true,
    usageHelpAutoWidth=true,
    subcommands = {GenerateCompletion.class})
public class WorkflowSubCommandAgent implements Callable<Integer> {
  @CommandLine.Mixin
  AgentBaseCommand agentBaseCommand;

  @Inject
  JarvisWorkflow jarvisWorkflow;

  @Override
  public Integer call() throws Exception {
    // Display common welcom message
    agentBaseCommand.welcomeMessage();

    if (!agentBaseCommand.interactive) {
      if (!agentBaseCommand.question.isEmpty()) {
        agentBaseCommand.processResponse(jarvisWorkflow.executeJarvisWorkflow(agentBaseCommand.question));
      }
    } else {
      while (true) {
        Log.info("💬> ");
        Scanner scanner = new Scanner(System.in);
        var prompt = scanner.nextLine();
        if (prompt.equals("exit")) {
          break;
        } else {
          agentBaseCommand.processResponse(jarvisWorkflow.executeJarvisWorkflow(prompt));
        }
      }
    }
    return 0;
  }
}

