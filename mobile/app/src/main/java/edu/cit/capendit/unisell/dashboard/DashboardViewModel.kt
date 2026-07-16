package edu.cit.capendit.unisell.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.cit.capendit.unisell.category.model.CategoryResponse
import edu.cit.capendit.unisell.core.ApiClient
import edu.cit.capendit.unisell.platform.model.PlatformResponse
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response

/**
 * Shared across CategoryFragment, PlatformFragment, and ProductFragment (all obtained via
 * activityViewModels() so they share one instance scoped to VendorDashboardActivity) so
 * categories and platforms are each fetched once per dashboard session instead of
 * independently by every fragment that needs them.
 *
 * OrderFragment intentionally does not use this — it has no dependency on categories or platforms.
 */
class DashboardViewModel : ViewModel() {

    private val _categories = MutableLiveData<List<CategoryResponse>>(emptyList())
    val categories: LiveData<List<CategoryResponse>> = _categories
    private var categoriesLoaded = false
    private var categoriesLoading = false

    private val _platforms = MutableLiveData<List<PlatformResponse>>(emptyList())
    val platforms: LiveData<List<PlatformResponse>> = _platforms
    private var platformsLoaded = false
    private var platformsLoading = false

    private fun extractErrorMessage(response: Response<*>): String {
        val raw = response.errorBody()?.string()
        if (raw.isNullOrBlank()) return "Something went wrong (${response.code()})"
        return try {
            JSONObject(raw).optString("message", raw)
        } catch (e: Exception) {
            raw
        }
    }

    fun loadCategoriesIfNeeded() {
        if (categoriesLoaded || categoriesLoading) return
        refreshCategories()
    }

    fun loadPlatformsIfNeeded() {
        if (platformsLoaded || platformsLoading) return
        refreshPlatforms()
    }

    fun refreshCategories(onComplete: ((success: Boolean, error: String?) -> Unit)? = null) {
        categoriesLoading = true
        viewModelScope.launch {
            try {
                val response = ApiClient.categoryApi.getCategories()
                if (response.isSuccessful && response.body() != null) {
                    _categories.value = response.body()!!
                    categoriesLoaded = true
                    onComplete?.invoke(true, null)
                } else {
                    onComplete?.invoke(false, extractErrorMessage(response))
                }
            } catch (e: Exception) {
                onComplete?.invoke(false, "Network error: ${e.message}")
            } finally {
                categoriesLoading = false
            }
        }
    }

    fun refreshPlatforms(onComplete: ((success: Boolean, error: String?) -> Unit)? = null) {
        platformsLoading = true
        viewModelScope.launch {
            try {
                val response = ApiClient.platformApi.getPlatforms()
                if (response.isSuccessful && response.body() != null) {
                    _platforms.value = response.body()!!
                    platformsLoaded = true
                    onComplete?.invoke(true, null)
                } else {
                    onComplete?.invoke(false, extractErrorMessage(response))
                }
            } catch (e: Exception) {
                onComplete?.invoke(false, "Network error: ${e.message}")
            } finally {
                platformsLoading = false
            }
        }
    }
}