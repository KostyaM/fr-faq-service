CREATE TABLE IF NOT EXISTS faq_tree_node
(
    id INTEGER PRIMARY KEY,
    option_text TEXT,
    parent_id INTEGER
);

CREATE TABLE IF NOT EXISTS user_session
(
    id BIGSERIAL PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE,
    stage INTEGER[]
);