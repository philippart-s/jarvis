package fr.wilda.picocli.sdk;

import fr.wilda.picocli.sdk.ai.AIEndpointService;
import fr.wilda.picocli.sdk.ai.McpToolsException;
import io.quarkiverse.langchain4j.runtime.aiservice.ChatEvent;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import picocli.AutoComplete;
import picocli.CommandLine;

import java.util.Scanner;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "mcp", description = "Use MCP to add knowledge to Jarvis.", mixinStandardHelpOptions = true,
        subcommands = {AutoComplete.GenerateCompletion.class})
public class McpSubCommand implements Callable<Integer> {

    @Inject
    AIEndpointService aiEndpointService;

    @CommandLine.Parameters(paramLabel = "<question>", description = "Question to ask to augmented Jarvis thanks to MCP.")
    String question;

    @Override
    public Integer call() throws Exception {
        try {
            aiEndpointService.askAQuestionEvent(question)
                    .onItem()
                    .invoke(event -> {
                        switch (event) {
                            case ChatEvent.PartialResponseEvent e -> {
                                Log.info(e.getChunk());
                            }
                            case ChatEvent.BeforeToolExecutionEvent e -> {
                                Log.info(String.format("⚠️ Please valid the tool usage: %s ⚠️%n", e.getRequest().name()));
                                Scanner scanner = new Scanner(System.in);
                                if (scanner.next()
                                        .equals("ok")) {
                                    Log.info(String.format("🔧 Using tool: %s", e.getRequest().name()));
                                } else {
                                    throw new McpToolsException();
                                }
                            }
                            default -> {
                            }
                        }
                    })
                    .collect()
                    .asList()
                    .await()
                    .indefinitely();

            Log.info("\n");
        } catch (McpToolsException e) {
            Log.info("⛔️ User did not validate the use of the tool ⛔️!");
        }

        return 0;
    }

}
