package fr.wilda.picocli;

import fr.wilda.picocli.sdk.ai.AgentAIService;
import fr.wilda.picocli.sdk.ai.AgentChatMemoryProvider;
import io.quarkiverse.langchain4j.runtime.aiservice.ChatEvent;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import picocli.AutoComplete.GenerateCompletion;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.Callable;

/**
 * Sous-commande illustrant l'approche Agent ReAct.
 *
 * L'agent utilise une boucle autonome:
 * 1. 🤔 THINK - Le LLM analyse et décide
 * 2. 🎯 ACT - Exécution de l'outil choisi
 * 3. 👁️ OBSERVE - Analyse du résultat
 * 4. 🔄 REPEAT - Jusqu'à la réponse finale
 *
 * La boucle est gérée automatiquement par LangChain4j.
 * Les événements ChatEvent permettent de visualiser chaque étape.
 */
@Command(name = "agent",
        description = "Mode agent ReAct - L'agent décide autonomement des actions (Think → Act → Observe → Repeat)",
        mixinStandardHelpOptions = true,
        subcommands = {GenerateCompletion.class})
public class AgentSubCommand implements Callable<Integer> {

    @Inject
    AgentAIService agentService;

    @Inject
    AgentChatMemoryProvider chatMemoryProvider;

    @Parameters(paramLabel = "<question>",
                description = "Question à poser à l'agent",
                defaultValue = "")
    String question;

    @Option(names = {"-v", "--verbose"},
            description = "Affiche le détail de la boucle ReAct (Think/Act/Observe)")
    boolean verbose;

    @Option(names = {"-i", "--interactive"},
            description = "Mode interactif avec conversation continue")
    boolean interactive;

    private static final String PROMPT = "🧑 Vous: ";
    private static final String EXIT_COMMANDS = "exit|quit|bye|au revoir|quitter|sortir";

    @Override
    public Integer call() throws Exception {
        String sessionId = UUID.randomUUID().toString();

        Log.info("\n🤖 Mode Agent ReAct\n");
        Log.info("━".repeat(50));

        if (verbose) {
            Log.info("Mode verbose activé - Affichage de la boucle ReAct\n");
        }

        if (interactive) {
            runInteractiveMode(sessionId);
        } else if (question != null && !question.isEmpty()) {
            processMessage(sessionId, question);
        } else {
            // Si pas de question, passer en mode interactif
            runInteractiveMode(sessionId);
        }

        // Nettoyer la mémoire à la fin
        chatMemoryProvider.clear(sessionId);

        return 0;
    }

    /**
     * Mode interactif avec boucle de conversation.
     */
    private void runInteractiveMode(String sessionId) {
        Log.info("Mode interactif activé. Tapez 'exit' pour quitter.\n");
        Log.info("L'agent peut utiliser les outils suivants:");
        Log.info("  • OVHcloud: infos compte, clusters Kubernetes");
        Log.info("  • RAG: questions avec documents");
        Log.info("  • MCP: outils OVHcloud cloud");
        Log.info("  • Utilitaires: heure, aide\n");
        Log.info("━".repeat(50) + "\n");

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print(PROMPT);
            System.out.flush();

            if (!scanner.hasNextLine()) {
                break;
            }

            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                continue;
            }

            if (input.toLowerCase().matches(EXIT_COMMANDS)) {
                Log.info("\n🤖 Jarvis: Au revoir! À bientôt!\n");
                break;
            }

            processMessage(sessionId, input);
            Log.info("\n");
        }
    }

    /**
     * Traite un message et affiche la réponse.
     * Utilise ChatEvent pour afficher la boucle ReAct en mode verbose.
     */
    private void processMessage(String sessionId, String message) {
        Log.info("\n🤖 Jarvis:\n");

        int[] toolCallCount = {0}; // Compteur d'appels d'outils

        try {
            agentService.chat(sessionId, message)
                    .onItem()
                    .invoke(event -> handleChatEvent(event, toolCallCount))
                    .collect()
                    .asList()
                    .await()
                    .indefinitely();

            if (verbose && toolCallCount[0] > 0) {
                Log.infof("\n\n📊 Résumé: %d outil(s) utilisé(s)", toolCallCount[0]);
            }

        } catch (Exception e) {
            Log.errorf("Erreur lors du traitement: %s", e.getMessage());
            if (verbose) {
                e.printStackTrace();
            }
        }

        Log.info("\n");
    }

    /**
     * Gère les différents types d'événements de la conversation.
     * Permet de visualiser la boucle ReAct.
     */
    private void handleChatEvent(ChatEvent event, int[] toolCallCount) {
        switch (event) {
            case ChatEvent.PartialResponseEvent e -> {
                // Réponse en streaming
                System.out.print(e.getChunk());
            }

            case ChatEvent.BeforeToolExecutionEvent e -> {
                toolCallCount[0]++;
                if (verbose) {
                    Log.info("\n");
                    Log.info("┌─────────────────────────────────────────");
                    Log.info("│ 🤔 THINK: L'agent décide d'utiliser un outil");
                    Log.infof("│ 🎯 ACT: Appel de l'outil '%s'", e.getRequest().name());
                    if (e.getRequest().arguments() != null && !e.getRequest().arguments().isEmpty()) {
                        Log.infof("│    Arguments: %s", e.getRequest().arguments());
                    }
                    Log.info("│ ⏳ Exécution en cours...");
                }
            }

            case ChatEvent.ToolExecutedEvent e -> {
                if (verbose) {
                    Log.info("│ 👁️ OBSERVE: Résultat reçu");
                    Log.info("└─────────────────────────────────────────\n");
                }
            }

            default -> {
                // Autres événements ignorés
            }
        }
    }
}

