package edu.cit.capendit.unisell.admin.activitylog.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.cit.capendit.unisell.R
import edu.cit.capendit.unisell.admin.activitylog.model.ActivityLogResponse

class ActivityLogAdapter(
    private val entries: MutableList<ActivityLogResponse>
) : RecyclerView.Adapter<ActivityLogAdapter.ActivityLogViewHolder>() {

    class ActivityLogViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvActor: TextView = view.findViewById(R.id.tvActivityActor)
        val tvAction: TextView = view.findViewById(R.id.tvActivityAction)
        val tvDescription: TextView = view.findViewById(R.id.tvActivityDescription)
        val tvTimestamp: TextView = view.findViewById(R.id.tvActivityTimestamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityLogViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_activity_log, parent, false)
        return ActivityLogViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActivityLogViewHolder, position: Int) {
        val entry = entries[position]
        holder.tvActor.text = "${entry.actorEmail} (${entry.actorRole})"
        holder.tvAction.text = entry.actionType
        holder.tvDescription.text = entry.description
        holder.tvTimestamp.text = entry.timestamp
    }

    override fun getItemCount(): Int = entries.size

    fun updateData(newEntries: List<ActivityLogResponse>) {
        entries.clear()
        entries.addAll(newEntries)
        notifyDataSetChanged()
    }
}
