package fr.wilda.picocli.sdk.ai.agent.workflow;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import fr.wilda.picocli.sdk.ai.TimeAndDateTool;
import io.quarkiverse.langchain4j.RegisterAiService;
import io.quarkiverse.langchain4j.ToolBox;
import io.quarkiverse.langchain4j.mcp.runtime.McpToolBox;
import io.quarkiverse.langchain4j.runtime.aiservice.ChatEvent;
import io.smallrye.mutiny.Multi;
import jakarta.enterprise.context.ApplicationScoped;

public interface JarvisAgent {
  @SystemMessage("""
                  You are a virtual assistant.
                  Your goal is to help as best as possible when you are asked a question.
                  If you don’t know how to answer, just reply “I don’t know how to answer this question.”
                  Answer in a concise and simple way.
                 """)
  @Agent(description = "Agent à utiliser lorsque la demande générale.", outputKey = "response")
  @UserMessage("{userInput}")
  String askAQuestion(String userInput);
}
