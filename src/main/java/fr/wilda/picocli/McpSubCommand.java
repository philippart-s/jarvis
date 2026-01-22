package fr.wilda.picocli;

import fr.wilda.picocli.sdk.ai.AIEndpointService;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import picocli.AutoComplete;
import picocli.CommandLine;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@CommandLine.Command(name = "mcp", description = "Use MCP to add knowledge to Jarvis.", mixinStandardHelpOptions = true,
    subcommands = {AutoComplete.GenerateCompletion.class})
public class McpSubCommand implements Callable<Integer> {

  @Inject
  AIEndpointService aiEndpointService;

  @CommandLine.Parameters(paramLabel = "<question>", description = "Question to ask to augmented Jarvis thanks to MCP.")
  String question;

  @Override
  public Integer call() throws Exception {
    aiEndpointService.askAQuestionAboutOVHcloud(question)
        .subscribe()
        .asStream()
        .forEach(token -> {
          try {
            TimeUnit.MILLISECONDS.sleep(150);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          Log.info(token);
        });

    Log.info("\n");

    return 0;
  }

}
