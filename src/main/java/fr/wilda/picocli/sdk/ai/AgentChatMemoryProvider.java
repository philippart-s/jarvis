package fr.wilda.picocli.sdk.ai;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Provider de mémoire de conversation pour l'agent ReAct.
 * Permet de maintenir le contexte de la conversation entre les échanges.
 * Utilisé avec @MemoryId dans AutonomousAgent.
 */
//@ApplicationScoped
public class AgentChatMemoryProvider implements ChatMemoryProvider {

    /**
     * Cache des mémoires de conversation par session.
     */
    private final Map<Object, ChatMemory> memories = new ConcurrentHashMap<>();

    /**
     * Nombre maximum de messages à conserver dans la mémoire.
     */
    private static final int MAX_MESSAGES = 20;

    @Override
    public ChatMemory get(Object sessionId) {
        return memories.computeIfAbsent(sessionId, id ->
            MessageWindowChatMemory.builder()
                .id(id)
                .maxMessages(MAX_MESSAGES)
                .build()
        );
    }

    /**
     * Nettoie la mémoire d'une session spécifique.
     *
     * @param sessionId L'identifiant de la session à nettoyer
     */
    public void clear(Object sessionId) {
        memories.remove(sessionId);
    }

    /**
     * Nettoie toutes les mémoires de conversation.
     */
    public void clearAll() {
        memories.clear();
    }

    /**
     * Retourne le nombre de sessions actives.
     *
     * @return Le nombre de sessions avec une mémoire active
     */
    public int getActiveSessionsCount() {
        return memories.size();
    }
}

