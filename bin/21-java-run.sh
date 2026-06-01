# 🛠️ Load environment variables 🛠️
source ../.env

clear

bat -P -r 8:9 $(basename "$0")

# Run the executable jar
java -jar ../target/quarkus-app/quarkus-run.jar "Riviera Dev"
echo ""

read -n 1 -p "Press any key to continue"

clear

bat -P -r 18: $(basename "$0")

java -jar ../target/quarkus-app/quarkus-run.jar ovhcloud -mk
