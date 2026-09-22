package fr.wilda.picocli.sdk.ai.mcp;

import dev.langchain4j.service.tool.ToolProvider;
import dev.langchain4j.service.tool.ToolProviderRequest;
import dev.langchain4j.service.tool.ToolProviderResult;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ApprovalMcpToolProvider implements ToolProvider {

  // 45-MCP-client

  @Override
  public ToolProviderResult provideTools(ToolProviderRequest request) {
    // 46-approval-tool
  }
}
