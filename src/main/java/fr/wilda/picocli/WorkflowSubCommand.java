package fr.wilda.picocli;

import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import picocli.AutoComplete.GenerateCompletion;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

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
    @Parameters(paramLabel = "<question>", description = "Question à poser à Jarvis mode workflow agentique")
    String question;

    @Inject


    @Override
    public Integer call() throws Exception {
        Log.info("\n🔄 Mode Workflow Agentique\n");
        Log.info("━".repeat(50));

        return 0;
    }
}

