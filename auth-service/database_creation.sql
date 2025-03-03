-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Users Table (with username and soft delete)
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    system_role VARCHAR(50) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT users_email_unique UNIQUE (email),
    CONSTRAINT users_username_unique UNIQUE (username)
);

-- Add indexes for users
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_deleted_at ON users(deleted_at);
CREATE INDEX idx_users_system_role ON users(system_role);

-- Refresh Tokens Table
CREATE TABLE refresh_tokens (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL,
    token VARCHAR(255) NOT NULL,
    is_revoked BOOLEAN NOT NULL DEFAULT FALSE,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT refresh_tokens_token_unique UNIQUE (token)
);

-- Add indexes for refresh_tokens
CREATE INDEX idx_refresh_tokens_token ON refresh_tokens(token);
CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);

-- Insert initial users (optional, usually created during first login)
INSERT INTO users (id, email, username, password_hash, system_role, created_at, updated_at)
VALUES
    ('11111111-1111-1111-1111-111111111111',
     'admin@taskify.com',
     'system_admin',
     '$2a$10$2YvhUdigrf2Tfp2cxSpseuU/NXLgdxIRHRzkfiY2GM0rlDSi4QUrm', -- 'admin123' hashed with BCrypt
     'SYSTEM_ADMIN',
     CURRENT_TIMESTAMP,
     CURRENT_TIMESTAMP),
    ('22222222-2222-2222-2222-222222222222',
     'user@taskify.com',
     'regular_user',
     '$2a$10$DdPlI3j1VvQTMGurW0UdleR5qxExHfNhXgsBIz2ppUkdtwOzPgJVC', -- 'user123' hashed with BCrypt
     'USER',
     CURRENT_TIMESTAMP,
     CURRENT_TIMESTAMP);

-- Insert initial refresh tokens (optional, usually created during first login)
INSERT INTO refresh_tokens (id, user_id, token, is_revoked, expires_at, created_at)
VALUES
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
     '11111111-1111-1111-1111-111111111111',
     'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
     FALSE,
     CURRENT_TIMESTAMP + INTERVAL '7 days',
     CURRENT_TIMESTAMP),
    ('cccccccc-cccc-cccc-cccc-cccccccccccc',
     '22222222-2222-2222-2222-222222222222',
     'dddddddd-dddd-dddd-dddd-dddddddddddd',
     FALSE,
     CURRENT_TIMESTAMP + INTERVAL '7 days',
     CURRENT_TIMESTAMP);