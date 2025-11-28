-- V2__create_plural_tables.sql
-- Ensure plural table names expected by JPA exist and copy data from existing singular tables

-- Create `users` table if missing and copy from `user_account` if present
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'users') THEN
        CREATE TABLE users (
            id BIGSERIAL PRIMARY KEY,
            username VARCHAR(100) NOT NULL UNIQUE,
            password VARCHAR(200) NOT NULL,
            role VARCHAR(50) NOT NULL,
            created_at TIMESTAMPTZ NOT NULL DEFAULT now()
        );
        IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'user_account') THEN
            INSERT INTO users (id, username, password, role, created_at)
            SELECT id, username, password, roles, created_at FROM user_account;
            -- Set sequence to max(id)
            PERFORM setval(pg_get_serial_sequence('users','id'), COALESCE((SELECT MAX(id) FROM users),0));
        END IF;
    END IF;
END$$;

-- Create `employees` table if missing and copy from `employee` if present
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'employees') THEN
        CREATE TABLE employees (
            id BIGSERIAL PRIMARY KEY,
            name VARCHAR(200) NOT NULL,
            email VARCHAR(200) NOT NULL UNIQUE,
            created_at TIMESTAMPTZ NOT NULL DEFAULT now()
        );
        IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'employee') THEN
            INSERT INTO employees (id, name, email, created_at)
            SELECT id, name, email, created_at FROM employee;
            PERFORM setval(pg_get_serial_sequence('employees','id'), COALESCE((SELECT MAX(id) FROM employees),0));
        END IF;
    END IF;
END$$;

-- Create `tasks` table if missing and copy from `task` if present
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'tasks') THEN
        CREATE TABLE tasks (
            id BIGSERIAL PRIMARY KEY,
            title VARCHAR(500) NOT NULL,
            description TEXT,
            status VARCHAR(50) NOT NULL,
            assigned_to_id BIGINT,
            due_date TIMESTAMPTZ,
            created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
            updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
        );
        IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'task') THEN
            INSERT INTO tasks (id, title, description, status, assigned_to_id, due_date, created_at, updated_at)
            SELECT id, title, description, status, assigned_to::bigint, (CASE WHEN due_date IS NOT NULL THEN due_date::timestamptz ELSE NULL END), created_at, updated_at FROM task;
            PERFORM setval(pg_get_serial_sequence('tasks','id'), COALESCE((SELECT MAX(id) FROM tasks),0));
        END IF;
    END IF;
END$$;
