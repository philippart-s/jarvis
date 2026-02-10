# 🛠️ Load environment variables 🛠️
source ../.env

clear

bat -P -r 10:11 $(basename "$0")

read -n 1 -p "Press any key to continue"

# Run native executable with AI call 🚀
jarvis-rag rag "Quelles sont les meilleures rillettes du monde?"