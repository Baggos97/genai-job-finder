## GenAI for Developers #25.11 – Demo Project

This repository hosts “The demo project we will develop during GenAI for Developers #25.11,” a showcase that demonstrates how modern GenAI techniques—LLMs, embeddings, vector databases, Retrieval-Augmented Generation (RAG), and autonomous agents—can be orchestrated across a full-stack application. The goal is to iteratively build a reference implementation that students can extend during the bootcamp.

### Module Overview

| Module | Status | Description |
| --- | --- | --- |
| `genai-fe` | ✅ active | Browser-only Next.js/React UI that already renders the Documents + Chat workspace. It demonstrates in-browser GenAI usage by simulating semantic search, chat state, agent configuration, and message rendering without relying on server APIs. |
| `genai-be` | ⏳ TODO | Spring Boot backend that will progressively expose REST endpoints, orchestrate GenAI services, and host RAG pipelines. TODO: flesh out data models, RAG services, auth, and observability. |
| `genai-db` | ⏳ TODO | Flyway-based database migration module managed via Maven. TODO: define schemas for documents, embeddings metadata, agent configs, and vector-store pointers. |

### Technology Stack

- **Frontend**: Next.js (Pages Router), React, plain JavaScript, CSS Modules, `next/font`.
- **Backend**: Spring Boot, Java 17+ (TODO exact version), standard layered architecture (controllers → services → repositories).
- **Database**: Flyway migrations, relational storage (TODO pick engine) plus integration hooks for Vector DB / embeddings store.
- **GenAI Concepts**: LLMs, embeddings, reranking models, vector DB indexing, RAG pipelines, configurable agents, streaming chat UX.

### Directory Structure

```
genai-25-11/
├── genai-fe/   # Next.js browser-only frontend
├── genai-be/   # Spring Boot backend (TODO build progressively)
├── genai-db/   # Flyway migration module (TODO expand)
└── .cursor/    # Cursor-specific docs (context, rules, architecture)
```

### How the Modules Fit Together

1. **Frontend (`genai-fe`)** renders the user experience: document exploration, chat interactions, and agent configuration. It currently mocks semantic search and chat behaviors directly in the browser.
2. **Backend (`genai-be`)** will expose APIs to:
   - Submit document filters to RAG pipelines.
   - Run embedding generation and vector similarity searches.
   - Manage agent profiles, LLM selections, and conversation state.
3. **Database (`genai-db`)** will version-control schema changes for:
   - Document metadata and storage pointers.
   - Embedding/vector indices (or references to managed Vector DBs).
   - Agent configurations and audit logs.

Data will eventually flow from the browser to backend APIs, which will orchestrate embedding + vector DB queries and return grounded responses. The frontend will remain API-only (no server components) and focus on presenting RAG results, agent settings, and chat transcripts.

### Future RAG & Agent Logic Locations

- **Embedding + Vector retrieval**: `genai-be` service layer (e.g., `rag/EmbeddingService`, `rag/VectorStoreClient`) with persistence metadata managed via `genai-db`.
- **Agent orchestration**: `genai-be` (e.g., `agents/AgentRuntime`, `agents/PolicyService`) referencing capabilities defined in DB migrations.
- **Frontend adapters**: `genai-fe` will consume backend endpoints via REST/GraphQL (TODO final choice). Until backends exist, browser-side mocks simulate GenAI behavior.

> **TODO**: Document concrete API contracts, vector store provider, auth strategy, and deployment topology once decisions are made.

