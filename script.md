## ## Cover

Hello, welcome everyone!

## Thank you

I'm very proud to be here.
Thank you for coming to this session, and thank you to the organizers for selecting my conference.


## Me

Quickly, 

I'm Stéphane Philippart I'm a developer advocate at OVHcloud: a French cloud provider.

I've been a Java developer for about 20 years, I've started to use Java when it was 1.0.2.
I'll let you calculate my age.

I'm the co-creator of a great meetup in Tours, just 1 hour train from Paris.

And, of course, as you can hear, I'm French so forgive my terrible accent.


## Picocli

Just few slides before coding.

If you are looking to create a CLI with Java, there is a high chances that you will come across Picocli, it is the most represented framework to make a CLI with Java.

Not only in pure Java but also in Kotlin, Scala and Groovy.

With Picocli it's a lot of annotations to simplify the writing, if you left Java 15 years ago: Java is like the others now we have annotations and syntactic sugar.
We just make arobas and then it works by magic, it's a big summary, obviously.

I will use Quarkus too.
Who knows Quarkus, who uses Quarkus in production?
Quarkus mantra is: subatomic, supersonic, Java.
What I'm saying is like your Java applications take a lot of vitamines! 
It's very efficient for cloud native development.
But we're not going to use it on the cloud native part we're going to use if for the developer experience.

You'll see it's quite magical and what I propose is, at the end of this talk, to know if I won my bet: did I make you a CLI in Java?

## Demo

Enough talking, let's move on the code part.

## 01-create-cli.sh

Before beginning to create the CLI, we need to create the Quarkus project.
To do this, we will use the Quarkus CLI to generate a new project.
And guess what? The Quarkus CLI is written with Picocli!

As you see when you run the CLI you set the artifactId, groupId, and version in the Maven style.
The cli option is to indicate to Quarkus CLI that we want to create a CLI application.
You see also that we want to use the `quarkus-rest-client-jackson` extension.
This extension allows us to use the Jackson library to serialize and deserialize JSON data.
It's a well known library for JSON processing in Java.

A Quarkus extension is a way to add functionality through libraries to a Quarkus application.
You can see that like the Spring Boot starters, but for Quarkus.

Don't pay attention to the `--dry-run` option, it's to avoid creating the project because I already have the project created.

As you see, the created project is a Maven project.

Don't be afraid by the other files with underscore or the bash scripts there are here for my next steps of the demonstration.

You can see the `pom.xml` file in the root of the project.
You can see also the `src/main/java` directory where the Java source code is located.
You can see also the `src/main/resources` directory where the configuration files are located.

A sample CLI application is created, named `GreetingsCommand`.

So, what is a CLI for Picocli?
As you see this is a Java class that implements the Runnable interface.
The class is annotated with the `@Command` annotation.
With this annotation you can set the name of the CLI. 
The name is the name that you type in the terminal to run the CLI.
The `mixinStandardHelpOptions` is a boolean that indicates if you want to add the standard help to the CLI when you type in the terminal `--help` or `-h`.
The help is build by using some other parameters from other annotations from Picocli.
One of them is the `@Parameters` annotation.
This annotation is used to define the parameters of the CLI. 
A parameter is not explicitly named in the terminal, it's a value that you pass to the command.
In this example, this an optional parameter, because it has a default value, as you see: `picocli`.
As I mentioned previously, the two options `paramLabel` and `description` are used to define the help message.

And at the end, as the `Runnable` interface is implemented, the `run` method is called when the CLI is executed.
The `run` method is where you put the logic of your CLI.
In the example, the `run` method prints a message to the terminal with the value of the type entered.

Ok it's time to test the CLI.

## 03-quarkus-dev-mode.sh

As I said previously, Quarkus is design with a cloud first approach.
It's not the point that I want to develop here.
I said also that Quarkus comes with a great developer experience.
What I want to say here is what is called the dev mode.

By run the `quarkus dev` command, you start the application in dev mode.
In this mode, you have a lot of features like :
- Live reload
- Dev UI
- Hot deployment
- Debugging
- Testing

Today we will just use the live reload feature.

##  play with the CLI : `--help`, `"xxx!!"`

As you see when we launched the CLI, as I didn't enter any parameter, the default value `picocli` was used.

The Quarkus dev mode allows you yo test the CLI by pressing the `e` key. 
In this mode, it's as you are in a terminal and you type the CLI name, greetings, plus space.
Let's try to use the `--help` option to see what we can do with the CLI.

Yep, the help is displayed and display bow to use the CLI with its options and parameters.
And as you see, the help message for the `name` parameter is build with the values of the two options of the `@Paramater` annotation.

I can now enter a name and press enter to see the greeting message.

## 04-pom-jarvis-sdk-dep

It's time to create our own assistant.
To do this I want to create a CLI named Jarvis.
The goal of this CLI will be to interact with the OVHcloud API to get information about my services.

The first step is to create a sort of SDK that will bu used by the CLI.
In my case I will just create a package named `sdk` but in the real world, it could be a separate project.

And the first thing to do is to add a dependency to the `pom.xml`?
This dependency is a small Java project that I wrote to make easier the serialisation of Java objects.
As it's note the purpose of this talk I will not go into details.

## 05-props-ovh-env && 06-props-rest-client 

It's time to add some configuration to our project.
First, I add OVHcloud properties to identify my public cloud project from which I want to get information.
Then, I add the rest client configuration to connect to the OVHcloud REST API.
You can see that I set the root URL of the API.
I could choose to set this parameter in the Java class also.

## 07-OVHcloudAPIService-annot && 08-OVHcloudAPIService-endpoints

Now, I can create my first service, OVHcloudAPIService, to call the OVHcloud REST API.
First, I set the `@RegisterRestClient` annotation to register the service as a REST client for Quarkus beans.
It will allow me to use the service in my application.
Then, I add some header parameters, they can be static like the `Content-Type` parameter or dynamic like the `X-Ovh-Application` parameter which takes its value from the application.properties file.

And last step, I add some endpoints call to the REST API.
For example, the `getMe` method will call the `/me` endpoint to get the information about my OVHcloud account.
You can see other methods that set some parameters to the request by injecting them in the called URL.

## 09-jarvis-cli-class-annot && 10-jarvis-cli-logger && 11-jarvis-cli-name-param && 12-jarvis-hello

Now I can create my first command, JarvisCommand, to interact with the OVHcloud REST API.
First, I set the `@Command` annotation to register the command in the application.

Perhaps you have noticed that I implement the `Callable` interface instead of the `Runnable` interface.
It allows me to return a value from the command.
It's useful when you want to return a status code from the command.
For example, you can return 0 if the command is successful and 1 if the command fails.

Then, I add a logger to the command to log some information.
After that, I add the same parameter as the previous command.
And last step, I print the message.

Let's test the command.

As you see there is an error message.
It's a "normal" error message and Quarkus try to help us to debug the problem.
It indicates that it's trying to find a bean annotated with `@TopCommand` but it can't find it.
Under the hood, we have created 2 beans with the `@Command` annotation.
Quarkus doesn't know which one to use as the top command.
To fix this, I need to add the `@TopCommand` annotation to my main command, which is `JarvisCommand`.

Once I've fixed the problem, I can test again the command.

As you see the new message is printed.

At this point, I can delete the `GreetingCommand` class because it's not used anymore.
Let's delete it.

## 13-ovh-cli-class-annot && 14-ovh-cli-logger && 15-ovh-cli-rest-client && 16-ovh-cli-ovh-stuff && 17-ovh-cli-options && 18-ovh-cli-me && 19-ovh-cli-kube

At this point we are ready to create the `ovhcloud` subcommand. 
Actually we could put the OVH REST API call in the `JarvisCommand` class, but it's better to create a subcommand to keep the code organized.
To do this we will create a new class `OVHcloudSubCommand` and annotate it with `@Command` like we did for `JarvisCommand`.
I add also the same logger as in `JarvisCommand`.
I use the service created by using the `@RestClient` annotation to inject the service in the class.
Here I add some parameters about OVHcloud to do the call to the OVH REST API.
This time I will not use a parameter but I will use options to pass the arguments to the command.
Unlike parameters an option is explicitly defined by the user.
I use the `@Option` annotation to define it.
As you can see you can define a short name and a long name for the option.
If this option is enter then the boolean value for the field is set to true and false otherwise.
The first option, --me, is to get information about my account.
The second option, --kube, is to get  information about my kubernetes cluster.

Now I've just to add the "business" code that will call the service and print the result.
The first one display information about my account.
The second one display information about my kubernetes cluster.

The last step is to add the subcommand to the `JarvisCommand` class.
To do this I add the `subcommand` option with the `OVHcloudSubCommand` class as value.

We can test this subcommand.
I can display again the help, anf you see the new subcommand `ovhcloud`.
If I display again the help for the `ovhcloud` subcommand, you see the new options `--me` and `--kube`.
And use this options display the information about my account and my kubernetes cluster.

## 20-props-logs-prod

Ok, now we have developed our features it's time to leave VSCode and go to the terminal to use the CLI.
The first thing to do is to configure the logs to be less verbose.

## 21-quarkus-build

Now I can create the executable JAR thanks to Quarkus CLI with the `quarkus build` command.
As you can see the size of the created JAR is around 17 Mb, which is not very big.

## 22-java-run

Once the JAR is created I can use my CLI in my terminal.
And as you see, it works as expected.

Yes, I agree with you, using the `java -jar` command is not very user-friendly.

And the other problem is that you must have a valid Java installation on your system.
So to the 17 Mb of the CLI you have to add the size of the Java installation.
It's not what I call a CLI, a CLI must be easy to use, install and distribute.

## 24-quarkus-build-native

To create a real executable I will use GraalVM from Oracle.
It allows you to create native executable from your Java applications.

As it take some time to create the native application, I run the command now and I will explain during the execution what is happening.

So, the first thing to know with GraalVM is that to reduce the size of the executable it will remove all the classes that are not used.
And some features are not available, for example, the Java reflection.
You must know these limitations before using GraalVM.
Or you can, like me, use Quarkus to build your native applications.
Quarkus will help you to build you application by adding the `--native` option to the `quarkus build` command.
And as you see it sets for us a lot of options to the command line, obviously you can still override them if you want.

Another important point with GraalVM is that the generated application is an executable for a specific platform.
The targeted platform is the one used to build the application and not only the operating system but also the architecture.
For example, for my CLI I built it on a Mac Book Air M3, so my executable will only run on a Mac Book ARM, not Intel and obviously not on a Linux or Windows machine.

There is a tip to create the linux executable, you can use the GraalVM container image to build your application.
In short words, it creates a linux container and builds your application inside it.

Let's look at the size of the generated binary: it's 61 Mb.
Indeed it's more than 17 Mb but it's less than if we had to install a JDK.

## 25-jarvis-api-run

It's time to test our new CLI.
As you perhaps see it's faster, you have still the duration of the REST API call but the startup time is reduced.

I have a B plan, I have created the executable, so I can use it to show you the result with the binary executable.

## 26-add-langchain-4j

Ok, we are in 2025 and a talk must have a demo with AI.
In my case, I want to create a real assistant thanks to AI.

To add AI I will use LangChain4J, in few words LangChain4j is one of the most famous Java library to interact with AI.
It's inspired from the Python and Javascript LangChain library.
But be careful, this not the same team that created LangChain, so you can have some different functionalities.
But the goal is the same: to simplify the interaction with AI.

So let's add langchain4j to our project.
To do that, I use, again, the Quarkus CLI.

The CLI has an `ext` option to manage the wanted extensions.
Once the command is executed, the CLI will add the dependency to the `pom.xml` file.

## 27-update-props-langchain4J

Once I have added the dependency, I need to update the `application.properties` file to add the configuration for LangChain4J.
For the demo, I will use the Mistral model provided by OVHcloud, a very powerful model.
If you want to use another model, feel free to go the AI Endpoints web site and choose the one you want.

As you see there are several configurations:
The first one is the endpoint URL, the second one is the API key.
The max tokens is the maximum number of tokens that the model can generate.
To summarize a token is approximatively a word or a part of a word.
The temperature is a measure of randomness in the generated text.
The more you increase the temperature, the more random the text will be.
And then you have the model name and some logs options.

## 28-AIEndpointService-annotation && 29-OVHcloudMistral-ask-method

I can now create the interface to define the methods that will be used to interact with the AI model.
I create the interface `AIEndpointService` and add the annotation `@RegisterAiService` to it.
This annotation enable the AI Services mode from LangChain4J.
In few words, it will manage the necessary code to send the user prompt to the AI model and receive the response.

At this point I just need to add the method `askQuestion` to the interface.
This method will be used to send the user prompt to the AI model and receive the response.
As you see I have added the `@SystemMessage` and `@UserMessage` annotations to the method.
Obviously the user message is the question that the user will ask to the AI model.
You can add more details, for example here I just add the fact that the AI model must answer as a virtual assistant.
Then I add the system message to the method.
The system message is the message that will be sent to the AI model before the user message.
The goal of the system message is to set the context for the AI model.
It help the model to understand the role it should play and the style of the response it should give.

Perhaps you have noticed that the method returns a `Multi<String>`.
This is because the AI model will send the response in a stream of tokens.
The `Multi<String>` is a reactive stream that will emit each token as it is received.
This is useful to display the response in a human typing style.

## 30-jarvis-cli-question-param && 31-jarvis-cli-ai-svc && 32-jarvis-cli-ai-svc-call

Now I can update the `JarvisCommand` class to use the `AIEndpointService`.
I will rename the `name` parameter to `question` and inject the `AIEndpointService` thanks to the `@Inject` annotation.
Then I call the `askAQuestion` method of the `AIEndpointService` to get the answer from the AI model.
As the method returns a `Multi<String>`, I subscribe to it and print each token.
You can notice that I add a temporisation to display the token in a human way readable.

Now I can test Jarvis with the following question: `"Can you tell me more about Devoxx UK?"`
Ok the log format is not very readable, I will update this later but you can see that our assistant Jarvis is working and answering the question.

## 33-jarvis-ai-run

Again, I can create a native executable thanks to Quarkus and run it.

As you see, I had previously created the binary and renamed it `jarvis-ai` to not spend time on the build process.

You can see that the Jarvis assistant works and answers to the question.

## 34-token-sentiment-service && 35-sentiment-service && 36-text2emotion-method

Since I have enough time, I suggest you to discover other features of Picocli.
To do this I will add a sentiment analysis service to our application.
The first step is to add the token for the request in the `application.properties` file.
Then I can create the Java class `AISentimentService`.
This is the same approach that I used for the `OVHcloudAPIService`, I will use the RestClient extension.

Actually I don't use a conversational LLM so I can't use LangChain4J.
No matter because with AI Endpoints, use a model is as simple as calling a REST API.

I set my headers and some options, as you see, this time I set the root URL in the Java class.
Then I create the method `text2emotions` that will call the endpoint `text2emotions` of the AI Endpoints service.
And thanks to Jackson I can deserialize the response into an `EmotionEvaluation` object.

## 37-sentiment-sentiment-client && 38-sentiment-option && 39-sentiment-output && 40-sentiment-subcommand

Now I update my subcommand `OVHcloudSubCommand` to add the sentiment analysis feature.
To do this I add a new way to set an option.
This time, the option will be a string so in the terminal I must enter a text behind the `-s` option.

Let's see the help message of my subcommand.
As you see the `-s` option is now available.
Ok let's test it!

Again I can test it directly in the Quarkus dev mode or in my terminal.

## 41-file-option && 42-file-ouput && 43-emotion-file

Last example of what you can do with option with Picocli is the automatic encapsulation.
What I want now is the ability to analyse a file.
To do this I add a new File option.
And Picocli will automatically try to create a File object from the path enter in the terminal after the `-f` option.

And after I can use the file object to read the content of the file and pass it to the `text2emotions` method.

Now it's time to test it.
I have a file `sad-java-poem.txt` in my resources folder.
Again I can test it directly in the Quarkus dev mode or in my terminal.

As you see the output is sadness as expected.

## Generate completion

The last but not the least feature I want to show you is the autocompletion.
Actually, when I use a CLI, I don't want to remember all the commands and options.
I want to use the autocompletion feature.
Picocli provides a way to generate the autocompletion script for different shells.

To do this I just have to add a subcommand to JarvisCommand and OVHcloudSubCommand.
The subcommand to add id `GenerateCompletion.class`.

Again, after generating the binary I can use jarvis with autocompletion mode.
To add the autocompletion I run `jarvis -h` and as you see I have a new sub command called `generate-completion`.
If I ask to see the help on this command it will display how to activate the autocompletion by sourcing the script generated by the command.

Not even afraid I run the command, don't to that at home.

And tada my CLI has autocompletion.

## Picocli advanced

If you want to go further with Picocli.
You can manipulate more sophisticated types with Picocli, even create your own converters.
You can use more default values with dynamic values, validation and so one.
You can generate man pages, you can have syntax coloring, you can have advanced help.
Again, executables are generated for the current OS, don't forget that when you want to distribute your application.

## AI Endpoints

If you want more information about AI endpoints, here is a brief overview:
AI endpoints provides AI as a service, offering various models like Llama, Mistral, and more. 
They are made for developers, making them easy to integrate and use. 
Your data is safe and secure in our trusted cloud environment.
Additionally, these endpoints comply with standard APIs.

And for a few days it's in general availability!

## OVHcloud

If you want more information about OVHcloud, don't hesitate to ask me. 
I'll be happy to answer any questions you might have.
We can talk about our managed Kubernetes, our Databases, our AI solutions, and much more.

## Links

If you want to explore more about the topics discussed, here are some links that might be of interest.

## Thank You and feedback

Thank you for your attention. 
I hope you enjoyed the presentation and learned something new. 
You have all the information with the slides QR code and please, feel free to give me feedbacks. 
Thank you again and have a great day!
