package edu.cit.capendit.unisell.admin.activitylog.model

data class ActivityLogResponse(
    val id: Long,
    val actorEmail: String,
    val actorRole: String,
    val actionType: String,
    val description: String,
    val targetType: String?,
    val targetId: Long?,
    val timestamp: String
)
