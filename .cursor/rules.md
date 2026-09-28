## Cursor Code Generation Rules

These guidelines tell Cursor how to scaffold and extend the three modules in this repository. Follow them unless a task explicitly overrides them.

---

### Frontend (`genai-fe`)

- **Framework**: Next.js (Pages Router) with React and plain JavaScript modules. Do **not** introduce TypeScript unless requested.
- **Component structure**:
  - Prefer functional components with hooks (`useState`, `useEffect`, `useMemo`, etc.).
  - Keep page-level components under `src/pages/`. Co-locate lightweight helpers/components in the same file unless they grow large (then move to `src/components/`).
  - Maintain minimal, readable CSS in `src/styles/*.css` using CSS Modules.
- **GenAI usage**: Simulate model calls in-browser until backend APIs exist. Annotate with `TODO: replace with backend call` where applicable.
- **Testing / linting**: Follow existing ESLint config. Avoid adding heavy tooling until requested.

---

### Backend (`genai-be`)

- **Stack**: Spring Boot, Java (25). Use Maven conventions already present.
- **Layering & naming**:
  - Controllers → `*Controller` under `...controllers`.
  - Services → `*Service` under `...services`.
  - DTOs → `*Request`, `*Response` under `...model/dtos`.
  - Entities / repositories → `*Entity`, `*Repository` under `...model/domain` / `...repositories`.
- **Error handling**: Use `@ControllerAdvice` with custom `ApiError` payloads. Services throw domain exceptions; controllers translate them.
- **Logging**: Use SLF4J (`@Slf4j` or `LoggerFactory`) with structured messages. No `System.out`.
- **AI/RAG placement**:
  - Encapsulate LLM + embedding + vector interactions in `rag/` packages (e.g., `rag.service.VectorStoreClient`, `rag.pipeline.RagOrchestrator`).
  - Keep agent runtime logic inside `agents/` packages, exposing service interfaces for the controllers.
  - Add `TODO` markers when stubs are created but behavior is pending.

---

### Database (`genai-db`)

- **Tooling**: Flyway migrations executed via Maven.
- **Naming**: Use Flyway semantic versioning (`V1__init.sql`, `V2__add_agent_tables.sql`, etc.). Lowercase words, separated by underscores.
- **Organization**:
  - SQL files live under `src/main/resources/db/migrations/`.
  - Configuration overrides go in `src/main/resources/db/`.
  - Each migration should be idempotent and include rollback notes as SQL comments if applicable.
- **Future schema**: Document tables for documents, embeddings metadata, agent configs, chat transcripts, and vector-store references with `TODO` comments until defined.

---

### General Rules

- **Commit messages**: Use imperative mood and reference scope, e.g., `feat: add agent settings modal` or `chore: update Flyway baseline`.
- **Coding workflow with Cursor**:
  - Outline approach, note assumptions, then implement with clear diffs (`apply_patch` when practical).
  - Keep changes focused; avoid touching unrelated modules without need.
  - Use `TODO:` markers for incomplete work, especially in backend/DB placeholders.
- **File organization**:
  - Keep module-specific assets inside their directories (`genai-fe`, `genai-be`, `genai-db`).
  - Place cross-cutting docs/config in repo root or `.cursor/`.
  - Prefer descriptive filenames and folder names.
- **Documentation**: Update `README.md` or module docs when behavior changes. Mention GenAI-specific assumptions (LLM provider, vector DB) once decisions are concrete.

