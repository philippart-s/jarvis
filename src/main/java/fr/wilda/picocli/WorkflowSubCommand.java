package fr.wilda.picocli;

import fr.wilda.picocli.sdk.ai.agent.workflow.JarvisWorkflow;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import picocli.AutoComplete.GenerateCompletion;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

@Command(name = "workflow",
    description = "Mode workflow agentique - Orchestration explicite des étapes (Classification → Routage → Exécution)",
    mixinStandardHelpOptions = true,
    subcommands = {GenerateCompletion.class})
public class WorkflowSubCommand implements Callable<Integer> {
  @Parameters(paramLabel = "<question>", description = "Question à poser à Jarvis mode workflow agentique")
  String question;

  @Inject
  JarvisWorkflow jarvisWorkflow;

  @Override
  public Integer call() throws Exception {
    Log.info("\n🔄 Mode Workflow Agentique\n");
    Log.info("━".repeat(50));
    Log.info("━".repeat(50) + "\n");

    Log.info("🤖: " + jarvisWorkflow.executeJarvisWorkflow(question));

    return 0;
  }
}

