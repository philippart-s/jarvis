package fr.wilda.picocli.sdk.ai.agent.workflow;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import fr.wilda.picocli.sdk.ai.tool.TimeAndDateTool;
import io.quarkiverse.langchain4j.ToolBox;
import io.quarkiverse.langchain4j.mcp.runtime.McpToolBox;

public interface OVHcloudAgent {

  @SystemMessage("""
                 You are specialized in OVHcloud products and account access.
                 If a request about OVHcloud resources is asked, use the tools provided for answser.
                 
                 If you don't know how to answer, just reply “⁉️ No data found for {userInput} on OVHcloud project ⁉️”
                 """)
  @UserMessage("{userInput}")
  @ToolBox(TimeAndDateTool.class)
  @McpToolBox
  @Agent(description = "Agent à utiliser lorsque la demande concerne OVHcloud", outputKey = "agentResponse")
  String askAQuestion(String userInput);
}
