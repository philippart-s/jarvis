# 🛠️ Load environment variables 🛠️
source .env

clear

bat -P -r 10:11 $(basename "$0")

# Run native executable with AI call 🚀
jarvis-ai-sentiment ovhcloud -s "I'm happy to be here"