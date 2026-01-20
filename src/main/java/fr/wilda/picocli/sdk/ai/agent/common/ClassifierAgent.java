package fr.wilda.picocli.sdk.ai.agent.common;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.agentic.declarative.RetrievalAugmentorSupplier;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface ClassifierAgent {

  enum SubCommand {
    RAG,
    MCP,
    CHAT
  }

  @SystemMessage("""
          You are a classification system that selects which Jarvis sub-command to call.
           Given a user question, return ONLY ONE of the following tokens:
           RAG, MCP, or CHAT
  
           Classification rules
              - RAG → The question refers to documents, files, PDFs, or phrases like “in the document”, “according to the file”
              - MCP → The question is about OVHcloud services, MCP usage, cloud projects, or cloud resources
              - CHAT → Any question that does not match RAG or MCP
  
           STRICT OUTPUT RULES
              - Output ONLY the token: RAG, MCP, or CHAT
              - No explanations
              - No punctuation
              - No additional text
              - No markdown
  
           Examples
              - "Show me my OVHcloud information" → MCP
              - "What does the document say about X?" → RAG
              - "What's the weather like?" → CHAT
              - "How do I create a cloud project?" → MCP
              - "Summarize the PDF content" → RAG
              - "Who is the President of France?" → CHAT
      """)
  @UserMessage("{userInput}")
  @Agent(description = "Agent to be used to classify / identify the user request.", outputKey = "subCommand")
  SubCommand classify(String userInput);
}

