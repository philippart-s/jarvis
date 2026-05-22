package fr.wilda.picocli.sdk.ai.mcp;

import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.mcp.McpToolExecutor;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.service.tool.ToolExecutor;
import dev.langchain4j.service.tool.ToolProvider;
import dev.langchain4j.service.tool.ToolProviderRequest;
import dev.langchain4j.service.tool.ToolProviderResult;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@ApplicationScoped
public class ApprovalMcpToolProvider implements ToolProvider {

  @Inject
  @Any
  McpClient mcpClient;

  @Inject
  ToolApproval toolApproval;

  private static final Set<String> ALLOWED = Set.of(
      "list-cloud-projects"
  );

  @Override
  public ToolProviderResult provideTools(ToolProviderRequest request) {
    Map<ToolSpecification, ToolExecutor> tools = new HashMap<>();
    for (ToolSpecification spec : mcpClient.listTools()) {
      if (!ALLOWED.contains(spec.name())) {
        continue;
      }

      tools.put(spec, (toolRequest, memoryId) -> {
        if (toolApproval.requestApproval(toolRequest.name())) {
          Log.info(String.format("🔧 Using tool: %s%n", toolRequest.name()));
          return new McpToolExecutor(mcpClient).execute(toolRequest, memoryId);
        }
        Log.info(String.format("⛔️ User did not validate the use of the tool ⛔️!%n"));
        return "⛔️ User did not validate the use of the tool ⛔️!";
      });
    }

    return ToolProviderResult.builder().addAll(tools).build();
  }
}
