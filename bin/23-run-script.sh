# 🛠️ Load environment variables 🛠️
source ../.env

clear

bat -P -r 8:9 $(basename "$0")

# Run the bash script
cd ../src/main/script/ && ./jarvis.sh "Pourquoi le ciel est bleu ?"
