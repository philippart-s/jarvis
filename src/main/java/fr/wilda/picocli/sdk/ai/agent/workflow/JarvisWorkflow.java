package fr.wilda.picocli.sdk.ai.agent.workflow;

import dev.langchain4j.agentic.declarative.SequenceAgent;
import dev.langchain4j.agentic.declarative.SubAgent;

public interface JarvisWorkflow {

  @SequenceAgent(description = "Jarvis Workflow", subAgents =
      {
          @SubAgent(type = ClassifierAgent.class),
          @SubAgent(type = AvailableAgents.class)
      })
  void executeJarvisWorkflow(String userInput);
}

