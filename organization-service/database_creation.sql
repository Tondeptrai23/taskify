-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Organization Status Enum
CREATE TYPE organization_status AS ENUM ('ACTIVE', 'INACTIVE', 'SUSPENDED');

-- Organizations Table
CREATE TABLE organizations (
   id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
   name VARCHAR(255) NOT NULL,
   description TEXT,
   owner_id UUID NOT NULL,
   status organization_status NOT NULL DEFAULT 'ACTIVE',
   created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
   updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
   deleted_at TIMESTAMP WITH TIME ZONE,
   created_by VARCHAR(255) NOT NULL,
   updated_by VARCHAR(255) NOT NULL,
   CONSTRAINT organizations_name_owner_unique UNIQUE (name, owner_id)
);

-- Add indexes for organizations
CREATE INDEX idx_organizations_owner_id ON organizations(owner_id);
CREATE INDEX idx_organizations_status ON organizations(status);
CREATE INDEX idx_organizations_deleted_at ON organizations(deleted_at);

-- Local Users Table (mirror of Auth Service users)
CREATE TABLE local_users (
     id UUID PRIMARY KEY,
     username VARCHAR(255) NOT NULL,
     email VARCHAR(255) NOT NULL,
     system_role VARCHAR(255),
     created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
     CONSTRAINT local_users_username_unique UNIQUE (username),
     CONSTRAINT local_users_email_unique UNIQUE (email)
);

-- Add index for local_users
CREATE INDEX idx_local_users_email ON local_users(email);

-- Memberships Table
CREATE TABLE memberships (
     id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
     organization_id UUID NOT NULL,
     user_id UUID NOT NULL,
     role_id UUID NOT NULL,
     is_admin BOOLEAN NOT NULL DEFAULT FALSE,
     is_active BOOLEAN NOT NULL DEFAULT TRUE,
     joined_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
     created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
     updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
     created_by VARCHAR(255) NOT NULL,
     updated_by VARCHAR(255) NOT NULL,
     CONSTRAINT memberships_org_user_unique UNIQUE (organization_id, user_id),
     CONSTRAINT fk_memberships_organization FOREIGN KEY (organization_id)
         REFERENCES organizations(id) ON DELETE CASCADE,
     CONSTRAINT fk_memberships_user FOREIGN KEY (user_id)
         REFERENCES local_users(id) ON DELETE CASCADE
);

-- Add indexes for memberships
CREATE INDEX idx_memberships_user_id ON memberships(user_id);
CREATE INDEX idx_memberships_organization_id ON memberships(organization_id);
CREATE INDEX idx_memberships_role_id ON memberships(role_id);
CREATE INDEX idx_memberships_is_active ON memberships(is_active);

-- Insert local users (mirroring Auth Service users)
INSERT INTO local_users (id, username, email, created_at, system_role)
VALUES
    ('11111111-1111-1111-1111-111111111111', 'system_admin', 'admin@taskify.com',
     CURRENT_TIMESTAMP, 'SYSTEM_ADMIN'),
    ('22222222-2222-2222-2222-222222222222', 'regular_user', 'user@taskify.com',
     CURRENT_TIMESTAMP, 'USER');

-- Create a default organization owned by the admin
INSERT INTO organizations (
    id, name, description, owner_id, status,
    created_at, updated_at, created_by, updated_by
)
VALUES (
   '33333333-3333-3333-3333-333333333333',
   'Taskify Default Organization',
   'Default organization created during system initialization',
   '11111111-1111-1111-1111-111111111111',
   'ACTIVE',
   CURRENT_TIMESTAMP,
   CURRENT_TIMESTAMP,
   'SYSTEM',
   'SYSTEM'
       );

-- Create memberships (both admin and regular user in default organization)
INSERT INTO memberships (
    id, organization_id, user_id, role_id,
    is_admin, is_active, joined_at,
    created_at, updated_at, created_by, updated_by
)
VALUES
    -- Admin membership
    ('44444444-4444-4444-4444-444444444444',
     '33333333-3333-3333-3333-333333333333',
     '11111111-1111-1111-1111-111111111111',
     '55555555-5555-5555-5555-555555555555', -- Assuming this role_id exists in IAM service
     TRUE,
     TRUE,
     CURRENT_TIMESTAMP,
     CURRENT_TIMESTAMP,
     CURRENT_TIMESTAMP,
     'SYSTEM',
     'SYSTEM'),
    -- Regular user membership
    ('66666666-6666-6666-6666-666666666666',
     '33333333-3333-3333-3333-333333333333',
     '22222222-2222-2222-2222-222222222222',
     '77777777-7777-7777-7777-777777777777', -- Assuming this role_id exists in IAM service
     FALSE,
     TRUE,
     CURRENT_TIMESTAMP,
     CURRENT_TIMESTAMP,
     CURRENT_TIMESTAMP,
     'SYSTEM',
     'SYSTEM');