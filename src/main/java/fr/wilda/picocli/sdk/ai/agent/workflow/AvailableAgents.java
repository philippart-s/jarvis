package fr.wilda.picocli.sdk.ai.agent.workflow;

import dev.langchain4j.agentic.declarative.ActivationCondition;
import dev.langchain4j.agentic.declarative.ConditionalAgent;
import fr.wilda.picocli.sdk.ai.agent.common.ClassifierAgent;
import fr.wilda.picocli.sdk.ai.agent.common.OVHcloudAgent;
import fr.wilda.picocli.sdk.ai.agent.common.RagAgent;
import io.quarkus.logging.Log;

public interface AvailableAgents {

  @ConditionalAgent(description = "Agent used to determine which type of agent is needed: CHAT, MCP, or RAG.",
      subAgents = {
          OVHcloudAgent.class,
          RagAgent.class,
          ChatAgent.class
      },
      outputKey = "agentResponse"
  )
  String executeAgent(String userInput, ClassifierAgent.SubCommand subCommand);

  @ActivationCondition(OVHcloudAgent.class)
  static boolean activateOVHcloudAgent(ClassifierAgent.SubCommand subCommand) {
    var isActivated = subCommand.equals(ClassifierAgent.SubCommand.MCP);
    if (isActivated) {
      Log.info(String.format("Agent to activate: %s%n",  subCommand));
    }
    return isActivated;
  }

  @ActivationCondition(RagAgent.class)
  static boolean activateRagAgent(ClassifierAgent.SubCommand subCommand) {
    var isActivated = subCommand.equals(ClassifierAgent.SubCommand.RAG);
    if (isActivated) {
      Log.info(String.format("Agent to activate: %s%n",  subCommand));
    }
    return isActivated;
  }

  @ActivationCondition(ChatAgent.class)
  static boolean activateJarvisAgent(ClassifierAgent.SubCommand subCommand) {
    var isActivated = subCommand.equals(ClassifierAgent.SubCommand.CHAT);
    if (isActivated) {
      Log.info(String.format("Agent to activate: %s%n",  subCommand));
    }
    return isActivated;
  }

}
