ALTER TABLE community_messages
    ADD COLUMN deleted BOOLEAN NOT NULL DEFAULT FALSE;

CREATE INDEX idx_community_messages_deleted ON community_messages(deleted);
