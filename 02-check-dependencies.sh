# 🛠️ Load environment variables 🛠️
source .env

clear

bat -P -r 10:11 $(basename "$0")

read -n 1 -p "Press any key to continue"

# Check Picocli extension version
mvn dependency:tree -Dincludes=info.picocli