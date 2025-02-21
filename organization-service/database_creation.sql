-- Insert local users (mirroring Auth Service users)
INSERT INTO local_users (id, username, email, created_at, updated_at, created_by, updated_by)
VALUES
    ('11111111-1111-1111-1111-111111111111', 'system_admin', 'admin@taskify.com',
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM'),
    ('22222222-2222-2222-2222-222222222222', 'regular_user', 'user@taskify.com',
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM');

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