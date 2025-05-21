# 🛠️ Load environment variables 🛠️
source .env

clear

bat -P -r 10:11 $(basename "$0")

read -n 1 -p "Press any key to continue"

# Run the executable jar
java -jar ./target/quarkus-app/quarkus-run.jar "Quel est le programme du Lava JUG le 22 mai ?"
echo ""

