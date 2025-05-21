# 🛠️ Load environment variables 🛠️
source .env

clear

bat -P -r 8: $(basename "$0")

# Run native executable with AI call 🚀
jarvis-ai "Quel est le programme du Lava JUG le 22 mai ?"
