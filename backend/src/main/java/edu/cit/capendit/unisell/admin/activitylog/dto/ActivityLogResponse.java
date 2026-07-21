package edu.cit.capendit.unisell.admin.activitylog.dto;

import edu.cit.capendit.unisell.admin.activitylog.model.ActivityLog;

import java.time.LocalDateTime;

public class ActivityLogResponse {

    private Long id;
    private String actorEmail;
    private String actorRole;
    private String actionType;
    private String description;
    private String targetType;
    private Long targetId;
    private LocalDateTime timestamp;

    public static ActivityLogResponse fromEntity(ActivityLog entity) {
        ActivityLogResponse dto = new ActivityLogResponse();
        dto.id = entity.getId();
        dto.actorEmail = entity.getActorEmail();
        dto.actorRole = entity.getActorRole();
        dto.actionType = entity.getActionType() != null ? entity.getActionType().name() : null;
        dto.description = entity.getDescription();
        dto.targetType = entity.getTargetType();
        dto.targetId = entity.getTargetId();
        dto.timestamp = entity.getTimestamp();
        return dto;
    }

    public Long getId() { return id; }
    public String getActorEmail() { return actorEmail; }
    public String getActorRole() { return actorRole; }
    public String getActionType() { return actionType; }
    public String getDescription() { return description; }
    public String getTargetType() { return targetType; }
    public Long getTargetId() { return targetId; }
    public LocalDateTime getTimestamp() { return timestamp; }
}