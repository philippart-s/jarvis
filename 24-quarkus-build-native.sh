# 🛠️ Load environment variables 🛠️
source .env

# Deactivate warning du to Java 24
export JDK_JAVA_OPTIONS='--add-opens java.base/java.lang=ALL-UNNAMED --enable-native-access=ALL-UNNAMED'

clear

bat -P -r 13: $(basename "$0")

read -n 1 -p "Press any key to continue"

# Make the native executable
quarkus build --native