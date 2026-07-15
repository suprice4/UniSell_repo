package edu.cit.capendit.unisell.platform.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.cit.capendit.unisell.R
import edu.cit.capendit.unisell.core.ApiClient
import edu.cit.capendit.unisell.platform.adapter.PlatformAdapter
import edu.cit.capendit.unisell.platform.model.PlatformRequest
import edu.cit.capendit.unisell.platform.model.PlatformResponse
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response

class PlatformFragment : Fragment(R.layout.fragment_platform) {

    private lateinit var etNewPlatform: EditText
    private lateinit var btnAddPlatform: Button
    private lateinit var tvPlatformError: TextView
    private lateinit var progressBarPlatforms: ProgressBar
    private lateinit var rvPlatforms: RecyclerView

    private val platformList = mutableListOf<PlatformResponse>()
    private lateinit var platformAdapter: PlatformAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etNewPlatform = view.findViewById(R.id.etNewPlatform)
        btnAddPlatform = view.findViewById(R.id.btnAddPlatform)
        tvPlatformError = view.findViewById(R.id.tvPlatformError)
        progressBarPlatforms = view.findViewById(R.id.progressBarPlatforms)
        rvPlatforms = view.findViewById(R.id.rvPlatforms)

        platformAdapter = PlatformAdapter(
            platformList,
            onEdit = { showEditPlatformDialog(it) },
            onDelete = { showDeletePlatformConfirm(it) }
        )
        rvPlatforms.layoutManager = LinearLayoutManager(requireContext())
        rvPlatforms.adapter = platformAdapter
        rvPlatforms.isNestedScrollingEnabled = false

        btnAddPlatform.setOnClickListener { addPlatform() }

        loadPlatforms()
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

    private fun setPlatformLoading(loading: Boolean) {
        progressBarPlatforms.visibility = if (loading) View.VISIBLE else View.GONE
        btnAddPlatform.isEnabled = !loading
        etNewPlatform.isEnabled = !loading
    }

    private fun showPlatformError(message: String?) {
        if (message == null) {
            tvPlatformError.visibility = View.GONE
        } else {
            tvPlatformError.text = message
            tvPlatformError.visibility = View.VISIBLE
        }
    }

    private fun loadPlatforms() {
        showPlatformError(null)
        setPlatformLoading(true)
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = ApiClient.platformApi.getPlatforms()
                if (response.isSuccessful && response.body() != null) {
                    platformAdapter.updateData(response.body()!!)
                } else {
                    showPlatformError(extractErrorMessage(response))
                }
            } catch (e: Exception) {
                showPlatformError("Network error: ${e.message}")
            } finally {
                setPlatformLoading(false)
            }
        }
    }

    private fun addPlatform() {
        val name = etNewPlatform.text.toString().trim()
        if (name.isEmpty()) {
            showPlatformError("Platform name cannot be empty")
            return
        }
        showPlatformError(null)
        setPlatformLoading(true)
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = ApiClient.platformApi.createPlatform(PlatformRequest(name))
                if (response.isSuccessful && response.body() != null) {
                    etNewPlatform.text.clear()
                    loadPlatforms()
                } else {
                    showPlatformError(extractErrorMessage(response))
                    setPlatformLoading(false)
                }
            } catch (e: Exception) {
                showPlatformError("Network error: ${e.message}")
                setPlatformLoading(false)
            }
        }
    }

    private fun showEditPlatformDialog(platform: PlatformResponse) {
        val input = EditText(requireContext())
        input.setText(platform.name)

        AlertDialog.Builder(requireContext())
            .setTitle("Edit Platform")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                val newName = input.text.toString().trim()
                updatePlatform(platform.id, newName)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updatePlatform(id: Long, name: String) {
        if (name.isEmpty()) {
            showPlatformError("Platform name cannot be empty")
            return
        }
        showPlatformError(null)
        setPlatformLoading(true)
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = ApiClient.platformApi.updatePlatform(id, PlatformRequest(name))
                if (response.isSuccessful && response.body() != null) {
                    loadPlatforms()
                } else {
                    showPlatformError(extractErrorMessage(response))
                    setPlatformLoading(false)
                }
            } catch (e: Exception) {
                showPlatformError("Network error: ${e.message}")
                setPlatformLoading(false)
            }
        }
    }

    private fun showDeletePlatformConfirm(platform: PlatformResponse) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Platform")
            .setMessage("Delete '${platform.name}'? This cannot be undone.")
            .setPositiveButton("Delete") { _, _ -> deletePlatform(platform.id) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deletePlatform(id: Long) {
        showPlatformError(null)
        setPlatformLoading(true)
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = ApiClient.platformApi.deletePlatform(id)
                if (response.isSuccessful) {
                    loadPlatforms()
                } else {
                    showPlatformError(extractErrorMessage(response))
                    setPlatformLoading(false)
                }
            } catch (e: Exception) {
                showPlatformError("Network error: ${e.message}")
                setPlatformLoading(false)
            }
        }
    }
}