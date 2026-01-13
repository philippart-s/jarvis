package fr.wilda.picocli.sdk.ai.agent.manual;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.runtime.aiservice.ChatEvent;
import io.smallrye.mutiny.Multi;

public interface JarvisAgent {
  @SystemMessage("""
                  You are a virtual assistant.
                  Your goal is to help as best as possible when you are asked a question.
                  If you don’t know how to answer, just reply “I don’t know how to answer this question.”
                  Answer in a concise and simple way.
                  
                  You can use date from agent : {agentResponse}
                 """)
  @Agent(description = "Agent à utiliser lorsque la demande générale.")
  @UserMessage("{userInput}")
  Multi<ChatEvent> askAQuestion(String userInput, String agentResponse);
}
