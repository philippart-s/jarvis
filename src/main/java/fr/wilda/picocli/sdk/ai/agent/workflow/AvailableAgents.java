package fr.wilda.picocli.sdk.ai.agent.workflow;

import dev.langchain4j.agentic.declarative.ConditionalAgent;
import dev.langchain4j.agentic.declarative.SubAgent;

public interface AvailableAgents {

  @ConditionalAgent(
      subAgents = {
          @SubAgent(type = OVHcloudAgent.class),
          @SubAgent(type = RagAgent.class)
      }
  )
  void chooseAgent(String userInput);
}
