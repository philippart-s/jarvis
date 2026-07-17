# 🛠️ Load environment variables 🛠️
source ../.env

clear

bat -P -r 8:9 $(basename "$0")

# Run native executable with MCP tools 🚀
jarvis-mcp mcp "Donne moi la liste de mes projet public cloud OVHCloud"
#../target/jarvis-0.0.1-SNAPSHOT-runner mcp "Donne moi la liste de mes projet public cloud OVHCloud"