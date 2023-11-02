package fr.wilda.picocli;

import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.quarkus.picocli.runtime.annotations.TopCommand;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@TopCommand
@Command(name = "jarvis", mixinStandardHelpOptions = true, subcommands = {OVHcloudSubCommand.class})
public class JarvisCommand implements Callable<Integer> {
  // Logger
  private static final Logger _LOG = LoggerFactory.getLogger(JarvisCommand.class);

  // Name to display
  @Parameters(paramLabel = "<name>", defaultValue = "Tony", description = "Your name.")
  private String name;

  @Override
  public Integer call() throws Exception {
    _LOG.info("Hello {}, what can I do for you today?", name);

    return 0;
  }
}
