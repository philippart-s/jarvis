package fr.wilda.picocli.sdk.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import io.quarkiverse.langchain4j.ToolBox;
import io.quarkiverse.langchain4j.mcp.runtime.McpToolBox;
import io.quarkiverse.langchain4j.runtime.aiservice.ChatEvent;
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
                  Si on te pose des questions sur OVHcloud utilise les outils à ta disposition.
                 """)
  @UserMessage("La question posée est la suivante : {question}")
  @ToolBox(TimeAndDateTool.class)
  @McpToolBox
  Multi<String> askAQuestion(String question);

  // Add some instructions to my LLM
  @SystemMessage("""
                  Ton nom est Jarvis. Tu es un assistant virtuel.
                  Ton but est d'aider le mieux possible lorsque l'on te pose une question.
                  Si tu ne sais pas répondre, réponds juste "je ne sais pas répondre à cette question".
                  Réponds de manière concise et simple.
                  Si on te pose des questions sur OVHcloud utilise les outils à ta disposition.
                 """)
  @UserMessage("La question posée est la suivante : {question}")
  @ToolBox(TimeAndDateTool.class)
  @McpToolBox
  Multi<ChatEvent> askAQuestionEvent(String question);
}
