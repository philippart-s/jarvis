package fr.wilda.picocli.sdk.ai.agent.workflow;

import dev.langchain4j.agentic.declarative.SequenceAgent;
import io.quarkiverse.langchain4j.runtime.aiservice.ChatEvent;
import io.smallrye.mutiny.Multi;

public interface JarvisWorkflow {

  @SequenceAgent(outputKey = "WFresponse", description = "Jarvis Workflow", subAgents =
      {
          ClassifierAgent.class,
          AvailableAgents.class
      })
  Multi<ChatEvent> executeJarvisWorkflow(String userInput);

}

