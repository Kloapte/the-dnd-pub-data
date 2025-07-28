CREATE SCHEMA IF NOT EXISTS data;

CREATE TABLE data.race (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type INTEGER NOT NULL,
    school VARCHAR(50),
    description TEXT
);