package fr.wilda.picocli.sdk.ai.agent.common;

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
      Analyse la question et retourne UNIQUEMENT l'un des mots suivants: RAG, MCP ou CHAT
      
      Règles de classification:
      - RAG: questions mentionnant documents, fichiers, PDF, "dans le document", "selon le fichier"
      - MCP: questions sur services OVHcloud via MCP, projets cloud, resources cloud
      - CHAT: toutes les autres questions ne relevant pas des deux catégories précédentes.
      
      IMPORTANT: Réponds UNIQUEMENT par le mot représentant la sous-commande en majuscules, rien d'autre.
      Pas d'explication, pas de phrase, juste le mot.
      
      Exemples:
      - "Montre-moi mes infos OVHcloud" → MCP
      - "Que dit le document sur X?" → RAG
      - "Quel temps fait-il?" → CHAT
      - "Comment créer un projet cloud?" → MCP
      - "Résume le contenu du fichier PDF" → RAG
      - "Qui est le président de la France?" → CHAT
      """)
  @UserMessage("{userInput}")
  @Agent(description = "Agent à utiliser pour classifier / identifier la demande utilisateur.",outputKey = "subCommand")
  SubCommand classify(String userInput);
}

