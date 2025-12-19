# Generate Snippets

```bash
snippets generate \
  --input ./.vscode/snippets.yml \
  --output ./.vscode/devoxx-snippet.code-snippets
```

# Do the demo

  - run [01-create-cli.sh](./_init_/01-create-cli.sh)
  - run [02-check-dependencies.sh](./02-check-dependencies.sh)
  - run [03-quarkus-dev-mode.sh](./03-quarkus-dev-mode.sh)
  - play with the CLI : `--help`, `"xxx!!"`
  - add to [application.properties](./src/main/resources/application.properties) (👨‍💻 _04-props-ovh-env_ && _05-props-rest-client_)
  - create [OVHcloudAPIService.java](./src/main/java/fr/wilda/picocli/sdk/OVHcloudAPIService.java) (👨‍💻 _06-OVHcloudAPIService-annot_ && _07-OVHcloudAPIService-endpoints_)
  - create [JarvisCommand.java](./src/main/java/fr/wilda/picocli/JarvisCommand.java) (👨‍💻 _08-jarvis-cli-class-annot_ && _09-jarvis-cli-logger_ && _10-jarvis-cli-name-param_ && _11-jarvis-hello_)
  - add `@TopCommand` to [JarvisCommand.java](./src/main/java/fr/wilda/picocli/JarvisCommand.java)
  - delete [GreetingCommand.java](./src/main/java/fr/wilda/picocli/GreetingCommand.java)
  - create [OVHcloudSubCommand.java](./src/main/java/fr/wilda/picocli/OVHcloudSubCommand.java) (👨‍💻 _12-ovh-cli-class-annot_ && _13-ovh-cli-logger_ && _14-ovh-cli-rest-client_ && _15-ovh-cli-ovh-stuff_ && _16-ovh-cli-options_ && _17-ovh-cli-me_ && _18-ovh-cli-kube_) 
  - add `subcommands = {OVHcloudSubCommand.class}` to [JarvisCommand.java](./src/main/java/fr/wilda/picocli/JarvisCommand.java)
  - set log in [application.properties](./src/main/resources/application.properties): (👨‍💻 _19-props-logs-prod_)
  - run [20-quarkus-build.sh](./20-quarkus-build.sh)
  - run [21-java-run.sh](./21-java-run.sh)
  - create [jarvis.sh](./src/main/script/jarvis.sh) (👨‍💻 _22-jarvis-bash_)
  - `chmod +x jarvis.sh`
  - run [23-run-script.sh](./23-run-script.sh)
  - run [24-quarkus-build-native.sh](./24-quarkus-build-native.sh)
  - run [25-jarvis-api-run.sh](./25-jarvis-api-run.sh)
  - run [26-add-langchain-4j.sh](./26-add-langchain-4j.sh)
  - update `application.properties`: (👨‍💻 _27-props-langchain4J_)
  - create interface [AIEndpointService.java](./src/main/java/fr/wilda/picocli/sdk/ai/AIEndpointService.java) and add annotations (👨‍💻 _28-AIEndpointService-annotation_)
  - add method `askQuestion` (👨‍💻 _29-OVHcloudMistral-ask-method_)
  - update [JarvisCommand](./src/main/java/fr/wilda/picocli/JarvisCommand.java):
    - `name` to `question` parameter (👨‍💻 _30-jarvis-cli-question-param_)
    - inject `AIEndpointService` (👨‍💻 _31-jarvis-cli-ai-svc_)
    - add the AI model call (👨‍💻 _32-jarvis-cli-ai-svc-call_)
  - run [24-quarkus-build-native.sh](./24-quarkus-build-native.sh)
  - run [33-jarvis-ai-run.sh](./33-jarvis-ai-run.sh)
  - add the token for request (👨‍💻 _34-token-sentiment-service_)
  - create the service [AISentimentService.java](./src/main/java/fr/wilda/picocli/sdk/ai/AISentimentService.java) (👨‍💻 _35-sentiment-service_ && 👨‍💻 _36-text2emotion-method_)
  - create sub command [SentimentSubCommand.java](./src/main/java/fr/wilda/picocli/SentimentSubCommand.java) (👨‍💻 _37-sentiment-sentiment-client_ && 👨‍💻 _38-sentiment-option_ && 👨‍💻 _39-sentiment-output_)
  - add the sub command to [JarvisCommand.java](./src/main/java/fr/wilda/picocli/JarvisCommand.java)
    - test the emotion analysis: `sentiment -s "I'm happy to be here"` or run [40-emotion-prompt.sh](./40-emotion-prompt.sh`)
  - run [24-quarkus-build-native.sh](./24-quarkus-build-native.sh)
  - add the file option in [SentimentSubCommand.java](./src/main/java/fr/wilda/picocli/SentimentSubCommand.java) (👨‍💻 _41-file-option_)
  - add file analysis output in [SentimentSubCommand.java](./src/main/java/fr/wilda/picocli/SentimentSubCommand.java) (👨‍💻 _42-file-ouput_)
    - test the file analysis: `sentiment -f "./src/main/resources/sad-java-poem.txt"` or run [43-emotion-file.sh](./43-emotion-file.sh)
  - add `GenerateCompletion` subcommand on [JarvisCommand.java](./src/main/java/fr/wilda/picocli/JarvisCommand.java), [OVHcloudSubCommand.java](./src/main/java/fr/wilda/picocli/OVHcloudSubCommand.java) and [SentimentSubCommand.java](./src/main/java/fr/wilda/picocli/SentimentSubCommand.java)
  - run [24-quarkus-build-native.sh](./24-quarkus-build-native.sh)
  - source the `.env` file, `source .env`
  - display help on autocompletion: `jarvis generate-completion --help`
  - activate the autocompletion: `source <(jarvis generate-completion)`
  - test the autocompletion
  - test the conference question (👨‍💻 `44-without-rag-prompt.sh`)
  - add EasyRAG (👨‍💻 `45-add-openai-for-rag`) 
  - add RAG configurations in [application.properties](./src/main/resources/application.properties) (👨‍💻 `47-rag-properties`)
  - create [RegisterOVHEmbeddedModel](./src/main/java/fr/wilda/picocli/sdk/ai/RegisterOVHEmbeddedModel.java) (👨‍💻 `48-register-embedding-model`)
  - run [20-quarkus-build.sh](./20-quarkus-build.sh) 
  - remove `%prod` from [application.properties](./src/main/resources/application.properties)
  - test the new chat bot: [49-java-run-rag.sh](./49-java-run-rag.sh)