package fr.wilda.picocli;

import fr.wilda.picocli.sdk.ai.AIEndpointService;
import io.quarkus.picocli.runtime.annotations.TopCommand;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.AutoComplete.GenerateCompletion;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

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
public class JarvisCommand implements Callable<Integer> {
  // Logger
  private static final Logger _LOG = LoggerFactory.getLogger(JarvisCommand.class);

  @Inject
  AIEndpointService aiEndpointService;

  // Question to ask
  @CommandLine.Parameters(paramLabel = "<question>", defaultValue = "Explique ton rôle en quelques mots", description = "La question à poser à Jarvis.")
  private String question;

  @Override
  public Integer call() throws Exception {
    _LOG.info("\n🤖:\n");

    aiEndpointService.askAQuestion(question)
        .subscribe()
        .asStream()
        .forEach(token -> {
          try {
            TimeUnit.MILLISECONDS.sleep(150);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          _LOG.info(token);
        });

    _LOG.info("\n");
    return 0;
  }
}
