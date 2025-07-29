# 🛠️ Load environment variables 🛠️
source .env

clear

bat -P -r 8: $(basename "$0")

# Run native executable with AI call 🚀
jarvis-ai "Quel est le programme du JUG Summer Camp le 5 septembre 2025 à 17h50 ?"
