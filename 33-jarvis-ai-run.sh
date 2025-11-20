# 🛠️ Load environment variables 🛠️
source .env

clear

bat -P -r 10:11 $(basename "$0")

read -n 1 -p "Press any key to continue"

# Run native executable with AI call 🚀
jarvis-ai "Tell me more about DevFest Paris"

#./target/jarvis-0.0.1-SNAPSHOT-runner "Tell me more about Anvers"