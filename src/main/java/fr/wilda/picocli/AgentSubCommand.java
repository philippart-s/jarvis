package fr.wilda.picocli;

import fr.wilda.picocli.sdk.ai.agent.AutonomousAgent;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import picocli.AutoComplete.GenerateCompletion;
import picocli.CommandLine.Command;

import java.util.Scanner;
import java.util.concurrent.Callable;

@Command(name = "agent",
    description = "⚠️ YOLO mode!!! Autonomous agent selection. ⚠️",
    mixinStandardHelpOptions = true,
    subcommands = {GenerateCompletion.class})
public class AgentSubCommand extends BaseCommand implements Callable<Integer> {

  @Inject
  AutonomousAgent agentService;

  @Override
  public Integer call() throws Exception {
    // agent "pourquoi le ciel est bleu?"
    // agent "donne moi le détail de mon compte ovhcloud"
    // agent "en te basant sur les documents en ta procession donne moi le programme du Mars JUG"
    // ie: donne moi le programme du Mars JUG contenu dans le document en ta disposition
    welcomeMessage();

    if (!interactive) {
      if (!question.isEmpty()) {
        Log.info(String.format("🤖> %s%n", agentService.ask(question)));
      }
    } else {
      while (true) {
        Log.info("💬> ");
        Scanner scanner = new Scanner(System.in);
        var prompt = scanner.nextLine();
        if (prompt.equals("exit")) {
          break;
        } else {
          Log.info(String.format("🤖> %s%n", agentService.ask(prompt)));
        }
      }
    }
    return 0;
  }
}

