# 🛠️ Load environment variables 🛠️
source .env

clear

bat -P -r 10:11 $(basename "$0")

read -n 1 -p "Press any key to continue"

# Add Easy RAG extension
quarkus ext add io.quarkiverse.langchain4j:quarkus-langchain4j-openai:$LANGCHAIN4J_VERSION