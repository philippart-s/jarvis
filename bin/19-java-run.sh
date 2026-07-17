# 🛠️ Load environment variables 🛠️
source ../.env

clear

bat -P -r 8:9 $(basename "$0")

# Run the executable jar
java -jar ../target/quarkus-app/quarkus-run.jar "Pourquoi le ciel est bleu ?"