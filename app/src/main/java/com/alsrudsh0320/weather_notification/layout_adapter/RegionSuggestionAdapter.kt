package com.alsrudsh0320.weather_notification.layout_adapter


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alsrudsh0320.weather_notification.R
import com.alsrudsh0320.weather_notification.data_load.RegionPoint

class RegionSuggestionAdapter(
    private var items: List<RegionPoint>,
    private val onClick: (RegionPoint) -> Unit
) : RecyclerView.Adapter<RegionSuggestionAdapter.VH>() {

    inner class VH(val tv: TextView) : RecyclerView.ViewHolder(tv) {
        init {
            tv.setOnClickListener {
                onClick(items[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val tv = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_item_seach_result, parent, false) as TextView
        return VH(tv)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val rp = items[position]
        // 1~3단계(level1~3)를 공백으로 합쳐서 표시
        val fullName = listOf(rp.regionLevel1, rp.regionLevel2, rp.regionLevel3)
            .filter { it.isNotBlank() }
            .joinToString(" ")
        holder.tv.text = fullName
    }

    override fun getItemCount() = items.size

    /** 검색어에 맞게 리스트 갱신 **/
    fun update(newItems: List<RegionPoint>) {
        items = newItems
        notifyDataSetChanged()
    }
}
