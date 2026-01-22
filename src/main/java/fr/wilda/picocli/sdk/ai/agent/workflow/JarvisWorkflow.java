package fr.wilda.picocli.sdk.ai.agent.workflow;

import dev.langchain4j.agentic.declarative.SequenceAgent;
import fr.wilda.picocli.sdk.ai.agent.common.ClassifierAgent;
import fr.wilda.picocli.sdk.ai.agent.common.JarvisAgent;
import io.smallrye.mutiny.Multi;

public interface JarvisWorkflow {

  @SequenceAgent(outputKey = "WFresponse", description = "Jarvis Workflow", subAgents =
      {
          ClassifierAgent.class,
          AvailableAgents.class,
          JarvisAgent.class
      })
  Multi<String> executeJarvisWorkflow(String userInput);

}

