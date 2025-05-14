# 🛠️ Load environment variables 🛠️
source .env

clear

bat -P -r 10:11 $(basename "$0")

read -n 1 -p "Press any key to continue"

# Make the executable jar
quarkus build

read -n 1 -p "Press any key to continue"

clear

bat -P -r 19: $(basename "$0")

du -h ./target/quarkus-app