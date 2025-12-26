package fr.wilda.picocli;

import fr.wilda.picocli.sdk.ai.AIEndpointService;
import fr.wilda.picocli.sdk.ai.DocumentLoader;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.AutoComplete.GenerateCompletion;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.nio.file.Path;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@Command(name = "rag", description = "Add document to Jarvis knowledge thanks to RAG.", mixinStandardHelpOptions = true, subcommands = {GenerateCompletion.class})
public class RagSubCommand implements Callable<Integer> {
    // Logger
    private static final Logger _LOG = LoggerFactory.getLogger(RagSubCommand.class);

    @Option(names = {"-p", "--path-to-files"}, paramLabel = "<Path to files to add to Jarvis knowledge>",
            description = "Path to files to use with RAG.")
    private Path pathToFile;

    @CommandLine.Parameters(paramLabel = "<question>", description = "Question to ask, Jarvis use the documents provided to ask.e")
    String question;

    @Inject
    AIEndpointService aiEndpointService;

    @Inject
    DocumentLoader documentLoader;

    @Override
    public Integer call() throws Exception {
        if (question != null && !question.isEmpty()) {
            documentLoader.loadDocument(pathToFile);

            _LOG.info("\n🤖:\n");
            aiEndpointService.askAQuestion(question)
                    .subscribe()
                    .asStream()
                    .forEach(token -> {
                        try {
                            TimeUnit.MILLISECONDS.sleep(150);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        _LOG.info(token);
                    });
            _LOG.info("\n");
        } else {
            _LOG.info("⁉️ Ask a question to use RAG feature ⁉️.");
        }

        return 0;
    }
}
