package fr.wilda.picocli;

import fr.wilda.jarvis.sdk.ovhcloud.EmotionEvaluation;
import fr.wilda.picocli.sdk.ai.AISentimentService;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.AutoComplete.GenerateCompletion;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.nio.file.Files;
import java.util.SortedMap;
import java.util.concurrent.Callable;

@Command(name = "sentiment", mixinStandardHelpOptions = true, subcommands = {GenerateCompletion.class})
public class SentimentSubCommand implements Callable<Integer> {
    // Logger
    private static final Logger _LOG = LoggerFactory.getLogger(SentimentSubCommand.class);

    @Option(names = {"-s", "--sentiment"}, paramLabel = "<SENTIMENT>",
            description = "Analyze a sentiment with the OVHcloud Text to Sentiment API.")
    private String sentimentToAnalyze;

    @Option(names = {"-f", "--file"}, description = "File to analyse (sentiment analysis)")
    private File fileToAnalyse;

    @RestClient
    AISentimentService aiSentimentService;

    @Override
    public Integer call() throws Exception {
        if (sentimentToAnalyze != null) {
            _LOG.debug("param {}", sentimentToAnalyze);
            SortedMap<String, Double> res =
                    EmotionEvaluation.toSortedMap(aiSentimentService.text2emotions(sentimentToAnalyze));

            _LOG.debug("First: {}", res.firstEntry());
            _LOG.info("Sentiment: {}", EmotionEvaluation.toEmoji(res.firstEntry().getKey()));
        }

        if (fileToAnalyse != null) {
            SortedMap<String, Double> res =
                    EmotionEvaluation.toSortedMap(
                            aiSentimentService.text2emotions(Files.readString(fileToAnalyse.toPath())));

            _LOG.debug("First: {}", res.firstEntry());
            _LOG.info("Sentiment: {}", EmotionEvaluation.toEmoji(res.firstEntry().getKey()));
        }

        return 0;
    }
}
