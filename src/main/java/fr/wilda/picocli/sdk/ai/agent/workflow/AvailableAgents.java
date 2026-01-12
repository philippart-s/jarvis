package fr.wilda.picocli.sdk.ai.agent.workflow;

import dev.langchain4j.agentic.declarative.ActivationCondition;
import dev.langchain4j.agentic.declarative.ConditionalAgent;
import io.quarkiverse.langchain4j.runtime.aiservice.ChatEvent;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Multi;

public interface AvailableAgents {

  @ConditionalAgent(description = "Agent à utiliser pour savoir de quel type d'agent on a besoin : CHAT, MCP ou RAG.",
      subAgents = {
          OVHcloudAgent.class,
          RagAgent.class,
          JarvisAgent.class,
      },
      outputKey = "WFresponse"
  )
  Multi<ChatEvent> executeAgent(String userInput, ClassifierAgent.SubCommand subCommand);

  @ActivationCondition(OVHcloudAgent.class)
  static boolean activateOVHcloudAgent(ClassifierAgent.SubCommand subCommand) {
    Log.info("Activation of OVHcloudAgent: " + subCommand);
    Log.info(subCommand.equals(ClassifierAgent.SubCommand.MCP));
    return subCommand.equals(ClassifierAgent.SubCommand.MCP);
  }

  @ActivationCondition(RagAgent.class)
  static boolean activateRagAgent(ClassifierAgent.SubCommand subCommand) {
    Log.info("Activation of RagAgent : " + subCommand);
    Log.info(subCommand.equals(ClassifierAgent.SubCommand.RAG));
    return subCommand.equals(ClassifierAgent.SubCommand.RAG);
  }

  @ActivationCondition(JarvisAgent.class)
  static boolean activateJarvisAgent(ClassifierAgent.SubCommand subCommand) {
    Log.info("Activation of Jarvis : " + subCommand);
    Log.info(subCommand.equals(ClassifierAgent.SubCommand.CHAT));
    return subCommand.equals(ClassifierAgent.SubCommand.CHAT);
  }

}
