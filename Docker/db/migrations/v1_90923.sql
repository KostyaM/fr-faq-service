CREATE TABLE IF NOT EXISTS faq_tree_node
(
    id INTEGER PRIMARY KEY,
    optionText TEXT NOT NULL,
    parentId INTEGER
);