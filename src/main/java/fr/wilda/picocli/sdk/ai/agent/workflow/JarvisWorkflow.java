package fr.wilda.picocli.sdk.ai.agent.workflow;

import dev.langchain4j.agentic.declarative.SequenceAgent;

public interface JarvisWorkflow {

  @SequenceAgent(outputKey = "response", description = "Jarvis Workflow", subAgents =
      {
          ClassifierAgent.class,
          AvailableAgents.class
      })
  String executeJarvisWorkflow(String userInput);

}

