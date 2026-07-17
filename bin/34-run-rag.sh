# 🛠️ Load environment variables 🛠️
source ../.env

clear

bat -P -r 10:11 $(basename "$0")

read -n 1 -p "Press any key to continue"

# Run native executable with AI call 🚀
jarvis-rag rag -p "../src/main/resources/rag" "Donne moi le programme du jeudi 12 février"

#../target/jarvis-0.0.1-SNAPSHOT-runner "Donne moi le programme du jeudi 12 février"