package edu.cit.capendit.unisell.admin.vendors.ui

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.cit.capendit.unisell.R
import edu.cit.capendit.unisell.admin.vendors.adapter.VendorAdapter
import edu.cit.capendit.unisell.admin.vendors.model.VendorResponse
import edu.cit.capendit.unisell.admin.vendors.model.VendorStatusUpdateRequest
import edu.cit.capendit.unisell.core.ApiClient
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response

class VendorsFragment : Fragment(R.layout.fragment_admin_vendors) {

    private lateinit var tvError: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var rvVendors: RecyclerView
    private lateinit var vendorAdapter: VendorAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvError = view.findViewById(R.id.tvError)
        progressBar = view.findViewById(R.id.progressBar)
        rvVendors = view.findViewById(R.id.rvVendors)

        vendorAdapter = VendorAdapter(mutableListOf(), onToggle = { toggleVendor(it) })
        rvVendors.layoutManager = LinearLayoutManager(requireContext())
        rvVendors.adapter = vendorAdapter
        rvVendors.isNestedScrollingEnabled = false

        loadVendors()
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

    private fun loadVendors() {
        showError(null)
        setLoading(true)
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = ApiClient.adminVendorsApi.getVendors()
                if (response.isSuccessful && response.body() != null) {
                    vendorAdapter.updateData(response.body()!!)
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

    private fun toggleVendor(vendor: VendorResponse) {
        showError(null)
        setLoading(true)
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = ApiClient.adminVendorsApi.updateVendorStatus(
                    vendor.id,
                    VendorStatusUpdateRequest(!vendor.enabled)
                )
                if (response.isSuccessful && response.body() != null) {
                    loadVendors()
                } else {
                    showError(extractErrorMessage(response))
                    setLoading(false)
                }
            } catch (e: Exception) {
                showError("Network error: ${e.message}")
                setLoading(false)
            }
        }
    }
}
