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