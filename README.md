# Discover Picocli

Here you can find the different steps to create your first CLI in Java with the [Picocli](https://picocli.info/) Framework.
The main steps of the project are stored each in a separate git branch chronological named with numbers.

## 🎉 - 01-Init project
 - all the resulted source code will be find in the branch `01-🎉-Init-Project`
 - init the Quarkus project: `quarkus create cli fr.wilda.picocli:jarvis:0.0.1-SNAPSHOT --no-wrapper`
```bash
$ quarkus create cli fr.wilda.picocli:jarvis:0.0.1-SNAPSHOT  
Looking for the newly published extensions in registry.quarkus.io
-----------
selected extensions: 
- io.quarkus:quarkus-picocli


applying codestarts...
📚 java
🔨 maven
📦 quarkus
📝 config-properties
🔧 dockerfiles
🔧 maven-wrapper
🚀 picocli-codestart

-----------
[SUCCESS] ✅  quarkus project has been successfully generated in:
--> /Users/stef/xxx/jarvis
-----------
Navigate into this directory and get started: quarkus dev
```
  - let's see what the command generated:
    - a [pom.xml](pom.xml) with all necessary dependences
      - take look to the dependecies section with a reference to the *picocli* extension
    - an example class, [GreetingCommand](./src/main/java/fr/wilda/picocli/GreetingCommand.java)
      - take a look to the annotations : 
        - `@Command(name = "greeting", mixinStandardHelpOptions = true)`: 'mixinStandardHelpOptions' adds the `--help` and `--version` options
        - `@Parameters(paramLabel = "<name>", defaultValue = "picocli",  description = "Your name.")`: parameter to set to the CLI, if empty use the `defaultValue` value. The description parameter will be displayed when calling the `--help` option
    - a set of Dockerfiles in [src/main/docker](./src/main/docker/)
    - an empty [application.properties](./src/main/resources/application.properties) 

## 02-Try the CLI

 - all the resulted source code will be find in the branch `02-📺-Try-the-CLI`
 - first use the Quarkus dev mode: `quarkus dev`:
```bash
$ quarkus dev

__  ____  __  _____   ___  __ ____  ______ 
 --/ __ \/ / / / _ | / _ \/ //_/ / / / __/ 
 -/ /_/ / /_/ / __ |/ , _/ ,< / /_/ /\ \   
--\___\_\____/_/ |_/_/|_/_/|_|\____/___/   
2023-10-29 18:46:11,317 INFO  [io.quarkus] (Quarkus Main Thread) jarvis 0.0.1-SNAPSHOT on JVM (powered by Quarkus 3.5.0) started in 0.497s. 

2023-10-29 18:46:11,319 INFO  [io.quarkus] (Quarkus Main Thread) Profile dev activated. Live Coding activated.
2023-10-29 18:46:11,319 INFO  [io.quarkus] (Quarkus Main Thread) Installed features: [cdi, picocli]
Hello picocli, go go commando!

2023-10-29 18:46:11,354 INFO  [io.quarkus] (Quarkus Main Thread) jarvis stopped in 0.002s

--
Tests paused
Press [space] to restart, [e] to edit command line args (currently ''), [r] to resume testing, [o] Toggle test output, [:] for the terminal, [h] for more options>
```
 - press `e` to test the CLI as you were in a terminal:
```bash
2023-10-29 18:46:11,354 INFO  [io.quarkus] (Quarkus Main Thread) jarvis stopped in 0.002s

--
Tests paused
-h

Usage: greeting [-hV] <name>
      <name>      Your name.
  -h, --help      Show this help message and exit.
  -V, --version   Print version information and exit.

2023-07-29 14:54:51,389 INFO  [io.quarkus] (Quarkus Main Thread) discover-picocli stopped in 0.000s

--
Tests paused
Press [space] to restart, [e] to edit command line args (currently ''), [r] to resume testing, [o] Toggle test output, [:] for the terminal, [h] for more options>

--
Tests paused
Stéphane

2023-07-29 14:58:33,334 INFO  [io.qua.dep.dev.RuntimeUpdatesProcessor] (Aesh InputStream Reader) Live reload total time: 0.097s 
Hello Stéphane, go go commando!

2023-10-29 18:51:31,003 INFO  [io.quarkus] (Quarkus Main Thread) jarvis stopped in 0.000s
--
Tests paused
Press [space] to restart, [e] to edit command line args (currently ''), [r] to resume testing, [o] Toggle test output, [:] for the terminal, [h] for more options>
```

## 03-ovhcloud-sdk

 - all the resulted source code will be find in the branch `03-🔗-ovhcloud-sdk`
 - add the _rest client reactive_ client: `quarkus extension add rest-client-reactive-jackson` (see it in the _dependencies_ section in the [pom.xml](./pom.xml))
 - add the `jarvis-sdk` dependency in the [pom.xml](./pom.xml):
```xml
    <dependency>
      <groupId>fr.wilda.jarvis.sdk</groupId>
      <artifactId>jarvis-sdk</artifactId>
      <version>1.0.0</version>
    </dependency>
```
  - create the API Service that is responsible to call the OVHcloud API: [OVHcloudAPIService.java](./src/main/java/fr/wilda/picocli/sdk/OVHcloudAPIService.java)
  - take a look to the _Jakarta_ annotations:
    - `@RegisterRestClient`: to use it as client to do API call, see [application.properties](./src/main/resources/application.properties) for parameters
    - `@Path("/v1")`: root path for the called end-point

## 04-🤖-create-jarvis

 - all the resulted source code will be find in the branch `04-🤖-add-ovhcloud-feature`
 - create the main entry point for the CLI: [JarvisCommand.java](./src/main/java/fr/wilda/picocli/JarvisCommand.java)
 - delete the `GreetingCommand.java` file - test your CLI with the developer mode: `quarkus dev`

## 05-☁️-add-ovhcloud-command

- all the resulted source code will be find in the branch `05-☁️-add-ovhcloud-command`
- create the OVHcloud sub command to access to the REST API: [OVHcloudSubCommand.java](./src/main/java/fr/wilda/picocli/OVHcloudSubCommand.java)
  - take a look to the annotations:
    - `@Option(names = {"-m", "--me"}, description = "Display the OVHcloud account details.")`, `@Option(names = {"-k", "--kube"}, description = "Display your Managed Kubernetes Service created.")`: create boolean options activated when setted
    - `@RestClient`: to use the API Service class [OVHcloudAPIService](./src/main/java/fr/wilda/picocli/sdk/OVHcloudAPIService.java)
    - `@ConfigProperty(name = "ovhcloud.projectId")`: to get the value of the key `ovhcloud.projectId` from [application.properties](./src/main/resources/application.properties) file.
- update the [JarvisCommand.java](./src/main/java/fr/wilda/picocli/JarvisCommand.java) with the `@TopCommand` annotation and the sub command list `subcommands = {OVHcloudSubCommand.class}` 
- test the new subcommand: `ovhcloud -m -k`

## 06-📦-package

  - all the resulted source code will be find in the branch `06-📦-package`
  - update the [application.properties](./src/main/resources/application.properties) to have clean outputs: `%prod.quarkus.log.category."fr.wilda".level=INFO` & `%prod.quarkus.log.console.format=%m`
  - launch the _build_ Quarkus command: `quarkus build`
    - take a look that the application is available in [target/quarkus-app](./target/quarkus-app/) folder, espacially the [lib](./target/quarkus-app/lib/) folder
    - take a look to the generated application: `du -h --max-depth=1`
  - test the packaged CLI: `java -jar ./target/quarkus-app/quarkus-run.jar Stéphane`, `java -jar ./target/quarkus-app/quarkus-run.jar ovhcloud -m -k`
  - create the [jarvis.sh](./src/main/script/jarvis.sh) script
  - in the [src/main/script](./src/main/script/) folder, test the CLI: `./jarvis.sh ovhcloud -m -k`

## 07-🚀-graalvm

  - all the resulted source code will be find in the branch `07-🚀-graalvm`
  - if needed install GraalVM: https://www.graalvm.org/latest/docs/getting-started/
  - set the `GRAALVM_HOME` environment variable: `GRAALVM_HOME=/Users/sphilipp/local-bin/graalvm-jdk-21.0.2+13.1/Contents/Home`
  - ⚠️ if your `/tmp` is mounted as `noexec`: change the `java.io.tmpdir` to a folder with `exec` permission by adding the following property on your build command: `-Dquarkus.native.additional-build-args=-Djava.io.tmpdir=$HOME/tmp` or use the experimental building feature: `quarkus build --native -Dquarkus.native.container-build=true` ⚠️
  - build the binary with the command `quarkus build --native`
  - use the generated CLI: `./target/jarvis-0.0.1-SNAPSHOT-runner ovhcloud -m -k`
  - rename and move the generated CLI: `cp ./target/jarvis-0.0.1-SNAPSHOT-runner ~/bin/jarvis`
  - use the CLI: `jarvis ovhcloud -m`

## 08-🤖-add-ai

>**⚠️ You need to set the following environment variables: ⚠️**
>  - `QUARKUS_LANGCHAIN4J_MISTRALAI_BASE_URL` with the API URL of Mixtral model. 
>  - `OVH_AI_ENDPOINTS_ACCESS_TOKEN` with your OVHcloud AI Endpoint API key (see https://endpoints.ai.cloud.ovh.net/)

 - add the following dependency in the pom.xml:
```xml
<dependency>
  <groupId>io.quarkiverse.langchain4j</groupId>
  <artifactId>quarkus-langchain4j-mistral-ai</artifactId>
  <version>0.15.1</version>
</dependency>
```
 - create the service for calling OVHcloud Mistral AI Endpoint: [AIEndpointService](./src/main/java/fr/wilda/picocli/sdk/ai/AIEndpointService.java) 
 - update the [application.properties](./src/main/resources/application.properties):
```java
quarkus.langchain4j.mistralai.api-key=${OVH_AI_ENDPOINTS_ACCESS_TOKEN}
quarkus.langchain4j.mistralai.chat-model.max-tokens=1500
quarkus.langchain4j.mistralai.chat-model.model-name=Mixtral-8x22B-Instruct-v0.1

quarkus.langchain4j.mistralai.log-requests=true
quarkus.langchain4j.mistralai.log-responses=true
```
 - update the [JarvisCommans](./src/main/java/fr/wilda/picocli/JarvisCommand.java) class:
  - inject the service [AIEndpointService.java](./src/main/java/fr/wilda/picocli/sdk/ai/AIEndpointService.java):
```java
@Inject
AIEndpointService aiEndpointService;
```
  - update the parameter name to become the question to ask:
```java
 // Question to ask
  @Parameters(paramLabel = "<question>", defaultValue = "Can you explain what are you?", description = "The question to ask to Jarvis.")
  private String question;
```
  - update the `call` method:
```java
  @Override
  public Integer call() throws Exception {
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
    return 0;
  }
```
 - set logs to false in [application.properties](./src/main/resources/application.properties):
```java
quarkus.langchain4j.mistralai.log-requests=false
quarkus.langchain4j.mistralai.log-responses=false
```
 - create the new binary: `quarkus build --native`
 - test the new CLI: `./target/jarvis-0.0.1-SNAPSHOT-runner "Who are you?"`

