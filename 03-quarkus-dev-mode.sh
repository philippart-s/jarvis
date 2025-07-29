# 🛠️ Load environment variables 🛠️
source .env

clear

bat -P -r 10: $(basename "$0")

read -n 1 -p "Press any key to continue"

# Deactivate warning du to Java 24
export JDK_JAVA_OPTIONS='--add-opens java.base/java.lang=ALL-UNNAMED --enable-native-access=ALL-UNNAMED'

# Run Quarkus dev mode
quarkus devv