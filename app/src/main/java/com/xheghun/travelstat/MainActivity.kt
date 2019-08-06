package com.xheghun.travelstat

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.xheghun.travelstat.data.TravelInfo2
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.travel_list_item.view.*

class MainActivity : FirebaseAppCompactActivity() {

    private lateinit var reference: DatabaseReference
    private lateinit var list: List<TravelInfo2>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        reference = FirebaseDatabase.getInstance().reference.child("travelInfo")
        toolbar.inflateMenu(R.menu.main_toolbar_option_menu)
        toolbar.setOnMenuItemClickListener {item: MenuItem? ->
            when(item!!.itemId) {
                R.id.logout -> {
                    auth.signOut()
                    startActivity(Intent(this, SignInActivity::class.java))
                }
            }
            true
        }

        new_travel_fab.setOnClickListener { startActivity(Intent(this, NewTravelActivity::class.java)) }

        list = ArrayList()

        travel_list.layoutManager = LinearLayoutManager(this)
        travel_list.setHasFixedSize(true)
        logRecyclerView()
    }


    private fun logRecyclerView() {
        val firebaseRecyclerAdapter = object : FirebaseRecyclerAdapter<TravelInfo2, TravelViewHolder>(
                TravelInfo2::class.java,
                R.layout.travel_list_item,
                TravelViewHolder::class.java,
                reference
        ) {
            override fun populateViewHolder(holder: TravelViewHolder?, model: TravelInfo2?, position: Int) {
                holder!!.itemView.rc_travel_place.text = model?.place
                holder.itemView.rc_travel_price.text = model?.price
                holder.itemView.rc_travel_description.text = model?.description
                Glide.with(applicationContext).load(model!!.imgLink)
                        .override(holder.itemView.rc_travel_img.width, holder.itemView.rc_travel_img.height)
                        .into(holder.itemView.rc_travel_img)

            }
        }
        val resId: Int = R.anim.layout_animation
        val animation: LayoutAnimationController = AnimationUtils.loadLayoutAnimation(travel_list.context, resId)
        travel_list.layoutAnimation = animation
        travel_list.adapter = firebaseRecyclerAdapter
    }

    class TravelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

}