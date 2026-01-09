package fr.wilda.picocli;

import fr.wilda.jarvis.sdk.ovhcloud.OVHcloudSignatureHelper;
import fr.wilda.picocli.sdk.OVHcloudAPIService;
import fr.wilda.picocli.sdk.ai.AIEndpointService;
import fr.wilda.picocli.sdk.ai.ClassifierAgent;
import fr.wilda.picocli.sdk.ai.DocumentLoader;
import fr.wilda.picocli.sdk.ai.IntentClassifierService;
import fr.wilda.picocli.sdk.ai.IntentClassifierService.Intent;
import fr.wilda.picocli.sdk.ai.TimeAndDateTool;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.AutoComplete.GenerateCompletion;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.nio.file.Path;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * Sous-commande illustrant l'approche Workflow Agentique.
 *
 * Le workflow orchestre explicitement 3 étapes:
 * 1. Classification de l'intention par le LLM
 * 2. Routage vers l'action appropriée
 * 3. Exécution et affichage du résultat
 */
@Command(name = "workflow",
        description = "Mode workflow agentique - Orchestration explicite des étapes (Classification → Routage → Exécution)",
        mixinStandardHelpOptions = true,
        subcommands = {GenerateCompletion.class})
public class WorkflowSubCommand implements Callable<Integer> {

    private static final Logger LOG = LoggerFactory.getLogger(WorkflowSubCommand.class);

    @Inject
    ClassifierAgent classifier;

    @Inject
    AIEndpointService aiService;

    @Inject
    TimeAndDateTool timeTool;

    @Inject
    DocumentLoader documentLoader;

    @RestClient
    OVHcloudAPIService ovhcloudAPI;

    @ConfigProperty(name = "ovhcloud.projectId")
    String projectId;

    @Parameters(paramLabel = "<question>", description = "Question à poser au workflow")
    String question;

    @Option(names = {"-p", "--path"}, description = "Chemin vers documents (requis pour intent RAG)")
    Path ragPath;

    @Option(names = {"-v", "--verbose"}, description = "Affiche les détails de chaque étape")
    boolean verbose;

    private Long ovhTimestamp;

    @Override
    public Integer call() throws Exception {
        LOG.info("\n🔄 Mode Workflow Agentique\n");
        LOG.info("━".repeat(50));

        // ═══════════════════════════════════════════════════════════════
        // ÉTAPE 1: Classification de l'intention
        // ═══════════════════════════════════════════════════════════════
        LOG.info("\n🔍 Étape 1: Classification de l'intention...");
        if (verbose) {
            LOG.info("   Question: \"{}\"", question);
        }

        Intent intent;
        try {
            intent = classifier.classify(question);
            LOG.info("   ✓ Intent détecté: {}", intent);
        } catch (Exception e) {
            LOG.error("   ✗ Erreur de classification: {}", e.getMessage());
            intent = Intent.CHAT; // Fallback
            LOG.info("   → Fallback vers: {}", intent);
        }

        // ═══════════════════════════════════════════════════════════════
        // ÉTAPE 2: Routage et Exécution
        // ═══════════════════════════════════════════════════════════════
        LOG.info("\n⚙️ Étape 2: Routage vers l'action [{}]...", intent);

        String result;
        try {
            result = executeAction(intent);
        } catch (Exception e) {
            LOG.error("   ✗ Erreur d'exécution: {}", e.getMessage());
            result = "Erreur lors de l'exécution: " + e.getMessage();
        }

        // ═══════════════════════════════════════════════════════════════
        // ÉTAPE 3: Affichage du résultat
        // ═══════════════════════════════════════════════════════════════
        LOG.info("\n✅ Étape 3: Résultat\n");
        LOG.info("━".repeat(50));
        LOG.info("\n{}\n", result);

        return 0;
    }

    /**
     * Route vers l'action appropriée selon l'intent détecté.
     */
    private String executeAction(Intent intent) throws Exception {
        return switch (intent) {
            case OVHCLOUD_INFO -> executeOvhcloudInfo();
            case OVHCLOUD_KUBE -> executeOvhcloudKube();
            case RAG -> executeRag();
            case MCP -> executeMcp();
            case TIME -> executeTime();
            case CHAT -> executeChat();
        };
    }

    /**
     * Récupère les informations du compte OVHcloud.
     */
    private String executeOvhcloudInfo() {
        LOG.info("   → Appel OVHcloudAPIService.getMe()");
        ovhTimestamp = System.currentTimeMillis() / 1000;
        var me = ovhcloudAPI.getMe(
                OVHcloudSignatureHelper.signature("me", ovhTimestamp),
                Long.toString(ovhTimestamp)
        );
        return "Informations du compte OVHcloud:\n" + me.toString();
    }

    /**
     * Liste les clusters Kubernetes.
     */
    private String executeOvhcloudKube() {
        LOG.info("   → Appel OVHcloudAPIService.getKubernetes()");
        ovhTimestamp = System.currentTimeMillis() / 1000;
        String[] kubes = ovhcloudAPI.getKubernetes(
                projectId,
                OVHcloudSignatureHelper.signature("cloud/project/" + projectId + "/kube", ovhTimestamp),
                Long.toString(ovhTimestamp)
        );

        StringBuilder result = new StringBuilder();
        result.append(String.format("Nombre de clusters Kubernetes: %d\n", kubes.length));

        for (String kubeId : kubes) {
            var kubeInfo = ovhcloudAPI.getKubernete(
                    projectId,
                    kubeId,
                    OVHcloudSignatureHelper.signature("cloud/project/" + projectId + "/kube/" + kubeId, ovhTimestamp),
                    Long.toString(ovhTimestamp)
            );
            result.append("\n").append(kubeInfo.toString());
        }

        return result.toString();
    }

    /**
     * Exécute une requête RAG avec les documents fournis.
     */
    private String executeRag() {
        if (ragPath == null) {
            return "⚠️ L'intent RAG nécessite un chemin vers les documents.\n" +
                   "Utilisez l'option -p ou --path pour spécifier le chemin.\n" +
                   "Exemple: jarvis workflow -p ./documents \"Que dit le document?\"";
        }

        LOG.info("   → Chargement des documents depuis: {}", ragPath);
        documentLoader.loadDocument(ragPath);

        LOG.info("   → Appel AIEndpointService.askAQuestion()");
        StringBuilder response = new StringBuilder();
        aiService.askAQuestion(question)
                .subscribe()
                .asStream()
                .forEach(response::append);

        return response.toString();
    }

    /**
     * Exécute une requête MCP (redirige vers la sous-commande mcp).
     */
    private String executeMcp() {
        return "⚠️ Pour les requêtes MCP avec validation humaine, utilisez:\n" +
               "jarvis mcp \"" + question + "\"";
    }

    /**
     * Retourne l'heure et la date actuelles.
     */
    private String executeTime() {
        LOG.info("   → Appel TimeAndDateTool.getTimeAndDate()");
        return "Date et heure actuelles: " + timeTool.getTimeAndDate();
    }

    /**
     * Conversation générale avec le LLM.
     */
    private String executeChat() {
        LOG.info("   → Appel AIEndpointService.askAQuestion()");
        StringBuilder response = new StringBuilder();
        aiService.askAQuestion(question)
                .subscribe()
                .asStream()
                .forEach(token -> {
                    try {
                        TimeUnit.MILLISECONDS.sleep(50);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    response.append(token);
                });

        return response.toString();
    }
}

