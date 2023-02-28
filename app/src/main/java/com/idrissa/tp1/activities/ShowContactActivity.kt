package com.idrissa.tp1.activities

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.idrissa.tp1.Person
import com.idrissa.tp1.R
import kotlinx.android.synthetic.main.details_contact.*
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class ShowContactActivity : AppCompatActivity() {

    private lateinit var nom : String
    private lateinit var prenom : String
    private lateinit var birth :String
    private lateinit var numereTelephone : String
    private lateinit var adrMail : String
    private lateinit var genre : String
    private var etatCB : Boolean = false

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.details_contact)

        this.prenom = intent?.extras?.getString("firstName").toString()
        this.nom = intent?.extras?.getString("lastName").toString()
        this.numereTelephone = intent?.extras?.getString("num").toString()
        this.birth = intent?.extras?.getString("dateNaiss").toString()
        this.adrMail = intent?.extras?.getString("mail").toString()
        this.genre = intent?.extras?.getString("genre").toString()
        this.etatCB = intent?.extras!!.getBoolean("favoris",false)

        val linkImg = intent?.extras?.getString("img")

        if(linkImg.toString() != "null"){
            try {
                dc_photo.setImageURI(Uri.parse(linkImg))
            }catch (ex : Exception){
                Log.e("erreur","$ex")
            }
        } else{
            dc_photo.setImageResource(this.resources.getIdentifier(genre,"drawable",this.packageName))
        }

        dc_nom.text = "$prenom $nom"
        dc_telephone.text = numereTelephone

        dc_date_naissance.text = birth
        dc_mail.text = adrMail

        if(etatCB) {
            msgFav.text = R.string.estDansFavoris.toString()
        }else{
            msgFav.text = R.string.estPasDansFavoris.toString()
        }

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

        dc_send_mail.setOnClickListener{
            val mailIntent = Intent(Intent.ACTION_SENDTO)

            mailIntent.data = Uri.parse("mailto:")
            mailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(adrMail))
            mailIntent.putExtra(Intent.EXTRA_SUBJECT, "Objet du mail")
            mailIntent.putExtra(Intent.EXTRA_TEXT, "Héhé efface ça et mets ton message")

            startActivity(mailIntent)
        }

        dc_calendrier.setOnClickListener{
            val CALENDAR_EVENT_TIME = getMillisFromDate(birth)

            val builder = CalendarContract.CONTENT_URI.buildUpon().appendPath("time")
            ContentUris.appendId(builder, CALENDAR_EVENT_TIME)
            val uri = builder.build()

            val intent = Intent(Intent.ACTION_VIEW)
                .setData(uri)
            startActivity(intent)
        }
    }

    /**
     * Convert dateFormat to milliseconds
     */
    @SuppressLint("SimpleDateFormat")
    private fun getMillisFromDate(date: String): Long {
        return try {
            val sdf = SimpleDateFormat("dd/MM/yyyy")
            val mDate = sdf.parse(date)
            val timeInMilliseconds = mDate!!.time
            timeInMilliseconds
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }
}