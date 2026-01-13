package fr.wilda.picocli;

import fr.wilda.picocli.sdk.ai.agent.AutonomousAgent;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import picocli.AutoComplete.GenerateCompletion;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.util.Scanner;
import java.util.concurrent.Callable;

@Command(name = "agent",
    description = "Mode agent ReAct - L'agent décide autonomement des actions (Think → Act → Observe → Repeat)",
    mixinStandardHelpOptions = true,
    subcommands = {GenerateCompletion.class})
public class AgentSubCommand implements Callable<Integer> {

  @Inject
  AutonomousAgent agentService;

  @Parameters(paramLabel = "<question>",
      description = "Question à poser à l'agent",
      defaultValue = "")
  String question;

  @CommandLine.Option(names = {"-i", "--interactive"})
  boolean interactive;


  @Override
  public Integer call() throws Exception {
    // agent "pourquoi le ciel est bleu?"
    // agent "donne moi le détail de mon compte ovhcloud"
    // agent "en te basant sur les documents en ta procession donne moi le programme du Mars JUG"
    // ie: donne moi le programme du Mars JUG contenu dans le document en ta disposition
    if (!interactive) {
      Log.info("💬: " + question);
      Log.info("🤖: " + agentService.chatSync(question));
    } else {
      while (true) {
        Log.info("💬> ");
        Scanner scanner = new Scanner(System.in);
        var prompt = scanner.nextLine();
        if (prompt.equals("exit")) {
          break;
        } else {
          Log.info(String.format("🤖> %s %n", agentService.chatSync(prompt)));
        }
      }
    }
    return 0;
  }
}

