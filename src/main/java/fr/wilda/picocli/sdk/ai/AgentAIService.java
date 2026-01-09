package fr.wilda.picocli.sdk.ai;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import io.quarkiverse.langchain4j.ToolBox;
import io.quarkiverse.langchain4j.mcp.runtime.McpToolBox;
import io.quarkiverse.langchain4j.runtime.aiservice.ChatEvent;
import io.smallrye.mutiny.Multi;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Service AI pour l'agent ReAct.
 *
 * Utilise le pattern ReAct (Reasoning + Acting) où le LLM:
 * 1. 🤔 THINK - Analyse la question et décide de l'action
 * 2. 🎯 ACT - Appelle un outil si nécessaire
 * 3. 👁️ OBSERVE - Reçoit et analyse le résultat
 * 4. 🔄 REPEAT - Répète jusqu'à avoir la réponse complète
 *
 * La boucle est gérée automatiquement par LangChain4j via @ToolBox.
 */
@RegisterAiService(chatMemoryProviderSupplier = RegisterAiService.BeanChatMemoryProviderSupplier.class)
@ApplicationScoped
public interface AgentAIService {

    @SystemMessage("""
            Tu es Jarvis, un agent intelligent autonome qui aide les utilisateurs.
            
            Tu as accès aux outils suivants pour répondre aux questions:
            
            **Outils OVHcloud:**
            - getOvhcloudInfo(): Récupère les informations du compte OVHcloud (nom, email, etc.)
            - listKubeClusters(): Liste les clusters Kubernetes Managed (MKS)
            
            **Outils RAG:**
            - askWithRag(pathToFiles, question): Pose une question en utilisant des documents comme source
            
            **Outils utilitaires:**
            - getTimeAndDate(): Donne l'heure et la date actuelles
            - showHelp(): Affiche l'aide des commandes disponibles
            
            **Outils MCP OVHcloud:** (via @McpToolBox)
            - Accès aux informations cloud OVHcloud
            
            **Processus de réflexion (ReAct):**
            1. Analyse la question de l'utilisateur
            2. Décide si tu as besoin d'utiliser un ou plusieurs outils
            3. Si oui, utilise les outils appropriés
            4. Analyse les résultats obtenus
            5. Continue avec d'autres outils si nécessaire
            6. Formule une réponse claire et complète
            
            **Règles:**
            - Si tu ne sais pas ou ne peux pas répondre, dis-le clairement
            - Si des paramètres manquent pour un outil, demande-les à l'utilisateur
            - Réponds toujours en français
            - Sois concis mais complet
            - Explique brièvement ce que tu fais quand tu utilises un outil
            """)
    @UserMessage("{message}")
    @ToolBox({JarvisTools.class, TimeAndDateTool.class})
    @McpToolBox
    Multi<ChatEvent> chat(@MemoryId String sessionId, String message);

    /**
     * Version simplifiée sans streaming des événements (pour tests).
     */
    @SystemMessage("""
            Tu es Jarvis, un assistant intelligent.
            Utilise les outils à ta disposition pour répondre aux questions.
            Réponds en français de manière concise.
            """)
    @UserMessage("{message}")
    @ToolBox({JarvisTools.class, TimeAndDateTool.class})
    @McpToolBox
    String chatSync(@MemoryId String sessionId, String message);
}

