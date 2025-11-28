-- V1__init.sql
-- Create schema for TaskManagementAPI and seed admin user

-- Enable pgcrypto for bcrypt functions
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Users table
CREATE TABLE IF NOT EXISTS users (
	id BIGSERIAL PRIMARY KEY,
	username VARCHAR(100) NOT NULL UNIQUE,
	password VARCHAR(200) NOT NULL,
	role VARCHAR(50) NOT NULL,
	created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Employees table
CREATE TABLE IF NOT EXISTS employees (
	id BIGSERIAL PRIMARY KEY,
	name VARCHAR(200) NOT NULL,
	email VARCHAR(200) NOT NULL UNIQUE,
	created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Tasks table
CREATE TABLE IF NOT EXISTS tasks (
	id BIGSERIAL PRIMARY KEY,
	title VARCHAR(500) NOT NULL,
	description TEXT,
	status VARCHAR(50) NOT NULL,
	assigned_to_id BIGINT,
	due_date TIMESTAMPTZ,
	created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
	updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Seed admin user with bcrypt hashed password using pgcrypto
-- Password: 123456
INSERT INTO users (username, password, role)
SELECT 'admin', crypt('123456', gen_salt('bf')), 'ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin');

