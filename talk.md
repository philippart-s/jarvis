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
  - create [JarvisCommand.java](src/main/java/fr/wilda/picocli/JarvisCommand.java_) (👨‍💻  _04_JarvisCommand.java && _👨‍💻 _05-jarvis-cli-class-annot_ && _06-jarvis-cli-logger_ && _07-jarvis-cli-name-param_ && _08-jarvis-hello_)
  - add `@TopCommand` to [JarvisCommand.java](src/main/java/fr/wilda/picocli/JarvisCommand.java_)
  - play with the CLI : `--help`, `"xxx!!"`
  - run [09-add-langchain-4j.sh](bin/09-add-langchain-4j.sh)
  - update `application.properties`: (👨‍💻 _10-props-langchain4J_)
  - create interface [AIEndpointService.java](src/main/java/fr/wilda/picocli/sdk/ai/AIEndpointService.java) (🧑‍💻 _12_AIEndpointsService_)
    - add annotations (👨‍💻 _13-AIEndpointService-annotation_)
    - add method `askQuestion` (👨‍💻 _14-ask-method_)
- update [JarvisCommand](src/main/java/fr/wilda/picocli/JarvisCommand.java_):
    - `name` to `question` parameter (👨‍💻 _15-jarvis-cli-question-param_)
    - inject `AIEndpointService` (👨‍💻 _16-jarvis-cli-ai-svc_)
    - add the AI model call (👨‍💻 _17-jarvis-cli-ai-svc-call_)
- run [20-quarkus-build.sh](bin/20-quarkus-build.sh)
- run [21-java-run.sh](bin/21-java-run.sh)
- create [jarvis.sh](./src/main/script/jarvis.sh) (👨‍💻 _22-jarvis-bash_)
- `chmod +x jarvis.sh`
- run [23-run-script.sh](bin/23-run-script.sh) 
- run [24-quarkus-build-native.sh](bin/24-quarkus-build-native.sh)
- run [25-jarvis-ai-run.sh](bin/25-jarvis-ai-run.sh)
- run [26-without-rag-prompt](bin/26-without-rag-prompt.sh)
- add RAG parameters to [application.properties](src/main/resources/application.properties) (👨‍💻 `27-rag-parameters`)
- create the RAG classes (🧑‍💻 _28-InMemoryEmbeddingStoreProvider_) 
  - add in memory store (👨‍💻 `29-in-memory-embedding-store`) in [InMemoryEmbeddingStoreProvider](src/main/java/fr/wilda/picocli/sdk/ai/InMemoryEmbeddingStoreProvider.java_) class
  - add fields and loadDocuments method (👨‍💻 `30-rag-resources` && 👨‍💻 `31-load-rag-files`) in [DocumentLoader](src/main/java/fr/wilda/picocli/sdk/ai/DocumentLoader.java_) class
  - add the retriever (👨‍💻 `32-retriever`) in the [DocumentRetriever](src/main/java/fr/wilda/picocli/sdk/ai/DocumentRetriever.java_)
- create the [RagSubCommand](src/main/java/fr/wilda/picocli/RagSubCommand.java_) file (👨‍💻 `33-RagSubCommand`)
    - add RAG option (👨‍💻 `34-rag-option`) 
    - add question parameter (👨‍💻 `35-question-with-rag`)
    - add services (👨‍💻 `36-inject-services`)
    - call the model with RAG feature (👨‍💻 `37-call-with-rag`)
- Add [RagSubCommand](src/main/java/fr/wilda/picocli/RagSubCommand.java_) to [JarvisCommand](src/main/java/fr/wilda/picocli/JarvisCommand.java_)
- add `subcommands = {RagSubCommand.class}` to [JarvisCommand.java](src/main/java/fr/wilda/picocli/JarvisCommand.java_)
- (optional) run [24-quarkus-build-native.sh](bin/24-quarkus-build-native.sh)
- test Jarvis with RAG [38-run-rag.sh](bin/38-run-rag.sh)
- run [39-without-mcp.sh](bin/39-without-mcp.sh)
- run [40-add-quarkus-mcp-client.sh](bin/40-add-quarkus-mcp-client.sh)
- add MCP configuration to [application.properties](./src/main/resources/application.properties) (👨‍💻 `41-mcp-parameters`)
- create the [McpToolsException](src/main/java/fr/wilda/picocli/sdk/ai/mcp/McpToolsException.java) class
- create the [OVHcloudMcpAuthProvider](src/main/java/fr/wilda/picocli/sdk/ai/mcp/OVHcloudMcpAuthProvider.java_) class
  - add PAT (👨‍💻 `43-OVHcloud-MCP-PAT`)
  - add Bearer (👨‍💻 `44-bearer-for-mcp`)
- create [ApprovalMcpToolProvider](src/main/java/fr/wilda/picocli/sdk/ai/mcp/ApprovalMcpToolProvider.java_)
    - add MCP client (👨‍💻 `45-MCP-client`)
    - add approval tool (👨‍💻 `46-approval-tool`)
  - update [AIEndpointService](src/main/java/fr/wilda/picocli/sdk/ai/AIEndpointService.java_)
    - add askAQuestionAboutOVHcloud method (👨‍💻 `47-ask-about-ovhcloud`)
  - create [McpSubCommand](src/main/java/fr/wilda/picocli/McpSubCommand.java_) file (‍💻 `48-McpSubCommand`)
    - add LLM call with MCP (👨‍💻 `49-call-llm-with-mcp`)
  - add `subcommands = {McpSubCommand.class}` to [JarvisCommand](src/main/java/fr/wilda/picocli/JarvisCommand.java_)
  - Optional: run [24-quarkus-build-native.sh](bin/24-quarkus-build-native.sh)
  - run [50-jarvis-mcp.sh](bin/50-jarvis-mcp.sh)
  - run [51-add-quarkus-agentic.sh](bin/51-add-quarkus-agentic.sh)
  - create the Workflow Agents classes (‍👨‍💻 `52-WorkflowAgents`)
      - in [ClassifierAgent.java](src/main/java/fr/wilda/picocli/sdk/ai/agent/common/ClassifierAgent.java_): 👨‍💻 `53-classifier-enum-agent` && `54-classifier-agent`
      - in [ChatAgent.java](src/main/java/fr/wilda/picocli/sdk/ai/agent/common/ChatAgent.java_): (👨‍💻 `55-chat-agent`)
      - in [RagAgent](src/main/java/fr/wilda/picocli/sdk/ai/agent/common/RagAgent.java_) (👨‍💻 `56-rag-agent`) 
      - create [RagTool.java](src/main/java/fr/wilda/picocli/sdk/ai/agent/tool/RagTool.java_) (👨‍💻 `57-RagTool`)
        - add tool (👨‍💻 `58-rag-tool`)
      - in [OVHcloudAgent.java](src/main/java/fr/wilda/picocli/sdk/ai/agent/common/OVHcloudAgent.java_): (👨‍💻 `59-ovhcloud-agent`)
      - in [JarvisAgent.java](src/main/java/fr/wilda/picocli/sdk/ai/agent/common/JarvisAgent.java_) (👨‍💻 `60-jarvis-agent`)
  - create Jarvis Workflow classes (‍👨‍💻 `61-JarvisWorkfow`)
    - in [AvailableAgents.java](./src/main/java/fr/wilda/picocli/sdk/ai/agent/common/AvailableAgents.java) (👨‍💻 `62-conditional-agent` && `63-activation-condition`)
    - in [JarvisWorkflow.java](./src/main/java/fr/wilda/picocli/sdk/ai/agent/common/JarvisWorkflow.java): (👨‍💻 `64-jarvis-workflow`)
  - create [WorkflowSubCommand.java](./src/main/java/fr/wilda/picocli/WorkflowSubCommand.java) (👨‍💻 `65-WorkflowSubCommand`)
  - add `WorkflowSubCommand` to [JarvisCommand.java](src/main/java/fr/wilda/picocli/JarvisCommand.java_)
  - Optional: run [24-quarkus-build-native.sh](bin/24-quarkus-build-native.sh)
  - run [66-run-agent-workflow.sh](bin/66-run-agent-workflow.sh)
  - create [AutonomousAgent.java](src/main/java/fr/wilda/picocli/sdk/ai/agent/AutonomousAgent.java_) (👨‍💻 `67-AutonomousAgent`) and add code (👨‍💻 `68-autonomous-agent`)
  - create [AgentSubCommand.java](./src/main/java/fr/wilda/picocli/AgentSubCommand.java) (👨‍💻 `69-AgentSubCommand`)
  - add `AgentSubCommand` to [JarvisCommand.java](src/main/java/fr/wilda/picocli/JarvisCommand.java_)
  - Optional: run [24-quarkus-build-native.sh](bin/24-quarkus-build-native.sh)
  - run [70-run-agent-supervidor.sh](bin/70-run-agent-supervisor.sh)
  - add `GenerateCompletion` subcommand on [JarvisCommand.java](src/main/java/fr/wilda/picocli/JarvisCommand.java_), [OVHcloudSubCommand.java](./src/main/java/fr/wilda/picocli/OVHcloudSubCommand.java), [RagSubCommand](src/main/java/fr/wilda/picocli/RagSubCommand.java_) and [McpSubCommand.java](src/main/java/fr/wilda/picocli/McpSubCommand.java_)
    - Optional: run [24-quarkus-build-native.sh](bin/24-quarkus-build-native.sh)
    - source the `.env` file, `source .env`
    - display help on autocompletion: `jarvis generate-completion --help`
    - activate the autocompletion: `source <(jarvis generate-completion)`
    - test the autocompletion
  - add TamboUI dependency (👨‍💻 `72-maven-tamboUI-version` & `73-maven-tamboUI-deps`) 
  - create [ToolApproval](./src/main/java/fr/wilda/picocli/sdk/ai/mcp/ToolApproval.java) (👨‍💻 `74-ToolApproval`)
  - create TUI for the Jarvis command (👨‍💻 `75-JarvisTUI`)
  - run [76-run-tui.sh](bin/76-run-tui.sh)



