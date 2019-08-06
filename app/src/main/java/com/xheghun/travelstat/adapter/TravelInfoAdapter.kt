package com.xheghun.travelstat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xheghun.travelstat.R
import com.xheghun.travelstat.data.TravelInfo2

class TravelInfoAdapter(var mContext: Context, var travelInfo: List<TravelInfo2>) : RecyclerView.Adapter<TravelViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TravelViewHolder {
        val view: View = LayoutInflater.from(mContext).inflate(R.layout.travel_list_item, parent, false)

        return TravelViewHolder(view)
    }

    override fun getItemCount(): Int {
        return travelInfo.size
    }

    override fun onBindViewHolder(holder: TravelViewHolder, position: Int) {
        holder.travel_place.text = travelInfo.get(position).place
        holder.travel_price.text = travelInfo.get(position).price
        holder.travel_description.text = travelInfo.get(position).description
        Glide.with(mContext).load(travelInfo.get(position).imgLink)
                .override(holder.travel_img.width, holder.travel_img.height).into(holder.travel_img)
    }


}

class TravelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val travel_img: ImageView = itemView.findViewById(R.id.rc_travel_img)
    val travel_place: TextView = itemView.findViewById(R.id.rc_travel_place)
    val travel_price: TextView = itemView.findViewById(R.id.rc_travel_price)
    val travel_description: TextView = itemView.findViewById(R.id.rc_travel_description)
}
