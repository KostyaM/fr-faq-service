CREATE TABLE IF NOT EXISTS faq_tree_node
(
    id INTEGER PRIMARY KEY,
    option_text TEXT,
    parent_id INTEGER
);