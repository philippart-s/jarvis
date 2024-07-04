# Generate Snippets

```bash
snippets generate \
  --input ./.vscode/snippets.yml \
  --output ./.vscode/devoxx-snippet.code-snippets
```

# Do the demo

  - in VSCode 3 terminals:
    - `source ~/local-bin/set-ovh-env-sp-lab.sh`
    - `export GRAALVM_HOME=/Users/sphilipp/local-bin/graalvm-jdk-21.0.2+13.1/Contents/Home`
    - `export QUARKUS_LANGCHAIN4J_MISTRALAI_BASE_URL=https://mixtral-8x22b-instruct-v01.endpoints.kepler.ai.cloud.ovh.net/api/openai_compat/v1`
    - `export OVH_AI_ENDPOINTS_ACCESS_TOKEN=<your token here>`
    - `export OVH_OLLAMA_API_KEY=<your token here>`
  - open a terminal and go to `/tmp`
  - create the project `quarkus create cli fr.wilda.picocli:jarvis-devoxx:0.0.1-SNAPSHOT`
  - `mvn dependency:tree -Dincludes=info.picocli`
  - go to branch `01-✨-init-project`
  - launch the CLI : `quarkus dev`
  - play with the CLI : `--help`, `"Voxxed Days Lux!!"`
  - add jarvis-sdk (👨‍💻 _01-pom-jarvis-sdk-dep_)
```xml
<dependency>
      <groupId>fr.wilda.jarvis.sdk</groupId>
      <artifactId>jarvis-sdk</artifactId>
      <version>1.0.0</version>
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
  - create `fr.wilda.picocli.JarvisCommand` (👨‍💻 _07-jarvis-cli-class-annot_ && _08-jarvis-cli-logger_ && _08-jarvis-cli-name-param_)
  - delete `fr.wilda.picocli.GreetingCommand`
  - create `fr.wilda.picocli.OVHcloudSubCommand` (👨‍💻 _09-ovh-cli-class-annot_ && _10-ovh-cli-logger_ && _11-ovh-cli-rest-client_ && _12-ovh-cli-ovh-stuff_ && _13-ovh-cli-options_ && _14-ovh-cli-me_ && _15-ovh-cli-kube_) 
  - add `@TopCommand` & `subcommands = {OVHcloudSubCommand.class}` to `fr.wilda.picocli.JarvisCommand`
  - set log in application.properties: (👨‍💻 _16-props-logs-prod_)
```java
# Make outputs readable
%prod.quarkus.log.level=OFF
%prod.quarkus.banner.enabled=false
%prod.quarkus.log.category."fr.wilda".level=INFO
%prod.quarkus.log.console.format=%m
```
  - `quarkus build`
  - `cd target/quarkus-app` && `du -h`
  - `cd ../..` & `java -jar ./target/quarkus-app/quarkus-run.jar "Voxxed Days Lux France"` && `java -jar ./target/quarkus-app/quarkus-run.jar ovhcloud -mk`
  - create `src/main/script/jarvis.sh` (👨‍💻 _17-jarvis-bash_)
  - `chmod +x jarvis.sh`
  - `export GRAALVM_HOME=/Users/sphilipp/local-bin/graalvm-jdk-21.0.2+13.1/Contents/Home`
  - `quarkus build --native`
  - `jarvis-bck ovhcloud -mk`
  - add to pom.xml: (👨‍💻 _18-pom-langchain4j-dep_)
```xml
<dependency>
      <groupId>io.quarkiverse.langchain4j</groupId>
      <artifactId>quarkus-langchain4j-mistral-ai</artifactId>
      <version>0.15.1</version>
</dependency>  
```
  - update `application.properties`: (👨‍💻 _19-props-langchain4J_)
```java
# Langchain4J parameters
quarkus.langchain4j.mistralai.api-key=${OVH_AI_ENDPOINTS_ACCESS_TOKEN}
quarkus.langchain4j.mistralai.chat-model.max-tokens=150
quarkus.langchain4j.mistralai.chat-model.model-name=Mistral-7B-Instruct-v0.2

quarkus.langchain4j.mistralai.log-requests=true
quarkus.langchain4j.mistralai.log-responses=true
quarkus.langchain4j.mistralai.timeout=60s    
```
  - set env variable : 
    - `export QUARKUS_LANGCHAIN4J_MISTRALAI_BASE_URL=https://mixtral-8x22b-instruct-v01.endpoints.kepler.ai.cloud.ovh.net/api/openai_compat/v1`
    - export OVH_AI_ENDPOINTS_ACCESS_TOKEN=
     
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
>public class OllamaClientAuthHeaderFilter implements ClientRequestFilter {
>
>    @Override
>    public void filter(ClientRequestContext requestContext) {
>        requestContext.getHeaders().add("Authorization", "Bearer " + System.getenv("OVH_OLLAMA_API_KEY"));
>    }
>}
>```
  - create interface `fr.wilda.picocli.sdk.ai.AIEndpointService` + `@RegisterAiService` + `@ApplicationScoped`
  - add method `askQuestion` (👨‍💻 _20-OVHcloudMistral-ask-method_)
  - update `JarvisCommand`:
    - `name` to `question` parameter (👨‍💻 _21-jarvis-cli-question-param_)
    - inject `AIEndpointService` (👨‍💻 _22-jarvis-cli-ai-svc_)
    - add the AI model call (👨‍💻 _23-jarvis-cli-ai-svc-call_)
  - test AI: `"Can you tell me more about Riviera Dev?"`
  - turn off AI log
  - `quarkus build --native`