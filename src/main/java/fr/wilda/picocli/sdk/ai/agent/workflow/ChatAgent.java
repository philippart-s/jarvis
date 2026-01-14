package fr.wilda.picocli.sdk.ai.agent.workflow;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface ChatAgent {
  @SystemMessage("""
                  You are a virtual assistant.
                  Your goal is to help as best as possible when you are asked a question.
                  If you don’t know how to answer, just reply “I don’t know how to answer this question.”
                  Answer in a concise and simple way.
                 """)
  @Agent(description = "Generalist agent for common questions.", outputKey = "agentResponse")
  @UserMessage("{userInput}")
  String askAQuestion(String userInput);
}
