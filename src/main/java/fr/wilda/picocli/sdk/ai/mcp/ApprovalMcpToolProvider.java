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
import java.util.Scanner;

@ApplicationScoped
public class ApprovalMcpToolProvider implements ToolProvider {

  @Inject
  @Any
  McpClient mcpClient;

  @Override
  public ToolProviderResult provideTools(ToolProviderRequest request) {
    Map<ToolSpecification, ToolExecutor> tools = new HashMap<>();
      for (ToolSpecification spec : mcpClient.listTools()) {
        tools.put(spec, (toolRequest, memoryId) -> {
          // Validationdon
          Log.info(String.format("⚠️ Please valid the tool usage: %s ⚠️%n", toolRequest.name()));
          Log.info("Please type 'ok' to confirm the use of the tool: ");
          Scanner scanner = new Scanner(System.in);
          if (scanner.next()
              .equals("ok")) {
            Log.info(String.format("🔧 Using tool: %s%n",toolRequest.name()));
          } else {
            Log.info(String.format("⛔️ User did not validate the use of the tool ⛔️!%n"));
            return "⛔️ User did not validate the use of the tool ⛔️!";
          }
          return new McpToolExecutor(mcpClient).execute(toolRequest, memoryId);
        });
      }


    return ToolProviderResult.builder().addAll(tools).build();
  }
}
