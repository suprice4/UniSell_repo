package edu.cit.capendit.unisell.admin.activitylog.repository;

import edu.cit.capendit.unisell.admin.activitylog.model.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    // Added now so Part 2's controller doesn't need to touch the repository layer
    List<ActivityLog> findAllByOrderByTimestampDesc();
}