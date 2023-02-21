package com.idrissa.tp1.custom

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat.startActivity
import com.idrissa.tp1.Person
import com.idrissa.tp1.R
import com.idrissa.tp1.activities.*


class PersonAdapater(activity: FirstActivity, context : Context, listeContact : List<Person>, val onRemoved: (Person) -> Unit) : BaseAdapter() {

    private var activity : Activity
    private var context : Context
    private var listeContact : List<Person>
    private var inflater: LayoutInflater

    init {
        this.activity = activity
        this.context = context
        this.listeContact = listeContact
        this.inflater = LayoutInflater.from(context)

    }

    override fun getCount(): Int {
        return  listeContact.size
    }

    override fun getItem(position: Int): Any {
        return listeContact[position]
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    @SuppressLint("SetTextI18n", "ViewHolder", "InflateParams")
    override fun getView(position: Int, p1: View?, p2: ViewGroup?): View {
        val view = this.inflater.inflate(R.layout.adapter_contact, null)

        val personneCourrante = getItem(position) as Person
        val nom = personneCourrante.getNom()
        val prenom = personneCourrante.getPrenom()
        val date = personneCourrante.getDateNaiss()
        val telephone = personneCourrante.getTelephne()
        val mail = personneCourrante.getMail()
        val fav = personneCourrante.isFavoris()

        view.findViewById<TextView>(R.id.nom_contact).text = "$prenom $nom"
        view.findViewById<TextView>(R.id.telephone_contact).text = telephone

        view.findViewById<Button>(R.id.appeler_contact).setOnClickListener {
            val appelIntent = Intent(Intent.ACTION_DIAL,Uri.parse("tel:$telephone"))
            startActivity(activity,appelIntent,null)
        }

        view.findViewById<Button>(R.id.message_contact).setOnClickListener {
            val messageIntent = Intent(Intent.ACTION_VIEW)
            messageIntent.type = "vnd.android-dir/mms-sms"
            messageIntent.putExtra("address", telephone)
            messageIntent.putExtra("sms_body", "Héhé efface ça et mets ton message")
            startActivity(activity,messageIntent, null)
        }

        view.findViewById<Button>(R.id.modifier_contact).setOnClickListener {
            val modifIntent = Intent(context, MainActivity::class.java)
                .putExtra("action","update")
                .putExtra("lastName",nom)
                .putExtra("firstName",prenom)
                .putExtra("dateNaiss",date)
                .putExtra("num",telephone)
                .putExtra("mail",mail)
                .putExtra("favoris",fav)
                .putExtra("position",position)
            startActivityForResult(activity ,modifIntent,0,null)
        }

        view.setOnClickListener{
            makeText(context, "$prenom $nom"  , Toast.LENGTH_SHORT).show()
        }

        view.setOnLongClickListener {
            onRemoved.invoke(personneCourrante)
            //FirstActivity().editNumberConatcts(listeContact.size.toString(),context)
            Log.e("size ", listeContact.size.toString())

            true
        }
        return view
    }
}