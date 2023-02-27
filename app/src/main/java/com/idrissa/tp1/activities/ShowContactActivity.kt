package com.idrissa.tp1.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.idrissa.tp1.Person
import com.idrissa.tp1.R
import kotlinx.android.synthetic.main.details_contact.*

@Suppress("DEPRECATION")
class ShowContactActivity : AppCompatActivity() {

    private var firstActivity : FirstActivity = FirstActivity()

    private var listeDeContact = mutableListOf<Person>()

    private lateinit var genreSelected : String
    private lateinit var nom : String
    private lateinit var prenom : String
    private lateinit var birth :String
    private lateinit var numereTelephone : String
    private lateinit var adrMail : String
    private lateinit var genre : String
    private var etatCB : Boolean = false

    private var linkImage : Uri = Uri.EMPTY
    private var index : Int = -1

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.details_contact)

        this.listeDeContact = firstActivity.getListeDeContact()

        //Toast.makeText(this, this.listeDeContact.size.toString(), Toast.LENGTH_SHORT).show()

        this.prenom = intent?.extras?.getString("firstName").toString()
        this.nom = intent?.extras?.getString("lastName").toString()
        this.numereTelephone = intent?.extras?.getString("num").toString()
        this.birth = intent?.extras?.getString("dateNaiss").toString()
        this.adrMail = intent?.extras?.getString("mail").toString()
        this.genre = intent?.extras?.getString("genre").toString()
        this.etatCB = intent?.extras!!.getBoolean("favoris",false)
        this.index = intent?.extras?.getInt("position").toString().toInt()
        //this.linkImage = intent?.extras?.getUri("img")

        val linkImg = intent?.extras?.getString("img")

        //Log.e("link dans show","$linkImg")
        if(linkImg.toString() != "null"){
            //Log.e("uri",linkimage)
            try {
                //activity.setImage(view,linkimage)
                dc_photo.setImageURI(Uri.parse(linkImg))
            }catch (ex : Exception){
                Log.e("erreur","$ex")
            }

        }else{
                dc_photo.setImageResource(this.resources.getIdentifier(genre,"drawable",this.packageName))
        }

        dc_nom.text = "$prenom $nom"
        dc_telephone.text = numereTelephone

        dc_date_naissance.text = birth
        dc_mail.text = adrMail

        dc_appeler.setOnClickListener{
            val appelIntent = Intent(Intent.ACTION_DIAL,Uri.parse("tel:$numereTelephone"))
            ContextCompat.startActivity(this@ShowContactActivity, appelIntent, null)
        }

        dc_message.setOnClickListener{
            val messageIntent = Intent(Intent.ACTION_VIEW)
            messageIntent.type = "vnd.android-dir/mms-sms"
            messageIntent.putExtra("address", numereTelephone)
            messageIntent.putExtra("sms_body", "Héhé efface ça et mets ton message")
            ContextCompat.startActivity(this@ShowContactActivity, messageIntent, null)
        }

        dc_modifier.setOnClickListener{
            val modifIntent = Intent(this,FormActivity::class.java)
                .putExtra("action","update")
                .putExtra("img",linkImage)
                .putExtra("lastName",nom)
                .putExtra("firstName",prenom)
                .putExtra("genre",genre)
                .putExtra("dateNaiss",birth)
                .putExtra("num",numereTelephone)
                .putExtra("mail",adrMail)
                .putExtra("favoris",etatCB)
                .putExtra("position",index)
            ActivityCompat.startActivityForResult(this@ShowContactActivity, modifIntent, 0, null)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == RESULT_OK){
            val action  = data?.getStringExtra("action")
            this.nom = data?.getStringExtra("nom").toString()
            this.prenom = data?.getStringExtra("prenom").toString()
            this.genre = data?.getStringExtra("genre").toString()
            this.birth = data?.getStringExtra("dateNaiss").toString()
            this.numereTelephone = data?.getStringExtra("telephone").toString()
            this.adrMail = data?.getStringExtra("mail").toString()
            this.etatCB = data!!.getBooleanExtra("favoris",true)
            this.index = data.getStringExtra("index").toString().toInt()
            //this.linkImage = data.data!!


            if (action == "update"){
                //val pos= data.getStringExtra("index").toString()
                dc_nom.text = "$prenom $nom"
                dc_telephone.text = numereTelephone

                dc_date_naissance.text = birth
                dc_mail.text = adrMail
            }

            //firstActivity.filterListe()

        }
    }

    override fun onStop() {
        super.onStop()
        var intentMainAct = Intent(this, FirstActivity::class.java)

        intentMainAct.putExtra("action","update")

        intentMainAct.putExtra("nom", this.nom)
        intentMainAct.putExtra("prenom", this.prenom)
        intentMainAct.putExtra("genre",this.genre)
        intentMainAct.putExtra("dateNaiss",this.birth)
        intentMainAct.putExtra("telephone", this.numereTelephone)
        intentMainAct.putExtra("mail",this.adrMail)
        intentMainAct.putExtra("favoris",this.etatCB)
        intentMainAct.putExtra("index",this.index.toString())
        if(linkImage != Uri.EMPTY){
            intentMainAct.data  = linkImage
        }
        //startActivity(intentMainAct)
        setResult(RESULT_OK, intentMainAct)

        //Toast.makeText(this, "dans le stop", Toast.LENGTH_SHORT).show()

    }

    override fun onResume() {
        super.onResume()
        //Toast.makeText(this, "dans le resume", Toast.LENGTH_SHORT).show()
    }
}