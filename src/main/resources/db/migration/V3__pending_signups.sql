-- Create table to hold signup requests pending admin approval
CREATE TABLE IF NOT EXISTS pending_signups (
    id serial PRIMARY KEY,
    name varchar(255) NOT NULL,
    email varchar(255) NOT NULL UNIQUE,
    phone varchar(100) NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now()
);
