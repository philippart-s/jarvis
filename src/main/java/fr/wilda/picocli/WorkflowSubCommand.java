package fr.wilda.picocli;

import fr.wilda.picocli.sdk.ai.agent.workflow.JarvisWorkflow;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.inject.Inject;
import picocli.AutoComplete.GenerateCompletion;
import picocli.CommandLine.Command;

import java.util.Scanner;
import java.util.concurrent.Callable;

@ActivateRequestContext
@Command(name = "workflow",
    description = "🔀 Agentic workflow - (Classification → Routing → Execution) 🔀",
    mixinStandardHelpOptions = true,
    usageHelpAutoWidth=true,
    subcommands = {GenerateCompletion.class})
public class WorkflowSubCommand extends BaseCommand implements Callable<Integer> {
  @Inject
  JarvisWorkflow jarvisWorkflow;

  @Override
  public Integer call() throws Exception {
    // workflow "pourquoi le ciel est bleu?"
    // workflow "donne moi le détail de mon compte ovhcloud"
    // workflow "en te basant sur les documents en ta procession donne moi le programme du Mars JUG"
    welcomeMessage();

    if (!interactive) {
      if (!question.isEmpty()) {
        processResponse(jarvisWorkflow.executeJarvisWorkflow(question));
      }
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
}

