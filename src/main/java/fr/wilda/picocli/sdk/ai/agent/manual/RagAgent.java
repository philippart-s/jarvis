package fr.wilda.picocli.sdk.ai.agent.manual;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import fr.wilda.picocli.sdk.ai.tool.RagTool;
import io.quarkiverse.langchain4j.ToolBox;

public interface RagAgent {

  @SystemMessage("""
                 Your are an agent specialized to determine which documents add to RAG.
                 If you don't find any information in the user prompt about the path, use DEFAULT as tool parameter.
                 Don't try to guess the path, send DEFAULT if you have any doubt.
                 Otherwise take only the path and not the file name and return it.
                 
                 Call the tool that help you to load document in the RAG system.
                 """)
  @UserMessage("{userInput}")
  @Agent(description = "This agent should be use when data are a document.")
  @ToolBox({RagTool.class})
  String askAQuestionEvent(String userInput);
}
