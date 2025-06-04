package com.alsrudsh0320.weather_notification.layout_adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import com.alsrudsh0320.weather_notification.R

// 날씨 정보를 화면에 바인딩할 데이터 모델
data class ShortTermForecastItem(
    val time: String,       // 예: "10:00"
    @DrawableRes val skyIconRes: Int,
    val tmp: String,       // 예: "15°C"
    val pop: String,        // 예: "20%"
    val pcp: String,        // 예: "0.5mm"
    val reh: String,        // 예: "60%"
    val wsd: String         // 예: "3.2m/s"
)

class ShortTermForecastAdapter(
    private var items: List<ShortTermForecastItem>
) : RecyclerView.Adapter<ShortTermForecastAdapter.VH>() {

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvForecastTime: TextView = itemView.findViewById(R.id.tvForecastTime)
        val ivSky: ImageView = itemView.findViewById(R.id.ivSky)
        val tvTmp: TextView = itemView.findViewById(R.id.tvTmp)
        val tvPop: TextView = itemView.findViewById(R.id.tvPop)
        val tvPcp: TextView = itemView.findViewById(R.id.tvPcp)
        val tvReh: TextView = itemView.findViewById(R.id.tvReh)
        val tvWsd: TextView = itemView.findViewById(R.id.tvWsd)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_item_short_term_forecast, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.tvForecastTime.text = item.time
        holder.ivSky.setImageResource(item.skyIconRes)
        holder.tvTmp.text           = item.tmp
        holder.tvPop.text          = item.pop
        holder.tvPcp.text          = item.pcp
        holder.tvReh.text          = item.reh
        holder.tvWsd.text          = item.wsd
    }

    override fun getItemCount(): Int = items.size

    /** 새 데이터로 전체 화면을 갱신합니다. */
    fun update(newItems: List<ShortTermForecastItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}
