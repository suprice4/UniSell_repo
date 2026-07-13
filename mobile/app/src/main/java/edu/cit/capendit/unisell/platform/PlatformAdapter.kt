package edu.cit.capendit.unisell.platform

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.cit.capendit.unisell.R

class PlatformAdapter(
    private val platforms: MutableList<PlatformResponse>,
    private val onEdit: (PlatformResponse) -> Unit,
    private val onDelete: (PlatformResponse) -> Unit
) : RecyclerView.Adapter<PlatformAdapter.PlatformViewHolder>() {

    class PlatformViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvPlatformName)
        val btnEdit: Button = view.findViewById(R.id.btnEditPlatform)
        val btnDelete: Button = view.findViewById(R.id.btnDeletePlatform)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlatformViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_platform, parent, false)
        return PlatformViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlatformViewHolder, position: Int) {
        val platform = platforms[position]
        holder.tvName.text = platform.name
        holder.btnEdit.setOnClickListener { onEdit(platform) }
        holder.btnDelete.setOnClickListener { onDelete(platform) }
    }

    override fun getItemCount(): Int = platforms.size

    fun updateData(newPlatforms: List<PlatformResponse>) {
        platforms.clear()
        platforms.addAll(newPlatforms)
        notifyDataSetChanged()
    }
}