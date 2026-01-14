package fr.wilda.picocli;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Multi;
import picocli.CommandLine;

import java.util.concurrent.TimeUnit;

/// This command group some utilities and paramter for all commands.
public class BaseCommand {

  @CommandLine.Parameters(paramLabel = "<question>", description = "💬Ask your question (non interactive mode)", defaultValue = "")
  String question;

  @CommandLine.Option(names = {"-i", "--interactive"}, description = "🔄 Interactive mode, enter exit to quit.")
  boolean interactive;

  @CommandLine.Spec
  CommandLine.Model.CommandSpec spec;

  void welcomeMessage() {
    Log.info(String.format("━".repeat(130) + "%n"));
    Log.info(String.format("🤖 Welcome to %s command 🤖%n", spec.commandLine().getCommandName()));
    Log.info(String.format("ℹ️ Enter exit to quit ℹ️%n"));
    spec.commandLine().usage(System.out);
    Log.info(String.format("━".repeat(130) + "%n"));
  }

  void processResponse(Multi<String> response) {
    response.subscribe()
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
  }
}
