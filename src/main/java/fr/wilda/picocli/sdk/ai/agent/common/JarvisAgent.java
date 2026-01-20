package fr.wilda.picocli.sdk.ai.agent.common;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.agentic.declarative.RetrievalAugmentorSupplier;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Multi;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Inject;

public interface JarvisAgent {
  @SystemMessage("""
                  You are a virtual assistant.
                  Your goal is to help as best as possible when you are asked a question.
                  If you don’t know how to answer, just reply “I don’t know how to answer this question.”
                  Answer in a concise and simple way.
                  
                  If you need more information, you can use information from agents: {agentResponse}
                 """)
  @Agent(description = "Chatbot agent that uses data from other agent to have accuracy response.", outputKey = "WFresponse")
  @UserMessage("{userInput}")
  Multi<String> askAQuestion(String userInput, String agentResponse);

  //@RetrievalAugmentorSupplier
  static RetrievalAugmentor ragSupplier() {
    Log.info("⚙️ Setting up Retrieval Augmentor for JarvisAgent...\\n");
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


