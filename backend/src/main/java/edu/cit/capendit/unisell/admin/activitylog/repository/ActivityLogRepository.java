package edu.cit.capendit.unisell.admin.activitylog.repository;

import edu.cit.capendit.unisell.admin.activitylog.model.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    List<ActivityLog> findAllByOrderByTimestampDesc();

    // Part 2: recent-N limit for the read endpoint (capstone scale, not paginated)
    List<ActivityLog> findTop100ByOrderByTimestampDesc();
}