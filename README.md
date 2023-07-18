# Discover Picocli

Here you can find the different steps to create a first CLI in Java with the [Picocli](https://picocli.info/) Framework.
The main steps of the project are store each in a separate git branch chronological named with numbers.

## 🎉 - 01-Init project
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

## 💻 - Discover dev mode
  - all the resulted source code will be find in the branch `02-💻-Discover-dev-mode`
  - launch quarkus in _dev mode_: `quarkus dev`
```bash
quarkus dev
[INFO] Scanning for projects...
[INFO] ...
Listening for transport dt_socket at address: 5005
__  ____  __  _____   ___  __ ____  ______ 
 --/ __ \/ / / / _ | / _ \/ //_/ / / / __/ 
 -/ /_/ / /_/ / __ |/ , _/ ,< / /_/ /\ \   
--\___\_\____/_/ |_/_/|_/_/|_|\____/___/   
2023-07-18 17:05:45,869 INFO  [io.quarkus] (Quarkus Main Thread) discover-picocli 0.0.1-SNAPSHOT on JVM (powered by Quarkus 3.2.0.Final) started in 0.495s. 
2023-07-18 17:05:45,879 INFO  [io.quarkus] (Quarkus Main Thread) Profile dev activated. Live Coding activated.
2023-07-18 17:05:45,879 INFO  [io.quarkus] (Quarkus Main Thread) Installed features: [cdi, picocli]
Hello picocli, go go commando!
2023-07-18 17:05:45,916 INFO  [io.quarkus] (Quarkus Main Thread) discover-picocli stopped in 0.003s

--
Tests paused
Press [space] to restart, [e] to edit command line args (currently ''), [r] to resume testing, [o] Toggle test output, [:] for the terminal, [h] for more options>
```
  - press _e_ and input a string:
```bash
Listening for transport dt_socket at address: 5005
__  ____  __  _____   ___  __ ____  ______ 
 --/ __ \/ / / / _ | / _ \/ //_/ / / / __/ 
 -/ /_/ / /_/ / __ |/ , _/ ,< / /_/ /\ \   
--\___\_\____/_/ |_/_/|_/_/|_|\____/___/   
2023-07-18 17:21:33,467 INFO  [io.quarkus] (Quarkus Main Thread) discover-picocli 0.0.1-SNAPSHOT on JVM (powered by Quarkus 3.2.0.Final) started in 0.516s. 

2023-07-18 17:21:33,478 INFO  [io.quarkus] (Quarkus Main Thread) Profile dev activated. Live Coding activated.
2023-07-18 17:21:33,478 INFO  [io.quarkus] (Quarkus Main Thread) Installed features: [cdi, picocli]
Hello picocli, go go commando!

2023-07-18 17:21:33,516 INFO  [io.quarkus] (Quarkus Main Thread) discover-picocli stopped in 0.003s

--
Tests paused
wilda

__  ____  __  _____   ___  __ ____  ______ 
 --/ __ \/ / / / _ | / _ \/ //_/ / / / __/ 
 -/ /_/ / /_/ / __ |/ , _/ ,< / /_/ /\ \   
--\___\_\____/_/ |_/_/|_/_/|_|\____/___/   
2023-07-18 17:21:51,705 INFO  [io.quarkus] (Quarkus Main Thread) discover-picocli 0.0.1-SNAPSHOT on JVM (powered by Quarkus 3.2.0.Final) started in 0.144s. 

2023-07-18 17:21:51,705 INFO  [io.quarkus] (Quarkus Main Thread) Profile dev activated. Live Coding activated.
2023-07-18 17:21:51,706 INFO  [io.quarkus] (Quarkus Main Thread) Installed features: [cdi, picocli]
2023-07-18 17:21:51,706 INFO  [io.qua.dep.dev.RuntimeUpdatesProcessor] (Aesh InputStream Reader) Live reload total time: 0.162s 
Hello wilda, go go commando!

2023-07-18 17:21:51,709 INFO  [io.quarkus] (Quarkus Main Thread) discover-picocli stopped in 0.001s

--
Tests paused
Press [space] to restart, [e] to edit command line args (currently ''), [r] to resume testing, [o] Toggle test output, [:] for the terminal, [h] for more options>
```
  - update the class `GreetingCommand`:
```java
package fr.wilda;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "greeting", mixinStandardHelpOptions = true)
public class GreetingCommand implements Runnable {

    @Parameters(paramLabel = "<name>", defaultValue = "picocli",
        description = "Your name.")
    String name;

    @Override
    public void run() {
        System.out.printf("👋, %s!\n", name);
    }
}
```
  - test again the CLI:
```bash
__  ____  __  _____   ___  __ ____  ______ 
 --/ __ \/ / / / _ | / _ \/ //_/ / / / __/ 
 -/ /_/ / /_/ / __ |/ , _/ ,< / /_/ /\ \   
--\___\_\____/_/ |_/_/|_/_/|_|\____/___/   
2023-07-18 17:25:21,511 INFO  [io.quarkus] (Quarkus Main Thread) discover-picocli 0.0.1-SNAPSHOT on JVM (powered by Quarkus 3.2.0.Final) started in 0.139s. 

2023-07-18 17:25:21,511 INFO  [io.quarkus] (Quarkus Main Thread) Profile dev activated. Live Coding activated.
2023-07-18 17:25:21,511 INFO  [io.quarkus] (Quarkus Main Thread) Installed features: [cdi, picocli]
2023-07-18 17:25:21,512 INFO  [io.qua.dep.dev.RuntimeUpdatesProcessor] (Aesh InputStream Reader) Live reload total time: 0.440s 
👋, wilda!

2023-07-18 17:25:21,515 INFO  [io.quarkus] (Quarkus Main Thread) discover-picocli stopped in 0.000s

--
Tests paused
Press [space] to restart, [e] to edit command line args (currently ''), [r] to resume testing, [o] Toggle test output, [:] for the terminal, [h] for more options>
```

## 📦 - Package the CLI
  - launch the _build_ Quarkus command: `quarkus build`
  - test the packaged CLI:
```bash
java -jar ./target/quarkus-app/quarkus-run.jar wilda
__  ____  __  _____   ___  __ ____  ______ 
 --/ __ \/ / / / _ | / _ \/ //_/ / / / __/ 
 -/ /_/ / /_/ / __ |/ , _/ ,< / /_/ /\ \   
--\___\_\____/_/ |_/_/|_/_/|_|\____/___/   
2023-07-18 17:58:13,619 INFO  [io.quarkus] (main) discover-picocli 0.0.1-SNAPSHOT on JVM (powered by Quarkus 3.2.0.Final) started in 0.180s. 
2023-07-18 17:58:13,636 INFO  [io.quarkus] (main) Profile prod activated. 
2023-07-18 17:58:13,636 INFO  [io.quarkus] (main) Installed features: [cdi, picocli]
👋, wilda!
2023-07-18 17:58:13,680 INFO  [io.quarkus] (main) discover-picocli stopped in 0.005s
```
  - create as sh file named _greeting.sh_:
```bash
#!/bin/bash

java -Dquarkus.log.console.enable=false -jar ../../target/quarkus-app/quarkus-run.jar $1
```
  - test the CLI:
```bash
./greeting.sh wilda
👋, wilda!
```