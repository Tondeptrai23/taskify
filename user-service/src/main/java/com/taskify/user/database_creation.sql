-- Enable UUID extension if using PostgreSQL
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create users table
CREATE TABLE users (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    system_role VARCHAR(50),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE
);

-- Create organizations table
CREATE TABLE organizations (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE
);

-- Create organization_roles table
CREATE TABLE organization_roles (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    isDefault BOOLEAN DEFAULT FALSE,
);

-- Create user_organizations table
CREATE TABLE user_organizations (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id),
    org_id UUID NOT NULL REFERENCES organizations(id),
    org_role_id UUID NOT NULL REFERENCES organization_roles(id),
    is_admin BOOLEAN DEFAULT FALSE,
    joined_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    UNIQUE(user_id, org_id)
);

-- Create indexes with updated conditions for deleted_at
CREATE INDEX idx_users_email ON users(email) WHERE deleted_at IS NULL;
CREATE INDEX idx_users_username ON users(username) WHERE deleted_at IS NULL;
CREATE INDEX idx_user_organizations_user_id ON user_organizations(user_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_user_organizations_org_id ON user_organizations(org_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_user_organizations_role_id ON user_organizations(org_role_id);

-- Insert default organization roles
INSERT INTO organization_roles (name, description, isDefault) VALUES
('DEVELOPER', 'Software developer role', TRUE),
('TESTER', 'Quality assurance role', FALSE),
('PRODUCT_OWNER', 'Product owner role', FALSE),
('SCRUM_MASTER', 'Scrum master role', FALSE);

INSERT INTO users (id, email, username, password_hash, system_role) VALUES
('00000000-0000-0000-0000-000000000000', 'admin@taskify.com', 'admin', 'admin', "SYSTEM_ADMIN")