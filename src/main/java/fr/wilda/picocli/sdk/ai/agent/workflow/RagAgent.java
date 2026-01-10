package fr.wilda.picocli.sdk.ai.agent.workflow;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import io.quarkiverse.langchain4j.ToolBox;
import io.smallrye.mutiny.Multi;

@RegisterAiService
public interface RagAgent {

  @SystemMessage("""
                 Tu es un agent spécialisé dans la réponse aux questions des utilisateurs en utilisant 
                 principalement les informations contenues dans les documents chargés.
                 Pour accéder aux documents, utilise l'outil mis à disposition pour charger les documents.
                 """)
  @UserMessage("La question posée est la suivante : {question}")
  @Agent
  @ToolBox({RagTool.class})
  Multi<String> askAQuestionEvent(String question);
}
