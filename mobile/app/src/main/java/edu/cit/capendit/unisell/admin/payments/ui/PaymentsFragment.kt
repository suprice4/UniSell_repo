package edu.cit.capendit.unisell.admin.payments.ui

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.cit.capendit.unisell.R
import edu.cit.capendit.unisell.admin.payments.adapter.AdminPaymentAdapter
import edu.cit.capendit.unisell.core.ApiClient
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response

class PaymentsFragment : Fragment(R.layout.fragment_admin_payments) {

    private lateinit var tvError: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var rvPayments: RecyclerView
    private lateinit var paymentAdapter: AdminPaymentAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvError = view.findViewById(R.id.tvError)
        progressBar = view.findViewById(R.id.progressBar)
        rvPayments = view.findViewById(R.id.rvPayments)

        paymentAdapter = AdminPaymentAdapter(mutableListOf())
        rvPayments.layoutManager = LinearLayoutManager(requireContext())
        rvPayments.adapter = paymentAdapter
        rvPayments.isNestedScrollingEnabled = false

        loadPayments()
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

    private fun loadPayments() {
        showError(null)
        setLoading(true)
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = ApiClient.adminPaymentsApi.getPayments()
                if (response.isSuccessful && response.body() != null) {
                    paymentAdapter.updateData(response.body()!!)
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
