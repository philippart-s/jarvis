# 🛠️ Load environment variables 🛠️
source ../.env

clear

bat -P -r 10:11 $(basename "$0")

read -n 1 -p "Press any key to continue"

# Run the native executable
../target/jarvis-0.0.1-SNAPSHOT-runner ovhcloud -mk

# B plan
#jarvis-api ovhcloud -mk