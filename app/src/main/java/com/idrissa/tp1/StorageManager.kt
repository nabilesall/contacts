package com.idrissa.tp1

import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import com.idrissa.tp1.activities.MainActivity
import java.util.Calendar

class StorageManager {

    private val _tag = "Storage Manager"
    private var urlImage : String = ""

    private val storageRef = FirebaseStorage.getInstance().reference

    fun telechargerImage (imageUri: Uri){
        //MainActivity().setLinkImage("dans telechargement")
        //val sd = getFileName(context, imageUri!!)
        val pathDate = Calendar.getInstance().timeInMillis.toString() + ".jpg"
        // Upload Task with upload to directory 'file'
        // and name of the file remains same
        val imagepath  = "upload/$pathDate"
        val uploadTask = storageRef.child(imagepath).putFile(imageUri)

        // On success, download the file URL and display it
        uploadTask.addOnSuccessListener {
            // using glide library to display the image
            storageRef.child(imagepath).downloadUrl.addOnSuccessListener {
                /*Glide.with(context)
                    .load(it)
                    .into(imgDeCouverture)*/
                //this.imageUploaded = true
                println(MainActivity())
                this.urlImage =it.toString()
                //setUrl(this.urlImage)
                //this.urlImage = urlImage.toString()
                //MainActivity().setLinkImage(urlImage)

                //Log.e("uri","$urlImage")
                Log.e("Firebase", "download passed")
            }.addOnFailureListener {
                Log.e("Firebase", "Failed in downloading !!!")
            }
            Log.e(_tag, "Image téléchargée vers firebase")
            Log.e(_tag, pathDate)

        }.addOnFailureListener {
            Log.e("Firebase", "Image impossible à télécharger")
        }
    }

    fun getUrl() : String{ return this.urlImage }
    //private fun setUrl(urlImage: String){this.urlImage = urlImage}

    /*@SuppressLint("Range")
    private fun getFileName(context: Context, uri: Uri): String? {
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor.use {
                if (cursor != null) {
                    if(cursor.moveToFirst()) {
                        return cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    }
                }
            }
        }
        return uri.path?.lastIndexOf('/')?.let { uri.path?.substring(it) }
    }*/
}