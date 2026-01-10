package fr.wilda.picocli.sdk.ai.agent.workflow;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Service de classification d'intentions pour l'approche Workflow.
 * Le LLM analyse la question et retourne l'intention correspondante.
 */
@RegisterAiService
@ApplicationScoped
public interface ClassifierAgent {

    /**
     * Énumération des intentions possibles.
     */
    enum SubCommand {
        OVHCLOUD_API,
        RAG,              // Questions nécessitant des documents
        MCP,              // Questions nécessitant outils MCP
        TIME,             // Questions sur l'heure/date
        CHAT              // Conversation générale
    }

    @SystemMessage("""
        Tu es un classificateur qui permet de déterminer quel type de sous commande il faut appeler dans Jarvis. 
        Analyse la question et retourne UNIQUEMENT l'un des mots suivants: OVHCLOUD_API, RAG, MCP, TIME, CHAT
        
        Règles de classification:
        - OVHCLOUD_API: demande explicite d'utiliser l'API pour accéder aux informations de compte OVHcloud ou des clusters Kubernetes
        - RAG: questions mentionnant documents, fichiers, PDF, "dans le document", "selon le fichier"
        - MCP: questions sur services OVHcloud via MCP, projets cloud, resources cloud
        - TIME: heure, date, "quelle heure", aujourd'hui, maintenant
        - CHAT: tout le reste (conversation générale, questions sans rapport avec les autres catégories)
        
        IMPORTANT: Réponds UNIQUEMENT par le mot représentant la sous-commande en majuscules, rien d'autre.
        Pas d'explication, pas de phrase, juste le mot.
        
        Exemples:
        - "Montre-moi mes infos OVHcloud" → MCP
        - "Liste mes clusters kubernetes en utilisant l'API" → OVHCLOUD_API
        - "Que dit le document sur X?" → RAG
        - "Quelle heure est-il?" → TIME
        - "Raconte-moi une blague" → CHAT
        """)
    @UserMessage("{question}")
    @Agent(outputKey = "subCommand")
    SubCommand classify(String question);
}

