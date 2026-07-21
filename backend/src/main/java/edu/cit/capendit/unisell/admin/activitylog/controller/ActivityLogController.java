package edu.cit.capendit.unisell.admin.activitylog.controller;

import edu.cit.capendit.unisell.admin.activitylog.dto.ActivityLogResponse;
import edu.cit.capendit.unisell.admin.activitylog.service.ActivityLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/activity-log")
public class ActivityLogController {

    @Autowired
    private ActivityLogService activityLogService;

    @GetMapping
    public ResponseEntity<List<ActivityLogResponse>> listRecentActivity() {
        List<ActivityLogResponse> response = activityLogService.getRecentActivity()
                .stream()
                .map(ActivityLogResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}