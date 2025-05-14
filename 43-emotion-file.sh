# 🛠️ Load environment variables 🛠️
source .env

clear

bat -P -r 8: $(basename "$0")

# Run native executable with AI call 🚀
jarvis-ai-file sentiment -f "./src/main/resources/sad-java-poem.txt"