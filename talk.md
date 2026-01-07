# Generate Snippets

```bash
snippets generate \
  --input ./.vscode/snippets.yml \
  --output ./.vscode/devoxx-snippet.code-snippets
```

# Do the demo

  - run [01-create-cli.sh](./bin/01-create-cli.sh)
  - run [02-check-dependencies.sh](bin/02-check-dependencies.sh)
  - run [03-quarkus-dev-mode.sh](bin/03-quarkus-dev-mode.sh)
  - play with the CLI : `--help`, `"xxx!!"`
  - add to [application.properties](./src/main/resources/application.properties) (👨‍💻 _04-props-ovh-env_ && _05-props-rest-client_)
  - create [OVHcloudAPIService.java](./src/main/java/fr/wilda/picocli/sdk/OVHcloudAPIService.java) (👨‍💻 _06-OVHcloudAPIService-annot_ && _07-OVHcloudAPIService-endpoints_)
  - create [JarvisCommand.java](./src/main/java/fr/wilda/picocli/JarvisCommand.java) (👨‍💻 _08-jarvis-cli-class-annot_ && _09-jarvis-cli-logger_ && _10-jarvis-cli-name-param_ && _11-jarvis-hello_)
  - add `@TopCommand` to [JarvisCommand.java](./src/main/java/fr/wilda/picocli/JarvisCommand.java)
  - delete [GreetingCommand.java](./src/main/java/fr/wilda/picocli/GreetingCommand.java)
  - create [OVHcloudSubCommand.java](./src/main/java/fr/wilda/picocli/OVHcloudSubCommand.java) (👨‍💻 _12-ovh-cli-class-annot_ && _13-ovh-cli-logger_ && _14-ovh-cli-rest-client_ && _15-ovh-cli-ovh-stuff_ && _16-ovh-cli-options_ && _17-ovh-cli-me_ && _18-ovh-cli-kube_) 
  - add `subcommands = {OVHcloudSubCommand.class}` to [JarvisCommand.java](./src/main/java/fr/wilda/picocli/JarvisCommand.java)
  - set log in [application.properties](./src/main/resources/application.properties): (👨‍💻 _19-props-logs-prod_)
  - run [20-quarkus-build.sh](bin/20-quarkus-build.sh)
  - run [21-java-run.sh](bin/21-java-run.sh)
  - create [jarvis.sh](./src/main/script/jarvis.sh) (👨‍💻 _22-jarvis-bash_)
  - `chmod +x jarvis.sh`
  - run [23-run-script.sh](bin/23-run-script.sh)
  - run [24-quarkus-build-native.sh](bin/24-quarkus-build-native.sh)
  - run [25-jarvis-api-run.sh](bin/25-jarvis-api-run.sh)
  - run [26-add-langchain-4j.sh](bin/26-add-langchain-4j.sh)
  - update `application.properties`: (👨‍💻 _27-props-langchain4J_)
  - create interface [AIEndpointService.java](./src/main/java/fr/wilda/picocli/sdk/ai/AIEndpointService.java) and add annotations (👨‍💻 _28-AIEndpointService-annotation_)
  - add method `askQuestion` (👨‍💻 _29-OVHcloudMistral-ask-method_)
  - update [JarvisCommand](./src/main/java/fr/wilda/picocli/JarvisCommand.java):
    - `name` to `question` parameter (👨‍💻 _30-jarvis-cli-question-param_)
    - inject `AIEndpointService` (👨‍💻 _31-jarvis-cli-ai-svc_)
    - add the AI model call (👨‍💻 _32-jarvis-cli-ai-svc-call_)
  - run [24-quarkus-build-native.sh](bin/24-quarkus-build-native.sh)
  - run [33-jarvis-ai-run.sh](bin/33-jarvis-ai-run.sh)
  - test the conference question (👨‍💻 `34-without-rag-prompt.sh`)
  - add RAG parameters to [application.properties](src/main/resources/application.properties) (👨‍💻 `35-rag-parameters`)
  - create the [InMemoryEmbeddingStoreProvider](src/main/java/fr/wilda/picocli/sdk/ai/InMemoryEmbeddingStoreProvider.java) class and add in memory store (👨‍💻 `36-in-memory-embedding-store`)
  - create the [DocumentLoader](src/main/java/fr/wilda/picocli/sdk/ai/DocumentLoader.java) class and add fields and loadDocuments method (👨‍💻 `37-rag-resources` && 👨‍💻 `38-rag-resources` && 👨‍💻 `39-ingest-docs`)
  - create the [DocumentRetriever](src/main/java/fr/wilda/picocli/sdk/ai/DocumentRetriever.java)
    - add the retriever (👨‍💻 `40-retriever`)
  - create the [RagSubCommand](src/main/java/fr/wilda/picocli/RagSubCommand.java) file
    - add RAG option (👨‍💻 `41-rag-option`) 
    - add question parameter (👨‍💻 `42-question-with-rag`)
    - add services (👨‍💻 `43-inject-services`)
    - call the model with RAG feature (👨‍💻 `44-call-with-rag`)
  - Add [RagSubCommand](src/main/java/fr/wilda/picocli/RagSubCommand.java) to [JarvisCommand](src/main/java/fr/wilda/picocli/JarvisCommand.java)
  - Optional: run [24-quarkus-build-native.sh](bin/24-quarkus-build-native.sh)
  - test Jarvis with RAG [45-java-run-rag.sh](bin/45-java-run-rag.sh)
  - run [46-add-quarkus-mcp-client.sh](bin/46-add-quarkus-mcp-client.sh)
  - add MCP configuration to [application.properties](./src/main/resources/application.properties) (👨‍💻 `47-mcp-parameters`)  
  - create the [McpToolsException](src/main/java/fr/wilda/picocli/sdk/ai/McpToolsException.java) class
  - create the [OVHcloudMcpAuthProvider](src/main/java/fr/wilda/picocli/sdk/ai/OVHcloudMcpAuthProvider.java) class
    - add PAT (👨‍💻 `48-OVHcloud-MCP-PAT`)
    - add Bearer (👨‍💻 `49-bearer-for-mcp`)
  - create [TimeAndDateTool](./src/main/java/fr/wilda/picocli/sdk/ai/TimeAndDateTool.java) class
  - add `askAQuestionEvent` method to [AIEndpointService.java](./src/main/java/fr/wilda/picocli/sdk/ai/AIEndpointService.java) (👨‍💻 `50-question-event`)
  - create [McpSubCommand](./src/main/java/fr/wilda/picocli/McpSubCommand.java)
    - add LLM call with MCP (👨‍💻 `51-call-llm-with-mcp`)
    - add subcommand to [JarvisCommand](./src/main/java/fr/wilda/picocli/JarvisCommand.java) 
  - Optional: run [24-quarkus-build-native.sh](bin/24-quarkus-build-native.sh)
  - run [52-jarvis-mcp.sh](bin/52-jarvis-mcp.sh)
  - add `GenerateCompletion` subcommand on [JarvisCommand.java](./src/main/java/fr/wilda/picocli/JarvisCommand.java), [OVHcloudSubCommand.java](./src/main/java/fr/wilda/picocli/OVHcloudSubCommand.java), [RagSubCommand](src/main/java/fr/wilda/picocli/RagSubCommand.java) and [McpSubCommand.java](./src/main/java/fr/wilda/picocli/McpSubCommand.java)
  - Optional: run [24-quarkus-build-native.sh](bin/24-quarkus-build-native.sh)
  - source the `.env` file, `source .env`
  - display help on autocompletion: `jarvis generate-completion --help`
  - activate the autocompletion: `source <(jarvis generate-completion)`
  - test the autocompletion