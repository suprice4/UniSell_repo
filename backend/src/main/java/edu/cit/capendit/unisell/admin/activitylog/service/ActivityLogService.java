package edu.cit.capendit.unisell.admin.activitylog.service;

import edu.cit.capendit.unisell.admin.activitylog.model.ActivityActionType;
import edu.cit.capendit.unisell.admin.activitylog.model.ActivityLog;
import edu.cit.capendit.unisell.admin.activitylog.repository.ActivityLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ActivityLogService {

    @Autowired
    private ActivityLogRepository activityLogRepository;

    public List<ActivityLog> getRecentActivity() {
        return activityLogRepository.findTop100ByOrderByTimestampDesc();
    }

    public void log(String actorEmail, String actorRole, ActivityActionType actionType, String description) {
        log(actorEmail, actorRole, actionType, description, null, null);
    }

    public void log(String actorEmail, String actorRole, ActivityActionType actionType, String description,
                     String targetType, Long targetId) {
        ActivityLog entry = new ActivityLog();
        entry.setActorEmail(actorEmail);
        entry.setActorRole(actorRole);
        entry.setActionType(actionType);
        entry.setDescription(description);
        entry.setTargetType(targetType);
        entry.setTargetId(targetId);
        entry.setTimestamp(LocalDateTime.now());
        activityLogRepository.save(entry);
    }
}