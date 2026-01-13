package fr.wilda.picocli;

import fr.wilda.picocli.sdk.ai.agent.manual.ClassifierAgent;
import fr.wilda.picocli.sdk.ai.agent.manual.OVHcloudAgent;
import fr.wilda.picocli.sdk.ai.agent.manual.RagAgent;
import fr.wilda.picocli.sdk.ai.agent.manual.JarvisAgent;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.inject.Inject;
import picocli.AutoComplete.GenerateCompletion;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@ActivateRequestContext
@Command(name = "manual-workflow",
    description = "Mode workflow agentique - Orchestration explicite des étapes (Classification → Routage → Exécution)",
    mixinStandardHelpOptions = true,
    subcommands = {GenerateCompletion.class})
public class ManualWorkflowSubCommand implements Callable<Integer> {
  @Parameters(paramLabel = "<question>", description = "Question à poser à Jarvis mode workflow agentique")
  String question;


  @Inject
  ClassifierAgent classifierAgent;

  @Inject
  RagAgent ragAgent;

  @Inject
  JarvisAgent jarvisAgent;

  @Inject
  OVHcloudAgent ovhcloudAgent;

  @Override
  public Integer call() throws Exception {
    // manual-workflow "pourquoi le ciel est bleu?"
    // manual-workflow "donne moi le détail de mon compte ovhcloud"
    // manual-workflow "en te basant sur les documents en ta procession donne moi le programme du Mars JUG de janvier 2026"

    String agentResponse = "";

    Log.info("━".repeat(50));
    Log.info("🔄 Mode Workflow Manuel");
    Log.info("━".repeat(50));

    Log.info("🔀 Choose the right agent given the user prompt.");

    var agentToCall = classifierAgent.classify(question);
    switch (agentToCall) {
      case MCP -> {
        Log.info("☁️ MCP Agent selected ☁️");
        agentResponse = ovhcloudAgent.askAQuestion(question);
      }
      case RAG -> {
        Log.info("📜 RAG Agent selected 📜");
        ragAgent.askAQuestionEvent(question);

      }
      case CHAT -> Log.info("Chat Agent selected");
      default -> {

      }
    }
      Log.info("🤖 Call Jarvis Agent after agents 🤖");
      jarvisAgent.askAQuestion(question, agentResponse)
          .subscribe()
        .asStream()
        .forEach(token -> {
          try {
            TimeUnit.MILLISECONDS.sleep(150);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          Log.info(token);
        });

    return 0;
  }
}

