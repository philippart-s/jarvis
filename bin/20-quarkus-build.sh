# 🛠️ Load environment variables 🛠️
source ../.env

clear

bat -P -r 11:12 $(basename "$0")

read -n 1 -p "Press any key to continue"

cd ..
# Make the executable jar
quarkus build

read -n 1 -p "Press any key to continue"

clear

bat -P -r 20: ./bin/$(basename "$0")

du -h ./target/quarkus-app