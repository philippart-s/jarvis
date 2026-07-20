# 🛠️ Load environment variables 🛠️
source ../.env

clear

bat -P -r 11:12 $(basename "$0")

read -n 1 -p "Press any key to continue"

cd ..
# Make the native executable
quarkus build --native

read -n 1 -p "Press any key to continue"

clear

ls -larth ./target/jarvis-0.0.1-SNAPSHOT-runner