# Discover Picocli

Here you can find the different steps to create your first CLI in Java with the [Picocli](https://picocli.info/) Framework.
The main steps of the project are stored each in a separate git branch chronological named with numbers.

## 01-🎉-Init-Project
 
This step is the result of the command `quarkus create cli fr.wilda.picocli:jarvis:0.0.1-SNAPSHOT --no-wrapper`  
To run the project: `quarkus dev` 

## 02-🔗-ovhcloud-sdk

This step is to create a service to call the OVHcloud API using an _SDK_.

## 03-🤖-create-jarvis

This step is to init the Jarvis CLI.

## 04-☁️-add-ovhcloud-command

This step is to add the OVHcloud API call to display the MKS and account details for a public cloud project.  
To test this part: `jarvis ovhcloud -m -k`.

## 05-📦-package

This step is to package in an executable JAR the CLI: `quarkus build`.  
To use it:
  - `java -jar ./target/quarkus-app/quarkus-run.jar Stéphane`, `java -jar ./target/quarkus-app/quarkus-run.jar ovhcloud -m -k`
  - `./jarvis.sh ovhcloud -m -k`

## 06-🚀-graalvm

> If needed, set the `GRAALVM_HOME` environment variable: `GRAALVM_HOME=/Users/sphilipp/local-bin/graalvm-jdk-21.0.2+13.1/Contents/Home`  
> ⚠️ if your `/tmp` is mounted as `noexec`: change the `java.io.tmpdir` to a folder with `exec` permission by adding the following property on your build command: `-Dquarkus.native.additional-build-args=-Djava.io.tmpdir=$HOME/tmp` or use the experimental building feature: `quarkus build --native -Dquarkus.native.container-build=true` ⚠️

This step is to build a native executable with GraalVM: `quarkus build --native`.  
To use it: 
  - `./target/jarvis-0.0.1-SNAPSHOT-runner ovhcloud -m -k`/
  - `jarvis ovhcloud -m`

## 07-🤖-add-ai

>**⚠️ You need to set the following environment variables: ⚠️**
>  - `QUARKUS_LANGCHAIN4J_MISTRALAI_BASE_URL` with the API URL of Mixtral model. 
>  - `OVH_AI_ENDPOINTS_ACCESS_TOKEN` with your OVHcloud AI Endpoint API key (see https://endpoints.ai.cloud.ovh.net/)

This step is to add the OVhcloud AI Endpoint to the Jarvis CLI.  
To test it:
 - create the new binary: `quarkus build --native`
 - test the new CLI: `./target/jarvis-0.0.1-SNAPSHOT-runner "Who are you?"`
 - jarvis "Who are you?"

## 08-❤️-ai-sentiment-analysis

This step is to add the sentiment analysis on the Jarvis CLI with AI Endpoints.  
To use it: `jarvis ovhcloud -s "I'm happy to be here"`.

## 09-📜-use-file

This step is to analyse the sentiment in a complete text file.  
To test it: 'jarvis ovhcloud -f <path>/src/main/resources/fun-java-poem.txt'
