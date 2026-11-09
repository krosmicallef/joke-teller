ALTER TABLE subscriber
    ADD COLUMN verified BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE subscriber
    ADD COLUMN verification_code VARCHAR(6) NULL;

ALTER TABLE subscriber
    ADD COLUMN verification_expires_at DATETIME(6) NULL;
