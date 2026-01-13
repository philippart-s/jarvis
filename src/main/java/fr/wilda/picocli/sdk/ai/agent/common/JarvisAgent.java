package fr.wilda.picocli.sdk.ai.agent.common;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.smallrye.mutiny.Multi;

public interface JarvisAgent {
  @SystemMessage("""
                  You are a virtual assistant.
                  Your goal is to help as best as possible when you are asked a question.
                  If you don’t know how to answer, just reply “I don’t know how to answer this question.”
                  Answer in a concise and simple way.
                  
                  If you need more information, you can use information from agents: {agentResponse}
                 """)
  @Agent(description = "Agent à utiliser lorsque la demande générale.", outputKey = "WFresponse")
  @UserMessage("{userInput}")
  Multi<String> askAQuestion(String userInput, String agentResponse);
}


