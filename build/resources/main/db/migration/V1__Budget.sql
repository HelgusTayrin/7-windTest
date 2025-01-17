CREATE TABLE author_table
(
    id uuid PRIMARY KEY,
    full_name TEXT NOT NULL,
    date_time TIMESTAMP NOT NULL
);

CREATE TABLE budget_table
(
    id uuid PRIMARY KEY,
    year INT NOT NULL,
    month INT NOT NULL,
    amount INT NOT NULL,
    type TEXT NOT NULL,
    author uuid REFERENCES author_table (id)
);

