package fr.wilda.picocli;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import fr.wilda.picocli.sdk.ai.AIEndpointMistral7bService;
import io.quarkus.picocli.runtime.annotations.TopCommand;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@TopCommand
@Command(name = "jarvis", mixinStandardHelpOptions = true, subcommands = {OVHcloudSubCommand.class})
public class JarvisCommand implements Callable<Integer> {
  // Logger
  private static final Logger _LOG = LoggerFactory.getLogger(JarvisCommand.class);

  // Question to ask
  @Parameters(paramLabel = "<question>",
      defaultValue = "What is the answer to life, the universe and everything?",
      description = "The question to ask to Jarvis.")
  private String question;

  @Inject
  AIEndpointMistral7bService aiEndpointMistral7bService;

  @Override
  public Integer call() throws Exception {
    _LOG.info("\n🤖:\n");
    aiEndpointMistral7bService.askAQuestion(question)
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
