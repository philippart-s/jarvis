# 🛠️ Load environment variables 🛠️
source .env

clear

bat -P -r 10:11 $(basename "$0")

read -n 1 -p "Press any key to continue"

# Run the executable jar, why Java version? See: https://github.com/quarkiverse/quarkus-langchain4j/issues/1490
java -jar ./target/quarkus-app/quarkus-run.jar "Quel est le programme du JUG Summer Camp le 5 septembre 2025 ?"
echo ""