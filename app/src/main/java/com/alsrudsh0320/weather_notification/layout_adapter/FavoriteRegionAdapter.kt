package com.alsrudsh0320.weather_notification.layout_adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alsrudsh0320.weather_notification.R
import com.alsrudsh0320.weather_notification.datastore.FavoriteRegion

class FavoriteRegionAdapter(
    private var items: List<FavoriteRegion>,
    private val onRegionClick: (FavoriteRegion) -> Unit,
    private val onAlarmClick: (FavoriteRegion) -> Unit
) : RecyclerView.Adapter<FavoriteRegionAdapter.VH>() {

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvFavoriteRegionName: TextView = itemView.findViewById(R.id.tv_favorite_region_name)
        val btnFavoriteAlarm: ImageButton = itemView.findViewById(R.id.btn_favorite_alarm)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_item_favorite_list , parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]

        holder.tvFavoriteRegionName.text = listOf(
            item.region.regionLevel1,
            item.region.regionLevel2,
            item.region.regionLevel3
        ).filter { it.isNotBlank() }
            .joinToString(" ")

        holder.btnFavoriteAlarm.setImageResource(
            if (item.isAlarmEnabled) R.drawable.baseline_circle_notifications_24
            else R.drawable.baseline_notifications_off_24
        )

        holder.tvFavoriteRegionName.setOnClickListener {
            onRegionClick(item)
        }

        holder.btnFavoriteAlarm.setOnClickListener {
            onAlarmClick(item)
        }
    }

    override fun getItemCount(): Int = items.size

    fun update(newItems: List<FavoriteRegion>) {
        items = newItems
        notifyDataSetChanged()
    }
}