package edu.cit.capendit.unisell.admin.activitylog.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "activity_log")
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String actorEmail;

    // Nullable: a failed login against a non-existent email has no known role
    @Column(nullable = true)
    private String actorRole;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityActionType actionType;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(nullable = true)
    private String targetType;

    @Column(nullable = true)
    private Long targetId;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    public ActivityLog() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getActorEmail() { return actorEmail; }
    public void setActorEmail(String actorEmail) { this.actorEmail = actorEmail; }

    public String getActorRole() { return actorRole; }
    public void setActorRole(String actorRole) { this.actorRole = actorRole; }

    public ActivityActionType getActionType() { return actionType; }
    public void setActionType(ActivityActionType actionType) { this.actionType = actionType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getTargetType() { return targetType; }
    public void setTargetType(String targetType) { this.targetType = targetType; }

    public Long getTargetId() { return targetId; }
    public void setTargetId(Long targetId) { this.targetId = targetId; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}