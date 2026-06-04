# 🛠️ Load environment variables 🛠️
source ../.env

clear

bat -P -r 8: $(basename "$0")

# Run native executable with AI call 🚀
jarvis-ai "Donne moi le programme du mardi 9 juin du Paris JUG"
