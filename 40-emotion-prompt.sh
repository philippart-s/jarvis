# 🛠️ Load environment variables 🛠️
source .env

clear

bat -P -r 8:9 $(basename "$0")

# Run native executable with AI call 🚀
jarvis-ai-sentiment sentiment -s "I'm happy to be here"

#./target/jarvis-0.0.1-SNAPSHOT-runner sentiment -s "I'm happy to be here"