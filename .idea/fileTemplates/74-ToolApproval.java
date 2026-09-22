package fr.wilda.picocli.sdk.ai.mcp;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

/// Centralized tool approval mechanism that handles both CLI and TUI modes.
/// In CLI mode, approval is requested via stdin (Scanner).
/// In TUI mode, approval is delegated to the TUI render loop via a CompletableFuture.
@ApplicationScoped
public class ToolApproval {

  private volatile boolean tuiMode = false;
  private final AtomicReference<CompletableFuture<Boolean>> pendingApproval = new AtomicReference<>();
  private volatile String pendingToolName = "";

  /// Activates TUI mode approval.
  public void enableTuiMode() {
    tuiMode = true;
  }

  /// Deactivates TUI mode approval.
  public void disableTuiMode() {
    tuiMode = false;
    // Cancel any pending approval
    var pending = pendingApproval.getAndSet(null);
    if (pending != null) {
      pending.complete(false);
    }
  }

  /// Requests approval for a tool execution. Blocks until the user responds.
  /// Automatically delegates to CLI (Scanner) or TUI (dialog) depending on active mode.
  /// Returns true if approved, false otherwise.
  public boolean requestApproval(String toolName) {
    if (!tuiMode) {
      return requestCliApproval(toolName);
    }
    return requestTuiApproval(toolName);
  }

  /// Called by the TUI when the user approves the tool usage.
  public void approve() {
    var pending = pendingApproval.get();
    if (pending != null) {
      pending.complete(true);
    }
  }

  /// Called by the TUI when the user rejects the tool usage.
  public void reject() {
    var pending = pendingApproval.get();
    if (pending != null) {
      pending.complete(false);
    }
  }

  /// Returns the name of the tool waiting for approval, or empty string if none.
  public String pendingToolName() {
    return pendingToolName;
  }

  /// Returns true if there is a pending approval request.
  public boolean hasPendingApproval() {
    return pendingApproval.get() != null;
  }

  private boolean requestCliApproval(String toolName) {
    Log.info(String.format("⚠️ Please validate the tool usage: %s ⚠️%n", toolName));
    Log.info("Please type 'ok' to confirm the use of the tool: ");
    var scanner = new Scanner(System.in);
    return scanner.next().equals("ok");
  }

  private boolean requestTuiApproval(String toolName) {
    pendingToolName = toolName;
    var future = new CompletableFuture<Boolean>();
    pendingApproval.set(future);
    try {
      return future.get();
    } catch (Exception e) {
      return false;
    } finally {
      pendingApproval.set(null);
      pendingToolName = "";
    }
  }
}
