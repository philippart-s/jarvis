# 🛠️ Load environment variables 🛠️
source ../.env

clear

bat -P -r 10:11 $(basename "$0")

read -n 1 -p "Press any key to continue"

# Run native executable with AI call 🚀
jarvis-ai "Explique moi ce qu'est Riviera Dev"

#../target/jarvis-0.0.1-SNAPSHOT-runner "Explique moi ce qu'est Riviera Dev"