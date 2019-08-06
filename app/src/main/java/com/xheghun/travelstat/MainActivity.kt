package com.xheghun.travelstat

import android.app.AlertDialog
import android.content.DialogInterface
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
                R.id.list_refresh -> {
                    logRecyclerView()
                }
            }
            true
        }

        new_travel_fab.setOnClickListener { startActivity(Intent(this, NewTravelActivity::class.java)) }
        travel_list.layoutManager = LinearLayoutManager(this)
        travel_list.setHasFixedSize(true)
        logRecyclerView()
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
                .setTitle("Travel Stat")
                .setMessage("Application will Exit")
                .setPositiveButton("yes") { dialogInterface: DialogInterface, i: Int -> super.onBackPressed() }
                .show()

    }

    private fun logRecyclerView() {
        val firebaseRecyclerAdapter = object : FirebaseRecyclerAdapter<TravelInfo2, TravelViewHolder>(
                TravelInfo2::class.java,
                R.layout.travel_list_item,
                TravelViewHolder::class.java,
                reference
        ) {
            override fun populateViewHolder(holder: TravelViewHolder?, model: TravelInfo2?, position: Int) {
                pending_messing.visibility = View.GONE
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