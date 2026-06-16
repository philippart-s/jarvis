# 🛠️ Load environment variables 🛠️
source ../.env

clear

bat -P -r 10:11 $(basename "$0")

read -n 1 -p "Press any key to continue"

# Run native executable without MCP tools 🚀
jarvis-mcp "Donne moi la liste de mes projet public cloud OVHCloud"
#../target/jarvis-0.0.1-SNAPSHOT-runner "Donne moi la liste de mes projet public cloud OVHCloud"