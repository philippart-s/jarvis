package fr.wilda.picocli.sdk.ai.tool;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.nio.file.Path;
import java.util.List;

import static dev.langchain4j.data.document.splitter.DocumentSplitters.recursive;

@Singleton
public class DocumentLoader {

  @Inject
  EmbeddingStore<TextSegment> store;

  @Inject
  EmbeddingModel embeddingModel;

  @ConfigProperty(name = "jarvis.rag.resources")
  String defaultRagResourcesPath;

  public void loadDocument(Path ragFiles) {
    // Load RAG files
    List<Document> documents = FileSystemDocumentLoader.loadDocuments((ragFiles != null ? ragFiles : Path.of(defaultRagResourcesPath)));

    // Ingest documents in embedding store
    for (Document document : documents) {
      Log.info(String.format("📜 Load document: %s%n",document.metadata().getString(Document.FILE_NAME)));
      EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor
          .builder()
          .embeddingStore(store)
          .embeddingModel(embeddingModel)
          .documentSplitter(recursive(800, 0))
          .build();
      ingestor.ingest(document);
    }
  }
}
