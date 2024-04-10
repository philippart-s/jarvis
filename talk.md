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
    - `export QUARKUS_LANGCHAIN4J_MISTRALAI_BASE_URL=https://mistral-7b-instruct-v02.endpoints.kepler.ai.cloud.ovh.net/api/openai_compat/v1`
  - open a terminal and go to `/tmp`
  - create the project `quarkus create cli fr.wilda.picocli:jarvis-devoxx:0.0.1-SNAPSHOT`
  - `mvn dependency:tree -Dincludes=info.picocli`
  - go to branch `01-✨-init-project`
  - launch the CLI : `quarkus dev`
  - play with the CLI : `--help`, `"Devoxx France!!"`
  - add jarvis-sdk (👨‍💻 _pom-jarvis-sdk-dep_)
```xml
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-rest-client-jackson</artifactId>
</dependency>
```
  - add rest-client dependency (👨‍💻 _pom-rest-dep_)
```xml
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-rest-client-jackson</artifactId>
</dependency>
```
  - add to application.properties (👨‍💻 _props-ovh-env_ && _props-rest-client_)
```java
# OVHcloud parameter
ovhcloud.consumer=${OVH_CONSUMER_KEY}
ovhcloud.application=${OVH_APPLICATION_KEY}
ovhcloud.projectId=${OVH_CLOUD_PROJECT_SERVICE}

# RestClient parameter
quarkus.rest-client."fr.wilda.picocli.sdk.OVHcloudAPIService".url=https://eu.api.ovh.com/
quarkus.rest-client."fr.wilda.picocli.sdk.OVHcloudAPIService".scope=javax.inject.Singleton 
```
  - create `fr.wilda.picocli.sdk.OVHcloudUser` (getter & setters + toString) (👨‍💻 _OVHcloudUser-fields_)
  - create `fr.wilda.picocli.sdk.OVHcloudKube` (getter & setters + toString) (👨‍💻 _OVHcloudKube-fields_)
  - create `fr.wilda.picocli.sdk.OVHcloudSignatureHelper` (👨‍💻 _OVHcloudSigHelper_)
  - create `fr.wilda.picocli.sdk.OVHcloudAPIService` (👨‍💻 _OVHcloudAPI-annot_ && _OVHcloudAPI-endpoints_)
  - create `fr.wilda.picocli.JarvisCommand` (👨‍💻 _ovh-cmd-class-annot_ && _ovh-cmd-logger_ && _ovh-cmd-options_)
  - delete `fr.wilda.picocli.GreetingCommand`
  - create `fr.wilda.picocli.OVHcloudSubCommand` (👨‍💻 _jarvis-cmd-class-annot_ && _jarvis-cmd-name-param_ && _jarvis-cmd-logger_ && _ovh-cmd-rest-client_ && _ovh-cmd-ovh-stuff_ && _ovh-cmd-me_ && _ovh-cmd-kube_) 
  - add `@TopCommand` & `subcommands = {OVHcloudSubCommand.class}` to `fr.wilda.picocli.JarvisCommand`
  - set log in application.properties: (👨‍💻 _props-logs-prod_)
```java
# Make outputs readable
%prod.quarkus.log.level=OFF
%prod.quarkus.banner.enabled=false
%prod.quarkus.log.category."fr.wilda".level=INFO
%prod.quarkus.log.console.format=%m
```
  - `quarkus build`
  - `cd target/quarkus-app` && `du -h`
  - `cd ../..` & `java -jar ./target/quarkus-app/quarkus-run.jar "Devoxx France"` && 
  - create `src/main/script/jarvis.sh` (👨‍💻 _jarvis-bash_)
  - `chmod +x jarvis.sh`
  - `export GRAALVM_HOME=/Users/sphilipp/local-bin/graalvm-jdk-21.0.2+13.1/Contents/Home`
  - `quarkus build --native`
  - `jarvis-bck ovhcloud -mk`
  - add to pom.xml: (👨‍💻 _pom-langchain4j-dep_)
```xml
<dependency>
      <groupId>io.quarkiverse.langchain4j</groupId>
      <artifactId>quarkus-langchain4j-mistral-ai</artifactId>
      <version>0.10.3</version>
</dependency>  
```
  - update `application.properties`: (👨‍💻 _props-langchain4J_)
```java
# Langchain4J parameters
quarkus.langchain4j.mistralai.api-key=foo
quarkus.langchain4j.mistralai.chat-model.max-tokens=150
quarkus.langchain4j.mistralai.chat-model.model-name=Mistral-7B-Instruct-v0.2

quarkus.langchain4j.mistralai.log-requests=true
quarkus.langchain4j.mistralai.log-responses=true
```
  - set env variable `export QUARKUS_LANGCHAIN4J_MISTRALAI_BASE_URL=https://mistral-7b-instruct-v02.endpoints.kepler.ai.cloud.ovh.net/api/openai_compat/v1`
  - create interface `fr.wilda.picocli.sdk.ai.AIEndpointMistral7bService` + `@RegisterAiService` + `@ApplicationScoped`
  - add method `askQuestion` (👨‍💻 _OVHcloudMistral-ask-method_)
  - update `JarvisCommand`:
    - `name` to `question` parameter (👨‍💻 _jarvis-cmd-question-param_)
    - inject `AIEndpointMistral7bService` (👨‍💻 _jarvis-cmd-ai-svc_)
    - new log
  - test AI: `"Can you tell me more abour Devoxx France?"`
  - turn off AI log
  - `quarkus build --native`