package fr.wilda.picocli;

import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "jarvis", mixinStandardHelpOptions = true)
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