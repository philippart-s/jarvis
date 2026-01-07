# 🛠️ Load environment variables 🛠️
source ../.env

clear

bat -P -r 10:11 $(basename "$0")

read -n 1 -p "Press any key to continue"

# Run native executable without MCP tools 🚀
jarvis-mcp "Donne moi la liste de mes projet public cloud OVHCloud"

read -n 1 -p "Press any key to continue"

clear

bat -P -r 19:20 $(basename "$0")

# Run native executable with MCP tools 🚀
jarvis-mcp mcp "Donne moi la liste de mes projet public cloud OVHCloud"