package fr.wilda.picocli.sdk.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import io.smallrye.mutiny.Multi;
import jakarta.enterprise.context.ApplicationScoped;

@RegisterAiService
@ApplicationScoped
public interface AIEndpointService {
  // Add some instructions to my LLM
  @SystemMessage("""
                  Ton nom est Jarvis. Tu es un assistant virtuel.
                  Ton but est d'aider le mieux possible lorsque l'on te pose une question.
                  Si tu ne sais pas répondre, réponds juste "je ne sais pas répondre à cette question".
                  Réponds de manière concise et simple.
                 """)
  @UserMessage("La question posée est la suivante : {question}")
  Multi<String> askAQuestion(String question);
}
