# Discover Picocli

Here you can find the different steps to create a first CLI in Java with the [Picocli](https://picocli.info/) Framework.
The main steps of the project are store each in a separate git branch chronological named with numbers.

## 01-Init project
 - all the resulted source code will be find in the branch `01-🎉-Init-Project`
 - init the Quarkus project: `quarkus create cli fr.wilda:discover-picocli:0.0.1-SNAPSHOT`
```bash
$ quarkus create cli fr.wilda:discover-picocli:0.0.1-SNAPSHOT
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
--> /Users/sphilipp/dev/talks/cli/discover-picocli
-----------
Navigate into this directory and get started: quarkus dev
```
 - the first CLI is generated in `GreetingCommand.java`:
```java
package fr.wilda;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "greeting", mixinStandardHelpOptions = true)
public class GreetingCommand implements Runnable {

    @Parameters(paramLabel = "<name>", defaultValue = "picocli",
        description = "Your name.")
    String name;

    @Override
    public void run() {
        System.out.printf("Hello %s, go go commando!\n", name);
    }
}
```

## 02-Try the CLI

 - all the resulted source code will be find in the branch `02-📺-Try-the-CLI`
 - first use the Quarkus dev mode: `quarkus dev`:
```bash
$ quarkus dev

__  ____  __  _____   ___  __ ____  ______ 
 --/ __ \/ / / / _ | / _ \/ //_/ / / / __/ 
 -/ /_/ / /_/ / __ |/ , _/ ,< / /_/ /\ \   
--\___\_\____/_/ |_/_/|_/_/|_|\____/___/   
2023-07-29 14:51:05,624 INFO  [io.quarkus] (Quarkus Main Thread) discover-picocli 0.0.1-SNAPSHOT on JVM (powered by Quarkus 3.2.0.Final) started in 0.498s. 

2023-07-29 14:51:05,633 INFO  [io.quarkus] (Quarkus Main Thread) Profile dev activated. Live Coding activated.
2023-07-29 14:51:05,634 INFO  [io.quarkus] (Quarkus Main Thread) Installed features: [cdi, picocli]
Hello picocli, go go commando!

2023-07-29 14:51:05,671 INFO  [io.quarkus] (Quarkus Main Thread) discover-picocli stopped in 0.002s

--
Tests paused
Press [space] to restart, [e] to edit command line args (currently ''), [r] to resume testing, [o] Toggle test output, [:] for the terminal, [h] for more options>
```
 - press `e` to test the CLI as you were in a terminal:
```bash
2023-07-29 14:54:20,539 INFO  [io.quarkus] (Quarkus Main Thread) discover-picocli stopped in 0.002s


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
🌍

2023-07-29 14:58:33,334 INFO  [io.qua.dep.dev.RuntimeUpdatesProcessor] (Aesh InputStream Reader) Live reload total time: 0.097s 
Hello 🌍, go go commando!

2023-07-29 14:58:33,338 INFO  [io.quarkus] (Quarkus Main Thread) discover-picocli stopped in 0.000s

--
Tests paused
Press [space] to restart, [e] to edit command line args (currently ''), [r] to resume testing, [o] Toggle test output, [:] for the terminal, [h] for more options>
```
 - packaging the CLI to use it in the terminal: `quarkus build`: it generates a jar (`quarkus-run`) in the `target/quarkus-app/` folder
 - run the command `java -jar ./target/quarkus-app/quarkus-run.jar`:
```bash
$ java -jar ./target/quarkus-app/quarkus-run.jar
__  ____  __  _____   ___  __ ____  ______ 
 --/ __ \/ / / / _ | / _ \/ //_/ / / / __/ 
 -/ /_/ / /_/ / __ |/ , _/ ,< / /_/ /\ \   
--\___\_\____/_/ |_/_/|_/_/|_|\____/___/   
2023-07-29 15:08:24,419 INFO  [io.quarkus] (main) discover-picocli 0.0.1-SNAPSHOT on JVM (powered by Quarkus 3.2.0.Final) started in 0.173s. 
2023-07-29 15:08:24,435 INFO  [io.quarkus] (main) Profile prod activated. 
2023-07-29 15:08:24,435 INFO  [io.quarkus] (main) Installed features: [cdi, picocli]
Hello picocli, go go commando!
2023-07-29 15:08:24,480 INFO  [io.quarkus] (main) discover-picocli stopped in 0.005s

$ java -jar ./target/quarkus-app/quarkus-run.jar 🌍
__  ____  __  _____   ___  __ ____  ______ 
 --/ __ \/ / / / _ | / _ \/ //_/ / / / __/ 
 -/ /_/ / /_/ / __ |/ , _/ ,< / /_/ /\ \   
--\___\_\____/_/ |_/_/|_/_/|_|\____/___/   
2023-07-29 15:09:10,279 INFO  [io.quarkus] (main) discover-picocli 0.0.1-SNAPSHOT on JVM (powered by Quarkus 3.2.0.Final) started in 0.173s. 
2023-07-29 15:09:10,296 INFO  [io.quarkus] (main) Profile prod activated. 
2023-07-29 15:09:10,296 INFO  [io.quarkus] (main) Installed features: [cdi, picocli]
Hello 🌍, go go commando!
2023-07-29 15:09:10,341 INFO  [io.quarkus] (main) discover-picocli stopped in 0.006s
```
 - update the `application.properties` file:
```java
%prod.quarkus.log.level=OFF
%prod.quarkus.banner.enabled=false
```
 - test again the CLI:
```bash
$ java -jar ./target/quarkus-app/quarkus-run.jar 🌍
Hello 🌍, go go commando!
```
 - time to have a _real_ CLI, create a `jarvis` file in `src/main/bin`:
```sh
#!/bin/bash

java -jar $PATH_TO_ROOT_DEV_FOLDER/discover-picocli/target/quarkus-app/quarkus-run.jar $1
```
 - test your CLI: `jarvis 🌍`:
```bash
$ ./jarvis 🌍
Hello 🌍, go go commando!
```