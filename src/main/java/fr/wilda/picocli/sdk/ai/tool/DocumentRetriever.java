package fr.wilda.picocli.sdk.ai.tool;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.AugmentationRequest;
import dev.langchain4j.rag.AugmentationResult;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DocumentRetriever implements RetrievalAugmentor {

  private final RetrievalAugmentor augmentor;

  DocumentRetriever(EmbeddingStore<TextSegment> store, EmbeddingModel model) {
    EmbeddingStoreContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
        .embeddingModel(model)
        .embeddingStore(store)
        .maxResults(3)
        .minScore(0.1)
        .build();
    augmentor = DefaultRetrievalAugmentor.builder()
        .contentRetriever(contentRetriever)
        .build();
  }

  @Override
  public AugmentationResult augment(AugmentationRequest augmentationRequest) {
    Log.info(String.format("🔍 Retrieving relevant documents for augmentation request.%n"));
    return augmentor.augment(augmentationRequest);
  }

}
