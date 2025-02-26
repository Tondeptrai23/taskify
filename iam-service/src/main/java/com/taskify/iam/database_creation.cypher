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
  updatedAt: datetime()
});

CREATE (user:User {
  id: '22222222-2222-2222-2222-222222222222',
  username: 'regular_user',
  email: 'user@taskify.com',
  systemRole: 'USER',
  createdAt: datetime(),
  updatedAt: datetime()
});

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
  id: 5,
  name: 'Role Management',
  description: 'Permissions related to role management',
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
  description: 'Ability to update member roles',
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

CREATE (manageRole:Permission {
  id: 5001,
  name: 'MANAGE_ROLE',
  description: 'Ability to create, update, and delete roles',
  prerequisites: ['VIEW_MEMBER'],
  createdAt: datetime(),
  updatedAt: datetime()
});

// Create Default Roles
CREATE (adminRole:Role {
  id: '55555555-5555-5555-5555-555555555555',
  name: 'Admin',
  description: 'Organization administrator with full access',
  isDefault: false,
  createdAt: datetime(),
  updatedAt: datetime()
});

CREATE (pmRole:Role {
  id: '77777777-7777-7777-7777-777777777777',
  name: 'Project Manager',
  description: 'Can manage projects and team members',
  isDefault: false,
  createdAt: datetime(),
  updatedAt: datetime()
});

CREATE (devRole:Role {
  id: '66666666-6666-6666-6666-666666666666',
  name: 'Developer',
  description: 'Can work on assigned tasks and view projects',
  isDefault: true,  // This is the default role for new members
  createdAt: datetime(),
  updatedAt: datetime()
});

// Create Sample Resources (Projects/Tasks)
CREATE (project1:Resource {
  id: '88888888-8888-8888-8888-888888888888',
  type: 'PROJECT',
  name: 'Sample Project 1',
  createdAt: datetime(),
  updatedAt: datetime()
});

CREATE (task1:Resource {
  id: '99999999-9999-9999-9999-999999999999',
  type: 'TASK',
  name: 'Sample Task 1',
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
MATCH (p:Permission {name: 'MANAGE_ROLE'})
CREATE (pg)-[:CONTAINS]->(p);

// Set up Role-Permission relationships
// Admin Role - gets all permissions
MATCH (r:Role {name: 'Admin'})
MATCH (p:Permission)
CREATE (r)-[:HAS_PERMISSION]->(p);

// Project Manager Role
MATCH (r:Role {name: 'Project Manager'})
MATCH (p:Permission)
  WHERE p.name IN [
    'CREATE_PROJECT', 'UPDATE_PROJECT', 'VIEW_PROJECT',
    'CREATE_TASK', 'UPDATE_TASK_STATUS', 'VIEW_TASK',
    'INVITE_MEMBER', 'UPDATE_MEMBER_ROLE', 'VIEW_MEMBER',
    'MANAGE_ROLE'
  ]
CREATE (r)-[:HAS_PERMISSION]->(p);

// Developer Role
MATCH (r:Role {name: 'Developer'})
MATCH (p:Permission)
  WHERE p.name IN [
    'VIEW_PROJECT',
    'CREATE_TASK', 'UPDATE_TASK_STATUS', 'VIEW_TASK',
    'VIEW_MEMBER'
  ]
CREATE (r)-[:HAS_PERMISSION]->(p);

// Connect Organization with Roles
MATCH (o:Organization {id: '33333333-3333-3333-3333-333333333333'})
MATCH (r:Role)
CREATE (o)-[:HAS_ROLE]->(r);

// Connect Users with Roles in Organization context
MATCH (u:User {username: 'system_admin'})
MATCH (r:Role {name: 'Admin'})
MATCH (o:Organization {id: '33333333-3333-3333-3333-333333333333'})
CREATE (u)-[:HAS_ROLE_IN {
  organizationId: o.id,
  grantedAt: datetime(),
  grantedBy: 'SYSTEM'
}]->(r);

MATCH (u:User {username: 'regular_user'})
MATCH (r:Role {name: 'Developer'})
MATCH (o:Organization {id: '33333333-3333-3333-3333-333333333333'})
CREATE (u)-[:HAS_ROLE_IN {
  organizationId: o.id,
  grantedAt: datetime(),
  grantedBy: 'SYSTEM'
}]->(r);

// Connect Resources to Organization
MATCH (o:Organization {id: '33333333-3333-3333-3333-333333333333'})
MATCH (r:Resource)
CREATE (r)-[:BELONGS_TO]->(o);

// Create indices for performance
CREATE INDEX user_id_index IF NOT EXISTS FOR (u:User) ON (u.id);
CREATE INDEX role_id_index IF NOT EXISTS FOR (r:Role) ON (r.id);
CREATE INDEX role_name_index IF NOT EXISTS FOR (r:Role) ON (r.name);
CREATE INDEX permission_id_index IF NOT EXISTS FOR (p:Permission) ON (p.id);
CREATE INDEX permission_name_index IF NOT EXISTS FOR (p:Permission) ON (p.name);
CREATE INDEX organization_id_index IF NOT EXISTS FOR (o:Organization) ON (o.id);
CREATE INDEX resource_id_index IF NOT EXISTS FOR (r:Resource) ON (r.id);