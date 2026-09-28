## Architecture Overview

### ASCII Diagram

```
┌──────────────────┐        ┌──────────────────┐        ┌──────────────────────────────┐
│   Frontend       │ -----> │  Backend APIs    │ -----> │   GenAI Providers (LLM,      │
│ (Next.js UI)     │        │ (Spring Boot)    │        │   Embeddings, ReRanking APIs)│
└──────────────────┘        └────────┬─────────┘        └──────────────────────────────┘
                                     │                         
                                     │                          
                                     │                
                                     ▼
                            ┌──────────────────┐
                            │   genai-db       │
                            │ (Flyway schema)  │
                            └──────────────────┘

```

### Module Roles & Interactions

- **`genai-fe` (Next.js + React)**  
  - Renders the Documents/Chat split view.  
  - Simulates semantic search, chat interactions, and agent configuration.  
  - Call backend APIs for document retrieval, embeddings, and agent orchestration once `genai-be` endpoints exist.

- **`genai-be` (Spring Boot)**  
  - Will expose REST endpoints such as:
    - `GET /api/documents?query=...` → Trigger RAG pipeline + vector similarity search.  
    - `POST /api/chat/{agentId}` → Stream LLM responses.  
    - `POST /api/agents` → Create/update agent settings.  
  - Hosts RAG orchestration, embedding generation jobs, vector DB connectors, and agent runtime logic.  
  - TODO: define authentication, caching, streaming protocol, and fault handling.

- **`genai-db` (Flyway)**  
  - Manages relational schema migrations for:
    - Document metadata tables and external storage references.  
    - Agent configuration tables (LLM selection, temperatures, behaviors).  
    - Embedding/vector metadata (links to external vector DB or hybrid storage).  
  - TODO: add migrations for chat transcripts, usage analytics, and RAG pipeline audit logs.

### Browser-only GenAI Usage (current state)

- Uses mocked semantic search: text input, debounce, spinner, filtered list stored in React state.  
- Chat panel stores messages locally, tagging each message with `userId`, `createdAt`, and agent metadata.  
- Agent settings modal lets users select/clone agents, edit fields (LLM, embeddings model, reranker, temperature, max tokens, behavior) and persists to local state.  
- No API calls are made; all GenAI actions are simulated until backend endpoints exist.



