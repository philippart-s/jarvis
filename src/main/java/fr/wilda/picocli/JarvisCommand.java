package fr.wilda.picocli;

import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import dev.langchain4j.observability.api.event.ToolExecutedEvent;
import fr.wilda.picocli.sdk.ai.McpToolsException;
import io.quarkiverse.langchain4j.runtime.aiservice.ChatEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import fr.wilda.picocli.sdk.ai.AIEndpointService;
import io.quarkus.picocli.runtime.annotations.TopCommand;
import jakarta.inject.Inject;
import picocli.AutoComplete.GenerateCompletion;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@TopCommand
@Command(name = "jarvis", mixinStandardHelpOptions = true, subcommands = {OVHcloudSubCommand.class, SentimentSubCommand.class, GenerateCompletion.class})
public class JarvisCommand implements Callable<Integer> {
  // Logger
  private static final Logger _LOG = LoggerFactory.getLogger(JarvisCommand.class);

  @Inject
  AIEndpointService aiEndpointService;

  // Question to ask
  @Parameters(paramLabel = "<question>", defaultValue = "Explique ton rôle en quelques mots", description = "La question à poser à Jarvis.")
  private String question;

  @Override
  public Integer call() throws Exception {
    _LOG.info("\n🤖:\n");

    try {
      aiEndpointService.askAQuestionEvent(question)
          .onItem()
          .invoke(event -> {
            switch (event) {
              case ChatEvent.PartialResponseEvent e -> {
                _LOG.info(e.getChunk());
              }
              case ChatEvent.BeforeToolExecutionEvent e -> {
                _LOG.info(String.format("⚠️ Please valid the tool usage: %s ⚠️%n",  e.getRequest().name()));
                Scanner scanner = new Scanner(System.in);
                if (scanner.next()
                    .equals("ok")) {
                  _LOG.info(String.format("🔧 Using tool: %s", e.getRequest().name()));
                } else {
                  throw new McpToolsException();
                }
              }
              default -> {
              }
            }
          })
          .collect()
          .asList()
          .await()
          .indefinitely();
    } catch (McpToolsException e) {
      _LOG.info("⛔️ User did not validate the use of the tool ⛔️!");
    }

    /*aiEndpointService.askAQuestion(question)
    .subscribe()
    .asStream()
    .forEach(token -> {
      try {
        TimeUnit.MILLISECONDS.sleep(150);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      _LOG.info(token);
    });*/
    _LOG.info("\n");

    return 0;
  }
}
