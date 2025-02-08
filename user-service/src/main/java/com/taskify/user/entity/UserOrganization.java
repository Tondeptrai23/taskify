package com.taskify.user.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "user_organizations")
public class UserOrganization {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "is_admin")
    private boolean isAdmin = false;

    @Column(name = "is_active")
    private boolean isActive = true;

    @CreatedDate
    @Column(name = "joined_at", nullable = false, updatable = false)
    private ZonedDateTime joinedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id", nullable = false)
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_role_id", nullable = false)
    private OrganizationRole role;
}