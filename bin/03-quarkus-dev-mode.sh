# 🛠️ Load environment variables 🛠️
source ../.env

clear

bat -P -r 11: $(basename "$0")

read -n 1 -p "Press any key to continue"

cd ..
# Run Quarkus dev mode
quarkus dev --clean