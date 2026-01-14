package fr.wilda.picocli;

import fr.wilda.picocli.sdk.ai.AIEndpointService;
import io.quarkus.logging.Log;
import io.quarkus.picocli.runtime.annotations.TopCommand;
import jakarta.inject.Inject;
import picocli.AutoComplete.GenerateCompletion;
import picocli.CommandLine.Command;

import java.util.Scanner;
import java.util.concurrent.Callable;

@TopCommand
@Command(name = "jarvis", usageHelpAutoWidth = true, mixinStandardHelpOptions = true, subcommands = {
    OVHcloudSubCommand.class,
    RagSubCommand.class,
    McpSubCommand.class,
    WorkflowSubCommand.class,
    AgentSubCommand.class,
    GenerateCompletion.class,
    ManualWorkflowSubCommand.class
})
public class JarvisCommand extends BaseCommand implements Callable<Integer> {
  @Inject
  AIEndpointService aiEndpointService;

  @Override
  public Integer call() throws Exception {
    welcomeMessage();

    if (!interactive) {
      if (!question.isEmpty()) {
        processResponse(aiEndpointService.askAQuestion(question));
      }
    } else {
      while (true) {
        Log.info("💬> ");
        Scanner scanner = new Scanner(System.in);
        var prompt = scanner.nextLine();
        if (prompt.equals("exit")) {
          break;
        } else {
          processResponse(aiEndpointService.askAQuestion(prompt));
        }
      }
    }
    return 0;
  }
}
