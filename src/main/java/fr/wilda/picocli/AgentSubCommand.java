package fr.wilda.picocli;

import fr.wilda.picocli.sdk.ai.AgentAIService;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import picocli.AutoComplete.GenerateCompletion;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

@Command(name = "agent",
    description = "Mode agent ReAct - L'agent décide autonomement des actions (Think → Act → Observe → Repeat)",
    mixinStandardHelpOptions = true,
    subcommands = {GenerateCompletion.class})
public class AgentSubCommand implements Callable<Integer> {

  @Inject
  AgentAIService agentService;
  @Parameters(paramLabel = "<question>",
      description = "Question à poser à l'agent",
      defaultValue = "")
  String question;


  @Override
  public Integer call() throws Exception {
    // agent "pourquoi le ciel est bleu?"
    // agent "donne moi le détail de mon compte ovhcloud"
    // agent "en te basant sur les documents en ta procession donne moi le programme du Mars JUG"
    // ie: donne moi le programme du Mars JUG contenu dans le document en ta disposition
    Log.info("💬: " + question);
    Log.info("🤖: " + agentService.chatSync(question));

    return 0;
  }
}

