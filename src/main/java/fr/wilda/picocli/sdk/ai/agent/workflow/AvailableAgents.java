package fr.wilda.picocli.sdk.ai.agent.workflow;

import dev.langchain4j.agentic.declarative.ActivationCondition;
import dev.langchain4j.agentic.declarative.ConditionalAgent;
import io.quarkus.logging.Log;

public interface AvailableAgents {

  @ConditionalAgent(description = "Agent à utiliser pour savoir de quel type d'agent on a besoin : CHAT, MCP ou RAG.",
      subAgents = {
          OVHcloudAgent.class,
          RagAgent.class,
          JarvisAgent.class
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

  @ActivationCondition(JarvisAgent.class)
  static boolean activateJarvisAgent(ClassifierAgent.SubCommand subCommand) {
    var isActivated = subCommand.equals(ClassifierAgent.SubCommand.CHAT);
    if (isActivated) {
      Log.info(String.format("Agent to activate: %s%n",  subCommand));
    }
    return isActivated;
  }

}
