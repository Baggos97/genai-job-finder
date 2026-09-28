# GenAI Job Finder Platform

> **GenAI for Developers #25.11** — Ctrl+Space Labs  
> A full-stack job-finder platform enhanced with Generative AI, built as part of the GenAI for Developers course.

---

## What It Does

The platform connects **candidates** looking for jobs with an **administrator** who manages job postings and reviews applicants. The GenAI layer turns it into a practical playground for:

- **Document chunking & embeddings** — job postings are automatically split into chunks and indexed using vector embeddings
- **Semantic search + reranking** — natural-language job search, not just keyword matching
- **Chat-based retrieval (RAG)** — a chat assistant answers questions using relevant job postings as context
- **Tool-using agents** — the agent can apply to a job *on behalf of the user* via a `job_apply` tool call

---

## User Roles

### 👤 Candidate (User)
- Browse all available job postings
- Search jobs using natural language (e.g. *"Senior Java developer remote in Athens"*)
- Chat with the AI assistant to get personalized recommendations
- Ask the agent to apply to a position on their behalf
- Track submitted applications

### 🛠️ Administrator
- Upload job postings (text) — automatically chunked, embedded and indexed
- View all applicants per position
- Use AI to summarize the applicant pool and order candidates by relevance

---

## GenAI Capabilities

### A) Job Posting Indexing — Chunking + Embeddings + Vector Store

When a job posting is uploaded, the system automatically:
1. Splits the description into chunks (~500 words each)
2. Computes an embedding vector for each chunk via OpenAI (`text-embedding-3-small`)
3. Stores the vectors in PostgreSQL using `pgvector`

```
Admin uploads job → split into chunks → embed each chunk → store in vector index
```

### B) Semantic Search + Reranking

Job search happens in two stages for better accuracy:
1. **Retrieve** — vector similarity search returns the top-N most relevant chunks
2. **Rerank** — Voyage AI reranking model re-orders the results by true relevance to the query

This gives significantly better results than keyword search or pure vector search alone.

### C) Chat Assistant — "Find me ideal jobs from my CV"

A chat interface where the user can describe their background and get personalised job recommendations. The agent:
- Extracts key signals from the user's message (skills, seniority, location preferences)
- Queries the semantic search endpoint
- Returns a curated list of top matches with explanations

### D) Agent Tool — `job_apply`

The chat assistant can submit a job application on the user's behalf using an agentic tool call.

**Flow:**
1. User: *"Apply to the Senior Java Developer role with this motivation: ..."*
2. Agent asks for confirmation: *"Apply to Senior Java Developer at Company X — yes or no?"*
3. User confirms
4. Agent calls the `job_apply` tool
5. Application is saved to the database
6. Agent confirms: *"Done! Application submitted. Application ID: ..."*

**Tool contract:**
```
job_apply(job_id, user_id, motivation_text) → application_id + status
```

### E) Admin AI Review — Summarize Applicants + Order by Relevance

For any job posting, the admin can trigger an AI review that:
- Generates a summary of the applicant pool (counts by seniority, key skills, highlights)
- Orders applicants by fit using a reranking model

---

## Tech Stack

| Layer | Technology |
|---|---|
| **Frontend** | Next.js 15 (React, Pages Router) |
| **Backend** | Java 21, Spring Boot 3 |
| **Database** | PostgreSQL 16 + pgvector extension |
| **Migrations** | Flyway |
| **LLM** | OpenAI (GPT-4o-mini / GPT-4.1) via REST |
| **Embeddings** | OpenAI `text-embedding-3-small` |
| **Reranking** | Voyage AI (`rerank-2.5`, `rerank-2.5-lite`) |
| **Agent loop** | Custom tool-calling loop (Spring Boot) |
| **Build** | Maven (backend), npm (frontend) |
| **Containerisation** | Docker Compose (database) |

---

## Architecture

```
┌─────────────────┐     REST      ┌──────────────────────┐
│   Next.js FE    │ ────────────► │  Spring Boot Backend  │
│  (port 3000)    │               │     (port 8080)       │
└─────────────────┘               └──────────┬───────────┘
                                             │
                        ┌────────────────────┼────────────────────┐
                        ▼                    ▼                    ▼
               ┌─────────────┐    ┌──────────────────┐  ┌─────────────────┐
               │  PostgreSQL  │    │  OpenAI API       │  │  Voyage AI API  │
               │ + pgvector  │    │  (LLM + Embed)   │  │   (Reranking)   │
               │ (port 5433) │    └──────────────────┘  └─────────────────┘
               └─────────────┘
```

### Agent Tool-Calling Loop

```
User message
     │
     ▼
 LLM call ──► tool_calls? ──Yes──► Execute tool (search / job_apply / sql)
     ▲                                        │
     └────────────────────────────────────────┘
                    (loop until no more tool calls)
     │
     ▼
Final response to user
```

---

## Project Structure

```
genai-ctr-space-labs/
├── genai-db/               # PostgreSQL + Flyway migrations
│   └── src/main/resources/db/migrations/
│       ├── V1__initial_schema.sql
│       ├── V2__seed_demo_data.sql
│       └── V3__add_application_table.sql
│
├── genai-be/               # Spring Boot backend
│   └── src/main/java/.../
│       ├── controllers/    # REST endpoints (Documents, Messages, Users)
│       ├── services/       # Business logic (AgentService, DocumentService, …)
│       ├── tools/          # Agent tools (SearchTool, JobApplyTool, SqlRunnerTool)
│       ├── models/         # Entities + DTOs
│       └── repositories/  # Spring Data JPA repositories
│
└── genai-fe/               # Next.js frontend
    └── src/pages/
        └── index.js        # Main UI (job list, semantic search, chat)
```

---

## How to Run

### 1. Database

```bash
cd genai-db
docker compose up
```

Starts PostgreSQL on port **5433** and runs Flyway migrations automatically.

### 2. Backend

```bash
cd genai-be
./mvnw spring-boot:run
```

Runs on port **8080**. Set your API keys as environment variables:

```bash
export GROQ_API_KEY=your_key_here
export OPENAI_API_KEY=your_key_here
export VOYAGE_API_KEY=your_key_here
```

### 3. Frontend

```bash
cd genai-fe
npm install
npm run dev
```

Open [http://localhost:3000](http://localhost:3000)

---

## Key API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/documents` | List all job postings (supports semantic search via `?searchText=`) |
| `POST` | `/documents` | Upload a new job posting (triggers auto-indexing) |
| `POST` | `/messages` | Send a message to the AI agent |
| `GET` | `/users/{id}` | Get user by ID |

---

## Screenshots

### Job Listings & Semantic Search
> The main page lists all available job postings. Users can search using natural language — results are retrieved via vector search and reranked for relevance.

![Job listings and semantic search](docs/screenshots/job-search.png)

### AI Chat Assistant
> The chat panel on the right lets users ask questions, get job recommendations, and instruct the agent to apply to positions on their behalf.

![AI chat assistant](docs/screenshots/chat-assistant.png)

### Agent Tool Call — Apply to Job
> When the user asks to apply, the agent confirms the job and motivation text before calling the `job_apply` tool and submitting the application.

![Agent applying to a job](docs/screenshots/job-apply-tool.png)

---

## Database Schema (simplified)

```
account ──< account_user >── app_user
   │
   ├──< agent
   ├──< chat_thread ──< chat_message
   ├──< document ──< document_section (embedding vector)
   └──< application (user ──> document + motivation_text)
```

---

*Built during the GenAI for Developers #25.11 course — Ctrl+Space Labs, Dec 2025*
