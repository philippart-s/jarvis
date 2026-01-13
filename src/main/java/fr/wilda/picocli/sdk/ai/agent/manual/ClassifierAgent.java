package fr.wilda.picocli.sdk.ai.agent.manual;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * Service de classification d'intentions pour l'approche Workflow.
 * Le LLM analyse la question et retourne l'intention correspondante.
 */
public interface ClassifierAgent {

  /**
   * Énumération des intentions possibles.
   */
  enum SubCommand {
    RAG,
    MCP,
    CHAT
  }

  @SystemMessage("""
      Tu es un classificateur qui permet de déterminer quel type de sous commande il faut appeler dans Jarvis. 
      Analyse la question et retourne UNIQUEMENT l'un des mots suivants: RAG ou MCP.
      
      Règles de classification:
      - RAG: questions sur les conférences.
      - MCP: questions sur services OVHcloud via MCP, projets cloud, resources cloud
      - CHAT: questions about common subjects
      
      IMPORTANT: Réponds UNIQUEMENT par le mot représentant la sous-commande en majuscules, rien d'autre.
      Pas d'explication, pas de phrase, juste le mot.
      
      Exemples:
      - "Montre-moi mes infos OVHcloud" → MCP
      - "Que dit le document sur X?" → RAG
      - "quel est le président de la france?" → CHAT
      - "quelle est la couleur du soleil?" -> CHAT
      - "Comment créer un projet cloud?" → MCP
      - "Résume le contenu du fichier PDF" → RAG
      """)
  @UserMessage("{userInput}")
  @Agent(description = "Agent à utiliser pour classifier / identifier la demande utilisateur.")
  SubCommand classify(String userInput);
}

