package fr.wilda.picocli.sdk.ai;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import io.quarkus.logging.Log;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.nio.file.Path;
import java.util.List;

import static dev.langchain4j.data.document.splitter.DocumentSplitters.recursive;

@Singleton
//@Startup
public class DocumentLoader {

  @Inject
  EmbeddingStore<TextSegment> store;

  @Inject
  EmbeddingModel embeddingModel;

  @ConfigProperty(name = "jarvis.rag.resources")
  String defautRagResourcesPath;

  //@PostConstruct
  public void loadDocument(Path ragFiles) {
    // Load RAG files
    List<Document> documents = FileSystemDocumentLoader.loadDocuments((ragFiles != null ? ragFiles : Path.of(defautRagResourcesPath)));

    for (Document document : documents) {
      Log.info("📜 Load document: " + document.metadata().getString(Document.FILE_NAME));
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
