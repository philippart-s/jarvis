package fr.wilda.picocli.sdk.ai.tool;

import dev.langchain4j.agent.tool.Tool;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;

@ApplicationScoped
public class TimeAndDateTool {

  /// See https://github.com/quarkiverse/quarkus-langchain4j/issues/1581 and https://github.com/quarkiverse/quarkus-langchain4j/issues/1877
  /// for more explanations.
  @Tool("Tool to give the current time and date")
  public String getTimeAndDate() {
    Log.info("⏰  Time and Date tool ⏰.\n");

    return LocalDateTime.now().toString();
  }
}