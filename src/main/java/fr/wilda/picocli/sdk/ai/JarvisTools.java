package fr.wilda.picocli.sdk.ai;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import fr.wilda.jarvis.sdk.ovhcloud.OVHcloudSignatureHelper;
import fr.wilda.picocli.sdk.OVHcloudAPIService;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.nio.file.Path;

/**
 * Outils disponibles pour l'agent ReAct.
 * Chaque méthode @Tool encapsule une fonctionnalité existante de l'application.
 * Le LLM peut décider autonomement d'appeler ces outils.
 */
@ApplicationScoped
public class JarvisTools {

    @RestClient
    OVHcloudAPIService ovhcloudAPI;

    @ConfigProperty(name = "ovhcloud.projectId")
    String projectId;

    @Inject
    DocumentLoader documentLoader;

    @Inject
    AIEndpointService aiService;

    // ═══════════════════════════════════════════════════════════════
    // OUTILS OVHCLOUD
    // ═══════════════════════════════════════════════════════════════

    @Tool("Récupère les informations du compte OVHcloud de l'utilisateur (nom, prénom, email, identifiant)")
    public String getOvhcloudInfo() {
        Log.info("🔧 Tool appelé: getOvhcloudInfo()");
        try {
            Long timestamp = System.currentTimeMillis() / 1000;
            var me = ovhcloudAPI.getMe(
                    OVHcloudSignatureHelper.signature("me", timestamp),
                    Long.toString(timestamp)
            );
            return "Informations du compte OVHcloud:\n" + me.toString();
        } catch (Exception e) {
            return "Erreur lors de la récupération des informations: " + e.getMessage();
        }
    }

    @Tool("Liste tous les clusters Kubernetes Managed (MKS) du projet OVHcloud avec leurs détails")
    public String listKubeClusters() {
        Log.info("🔧 Tool appelé: listKubeClusters()");
        try {
            Long timestamp = System.currentTimeMillis() / 1000;
            String[] kubes = ovhcloudAPI.getKubernetes(
                    projectId,
                    OVHcloudSignatureHelper.signature("cloud/project/" + projectId + "/kube", timestamp),
                    Long.toString(timestamp)
            );

            if (kubes.length == 0) {
                return "Aucun cluster Kubernetes trouvé dans le projet.";
            }

            StringBuilder result = new StringBuilder();
            result.append(String.format("Nombre de clusters Kubernetes: %d\n\n", kubes.length));

            for (String kubeId : kubes) {
                var kubeInfo = ovhcloudAPI.getKubernete(
                        projectId,
                        kubeId,
                        OVHcloudSignatureHelper.signature("cloud/project/" + projectId + "/kube/" + kubeId, timestamp),
                        Long.toString(timestamp)
                );
                result.append(kubeInfo.toString()).append("\n\n");
            }

            return result.toString();
        } catch (Exception e) {
            return "Erreur lors de la récupération des clusters: " + e.getMessage();
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // OUTIL RAG
    // ═══════════════════════════════════════════════════════════════

    @Tool("Pose une question en utilisant des documents comme source de connaissance (RAG). Nécessite le chemin vers les fichiers et la question.")
    public String askWithRag(
            @P("Le chemin vers le dossier contenant les documents à utiliser") String pathToFiles,
            @P("La question à poser en utilisant les documents comme contexte") String question) {
        Log.infof("🔧 Tool appelé: askWithRag(path=%s, question=%s)", pathToFiles, question);
        try {
            documentLoader.loadDocument(Path.of(pathToFiles));

            StringBuilder response = new StringBuilder();
            aiService.askAQuestion(question)
                    .subscribe()
                    .asStream()
                    .forEach(response::append);

            return response.toString();
        } catch (Exception e) {
            return "Erreur lors de l'utilisation du RAG: " + e.getMessage();
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // OUTIL AIDE
    // ═══════════════════════════════════════════════════════════════

    @Tool("Affiche l'aide sur les commandes et fonctionnalités disponibles dans Jarvis CLI")
    public String showHelp() {
        Log.info("🔧 Tool appelé: showHelp()");
        return """
                Commandes disponibles dans Jarvis CLI:
                
                1. **jarvis [question]** - Chat simple avec Jarvis
                   Exemple: jarvis "Raconte-moi une blague"
                
                2. **jarvis ovhcloud** - Informations OVHcloud
                   - `-m, --me` : Affiche les informations du compte
                   - `-k, --kube` : Liste les clusters Kubernetes
                
                3. **jarvis rag -p <chemin> <question>** - Questions avec documents (RAG)
                   - `-p, --path-to-files` : Chemin vers les documents
                   Exemple: jarvis rag -p ./docs "Résume le document"
                
                4. **jarvis mcp <question>** - Utilise les outils MCP OVHcloud
                   Exemple: jarvis mcp "Liste mes projets cloud"
                
                5. **jarvis workflow <question>** - Mode workflow agentique
                   - Classification → Routage → Exécution
                   - `-v` : Mode verbose
                
                6. **jarvis agent <question>** - Mode agent ReAct
                   - Boucle autonome Think → Act → Observe
                   - `-v` : Affiche la boucle ReAct
                   - `-i` : Mode interactif
                """;
    }
}

