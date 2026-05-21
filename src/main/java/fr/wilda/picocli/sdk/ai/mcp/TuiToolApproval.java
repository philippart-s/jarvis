package fr.wilda.picocli.sdk.ai.mcp;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

/// Bridge between the TUI and the MCP tool approval mechanism.
/// When TUI mode is active, tool approval is delegated to the TUI
/// via a CompletableFuture that blocks until the user responds.
@ApplicationScoped
public class TuiToolApproval {

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

  /// Returns true if TUI mode is active.
  public boolean isTuiMode() {
    return tuiMode;
  }

  /// Requests approval for a tool usage. Blocks until the TUI user responds.
  /// Returns true if approved, false otherwise.
  public boolean requestApproval(String toolName) {
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
}
