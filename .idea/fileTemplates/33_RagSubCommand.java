package fr.wilda.picocli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(name = "rag", description = "Add document to Jarvis knowledge thanks to RAG.", mixinStandardHelpOptions = true)
public class RagSubCommand implements Callable<Integer> {
    // Logger
    private static final Logger _LOG = LoggerFactory.getLogger(RagSubCommand.class);

    // 34-rag-option

    // 35-question-with-rag

    // 36-inject-services

    @Override
    public Integer call() throws Exception {
        // 37-call-with-rag

        return 0;
    }
}
