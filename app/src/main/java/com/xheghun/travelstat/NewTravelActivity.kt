package com.xheghun.travelstat

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_new_travel.*

class NewTravelActivity : FirebaseAppCompactActivity() {

    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_travel)

        toolbar.inflateMenu(R.menu.new_travel_menu)

        toolbar.setOnMenuItemClickListener { item: MenuItem? ->
            when (item!!.itemId) {
                R.id.save -> {
                    uploadTravelDetails()
                }
                R.id.discard -> {
                    val dialog = AlertDialog.Builder(this)

                    dialog.setTitle("Discard Items")
                            .setMessage("You  will lose all data")
                            .setPositiveButton("Ok") { dialogInterface: DialogInterface, i: Int -> onBackPressed() }
                    dialog.show()
                }
            }
            true
        }

        if (auth.currentUser == null) {
            startActivity(Intent(this, SignInActivity::class.java))
        }
        database = FirebaseDatabase.getInstance().reference

        select_img_fab.setOnClickListener { selectImage() }
    }

    private fun uploadTravelDetails() {
        val place: String = travel_place.text.toString()
        val price: String = travel_price.text.toString()
        val description: String = travel_description.text.toString()
        val error = "this field is required"
        when {
            place.isEmpty() -> {
                travel_description_layout.isErrorEnabled = false; travel_price_layout.isErrorEnabled = true;
                travel_description_layout.error = ""; travel_price_layout.error = "";
                travel_place_layout.isErrorEnabled = true
                travel_place_layout.error = error
            }
            travel_price.text!!.isEmpty() -> {
                travel_description_layout.isErrorEnabled = false; travel_place_layout.isErrorEnabled = false
                travel_description_layout.error = ""; travel_place_layout.error = ""
                travel_price_layout.isErrorEnabled = true
                travel_price_layout.error = error
            }
            description.isEmpty() -> {
                travel_price_layout.isErrorEnabled = true; travel_place_layout.isErrorEnabled = false
                travel_price_layout.error = ""; travel_place_layout.error = ""
                travel_description_layout.isErrorEnabled = true
                travel_description_layout.error = error
            }
            else -> {
                travel_description_layout.isErrorEnabled = false; travel_price_layout.isErrorEnabled = true; travel_place_layout.isErrorEnabled = false
                travel_description_layout.error = ""; travel_price_layout.error = ""; travel_place_layout.error = ""
                val info = TravelInfo(place = place, price = price, description = description, imgLink = null)
                database.child("travelInfo").child(auth.currentUser!!.uid).setValue(info)
            }
        }
    }


    fun selectImage() {
        val builder = AlertDialog.Builder(this)
        val options = arrayOf("Take Photo", "Choose from Gallery", "Cancel")
        builder.setTitle("Select TravelInfo Image")
        builder.setItems(options) { dialogInterface, i ->
            if (options[i] == "Take Photo") {
                /*      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (check)
                }*/
                val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(takePicture, 0)
            } else if (options[i] == "Choose from Gallery") {
                val pickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(pickPhoto, 1)
            } else if (options[i] == "Cancel") {
                dialogInterface.dismiss()
            }
        }
        builder.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (resultCode) {
            Activity.RESULT_CANCELED -> {
                when (requestCode) {
                    0 -> {
                        if (requestCode == Activity.RESULT_OK && data != null) {
                            val selectedImage: Bitmap = data.extras!!.get("data") as Bitmap
                            travel_img.setImageBitmap(selectedImage)
                        }
                    }
                    1 -> {
                        if (requestCode == Activity.RESULT_OK && data != null) {
                            val selectedImage: Uri = data.data!!
                            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                            val cursor: Cursor? = contentResolver.query(selectedImage, filePathColumn, null, null, null)
                            if (cursor != null) {
                                cursor.moveToFirst()
                                val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                                val picturePath = cursor.getString(columnIndex)
                                val decodeFile = BitmapFactory.decodeFile(picturePath)
                                Glide.with(this).load(decodeFile).placeholder(R.drawable.ic_cloud).into(travel_img)
                                cursor.close()
                            }
                        }
                    }
                }
            }
        }
    }

    companion object {
        private val IMAGE_PICK_CODE = 1000
        private val PERMISSION_CODE = 1001
    }
}