package com.taskify.user.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
@Entity
@Table(name = "organization_roles")
public class OrganizationRole {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "is_default")
    private boolean isDefault = false;

    @OneToMany(mappedBy = "role")
    private Set<UserOrganization> userOrganizations;
}
