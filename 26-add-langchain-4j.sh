# 🛠️ Load environment variables 🛠️
source .env

clear

bat -P -r 10:11 $(basename "$0")

read -n 1 -p "Press any key to continue"

# Add LangChain4J extension
quarkus ext add io.quarkiverse.langchain4j:quarkus-langchain4j-mistral-ai:1.4.2