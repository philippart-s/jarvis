package fr.wilda.picocli.sdk.ai.tool;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

@ApplicationScoped
public class InMemoryEmbeddingStoreProvider {
  @Produces
  @ApplicationScoped
  public EmbeddingStore<TextSegment> embeddingStore() {
    return new InMemoryEmbeddingStore<>();
  }
}
