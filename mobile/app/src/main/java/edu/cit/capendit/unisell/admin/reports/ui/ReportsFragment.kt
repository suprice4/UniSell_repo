package edu.cit.capendit.unisell.admin.reports.ui

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.cit.capendit.unisell.R
import edu.cit.capendit.unisell.admin.reports.adapter.AdminReportAdapter
import edu.cit.capendit.unisell.core.ApiClient
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response

class ReportsFragment : Fragment(R.layout.fragment_admin_reports) {

    private lateinit var tvError: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var rvReports: RecyclerView
    private lateinit var reportAdapter: AdminReportAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvError = view.findViewById(R.id.tvError)
        progressBar = view.findViewById(R.id.progressBar)
        rvReports = view.findViewById(R.id.rvReports)

        reportAdapter = AdminReportAdapter(mutableListOf())
        rvReports.layoutManager = LinearLayoutManager(requireContext())
        rvReports.adapter = reportAdapter
        rvReports.isNestedScrollingEnabled = false

        loadReports()
    }

    private fun extractErrorMessage(response: Response<*>): String {
        val raw = response.errorBody()?.string()
        if (raw.isNullOrBlank()) return "Something went wrong (${response.code()})"
        return try {
            JSONObject(raw).optString("message", raw)
        } catch (e: Exception) {
            raw
        }
    }

    private fun setLoading(loading: Boolean) {
        progressBar.visibility = if (loading) View.VISIBLE else View.GONE
    }

    private fun showError(message: String?) {
        if (message == null) {
            tvError.visibility = View.GONE
        } else {
            tvError.text = message
            tvError.visibility = View.VISIBLE
        }
    }

    private fun loadReports() {
        showError(null)
        setLoading(true)
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = ApiClient.adminReportsApi.getVendorReports()
                if (response.isSuccessful && response.body() != null) {
                    reportAdapter.updateData(response.body()!!)
                } else {
                    showError(extractErrorMessage(response))
                }
            } catch (e: Exception) {
                showError("Network error: ${e.message}")
            } finally {
                setLoading(false)
            }
        }
    }
}
