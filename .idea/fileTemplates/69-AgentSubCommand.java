package fr.wilda.picocli;

import fr.wilda.picocli.sdk.ai.agent.AutonomousAgent;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

@Command(name = "agent",
    description = "⚠️ YOLO mode!!! Autonomous agent selection. ⚠️",
    mixinStandardHelpOptions = true)
public class AgentSubCommand implements Callable<Integer> {

  @Inject
  AutonomousAgent agentService;

  @CommandLine.Parameters(paramLabel = "<question>", description = "💬 Ask your question", defaultValue = "")
  String question;

  @Override
  public Integer call() throws Exception {
    Log.info(String.format("🤖> %s%n", agentService.ask(question)));
    return 0;
  }
}

