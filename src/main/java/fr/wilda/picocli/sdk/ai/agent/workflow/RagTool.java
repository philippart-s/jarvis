package fr.wilda.picocli.sdk.ai.agent.workflow;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.service.V;
import fr.wilda.picocli.sdk.ai.DocumentLoader;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.nio.file.Path;

@ApplicationScoped
public class RagTool {

  @Inject
  DocumentLoader loader;

  @Tool("Load document from given path into RAG system")
  void loadDocument(@V("Path to document to load into RAG system") String path) {
    Log.info("📜 Loading RAG document from " + path);

    if ("DEFAULT".equals(path)) {
      loader.loadDocument(null);    }
    else {
      loader.loadDocument(Path.of(path));
    }
  }
}
