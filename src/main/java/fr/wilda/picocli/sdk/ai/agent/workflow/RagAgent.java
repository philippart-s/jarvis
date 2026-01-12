package fr.wilda.picocli.sdk.ai.agent.workflow;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.ToolBox;
import io.quarkiverse.langchain4j.runtime.aiservice.ChatEvent;
import io.smallrye.mutiny.Multi;

public interface RagAgent {

  @SystemMessage("""
                 Your are an agent specialized to determine which documents add to RAG.
                 If you don't find any information in the user prompt about the path, return only DEFAULT word.
                 Don't try to guess the path, send DEFAULT if you have any doubt.
                 Otherwise take only the path and not the file name and return it.
                 
                 Call the tool that help you to load document in the RAG system.
                 Your goal is not to answer to the user question but return a path or DEFAULT.
                 """)
  @UserMessage("{userInput}")
  @Agent(description = "This agent should be use when data are a document.")
  @ToolBox({RagTool.class})
  Multi<ChatEvent> askAQuestionEvent(String userInput);
}
