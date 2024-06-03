package fr.wilda.picocli.sdk.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import io.smallrye.mutiny.Multi;
import jakarta.enterprise.context.ApplicationScoped;

@RegisterAiService
@ApplicationScoped
public interface AIEndpointMistral7bService {

  @SystemMessage("You are Jarvis, an AI assistant.")
  @UserMessage("Answer as best possible to the following question: {question}. The answer must be in a style of a virtual assistant.")
    Multi<String> askAQuestion(String question);
}
