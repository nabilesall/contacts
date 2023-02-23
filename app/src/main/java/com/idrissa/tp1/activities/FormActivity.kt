package com.idrissa.tp1.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment.DIRECTORY_PICTURES
import android.provider.MediaStore
import android.provider.OpenableColumns.*
import android.widget.RadioButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.android.material.snackbar.Snackbar
import com.idrissa.tp1.custom.PopupDialog
import com.idrissa.tp1.R
import kotlinx.android.synthetic.main.activity_form.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


@Suppress("DEPRECATION")
class FormActivity : AppCompatActivity() {

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
            val linkImg = results?.getString("img").toString()
            val genre = results?.getString("genre").toString()

            if(linkImg == "null"){
                imgDeCouverture.setImageResource(this.resources.getIdentifier(genre,"drawable",this.packageName))
            }

            when (genre) {
                "homme" -> {
                    groupeGenre.check(homme_btn.id)
                }
                "femme" -> {
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
                //val selectedDate : String =  DateFormat.getDateInstance(DateFormat.DATE_FIELD).format(now.time)

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
            this.nom = inputNom.text.toString()
            this.prenom = inputPrenom.text.toString()
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
                Snackbar.make(it,"Format numéro de téléphone invalide.",Snackbar.LENGTH_SHORT).show()
            }

            else if(!isEmailValid(adrMail)){
                Snackbar.make(it, R.string.sbErrMail,Snackbar.LENGTH_SHORT).show()
            }

            else{
                this.printMessage = true
            }

            val message : String = "Nom et prénom: " + this.nom + " " + this.prenom +"\n"+
                                    "Genre: "+this.genreSelected+"\n"+
                                    "Date de naissance: " + this.birth + "\n" +
                                    "Numéro de téléphone: "+ this.numereTelephone +"\n"+
                                    "Adresse Electronique: "+ this.adrMail + "\n"+
                                    "Ajouter aux favoris: "+ if(this.etatCB)  "Oui" else "Non" + "\n"+
                                    "link Image : "

            // save or cancel ?
            val popupDialog = PopupDialog(this)
            popupDialog.setTitrePopupDialog(getString(R.string.titrepopup))
            popupDialog.setMessagePopupDialog(message)
            popupDialog.setTextLeftButtonPopup("Annuler")
            popupDialog.setTextRightButtonPopup("Valider")

            // Cancel button
            popupDialog.getLeftButtonPopup().setOnClickListener{
                popupDialog.dismiss()
                Toast.makeText(this, " Annulé", Toast.LENGTH_SHORT).show()
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
                if(linkImage != Uri.EMPTY){
                    intentMainAct.data  = linkImage
                }

                setResult(Activity.RESULT_OK, intentMainAct)

                resetData()
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
            popupDialog.setTextLeftButtonPopup("App. photo")
            popupDialog.setTextRightButtonPopup("Galerie")

            //App photo
            popupDialog.getLeftButtonPopup().setOnClickListener{
                val popupDialogPermAppPhoto = PopupDialog(this)
                popupDialogPermAppPhoto.setTitrePopupDialog(getString(R.string.titreAutorisation))
                popupDialogPermAppPhoto.setMessagePopupDialog("Autoriser MyFisrtApp à accéser à votre caméra")
                popupDialogPermAppPhoto.setTextLeftButtonPopup(getString(R.string.nonBouton))
                popupDialogPermAppPhoto.setTextRightButtonPopup(getString(R.string.ouiBouton))

                popupDialogPermAppPhoto.getLeftButtonPopup().setOnClickListener{
                    popupDialogPermAppPhoto.dismiss()
                }

                popupDialogPermAppPhoto.getRightButtonPopup().setOnClickListener{
                    val prendrePhoto = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    //add
                    file = getFile(nomfichier)
                    val fileProvider = FileProvider.getUriForFile(this,"com.idrissa.tp1",file)
                    prendrePhoto.putExtra(MediaStore.EXTRA_OUTPUT,fileProvider)
                    //fin add

                    if(prendrePhoto.resolveActivity(this.packageManager) != null){
                        startActivityForResult(prendrePhoto, cameracode)

                        popupDialogPermAppPhoto.dismiss()
                        popupDialog.dismiss()
                    }else{
                        Toast.makeText(this, R.string.msgErrorPhoto, Toast.LENGTH_SHORT).show()
                        popupDialogPermAppPhoto.dismiss()
                        popupDialog.dismiss()
                    }
                }

                popupDialogPermAppPhoto.build()
            }

            //Gallery
            popupDialog.getRightButtonPopup().setOnClickListener{
                val popupDialogPermGallery = PopupDialog(this)
                popupDialogPermGallery.setTitrePopupDialog(getString(R.string.titreAutorisation))
                popupDialogPermGallery.setMessagePopupDialog("Autoriser MyFisrtApp à accéser à votre galerie")
                popupDialogPermGallery.setTextLeftButtonPopup(getString(R.string.nonBouton))
                popupDialogPermGallery.setTextRightButtonPopup(getString(R.string.ouiBouton))

                popupDialogPermGallery.getLeftButtonPopup().setOnClickListener{
                    popupDialogPermGallery.dismiss()
                    //popupDialog.dismiss()
                }

                popupDialogPermGallery.getRightButtonPopup().setOnClickListener{
                    // PICK INTENT picks item from data
                    // and returned selected item
                    val galleryIntent = Intent(Intent.ACTION_PICK)

                    //val galleryIntent = Intent(MediaStore.ACTION_PICK_IMAGES)
                    galleryIntent.type = "image/*"
                    startActivityForResult(galleryIntent,gallerycode)

                    popupDialogPermGallery.dismiss()
                    popupDialog.dismiss()
                }
                popupDialogPermGallery.build()

            }
            popupDialog.build()
        }

        //partie bonus 2 pour changer la
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
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == Activity.RESULT_OK){
            when(requestCode){
                gallerycode ->{
                    //val stm = StorageManager()
                    val fileUriGall = data!!.data
                    if (fileUriGall != null) this.linkImage = fileUriGall
                    imgDeCouverture.setImageURI(fileUriGall)

                    this.imageUploaded = true
                }

                cameracode ->{
                    val fileUriCamera = Uri.fromFile(file)
                    /*
                    //val imageChoisie = data?.extras?.get("data") as Bitmap
                    //imgDeCouverture.setImageBitmap(imageChoisie)
                    val imageChoisie = BitmapFactory.decodeFile(file.absolutePath)
                    //imgDeCouverture.setImageBitmap(imageChoisie)
                    val bytes = ByteArrayOutputStream()
                    imageChoisie.compress(Bitmap.CompressFormat.JPEG,100,bytes)
                    val path = MediaStore.Images.Media.insertImage(contentResolver,imageChoisie,"val",null)
                    val uri : Uri = Uri.parse(path)
                    imgDeCouverture.setImageURI(uri)
                    StorageManager().TelechargerImage(this, uri)*/

                    imgDeCouverture.setImageURI(fileUriCamera)
                    //StorageManager().telechargerImage(fileUri)
                    this.imageUploaded = true
                    //this.linkImage = StorageManager().getUrl()
                }
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    /***
     * check a correct email format
     */
    private fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

}