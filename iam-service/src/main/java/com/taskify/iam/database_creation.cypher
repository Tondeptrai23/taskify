// Clear existing data (For development only - use with caution)
// In production, consider using more targeted deletion queries
MATCH (n) DETACH DELETE n;

// Create Organizations
CREATE (org:Organization {
  id: '33333333-3333-3333-3333-333333333333',
  name: 'Taskify Default Organization',
  status: 'ACTIVE',
  createdAt: datetime(),
  updatedAt: datetime()
});

// Create Users (mirroring Auth Service)
CREATE (admin:User {
  id: '11111111-1111-1111-1111-111111111111',
  username: 'system_admin',
  email: 'admin@taskify.com',
  systemRole: 'SYSTEM_ADMIN',
  createdAt: datetime(),
  updatedAt: datetime(),
  isDeleted: false
});

CREATE (user:User {
  id: '22222222-2222-2222-2222-222222222222',
  username: 'regular_user',
  email: 'user@taskify.com',
  systemRole: 'USER',
  createdAt: datetime(),
  updatedAt: datetime(),
  isDeleted: false
});

// Create Sample Projects with meaningful names
CREATE (webProject:Project {
  id: '88888888-8888-8888-8888-888888888888',
  name: 'Website Redesign',
  description: 'Complete overhaul of company website with new branding',
  createdAt: datetime(),
  updatedAt: datetime()
});

CREATE (mobileProject:Project {
  id: '77777777-7777-7777-7777-777777777777',
  name: 'Mobile App Development',
  description: 'New iOS and Android app for customer engagement',
  createdAt: datetime(),
  updatedAt: datetime()
});

// Connect Projects to Organization
MATCH (p:Project {id: '88888888-8888-8888-8888-888888888888'})
MATCH (o:Organization {id: '33333333-3333-3333-3333-333333333333'})
CREATE (p)-[:BELONGS_TO]->(o);

MATCH (p:Project {id: '77777777-7777-7777-7777-777777777777'})
MATCH (o:Organization {id: '33333333-3333-3333-3333-333333333333'})
CREATE (p)-[:BELONGS_TO]->(o);

// Create Permission Groups with static IDs
CREATE (projectPerms:PermissionGroup {
  id: 1,
  name: 'Project Permissions',
  description: 'Permissions related to project management',
  createdAt: datetime(),
  updatedAt: datetime()
});

CREATE (taskPerms:PermissionGroup {
  id: 2,
  name: 'Task Permissions',
  description: 'Permissions related to task management',
  createdAt: datetime(),
  updatedAt: datetime()
});

CREATE (memberPerms:PermissionGroup {
  id: 3,
  name: 'Member Permissions',
  description: 'Permissions related to member management',
  createdAt: datetime(),
  updatedAt: datetime()
});

CREATE (rolePerms:PermissionGroup {
  id: 4,
  name: 'Role Management',
  description: 'Permissions related to organizationRole management',
  createdAt: datetime(),
  updatedAt: datetime()
});

// Create Base Permissions with static IDs and prerequisites
// Project Permissions (1000-1999 range)
CREATE (createProject:Permission {
  id: 1001,
  name: 'CREATE_PROJECT',
  description: 'Ability to create new projects',
  prerequisites: [],
  createdAt: datetime(),
  updatedAt: datetime()
});

CREATE (viewProject:Permission {
  id: 1004,
  name: 'VIEW_PROJECT',
  description: 'Ability to view project details',
  prerequisites: [],
  createdAt: datetime(),
  updatedAt: datetime()
});

CREATE (updateProject:Permission {
  id: 1002,
  name: 'UPDATE_PROJECT',
  description: 'Ability to update project details',
  prerequisites: ['VIEW_PROJECT'],
  createdAt: datetime(),
  updatedAt: datetime()
});

CREATE (deleteProject:Permission {
  id: 1003,
  name: 'DELETE_PROJECT',
  description: 'Ability to delete projects',
  prerequisites: ['VIEW_PROJECT'],
  createdAt: datetime(),
  updatedAt: datetime()
});

// Task Permissions (2000-2999 range)
CREATE (createTask:Permission {
  id: 2001,
  name: 'CREATE_TASK',
  description: 'Ability to create new tasks',
  prerequisites: ['VIEW_PROJECT'],
  createdAt: datetime(),
  updatedAt: datetime()
});

CREATE (viewTask:Permission {
  id: 2004,
  name: 'VIEW_TASK',
  description: 'Ability to view task details',
  prerequisites: [],
  createdAt: datetime(),
  updatedAt: datetime()
});

CREATE (updateTaskStatus:Permission {
  id: 2002,
  name: 'UPDATE_TASK_STATUS',
  description: 'Ability to update task status',
  prerequisites: ['VIEW_TASK'],
  createdAt: datetime(),
  updatedAt: datetime()
});

CREATE (deleteTask:Permission {
  id: 2003,
  name: 'DELETE_TASK',
  description: 'Ability to delete tasks',
  prerequisites: ['VIEW_TASK'],
  createdAt: datetime(),
  updatedAt: datetime()
});

// Member Permissions (3000-3999 range)
CREATE (viewMember:Permission {
  id: 3004,
  name: 'VIEW_MEMBER',
  description: 'Ability to view member details',
  prerequisites: [],
  createdAt: datetime(),
  updatedAt: datetime()
});

CREATE (inviteMember:Permission {
  id: 3001,
  name: 'INVITE_MEMBER',
  description: 'Ability to invite new members',
  prerequisites: ['VIEW_MEMBER'],
  createdAt: datetime(),
  updatedAt: datetime()
});

CREATE (updateMemberRole:Permission {
  id: 3003,
  name: 'UPDATE_MEMBER_ROLE',
  description: 'Ability to update member organizationRoles',
  prerequisites: ['VIEW_MEMBER'],
  createdAt: datetime(),
  updatedAt: datetime()
});

CREATE (removeMember:Permission {
  id: 3002,
  name: 'REMOVE_MEMBER',
  description: 'Ability to remove members',
  prerequisites: ['VIEW_MEMBER'],
  createdAt: datetime(),
  updatedAt: datetime()
});

// Role Management Permissions (4000-4999 range)
CREATE (manageRole:Permission {
  id: 4001,
  name: 'MANAGE_ROLE',
  description: 'Ability to create, update, and delete organizationRoles',
  prerequisites: ['VIEW_MEMBER'],
  createdAt: datetime(),
  updatedAt: datetime()
});

// Create Organization Roles
CREATE (orgAdminRole:OrganizationRole {
  id: '55555555-5555-5555-5555-555555555555',
  name: 'Admin',
  description: 'Organization administrator with full access',
  isDefault: false,
  createdAt: datetime(),
  updatedAt: datetime()
});

CREATE (orgPMRole:OrganizationRole {
  id: '66666666-6666-6666-6666-666666666666',
  name: 'Project Manager',
  description: 'Can manage projects and team members',
  isDefault: false,
  createdAt: datetime(),
  updatedAt: datetime()
});

CREATE (orgDevRole:OrganizationRole {
  id: '77777777-7777-7777-7777-777777777777',
  name: 'Developer',
  description: 'Can work on assigned tasks and view projects',
  isDefault: true,  // This is the default organizationRole for new members
  createdAt: datetime(),
  updatedAt: datetime()
});

// Create Project Roles for Website Redesign project
CREATE (webProjectLead:ProjectRole {
  id: 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
  name: 'Web Design Lead',
  description: 'Lead for website redesign project with design authority',
  isDefault: false,
  createdAt: datetime(),
  updatedAt: datetime()
});

CREATE (webDesigner:ProjectRole {
  id: 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
  name: 'UI/UX Designer',
  description: 'Designer responsible for user interface and experience',
  isDefault: true,  // Default organizationRole for project members
  createdAt: datetime(),
  updatedAt: datetime()
});

CREATE (webContentEditor:ProjectRole {
  id: 'cccccccc-cccc-cccc-cccc-cccccccccccc',
  name: 'Content Editor',
  description: 'Responsible for website content and copy',
  isDefault: false,
  createdAt: datetime(),
  updatedAt: datetime()
});

// Create Project Roles for Mobile App Development project
CREATE (mobileProjectLead:ProjectRole {
  id: 'dddddddd-dddd-dddd-dddd-dddddddddddd',
  name: 'Mobile Development Lead',
  description: 'Lead for mobile app development with technical oversight',
  isDefault: false,
  createdAt: datetime(),
  updatedAt: datetime()
});

CREATE (mobileDeveloper:ProjectRole {
  id: 'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee',
  name: 'Mobile Developer',
  description: 'Developer working on mobile app code',
  isDefault: true,  // Default organizationRole for project members
  createdAt: datetime(),
  updatedAt: datetime()
});

CREATE (mobileQA:ProjectRole {
  id: 'ffffffff-ffff-ffff-ffff-ffffffffffff',
  name: 'QA Tester',
  description: 'Responsible for testing mobile app functionality',
  isDefault: false,
  createdAt: datetime(),
  updatedAt: datetime()
});

// Connect Permission Groups with Permissions
MATCH (pg:PermissionGroup {name: 'Project Permissions'})
MATCH (p:Permission)
  WHERE p.name IN ['CREATE_PROJECT', 'UPDATE_PROJECT', 'DELETE_PROJECT', 'VIEW_PROJECT']
CREATE (pg)-[:CONTAINS]->(p);

MATCH (pg:PermissionGroup {name: 'Task Permissions'})
MATCH (p:Permission)
  WHERE p.name IN ['CREATE_TASK', 'UPDATE_TASK_STATUS', 'DELETE_TASK', 'VIEW_TASK']
CREATE (pg)-[:CONTAINS]->(p);

MATCH (pg:PermissionGroup {name: 'Member Permissions'})
MATCH (p:Permission)
  WHERE p.name IN ['INVITE_MEMBER', 'REMOVE_MEMBER', 'UPDATE_MEMBER_ROLE', 'VIEW_MEMBER']
CREATE (pg)-[:CONTAINS]->(p);

MATCH (pg:PermissionGroup {name: 'Role Management'})
MATCH (p:Permission)
  WHERE p.name IN ['MANAGE_ROLE']
CREATE (pg)-[:CONTAINS]->(p);

// Set up Organization Role-Permission relationships
// Admin Role - gets all permissions
MATCH (r:OrganizationRole {name: 'Admin'})
MATCH (p:Permission)
CREATE (r)-[:HAS_PERMISSION]->(p);

// Project Manager Role
MATCH (r:OrganizationRole {name: 'Project Manager'})
MATCH (p:Permission)
  WHERE p.name IN [
    'CREATE_PROJECT', 'UPDATE_PROJECT', 'VIEW_PROJECT',
    'CREATE_TASK', 'UPDATE_TASK_STATUS', 'VIEW_TASK',
    'INVITE_MEMBER', 'UPDATE_MEMBER_ROLE', 'VIEW_MEMBER',
    'MANAGE_ROLE'
  ]
CREATE (r)-[:HAS_PERMISSION]->(p);

// Developer Role
MATCH (r:OrganizationRole {name: 'Developer'})
MATCH (p:Permission)
  WHERE p.name IN [
    'VIEW_PROJECT',
    'CREATE_TASK', 'UPDATE_TASK_STATUS', 'VIEW_TASK',
    'VIEW_MEMBER'
  ]
CREATE (r)-[:HAS_PERMISSION]->(p);

// Set up Project Role-Permission relationships for Website Redesign project
// Web Design Lead Role
MATCH (r:ProjectRole {name: 'Web Design Lead'})
MATCH (p:Permission)
  WHERE p.name IN [
    'UPDATE_PROJECT', 'VIEW_PROJECT',
    'CREATE_TASK', 'UPDATE_TASK_STATUS', 'DELETE_TASK', 'VIEW_TASK',
    'INVITE_MEMBER', 'VIEW_MEMBER'
  ]
CREATE (r)-[:HAS_PERMISSION]->(p);

// UI/UX Designer Role
MATCH (r:ProjectRole {name: 'UI/UX Designer'})
MATCH (p:Permission)
  WHERE p.name IN [
    'VIEW_PROJECT',
    'CREATE_TASK', 'UPDATE_TASK_STATUS', 'VIEW_TASK',
    'VIEW_MEMBER'
  ]
CREATE (r)-[:HAS_PERMISSION]->(p);

// Content Editor Role
MATCH (r:ProjectRole {name: 'Content Editor'})
MATCH (p:Permission)
  WHERE p.name IN [
    'VIEW_PROJECT',
    'VIEW_TASK', 'UPDATE_TASK_STATUS',
    'VIEW_MEMBER'
  ]
CREATE (r)-[:HAS_PERMISSION]->(p);

// Set up Project Role-Permission relationships for Mobile App Development project
// Mobile Development Lead Role
MATCH (r:ProjectRole {name: 'Mobile Development Lead'})
MATCH (p:Permission)
  WHERE p.name IN [
    'UPDATE_PROJECT', 'VIEW_PROJECT',
    'CREATE_TASK', 'UPDATE_TASK_STATUS', 'DELETE_TASK', 'VIEW_TASK',
    'INVITE_MEMBER', 'VIEW_MEMBER'
  ]
CREATE (r)-[:HAS_PERMISSION]->(p);

// Mobile Developer Role
MATCH (r:ProjectRole {name: 'Mobile Developer'})
MATCH (p:Permission)
  WHERE p.name IN [
    'VIEW_PROJECT',
    'CREATE_TASK', 'UPDATE_TASK_STATUS', 'VIEW_TASK',
    'VIEW_MEMBER'
  ]
CREATE (r)-[:HAS_PERMISSION]->(p);

// QA Tester Role
MATCH (r:ProjectRole {name: 'QA Tester'})
MATCH (p:Permission)
  WHERE p.name IN [
    'VIEW_PROJECT',
    'CREATE_TASK', 'UPDATE_TASK_STATUS', 'VIEW_TASK',
    'VIEW_MEMBER'
  ]
CREATE (r)-[:HAS_PERMISSION]->(p);

// Connect Organizations with Organization Roles
MATCH (o:Organization {id: '33333333-3333-3333-3333-333333333333'})
MATCH (r:OrganizationRole)
CREATE (o)-[:HAS_ROLE]->(r);

// Connect Website Redesign Project with its specific Project Roles
MATCH (p:Project {id: '88888888-8888-8888-8888-888888888888'})
MATCH (r:ProjectRole)
  WHERE r.name IN ['Web Design Lead', 'UI/UX Designer', 'Content Editor']
CREATE (p)-[:HAS_ROLE]->(r);

// Connect Mobile App Development Project with its specific Project Roles
MATCH (p:Project {id: '77777777-7777-7777-7777-777777777777'})
MATCH (r:ProjectRole)
  WHERE r.name IN ['Mobile Development Lead', 'Mobile Developer', 'QA Tester']
CREATE (p)-[:HAS_ROLE]->(r);

// Connect Users with Organization Roles
MATCH (u:User {username: 'system_admin'})
MATCH (r:OrganizationRole {name: 'Admin'})
CREATE (u)-[:HAS_ORG_ROLE {
  organizationId: '33333333-3333-3333-3333-333333333333',
  grantedAt: datetime(),
  grantedBy: 'SYSTEM'
}]->(r);

MATCH (u:User {username: 'regular_user'})
MATCH (r:OrganizationRole {name: 'Developer'})
CREATE (u)-[:HAS_ORG_ROLE {
  organizationId: '33333333-3333-3333-3333-333333333333',
  grantedAt: datetime(),
  grantedBy: 'SYSTEM'
}]->(r);

// Connect Users with Project Roles for Website Redesign project
MATCH (u:User {username: 'system_admin'})
MATCH (r:ProjectRole {name: 'Web Design Lead'})
CREATE (u)-[:HAS_PROJECT_ROLE {
  projectId: '88888888-8888-8888-8888-888888888888',
  organizationId: '33333333-3333-3333-3333-333333333333',
  grantedAt: datetime(),
  grantedBy: 'SYSTEM'
}]->(r);

MATCH (u:User {username: 'regular_user'})
MATCH (r:ProjectRole {name: 'UI/UX Designer'})
CREATE (u)-[:HAS_PROJECT_ROLE {
  projectId: '88888888-8888-8888-8888-888888888888',
  organizationId: '33333333-3333-3333-3333-333333333333',
  grantedAt: datetime(),
  grantedBy: 'SYSTEM'
}]->(r);

// Connect Users with Project Roles for Mobile App Development project
MATCH (u:User {username: 'system_admin'})
MATCH (r:ProjectRole {name: 'Mobile Development Lead'})
CREATE (u)-[:HAS_PROJECT_ROLE {
  projectId: '77777777-7777-7777-7777-777777777777',
  organizationId: '33333333-3333-3333-3333-333333333333',
  grantedAt: datetime(),
  grantedBy: 'SYSTEM'
}]->(r);

MATCH (u:User {username: 'regular_user'})
MATCH (r:ProjectRole {name: 'Mobile Developer'})
CREATE (u)-[:HAS_PROJECT_ROLE {
  projectId: '77777777-7777-7777-7777-777777777777',
  organizationId: '33333333-3333-3333-3333-333333333333',
  grantedAt: datetime(),
  grantedBy: 'SYSTEM'
}]->(r);

// Create indices for performance
CREATE INDEX user_id_index IF NOT EXISTS FOR (u:User) ON (u.id);
CREATE INDEX organization_role_id_index IF NOT EXISTS FOR (r:OrganizationRole) ON (r.id);
CREATE INDEX organization_role_name_index IF NOT EXISTS FOR (r:OrganizationRole) ON (r.name);
CREATE INDEX project_role_id_index IF NOT EXISTS FOR (r:ProjectRole) ON (r.id);
CREATE INDEX project_role_name_index IF NOT EXISTS FOR (r:ProjectRole) ON (r.name);
CREATE INDEX permission_id_index IF NOT EXISTS FOR (p:Permission) ON (p.id);
CREATE INDEX permission_name_index IF NOT EXISTS FOR (p:Permission) ON (p.name);
CREATE INDEX organization_id_index IF NOT EXISTS FOR (o:Organization) ON (o.id);
CREATE INDEX project_id_index IF NOT EXISTS FOR (p:Project) ON (p.id);


// For testing purpose

CREATE (testUser:User {
  id: '33333333-3333-3333-3333-333333333334',
  username: 'test_user',
  email: 'test@taskify.com',
  systemRole: 'USER',
  createdAt: datetime(),
  updatedAt: datetime(),
  isDeleted: false
});

CREATE (restrictedRole:OrganizationRole {
  id: '88888888-8888-8888-8888-888888888889',
  name: 'Restricted User',
  description: 'User with minimal organization-level permissions',
  isDefault: false,
  createdAt: datetime(),
  updatedAt: datetime(),
  isDeleted: false
});

MATCH (r:OrganizationRole {name: 'Developer'})
MATCH (p:Permission {name: 'CREATE_TASK'})
MATCH (r)-[rel:HAS_PERMISSION]->(p)
DELETE rel;

CREATE (projectPowerUser:ProjectRole {
  id: 'aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee',
  name: 'Project Power User',
  description: 'Has project-specific permissions that exceed their org permissions',
  isDefault: false,
  createdAt: datetime(),
  updatedAt: datetime()
});

MATCH (o:Organization {id: '33333333-3333-3333-3333-333333333333'})
MATCH (r:OrganizationRole {name: 'Restricted User'})
CREATE (o)-[:HAS_ROLE]->(r);

MATCH (p:Project {id: '88888888-8888-8888-8888-888888888888'})
MATCH (r:ProjectRole {name: 'Project Power User'})
CREATE (p)-[:HAS_ROLE]->(r);

MATCH (r:OrganizationRole {name: 'Restricted User'})
MATCH (p:Permission)
  WHERE p.name IN ['VIEW_PROJECT', 'VIEW_TASK', 'VIEW_MEMBER']
CREATE (r)-[:HAS_PERMISSION]->(p);

MATCH (r:ProjectRole {name: 'Project Power User'})
MATCH (p:Permission)
  WHERE p.name IN [
    'VIEW_PROJECT', 'UPDATE_PROJECT',
    'CREATE_TASK', 'UPDATE_TASK_STATUS', 'DELETE_TASK', 'VIEW_TASK',
    'VIEW_MEMBER'
  ]
CREATE (r)-[:HAS_PERMISSION]->(p);

MATCH (u:User {username: 'test_user'})
MATCH (r:OrganizationRole {name: 'Restricted User'})
CREATE (u)-[:HAS_ORG_ROLE {
  organizationId: '33333333-3333-3333-3333-333333333333',
  grantedAt: datetime(),
  grantedBy: 'SYSTEM'
}]->(r);

MATCH (u:User {username: 'test_user'})
MATCH (r:ProjectRole {name: 'Project Power User'})
CREATE (u)-[:HAS_PROJECT_ROLE {
  projectId: '88888888-8888-8888-8888-888888888888',
  organizationId: '33333333-3333-3333-3333-333333333333',
  grantedAt: datetime(),
  grantedBy: 'SYSTEM'
}]->(r);

MATCH (u:User {username: 'regular_user'})
MATCH (r:ProjectRole {name: 'Web Design Lead'})
CREATE (u)-[:HAS_PROJECT_ROLE {
  projectId: '88888888-8888-8888-8888-888888888888',
  organizationId: '33333333-3333-3333-3333-333333333333',
  grantedAt: datetime(),
  grantedBy: 'SYSTEM'
}]->(r);

CREATE (restrictedProjectUser:User {
  id: '44444444-4444-4444-4444-444444444444',
  username: 'restricted_project_user',
  email: 'restricted_project@taskify.com',
  systemRole: 'USER',
  createdAt: datetime(),
  updatedAt: datetime()
});

CREATE (restrictedProjectRole:ProjectRole {
  id: 'aaaaaaaa-bbbb-cccc-dddd-ffffffffffff',
  name: 'Restricted Project User',
  description: 'Has fewer permissions in project than at org level',
  isDefault: false,
  createdAt: datetime(),
  updatedAt: datetime()
});

MATCH (p:Project {id: '77777777-7777-7777-7777-777777777777'})
MATCH (r:ProjectRole {name: 'Restricted Project User'})
CREATE (p)-[:HAS_ROLE]->(r);

MATCH (r:ProjectRole {name: 'Restricted Project User'})
MATCH (p:Permission)
  WHERE p.name IN ['VIEW_PROJECT', 'VIEW_TASK']
CREATE (r)-[:HAS_PERMISSION]->(p);

MATCH (u:User {username: 'restricted_project_user'})
MATCH (r:OrganizationRole {name: 'Project Manager'})
CREATE (u)-[:HAS_ORG_ROLE {
  organizationId: '33333333-3333-3333-3333-333333333333',
  grantedAt: datetime(),
  grantedBy: 'SYSTEM'
}]->(r);

// Assign restricted_project_user to Restricted Project Role
MATCH (u:User {username: 'restricted_project_user'})
MATCH (r:ProjectRole {name: 'Restricted Project User'})
CREATE (u)-[:HAS_PROJECT_ROLE {
  projectId: '77777777-7777-7777-7777-777777777777',
  organizationId: '33333333-3333-3333-3333-333333333333',
  grantedAt: datetime(),
  grantedBy: 'SYSTEM'
}]->(r);