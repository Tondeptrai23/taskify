-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Project Status Enum
CREATE TYPE project_status AS ENUM ('ACTIVE', 'ARCHIVED', 'SUSPENDED');

-- Projects Table
CREATE TABLE projects (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    key VARCHAR(50) NOT NULL,
    organization_id UUID NOT NULL,
    author_id UUID NOT NULL,
    status project_status NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT projects_org_key_unique UNIQUE (organization_id, key)
);

-- Add indexes for projects
CREATE INDEX idx_projects_organization_id ON projects(organization_id);
CREATE INDEX idx_projects_author_id ON projects(author_id);
CREATE INDEX idx_projects_status ON projects(status);
CREATE INDEX idx_projects_deleted_at ON projects(deleted_at);

-- Local Users Table (mirror of Auth Service users)
CREATE TABLE local_users (
    id UUID PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    system_role VARCHAR(255),
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT local_users_username_unique UNIQUE (username),
    CONSTRAINT local_users_email_unique UNIQUE (email)
);

-- Add index for local_users
CREATE INDEX idx_local_users_email ON local_users(email);

-- Insert default users (mirroring the data from Auth service)
INSERT INTO local_users (id, username, email, created_at, system_role)
VALUES
    ('11111111-1111-1111-1111-111111111111', 'system_admin', 'admin@taskify.com',
     CURRENT_TIMESTAMP, 'SYSTEM_ADMIN'),
    ('22222222-2222-2222-2222-222222222222', 'regular_user', 'user@taskify.com',
     CURRENT_TIMESTAMP, 'USER');

-- Create sample projects for the default organization
INSERT INTO projects (
    id, name, description, key, organization_id, author_id,
    status, created_at, updated_at
)
VALUES
    (
        '88888888-8888-8888-8888-888888888888',
        'Website Redesign',
        'Complete overhaul of company website with new branding',
        'WEB',
        '33333333-3333-3333-3333-333333333333',
        '11111111-1111-1111-1111-111111111111',
        'ACTIVE',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        '77777777-7777-7777-7777-777777777777',
        'Mobile App Development',
        'New iOS and Android app for customer engagement',
        'MOBILE',
        '33333333-3333-3333-3333-333333333333',
        '11111111-1111-1111-1111-111111111111',
        'ACTIVE',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    );