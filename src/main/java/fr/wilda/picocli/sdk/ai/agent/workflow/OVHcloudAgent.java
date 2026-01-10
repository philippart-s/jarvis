package fr.wilda.picocli.sdk.ai.agent.workflow;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import fr.wilda.picocli.sdk.ai.TimeAndDateTool;
import io.quarkiverse.langchain4j.RegisterAiService;
import io.quarkiverse.langchain4j.ToolBox;
import io.quarkiverse.langchain4j.mcp.runtime.McpToolBox;
import io.quarkiverse.langchain4j.runtime.aiservice.ChatEvent;
import io.smallrye.mutiny.Multi;
import jakarta.enterprise.context.ApplicationScoped;

@RegisterAiService
@ApplicationScoped
public interface OVHcloudAgent {

  @SystemMessage("""
                 Tu es un agent spécialiste de l'écosystème OVHcloud. 
                 Pour accéder aux informations, utilise les outils à ta disposition.
                 Si tu ne sais pas répondre à la question, indique que tu ne sais pas.
                 """)
  @UserMessage("La question posée est la suivante : {question}")
  @ToolBox(TimeAndDateTool.class)
  @McpToolBox
  @Agent
  Multi<ChatEvent> askAQuestionEvent(String question);
}
