package com.taskify.project.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "project_memberships")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
public class ProjectMembership {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

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
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    public ProjectMembership(Project project, LocalUser user) {
        this.project = project;
        this.user = user;
    }
}