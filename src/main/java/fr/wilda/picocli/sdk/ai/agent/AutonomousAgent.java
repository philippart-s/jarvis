package fr.wilda.picocli.sdk.ai.agent;

import dev.langchain4j.agentic.declarative.SupervisorAgent;
import dev.langchain4j.agentic.declarative.SupervisorRequest;
import dev.langchain4j.agentic.supervisor.SupervisorResponseStrategy;
import dev.langchain4j.service.V;
import fr.wilda.picocli.sdk.ai.agent.workflow.JarvisAgent;
import fr.wilda.picocli.sdk.ai.agent.workflow.OVHcloudAgent;
import fr.wilda.picocli.sdk.ai.agent.workflow.RagAgent;

public interface AutonomousAgent {
    @SupervisorAgent(subAgents = {JarvisAgent.class, RagAgent.class, OVHcloudAgent.class}, outputKey = "response",
        maxAgentsInvocations = 3, responseStrategy = SupervisorResponseStrategy.LAST)
    String chatSync(@V("userInput") String userInput);

    @SupervisorRequest
    static String request(@V("userInput") String userInput) {
      //Log.info("Request received: " + userInput);
      return "Answer to the following question: " + userInput;
    }
}

