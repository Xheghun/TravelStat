package com.xheghun.travelstat

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.xheghun.travelstat.data.TravelInfo
import kotlinx.android.synthetic.main.activity_new_travel.*
import java.io.ByteArrayOutputStream

class NewTravelActivity : FirebaseAppCompactActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_travel)

        storage = FirebaseStorage.getInstance()

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

                //uploadImage
                var storageRef = storage.reference
                var imagesRef: StorageReference? = storageRef.child("images/")
                travel_img.isDrawingCacheEnabled = true
                travel_img.buildDrawingCache()

                val bitmap = (travel_img.drawable as BitmapDrawable).bitmap
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()

                var uploadTask = imagesRef!!.putBytes(data)
                uploadTask.addOnFailureListener { Snackbar.make(root_view, "image upload failed", Snackbar.LENGTH_SHORT) }
                        .addOnSuccessListener {
                            val info = TravelInfo(place = place, price = price, description = description, imgLink = null)

                            database.child("travelInfo").child("$place ${auth.currentUser!!.uid}").setValue(info)
                                    .addOnSuccessListener { Toast.makeText(this, "info successfully uploaded", Toast.LENGTH_SHORT).show();onBackPressed() }
                                    .addOnFailureListener {
                                        Snackbar.make(root_view, "unable to upload info", Snackbar.LENGTH_INDEFINITE)
                                                .setAction("Try Again") { uploadTravelDetails() }
                                    }
                        }
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                        val permission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                        requestPermissions(permission, PERMISSION_CODE)
                    } else {
                        pickImageFromGallery()
                    }
                } else {
                    //version is below marshmallow
                    pickImageFromGallery()
                }
            } else if (options[i] == "Cancel") {
                dialogInterface.dismiss()
            }
        }
        builder.show()
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImageFromGallery()
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            travel_img.setImageURI(data?.data)
        }
    }

    companion object {
        private val IMAGE_PICK_CODE = 1000
        private val PERMISSION_CODE = 1001
    }
}