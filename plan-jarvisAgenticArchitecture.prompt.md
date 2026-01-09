# Plan : Conception Agentique pour Jarvis CLI

**TL;DR** : À partir des 4 sous-commandes existantes (`jarvis`, `ovhcloud`, `rag`, `mcp`), créer 2 nouvelles sous-commandes pour illustrer les approches **Workflow** (classification → routage → exécution) et **ReAct** (boucle autonome Think → Act → Observe).

---

## État actuel de l'application

```mermaid
flowchart TB
    subgraph CLI["Jarvis CLI existant"]
        JC["jarvis &lt;question&gt;<br/>Chat simple avec LLM"]
        OVH["jarvis ovhcloud<br/>-m: info compte<br/>-k: clusters kube"]
        RAG["jarvis rag -p path &lt;question&gt;<br/>LLM + documents"]
        MCP["jarvis mcp &lt;question&gt;<br/>LLM + MCP tools + validation humaine"]
    end
    
    subgraph Services["Services existants"]
        AIS["AIEndpointService<br/>askAQuestion()<br/>askAQuestionEvent()"]
        API["OVHcloudAPIService<br/>getMe(), getKubernetes()"]
        DL["DocumentLoader"]
        TDT["TimeAndDateTool @Tool"]
    end
```

**Composants clés réutilisables :**
- `AIEndpointService.askAQuestionEvent()` → retourne `Multi<ChatEvent>` avec `BeforeToolExecutionEvent`
- `OVHcloudAPIService` → appels REST OVHcloud
- `DocumentLoader` → ingestion documents pour RAG
- `TimeAndDateTool` → outil `@Tool` existant
- `@McpToolBox` → outils MCP déjà configurés

---

## Approche 1 : Workflow Agentique

### Concept
Le **code orchestre** les étapes. Le LLM sert uniquement à **classifier l'intention**.

### Architecture

```mermaid
flowchart TB
    subgraph Workflow["jarvis workflow &lt;question&gt;"]
        Q[Question] --> S1
        
        subgraph S1["🔍 Étape 1: Classification"]
            CLASSIFIER["IntentClassifierService<br/>@RegisterAiService"]
        end
        
        S1 --> S2
        
        subgraph S2["🔀 Étape 2: Routage"]
            SWITCH{"switch(intent)"}
        end
        
        SWITCH -->|OVHCLOUD_INFO| E1["OVHcloudAPIService.getMe()"]
        SWITCH -->|OVHCLOUD_KUBE| E2["OVHcloudAPIService.getKubernetes()"]
        SWITCH -->|RAG| E3["DocumentLoader + AIEndpointService"]
        SWITCH -->|TIME| E4["TimeAndDateTool"]
        SWITCH -->|CHAT| E5["AIEndpointService.askAQuestion()"]
        
        E1 & E2 & E3 & E4 & E5 --> S3["✅ Étape 3: Affichage"]
    end
```

### Fichiers à créer

| Fichier | Description |
|---------|-------------|
| `IntentClassifierService.java` | Interface `@RegisterAiService` qui retourne un `enum Intent` |
| `WorkflowSubCommand.java` | Sous-commande avec les 3 étapes visibles dans les logs |

### Séquence

```mermaid
sequenceDiagram
    participant U as User
    participant W as WorkflowSubCommand
    participant C as IntentClassifierService
    participant S as Services existants
    
    U->>W: "liste mes clusters"
    W->>W: LOG "🔍 Étape 1: Classification..."
    W->>C: classify(question)
    C-->>W: OVHCLOUD_KUBE
    W->>W: LOG "→ Intent: OVHCLOUD_KUBE"
    W->>W: LOG "⚙️ Étape 2: Exécution..."
    W->>S: OVHcloudAPIService.getKubernetes()
    S-->>W: résultat
    W->>W: LOG "✅ Étape 3: Résultat"
    W-->>U: affichage
```

---

## Approche 2 : Agent ReAct

### Concept
Le **LLM décide** des actions via une boucle autonome : **Think → Act → Observe → Repeat**.

### Architecture

```mermaid
flowchart TB
    subgraph Agent["jarvis agent &lt;question&gt;"]
        Q[Question] --> LOOP
        
        subgraph LOOP["🔄 Boucle ReAct"]
            THINK["🤔 THINK<br/>LLM analyse"]
            ACT["🎯 ACT<br/>Appel outil"]
            OBS["👁️ OBSERVE<br/>Résultat"]
            DEC{Fini?}
            
            THINK --> ACT --> OBS --> DEC
            DEC -->|Non| THINK
        end
        
        DEC -->|Oui| OUT[Réponse]
    end
    
    subgraph Tools["JarvisTools @ToolBox"]
        T1["getOvhcloudInfo()"]
        T2["listKubeClusters()"]
        T3["askWithRag()"]
        T4["getTimeAndDate()"]
    end
    
    ACT --> Tools
```

### Fichiers à créer

| Fichier | Description |
|---------|-------------|
| `JarvisTools.java` | Classe avec méthodes `@Tool` encapsulant les services existants |
| `AgentAIService.java` | Interface `@RegisterAiService` + `@ToolBox` + `@McpToolBox` |
| `AgentSubCommand.java` | Sous-commande utilisant `ChatEvent` pour afficher la boucle |
| `AgentChatMemoryProvider.java` | Provider pour `@MemoryId` (mémoire conversation) |

### Séquence

```mermaid
sequenceDiagram
    participant U as User
    participant A as AgentSubCommand
    participant S as AgentAIService
    participant LLM as LLM
    participant T as JarvisTools
    
    U->>A: "Combien de clusters et quelle heure?"
    A->>S: chat(sessionId, question)
    
    rect rgb(255,250,240)
        Note over LLM: 🤔 THINK
        LLM->>LLM: "Je dois lister les clusters"
        LLM-->>S: tool_call: listKubeClusters
    end
    
    rect rgb(240,255,240)
        Note over S,T: 🎯 ACT
        S->>T: listKubeClusters()
        T-->>S: "2 clusters"
    end
    
    rect rgb(240,248,255)
        Note over S: 👁️ OBSERVE
        S->>LLM: observation
    end
    
    rect rgb(255,250,240)
        Note over LLM: 🤔 THINK
        LLM->>LLM: "Maintenant l'heure"
        LLM-->>S: tool_call: getTimeAndDate
    end
    
    rect rgb(240,255,240)
        S->>T: getTimeAndDate()
        T-->>S: "14:30"
    end
    
    rect rgb(240,248,255)
        S->>LLM: observation
    end
    
    LLM-->>S: "Vous avez 2 clusters. Il est 14h30."
    S-->>A: stream
    A-->>U: affichage
```

---

## Comparaison

| Aspect | Workflow | ReAct |
|--------|----------|-------|
| **Contrôle** | Code | LLM |
| **LLM sert à** | Classifier | Décider + Agir |
| **Étapes** | Fixes, visibles | Dynamiques |
| **Flexibilité** | Cas prévus | Gère l'imprévu |
| **Tokens** | ~1 appel | N appels (boucle) |

---

## Steps

1. **Créer** `IntentClassifierService.java` - enum Intent + méthode classify()
2. **Créer** `WorkflowSubCommand.java` - 3 étapes avec logs
3. **Créer** `JarvisTools.java` - @Tool wrappant les services existants
4. **Créer** `AgentAIService.java` - @RegisterAiService + @ToolBox + @McpToolBox
5. **Créer** `AgentSubCommand.java` - utilisant ChatEvent pour verbose
6. **Créer** `AgentChatMemoryProvider.java` - pour @MemoryId
7. **Modifier** `JarvisCommand.java` - ajouter les 2 nouvelles subcommands
8. **Modifier** `TimeAndDateTool.java` - rendre getTimeAndDate() public

---

## Further Considerations

1. **Validation humaine pour ReAct ?** Comme dans `McpSubCommand`, ajouter une option pour valider chaque outil avant exécution.
2. **Limite d'itérations ReAct ?** Ajouter un paramètre `--max-iterations` pour éviter les boucles infinies.
3. **Mode verbose par défaut ?** Pour la démo, afficher systématiquement Think/Act/Observe ou seulement avec `-v`.

