package fr.wilda.picocli.sdk.ai;

import dev.langchain4j.agentic.declarative.SupervisorAgent;
import dev.langchain4j.agentic.declarative.SupervisorRequest;
import dev.langchain4j.agentic.supervisor.SupervisorResponseStrategy;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import fr.wilda.picocli.sdk.ai.agent.workflow.AvailableAgents;
import fr.wilda.picocli.sdk.ai.agent.workflow.ClassifierAgent;
import fr.wilda.picocli.sdk.ai.agent.workflow.JarvisAgent;
import fr.wilda.picocli.sdk.ai.agent.workflow.OVHcloudAgent;
import fr.wilda.picocli.sdk.ai.agent.workflow.RagAgent;
import io.quarkiverse.langchain4j.RegisterAiService;
import io.quarkiverse.langchain4j.ToolBox;
import io.quarkiverse.langchain4j.mcp.runtime.McpToolBox;
import io.quarkiverse.langchain4j.runtime.aiservice.ChatEvent;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Multi;
import jakarta.enterprise.context.ApplicationScoped;

public interface AgentAIService {
    @SupervisorAgent(subAgents = {JarvisAgent.class, RagAgent.class, OVHcloudAgent.class}, outputKey = "response",
        maxAgentsInvocations = 3, responseStrategy = SupervisorResponseStrategy.LAST)
    String chatSync(@V("userInput") String userInput);

    @SupervisorRequest
    static String request(@V("userInput") String userInput) {
      //Log.info("Request received: " + userInput);
      return "Answer to the following question: " + userInput;
    }
}

