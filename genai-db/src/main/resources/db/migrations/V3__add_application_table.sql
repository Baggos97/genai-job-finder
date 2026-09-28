-- Applications: a user applies to a job (document) via the agent
CREATE TABLE IF NOT EXISTS application (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    account_id      UUID NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    user_id         UUID NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    document_id     UUID NOT NULL REFERENCES document(id) ON DELETE CASCADE,
    motivation_text TEXT NOT NULL,
    status          TEXT NOT NULL DEFAULT 'submitted',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_application_user     ON application(user_id);
CREATE INDEX IF NOT EXISTS idx_application_document ON application(document_id);
CREATE INDEX IF NOT EXISTS idx_application_account  ON application(account_id);
