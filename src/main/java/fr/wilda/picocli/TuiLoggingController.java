package fr.wilda.picocli;

import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logmanager.LogContext;
import org.jboss.logmanager.Logger;
import org.jboss.logmanager.formatters.PatternFormatter;
import org.jboss.logmanager.handlers.OutputStreamHandler;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;

/**
 * Controls logging redirection during TUI sessions.
 * <p>
 * The Quarkus console handler holds a static reference to stdout.
 * When JLine is active, bytes written to stdout are interpreted as keyboard input.
 * This controller removes all console handlers and redirects logs to a custom output stream
 * (typically the TUI logs panel).
 */
@ApplicationScoped
public class TuiLoggingController {

  private Logger rootLogger;
  private OutputStreamHandler tuiHandler;
  private final List<Handler> previousHandlers = new ArrayList<>();

  public void enable(OutputStream outputStream) {
    if (tuiHandler != null) {
      return;
    }

    rootLogger = LogContext.getLogContext().getLogger("");

    for (Handler handler : rootLogger.getHandlers()) {
      previousHandlers.add(handler);
      rootLogger.removeHandler(handler);
    }

    tuiHandler = new OutputStreamHandler(
        outputStream,
        new PatternFormatter("%s%n")
    );
    tuiHandler.setLevel(Level.INFO);
    rootLogger.addHandler(tuiHandler);
  }

  public void disable() {
    if (rootLogger == null) {
      return;
    }

    if (tuiHandler != null) {
      rootLogger.removeHandler(tuiHandler);
      tuiHandler.close();
      tuiHandler = null;
    }

    for (Handler handler : previousHandlers) {
      rootLogger.addHandler(handler);
    }
    previousHandlers.clear();
    rootLogger = null;
  }
}
