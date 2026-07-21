package edu.cit.capendit.unisell.admin.activitylog.api

import edu.cit.capendit.unisell.admin.activitylog.model.ActivityLogResponse
import retrofit2.Response
import retrofit2.http.GET

interface ActivityLogApi {

    @GET("admin/activity-log")
    suspend fun getRecentActivity(): Response<List<ActivityLogResponse>>
}
