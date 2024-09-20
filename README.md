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

  