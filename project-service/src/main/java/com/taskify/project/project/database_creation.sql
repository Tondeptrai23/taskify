-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create local users table for denormalization
CREATE TABLE local_users (
    id UUID PRIMARY KEY,  -- Using same ID from User Service
    name VARCHAR(255) NOT NULL
);

-- Create projects table
CREATE TABLE projects (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    organization_id UUID NOT NULL,
    status VARCHAR(50) DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES local_users(id),
    updated_by UUID REFERENCES local_users(id),
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_local_users_name ON local_users(name);
CREATE INDEX idx_projects_org_id ON projects(organization_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_projects_created_by ON projects(created_by);
CREATE INDEX idx_projects_status ON projects(status) WHERE deleted_at IS NULL;
