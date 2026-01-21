package fr.wilda.picocli.sdk.ai.agent.common;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.agentic.declarative.RetrievalAugmentorSupplier;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import fr.wilda.picocli.sdk.ai.tool.RagTool;
import io.quarkiverse.langchain4j.ToolBox;
import io.quarkus.logging.Log;
import jakarta.enterprise.inject.spi.CDI;

public interface RagAgent {

  @SystemMessage("""
                 Your are an agent specialized to determine which documents add to RAG.
                 If you don't find any information in the user prompt about the path, use DEFAULT as tool parameter.
                 Don't try to guess the path, send DEFAULT if you have any doubt.
                 Otherwise take only the path and not the file name and return it.
                 
                 Call the tool that help you to load document in the RAG system.
                 """)
  @UserMessage("{userInput}")
  @Agent(description = "This agent should be use when prompt is about to get some information thanks to documents.", outputKey = "agentResponse")
  @ToolBox({RagTool.class})
  String askAQuestionEvent(String userInput);

  //@RetrievalAugmentorSupplier
  static RetrievalAugmentor ragSupplier() {
    Log.info("⚙️ Setting up Retrieval Augmentor for RAGAgent...\n");
    EmbeddingStoreContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
        .embeddingModel(CDI.current().select(EmbeddingModel.class).get())
        .embeddingStore(new InMemoryEmbeddingStore<>())
        .maxResults(3)
        .minScore(0.1)
        .build();
    return DefaultRetrievalAugmentor.builder()
        .contentRetriever(contentRetriever)
        .build();
  }
}
