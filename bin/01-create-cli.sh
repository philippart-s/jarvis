# 🛠️ Load environment variables 🛠️
source ../.env

clear

bat -P -r 11: $(basename "$0")

read -n 1 -p "Press any key to continue"

cd ..
# Create CLI project
quarkus create cli fr.wilda.picocli:jarvis-init:0.0.1-SNAPSHOT --no-wrapper --dry-run