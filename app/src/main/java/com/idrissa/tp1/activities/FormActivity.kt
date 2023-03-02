package com.idrissa.tp1.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment.DIRECTORY_PICTURES
import android.provider.MediaStore
import android.provider.OpenableColumns.*
import android.util.Log
import android.widget.RadioButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.material.snackbar.Snackbar
import com.idrissa.tp1.R
import com.idrissa.tp1.custom.PopupDialog
import kotlinx.android.synthetic.main.activity_form.*
import kotlinx.android.synthetic.main.details_contact.*
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


@Suppress("DEPRECATION")
class FormActivity : AppCompatActivity() {

    private var linkImgForUpdate: String = ""
    private lateinit var genreSelected : String
    private lateinit var nom : String
    private lateinit var prenom : String
    private lateinit var birth :String
    private lateinit var numereTelephone : String
    private lateinit var adrMail : String
    private var etatCB : Boolean = false

    private var printMessage : Boolean = false
    private var imageUploaded : Boolean = false

    private lateinit var file : File
    private var nomfichier = ".jpg"
    private var gallerycode = 7187
    private var cameracode = 897498

    private var linkImage : Uri = Uri.EMPTY
    private var imgUriGallery : Uri = Uri.EMPTY

    private lateinit var actionAFaire : String
    private var indexContact : String = ""

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("ResourceType", "QueryPermissionsNeeded", "SimpleDateFormat", "IntentReset")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)

        /*
        *Pour les modif et ajout de nouveaux contacts
        */
        this.actionAFaire = intent?.extras?.getString("action").toString()

        if (actionAFaire == "add"){
            val firstname = intent?.extras?.getString("firstName").toString()
            inputPrenom.setText(firstname)
        }

        else if(actionAFaire == "update"){
            val results  = intent?.extras
            this.linkImgForUpdate = results?.getString("img").toString()
            val genre = results?.getString("genre").toString()

            if(linkImgForUpdate == "null"){
                imgDeCouverture.setImageResource(this.resources.getIdentifier(genre,"drawable",this.packageName))
            }else{
                try {
                    imgDeCouverture.setImageURI(Uri.parse(linkImgForUpdate))
                }catch (ex : Exception){
                    Log.e("erreur","$ex")
                }
            }

            when (genre) {
                "homme" -> {
                    groupeGenre.check(homme_btn.id)
                }
                "male" -> {
                    groupeGenre.check(homme_btn.id)
                }
                "femme" -> {
                    groupeGenre.check(femme_btn.id)
                }
                "female" -> {
                    groupeGenre.check(femme_btn.id)
                }
                else -> {
                    groupeGenre.check(autre_btn.id)
                }
            }

            inputNom.setText(results?.getString("lastName").toString())
            inputPrenom.setText(results?.getString("firstName").toString())
            inputBirth.setText(results?.getString("dateNaiss").toString())
            inputTelephone.setText(results?.getString("num").toString())
            inputMail.setText(results?.getString("mail").toString())
            cb_fav.isChecked = results!!.getBoolean("favoris",false)
            this.indexContact = results.getInt("position").toString()
        }

        //pour le datepickerdialog
        val now : Calendar = Calendar.getInstance()
        val nowMax : Calendar = Calendar.getInstance()
        val year = now.get(Calendar.YEAR)
        val month = now.get(Calendar.MONTH)
        val day = now.get(Calendar.DAY_OF_MONTH)
        //DatePickerDialog
        inputBirth.setOnClickListener {
            val dp = DatePickerDialog(this, { _, dpYear, dpMonth, dpDay ->
                now.set(Calendar.YEAR,dpYear)
                now.set(Calendar.MONTH,dpMonth)
                now.set(Calendar.DAY_OF_MONTH,dpDay)

                val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
                val dateTimeFormat : String = simpleDateFormat.format(now.time).toString()
                inputBirth.setText(dateTimeFormat)

            },year,month,day)
            val maxYear = nowMax.get(Calendar.YEAR)
            val maxMonth = nowMax.get(Calendar.MONTH)
            val maxDay = nowMax.get(Calendar.DAY_OF_MONTH)
            nowMax.set(maxYear, maxMonth, maxDay)
            dp.datePicker.maxDate = nowMax.timeInMillis
            dp.show()
        }

        //validation button
        validate_btn.setOnClickListener {

            val selectedId = groupeGenre.checkedRadioButtonId

            if(selectedId != -1){
                val rbChoosen = findViewById<RadioButton>(selectedId)
                this.genreSelected = rbChoosen.text as String
            }
            this.nom = inputNom.text.toString().capitalize(Locale.ROOT)
            this.prenom = inputPrenom.text.toString().capitalize(Locale.ROOT)
            birth = inputBirth.text.toString()
            adrMail = inputMail.text.toString()
            numereTelephone = inputTelephone.text.toString()
            etatCB = cb_fav.isChecked

            if(nom.isEmpty()){
                Snackbar.make(it, R.string.sbNom,Snackbar.LENGTH_SHORT).show()
            }

            else if(prenom.isEmpty()){
                Snackbar.make(it, R.string.sbPrenom,Snackbar.LENGTH_SHORT).show()
            }

            else if(adrMail.isEmpty()){
                Snackbar.make(it, R.string.sbMail,Snackbar.LENGTH_SHORT).show()
            }

            else if(numereTelephone.length <5 || numereTelephone.length >14){
                Snackbar.make(it,R.string.sbErrTel,Snackbar.LENGTH_SHORT).show()
            }

            else if(!isEmailValid(adrMail)){
                Snackbar.make(it, R.string.sbErrMail,Snackbar.LENGTH_SHORT).show()
            }

            else{
                this.printMessage = true
            }

            val message : String = getString(R.string.msgNom) + this.nom + " " + this.prenom +"\n"+
                                    getString(R.string.msgGenre) +this.genreSelected+"\n"+
                                    getString(R.string.msgBirth) + this.birth + "\n" +
                                    getString(R.string.msgNum) + this.numereTelephone +"\n"+
                                    getString(R.string.msgMail) + this.adrMail + "\n"+
                                    getString(R.string.msgFav) + if(this.etatCB)  getString(R.string.ouiBouton) else getString(R.string.nonBouton) + "\n"

            // save or cancel ?
            val popupDialog = PopupDialog(this)
            popupDialog.setTitrePopupDialog(getString(R.string.titrepopup))
            popupDialog.setMessagePopupDialog(message)
            popupDialog.setTextLeftButtonPopup(getString(R.string.annuler))
            popupDialog.setTextRightButtonPopup(getString(R.string.valider))

            // Cancel button
            popupDialog.getLeftButtonPopup().setOnClickListener{
                popupDialog.dismiss()
                Toast.makeText(this, R.string.annulee, Toast.LENGTH_SHORT).show()
            }

            // Validation
            popupDialog.getRightButtonPopup().setOnClickListener{
                val intentMainAct = Intent(this, FirstActivity::class.java)

                intentMainAct.putExtra("action",actionAFaire)

                intentMainAct.putExtra("nom", this.nom)
                intentMainAct.putExtra("prenom", this.prenom)
                intentMainAct.putExtra("genre",this.genreSelected)
                intentMainAct.putExtra("dateNaiss",this.birth)
                intentMainAct.putExtra("telephone", this.numereTelephone)
                intentMainAct.putExtra("mail",this.adrMail)
                intentMainAct.putExtra("favoris",this.etatCB)
                intentMainAct.putExtra("index",this.indexContact)
                if(this.actionAFaire == "update" && this.linkImage == Uri.EMPTY){
                    intentMainAct.data  = Uri.parse(this.linkImgForUpdate)
                }
                else if(linkImage != Uri.EMPTY){
                    intentMainAct.data  = linkImage
                }

                setResult(RESULT_OK, intentMainAct)
                popupDialog.dismiss()
                finish()
            }

            if(printMessage){ popupDialog.build() }
        }

        imgDeCouverture.setOnClickListener{
            // App photo or gallery ?
            val popupDialog = PopupDialog(this)
            popupDialog.setTitrePopupDialog(getString(R.string.titrepopup))
            popupDialog.setMessagePopupDialog(getString(R.string.msgImgDeCouv))
            popupDialog.setTextLeftButtonPopup(getString(R.string.appPhoto))
            popupDialog.setTextRightButtonPopup(getString(R.string.galerie))

            //App photo
            popupDialog.getLeftButtonPopup().setOnClickListener{
                val readCameraPermission = android.Manifest.permission.CAMERA
                if(ContextCompat.checkSelfPermission(this,readCameraPermission) == PackageManager.PERMISSION_GRANTED){
                    openCamera()
                }
                else{
                    requestPermissions(arrayOf(readCameraPermission), 2)
                }
                popupDialog.dismiss()
            }

            //Gallery
            popupDialog.getRightButtonPopup().setOnClickListener{
                val readImagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) android.Manifest.permission.READ_MEDIA_IMAGES else android.Manifest.permission.READ_EXTERNAL_STORAGE
                if(ContextCompat.checkSelfPermission(this, readImagePermission) == PackageManager.PERMISSION_GRANTED){
                    //permission granted
                    openGallery()
                }else{
                    requestPermissions(arrayOf(readImagePermission), 1)
                }
                popupDialog.dismiss()
            }
            popupDialog.build()
        }

        femme_btn.setOnClickListener {
            if(!this.imageUploaded){
                imgDeCouverture.setImageResource(R.drawable.femme)
            }
        }

        homme_btn.setOnClickListener{
            if(!this.imageUploaded){
                imgDeCouverture.setImageResource(R.drawable.homme)
            }
        }

        autre_btn.setOnClickListener{
            if(!this.imageUploaded){
                imgDeCouverture.setImageResource(R.drawable.homme)
            }
        }
    }

    /**
     * reset the data in the form
     */
    private fun resetData() {
        groupeGenre.check(homme_btn.id)
        imgDeCouverture.setImageResource(R.drawable.homme)
        inputNom.setText("")
        inputPrenom.setText("")
        inputBirth.setText("")
        inputTelephone.setText("")
        inputMail.setText("")
        cb_fav.isChecked = false
    }

    /**
     * git the file
     */
    private fun getFile(nomFichier: String): File {
        val strgDirec = getExternalFilesDir(DIRECTORY_PICTURES)
        return  File.createTempFile(nomFichier,".jpg",strgDirec)
    }

    /**
     * this function reprsente the activity result.
     * it restotres the data from the FirstActivity
     */
    @SuppressLint("WrongConstant")
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == Activity.RESULT_OK){
            when(requestCode){
                gallerycode ->{
                    this.imgUriGallery = uploadImageFromGallery(data!!.data!!)
                    this.linkImage = imgUriGallery
                    imgDeCouverture.setImageURI(imgUriGallery)
                    this.imageUploaded = true
                }

                cameracode ->{
                    val fileUriCamera = Uri.fromFile(file)
                    if (fileUriCamera != null) this.linkImage = fileUriCamera
                    imgDeCouverture.setImageURI(fileUriCamera)
                    this.imageUploaded = true
                }
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery()
            } else {
                Toast.makeText(this, getString(R.string.errorPerm), Toast.LENGTH_SHORT).show()
            }
        }

        if (requestCode == 2) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                Toast.makeText(this, getString(R.string.errorPerm), Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * upload image from gallery
     */
    @SuppressLint("Recycle")
    private fun uploadImageFromGallery(uri: Uri): Uri {
        val dataPath = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, dataPath, null, null, null)
        cursor!!.moveToFirst()
        val index = cursor.getColumnIndex(dataPath[0])
        val picturePath = cursor.getString(index)
        cursor.close()
        val bitmap = BitmapFactory.decodeFile(picturePath)
        val file = creteFile()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, FileOutputStream(file))

        return FileProvider.getUriForFile(this,"com.idrissa.tp1", file)
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK)
        file = getFile(nomfichier)
        val fileProvider = FileProvider.getUriForFile(this,"com.idrissa.tp1", file)
        galleryIntent.putExtra(MediaStore.EXTRA_OUTPUT,fileProvider)
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent, gallerycode)
    }

    private fun openCamera() {
        val prendrePhoto = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        file = getFile(nomfichier)
        val fileProvider = FileProvider.getUriForFile(this,"com.idrissa.tp1",file)
        prendrePhoto.putExtra(MediaStore.EXTRA_OUTPUT,fileProvider)
        startActivityForResult(prendrePhoto, cameracode)
    }

    /**
     * create a file
     */
    @SuppressLint("SimpleDateFormat")
    private fun creteFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(DIRECTORY_PICTURES)
        return File.createTempFile(imageFileName, ".jpg", storageDir)
    }

    /***
     * check a correct email format
     */
    private fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}