package fr.wilda.picocli;

import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logmanager.LogContext;
import org.jboss.logmanager.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Controls logging redirection during TUI sessions.
 * <p>
 * The Quarkus console handler holds a static reference to stdout.
 * When JLine is active, bytes written to stdout are interpreted as keyboard input.
 * This controller removes all console handlers and redirects log messages
 * to a {@link Consumer} callback (typically the TUI logs panel).
 */
@ApplicationScoped
public class TuiLoggingController {

  private Logger rootLogger;
  private Handler tuiHandler;
  private final List<Handler> previousHandlers = new ArrayList<>();

  public void enable(Consumer<String> onMessage) {
    if (tuiHandler != null) {
      return;
    }

    rootLogger = LogContext.getLogContext().getLogger("");

    for (Handler handler : rootLogger.getHandlers()) {
      previousHandlers.add(handler);
      rootLogger.removeHandler(handler);
    }

    tuiHandler = new Handler() {
      @Override
      public void publish(LogRecord record) {
        if (record.getMessage() != null) {
          onMessage.accept(record.getMessage());
        }
      }
      @Override public void flush() {}
      @Override public void close() {}
    };
    tuiHandler.setLevel(Level.INFO);
    rootLogger.addHandler(tuiHandler);
  }

  public void disable() {
    if (rootLogger == null) {
      return;
    }

    if (tuiHandler != null) {
      rootLogger.removeHandler(tuiHandler);
      tuiHandler = null;
    }

    for (Handler handler : previousHandlers) {
      rootLogger.addHandler(handler);
    }
    previousHandlers.clear();
    rootLogger = null;
  }
}
