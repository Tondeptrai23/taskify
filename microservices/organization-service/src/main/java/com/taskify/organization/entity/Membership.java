package com.taskify.organization.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "memberships")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
public class Membership {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "is_admin")
    private boolean isAdmin = false;

    @Column(name = "is_active")
    private boolean isActive = true;

    @Column(name = "role_id")
    private UUID roleId;

    @CreatedDate
    @Column(name = "joined_at", nullable = false, updatable = false)
    private ZonedDateTime joinedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private LocalUser user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    public Membership(Organization organization, LocalUser user) {
        this.organization = organization;
        this.user = user;
    }
}