# 🛠️ Load environment variables 🛠️
source ../.env

clear

bat -P -r 11: $(basename "$0")

read -n 1 -p "Press any key to continue"

cd ..
# Add LangChain4J extension
quarkus ext add io.quarkiverse.langchain4j:quarkus-langchain4j-agentic:1.10.0