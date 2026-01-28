package fr.wilda.picocli.sdk.ai.agent;

import dev.langchain4j.agentic.declarative.SupervisorAgent;
import dev.langchain4j.agentic.declarative.SupervisorRequest;
import dev.langchain4j.agentic.supervisor.SupervisorResponseStrategy;
import dev.langchain4j.service.V;
import fr.wilda.picocli.sdk.ai.agent.common.OVHcloudAgent;
import fr.wilda.picocli.sdk.ai.agent.common.RagAgent;
import fr.wilda.picocli.sdk.ai.agent.workflow.ChatAgent;

public interface AutonomousAgent {
    @SupervisorAgent(subAgents = {ChatAgent.class, RagAgent.class, OVHcloudAgent.class}, outputKey = "response",
        maxAgentsInvocations = 4, responseStrategy = SupervisorResponseStrategy.LAST)
    String ask(@V("userInput") String userInput);

    @SupervisorRequest
    static String request(@V("userInput") String userInput) {
      return "Answer to the following question: " + userInput;
    }
}

