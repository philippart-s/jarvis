package fr.wilda.picocli.sdk.ai;

import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.ovhai.OvhAiEmbeddingModel;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.eclipse.microprofile.config.ConfigProvider;

@ApplicationScoped
public class RegisterOVHEmbeddedModel {
   @Produces
   public EmbeddingModel ovhAIEmbeddingModel() {
        return OvhAiEmbeddingModel.builder()
                .baseUrl(ConfigProvider.getConfig().getValue("ovhcloud.ai-endpoints.embedding.base-url", String.class))
                .apiKey(ConfigProvider.getConfig().getValue("ovhcloud.ai-endpoints.token", String.class))
                .logRequests(false)
                .logResponses(false)
                .build();
    }
}
