package com.xheghun.travelstat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : FirebaseAppCompactActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar.inflateMenu(R.menu.main_toolbar_option_menu)

        toolbar.setOnMenuItemClickListener {item: MenuItem? ->
            when(item!!.itemId) {
                R.id.new_travel -> { startActivity(Intent(this,NewTravelActivity::class.java))}
            }
            true
        }
    }
}