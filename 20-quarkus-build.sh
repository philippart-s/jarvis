# 🛠️ Load environment variables 🛠️
source .env

# Deactivate warning du to Java 24
export JDK_JAVA_OPTIONS='--add-opens java.base/java.lang=ALL-UNNAMED --enable-native-access=ALL-UNNAMED'

clear

bat -P -r 13:14 $(basename "$0")

read -n 1 -p "Press any key to continue"

# Make the executable jar
quarkus build

read -n 1 -p "Press any key to continue"

clear

bat -P -r 20: $(basename "$0")

du -h ./target/quarkus-app