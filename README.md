# Discover Picocli

Here you can find the different steps to create your first CLI in Java with the [Picocli](https://picocli.info/) Framework.
The main steps of the project are stored each in a separate git branch chronological named with numbers.

## 01-Init project
 - all the resulted source code will be find in the branch `01-🎉-Init-Project`
 - init the Quarkus project: `quarkus create cli fr.wilda.picocli:jarvis:0.0.1-SNAPSHOT`
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
 - create the two DTO classes used to store the result of the calling API: [OVHcloudUser.java](./src/main/java/fr/wilda/picocli/sdk/OVHcloudUser.java) and [OVHcloudKube.java](./src/main/java/fr/wilda/picocli/sdk/OVHcloudKube.java)
 - create the API Service that is responsible to call the OVHcloud API: [OVHcloudAPIService.java](./src/main/java/fr/wilda/picocli/sdk/OVHcloudAPIService.java)
  - take a look to the _Jakarta_ anotations:
    - `@RegisterRestClient`: to use it as client to do API call, see [application.properties](./src/main/resources/application.properties) for parameters
    - `@Path("/v1")`: root path for the called end-point
    - `@ClientHeaderParam(name = "X-Ovh-Consumer", value = "${ovhcloud.consumer}")`, `@ClientHeaderParam(name = "X-Ovh-Application", value = "${ovhcloud.application}")`, `@ClientHeaderParam(name = "Content-Type", value = "application/json")`: header parameters, see [application.properties](./src/main/resources/application.properties) for dynamic parameters
 - create the OVHcloud helper: [OVHcloudSignatureHelper](./src/main/java/fr/wilda/picocli/sdk/OVHcloudSignatureHelper.java)
  - the hash method is mandatory to use the OVHcloud API
