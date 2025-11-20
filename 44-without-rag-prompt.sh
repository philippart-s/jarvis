# 🛠️ Load environment variables 🛠️
source .env

clear

bat -P -r 8: $(basename "$0")

# Run native executable with AI call 🚀
jarvis-ai "Quel est le programme du DevFest Paris le 21 novembre à 11h10 ?"
