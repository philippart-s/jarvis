# Generate Snippets

```bash
snippets generate \
  --input ./.vscode/snippets.yml \
  --output ./.vscode/devoxx-snippet.code-snippets
```

# Do the demo

  - in VSCode 3 terminals:`source .env`
  - open a terminal and go to `/tmp`
  - create the project `quarkus create cli fr.wilda.picocli:jarvis:0.0.1-SNAPSHOT`
  - `mvn dependency:tree -Dincludes=info.picocli`
  - go to branch `01-✨-init-project`
  - launch the CLI : `quarkus dev`
  - play with the CLI : `--help`, `"xxx!!"`
  - add jarvis-sdk (👨‍💻 _01-pom-jarvis-sdk-dep_)
```xml
<dependency>
      <groupId>fr.wilda.jarvis.sdk</groupId>
      <artifactId>jarvis-sdk</artifactId>
      <version>1.1.0</version>
</dependency>
```
  - add rest-client dependency (👨‍💻 _02-pom-rest-dep_)
```xml
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-rest-client-jackson</artifactId>
</dependency>
```
  - add to application.properties (👨‍💻 _03-props-ovh-env_ && _04-props-rest-client_)
```java
# OVHcloud parameter
ovhcloud.consumer=${OVH_CONSUMER_KEY}
ovhcloud.application=${OVH_APPLICATION_KEY}
ovhcloud.projectId=${OVH_CLOUD_PROJECT_SERVICE}

# RestClient parameter
quarkus.rest-client."fr.wilda.picocli.sdk.OVHcloudAPIService".url=https://eu.api.ovh.com/
quarkus.rest-client."fr.wilda.picocli.sdk.OVHcloudAPIService".scope=javax.inject.Singleton 
```
  - create `fr.wilda.picocli.sdk.OVHcloudAPIService` (👨‍💻 _05-OVHcloudAPIService-annot_ && _06-OVHcloudAPIService-endpoints_)
  - create `fr.wilda.picocli.JarvisCommand` (👨‍💻 _07-jarvis-cli-class-annot_ && _08-jarvis-cli-logger_ && _09-jarvis-cli-name-param_ && _10-jarvis-hello_)
  - delete `fr.wilda.picocli.GreetingCommand`
  - create `fr.wilda.picocli.OVHcloudSubCommand` (👨‍💻 _11-ovh-cli-class-annot_ && _12-ovh-cli-logger_ && _13-ovh-cli-rest-client_ && _14-ovh-cli-ovh-stuff_ && _15-ovh-cli-options_ && _16-ovh-cli-me_ && _17-ovh-cli-kube_) 
  - add `@TopCommand` & `subcommands = {OVHcloudSubCommand.class}` to `fr.wilda.picocli.JarvisCommand`
  - set log in application.properties: (👨‍💻 _18-props-logs-prod_)
```java
# Make outputs readable
%prod.quarkus.log.level=OFF
%prod.quarkus.banner.enabled=false
%prod.quarkus.log.category."fr.wilda".level=INFO
%prod.quarkus.log.console.format=%m
```
  - `quarkus build`
  - `du -h ./target/quarkus-app`
  - `java -jar ./target/quarkus-app/quarkus-run.jar "xxx"` && `java -jar ./target/quarkus-app/quarkus-run.jar ovhcloud -mk`
  - create `src/main/script/jarvis.sh` (👨‍💻 _19-jarvis-bash_)
  - `chmod +x jarvis.sh`
  - './jarvis.sh ovhcloud --me'
  - `quarkus build --native`
  - `jarvis-api ovhcloud -mk`
  - add to pom.xml: (👨‍💻 _20-pom-langchain4j-dep_)
```xml
<dependency>
      <groupId>io.quarkiverse.langchain4j</groupId>
      <artifactId>quarkus-langchain4j-mistral-ai</artifactId>
      <version>0.18.0</version>
</dependency>  
```
  - update `application.properties`: (👨‍💻 _21-props-langchain4J_)
```java
# Langchain4J parameters
quarkus.langchain4j.mistralai.base-url=${OVH_AI_ENDPOINTS_MODEL_URL}
quarkus.langchain4j.mistralai.api-key=${OVH_AI_ENDPOINTS_ACCESS_TOKEN}
quarkus.langchain4j.mistralai.chat-model.max-tokens=512
quarkus.langchain4j.mistralai.chat-model.model-name=${OVH_AI_ENDPOINTS_MODEL_NAME}

quarkus.langchain4j.mistralai.log-requests=false
quarkus.langchain4j.mistralai.log-responses=false
quarkus.langchain4j.mistralai.timeout=60s    
```
     
> **⛑️ B plan**
> - launch ollama with AI Deploy: `ovhai app start 80296e7a-caf0-4c1c-9744-a09857e0898c`
> - export OVH_OLLAMA_API_KEY=
> - run Mistral model: 
>```bash
>curl -H "Authorization: Bearer $OVH_OLLAMA_API_KEY" https://80296e7a-caf0-4c1c-9744-a09857e0898c.app.gra.ai.cloud.ovh.net/api/pull -d '{
>"name": "mixtral"
>}'
>```
> - add following properties:
>```java
>quarkus.langchain4j.ollama.base-url=https://80296e7a-caf0-4c1c-9744-a09857e0898c.app.gra.ai.cloud.ovh.net
>quarkus.langchain4j.ollama.log-requests=true
>quarkus.langchain4j.ollama.timeout=60s    
>quarkus.langchain4j.ollama.embedding-model.enabled=false
>quarkus.langchain4j.ollama.chat-model.enabled=true              
>quarkus.langchain4j.ollama.chat-model.model-id=mixtral
>quarkus.langchain4j.chat-model.provider=ollama
>```        
> - add the following dependency:
>```xml
><dependency>
>    <groupId>io.quarkiverse.langchain4j</groupId>
>    <artifactId>quarkus-langchain4j-ollama</artifactId>
>    <version>0.15.1</version>
></dependency>
>```  
> - add the `OllamaClientAuthHeaderFilter` class:
>```java
>@Provider
>@RegisterForReflection
>public class OllamaClientAuthHeaderFilter implements ClientRequestFilter {
>
>    @Override
>    public void filter(ClientRequestContext requestContext) {
>        requestContext.getHeaders().add("Authorization", "Bearer " + System.getenv("OVH_OLLAMA_API_KEY"));
>    }
>}
>```
  - create interface `fr.wilda.picocli.sdk.ai.AIEndpointService` and add annotations (👨‍💻 _22-AIEndpointService-annotation_)
  - add method `askQuestion` (👨‍💻 _23-OVHcloudMistral-ask-method_)
  - update `JarvisCommand`:
    - `name` to `question` parameter (👨‍💻 _24-jarvis-cli-question-param_)
    - inject `AIEndpointService` (👨‍💻 _25-jarvis-cli-ai-svc_)
    - add the AI model call (👨‍💻 _26-jarvis-cli-ai-svc-call_)
  - test AI: `"Can you tell me more about xxx?"`
  - turn off AI log
  - `quarkus build --native`
  - 'jarvis-ai "What is XXX?"'
  - add the token for request (👨‍💻 _27-token-sentiment-service_)
  - create the service AISentimentService.java (👨‍💻 _28-sentiment-service_)
  - add the `textToEmotion` method (👨‍💻 _29-text2emotion-method_)
  - add the sentiment client in `OVHcloudSubCommand` (👨‍💻 _30-sentiment-client_)
  - add the sentiment option in `OVHcloudSubCommand` (👨‍💻 _31-sentiment-option_)
  - add the sentiment output in `OVHcloudSubCommand` (👨‍💻 _32-sentiment-output_)
  - test the emotion analysis: `ovhcloud -s "I'm happy to be here"` or `jarvis-ai-sentiment ovhcloud -s "I'm happy to be here"`
  - add the file option in `OVHcloudSubCommand` (👨‍💻 _33-file-option_)
  - add file analysis output in `OVHcloudSubCommand` (👨‍💻 _34-file-ouput_)
  - test the file analysis: `ovhcloud -f "./src/main/resources/sad-java-poem.txt"` or `jarvis-ai-file ovhcloud -f "./src/main/resources/fun-java-poem.txt"`
  - add `GenerateCompletion` subcommand on `JarvisCommand` and `OVHcloudSubCommand`
  - display help on autocompletion: `jarvis generate-completion --help`
  - activate the autocompletion: `source <(jarvis generate-completion)`
  - test the autocompletion
