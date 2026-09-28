# GenAI Job Finder

A full-stack job-finder platform with a Generative AI layer, built during the **GenAI for Developers #25.11** course at Ctrl+Space Labs.

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Java 21, Spring Boot 3 |
| Frontend | Next.js 15 (Pages Router) |
| Database | PostgreSQL 16 + pgvector |
| LLM | OpenAI API (GPT-4o, text-embedding-3-small) |
| Reranking | Voyage AI (rerank-2.5) |
| Migrations | Flyway |
| Infra | Docker Compose |

## GenAI Features

**1. Chunking + Embeddings** — Job postings are split into chunks and indexed as vector embeddings in PostgreSQL via pgvector.

**2. Semantic Search + Reranking** — Natural-language job search using vector similarity, re-ranked with Voyage AI for better accuracy.

**3. RAG Chat** — An AI assistant answers candidate questions using the job postings as context (Retrieval-Augmented Generation).

**4. Agentic Tool Calling** — A custom agent loop (do-while on `finish_reason`) that can call tools. Includes a `job_apply` tool that applies to a job on behalf of the user.

## Project Structure

```
genai-job-finder/
├── genai-be/       # Spring Boot backend (agents, RAG, embeddings)
├── genai-fe/       # Next.js frontend
└── genai-db/       # Flyway SQL migrations + Docker Compose
```

## How to Run

### 1. Start the Database
```bash
cd genai-db
docker compose up postgres -d
```

### 2. Start the Backend
```bash
cd genai-be
# Set env vars: OPENAI_API_KEY, VOYAGE_API_KEY
./mvnw spring-boot:run
```

### 3. Start the Frontend
```bash
cd genai-fe
npm install
npm run dev
# → http://localhost:3000
```

## Environment Variables

| Variable | Description |
|----------|-------------|
| `OPENAI_API_KEY` | OpenAI API key (LLM + embeddings) |
| `VOYAGE_API_KEY` | Voyage AI key (reranking) |
