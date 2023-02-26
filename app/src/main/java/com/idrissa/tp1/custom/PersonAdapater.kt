package com.idrissa.tp1.custom

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat.startActivity
import com.idrissa.tp1.Person
import com.idrissa.tp1.R
import com.idrissa.tp1.activities.*
import kotlinx.android.synthetic.main.first_activity.view.*


class PersonAdapater(private val activity: FirstActivity,
                     private val context : Context,
                     private var listeContact : List<Person>,
                     private val onRemoved: (Person) -> Unit) : BaseAdapter() {

    private var inflater: LayoutInflater = LayoutInflater.from(context)
    
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
        val view = this.inflater.inflate(R.layout.adapter_contact,null)

        val personneCourrante = getItem(position) as Person
        val nom = personneCourrante.getNom()
        val prenom = personneCourrante.getPrenom()
        val genre = personneCourrante.getGenre()
        val date = personneCourrante.getDateNaiss()
        val telephone = personneCourrante.getTelephne()
        val mail = personneCourrante.getMail()
        val fav = personneCourrante.isFavoris()
        val linkimage = personneCourrante.getLinkImageUri()

        view.findViewById<TextView>(R.id.nom_contact).text = "$prenom $nom"
        view.findViewById<TextView>(R.id.telephone_contact).text = telephone

        if(linkimage.toString() != "null"){
            //Log.e("uri",linkimage)
            try {
                //activity.setImage(view,linkimage)
                //view.findViewById<ImageView>(R.id.photo_contact).setImageURI(linkimage)
            }catch (ex : java.lang.Exception){
                ex.printStackTrace()
                Log.e("erreur","$ex")
            }

        }else{
            view.findViewById<ImageView>(R.id.photo_contact)
                .setImageResource(context.resources.getIdentifier(genre,"drawable",context.packageName))

        }

        // call button
        view.findViewById<Button>(R.id.appeler_contact).setOnClickListener {
            val appelIntent = Intent(Intent.ACTION_DIAL,Uri.parse("tel:$telephone"))
            startActivity(activity,appelIntent,null)
        }

        // message button
        view.findViewById<Button>(R.id.message_contact).setOnClickListener {
            val messageIntent = Intent(Intent.ACTION_VIEW)
            messageIntent.type = "vnd.android-dir/mms-sms"
            messageIntent.putExtra("address", telephone)
            messageIntent.putExtra("sms_body", "Héhé efface ça et mets ton message")
            startActivity(activity,messageIntent, null)
        }

        // modify button
        view.findViewById<Button>(R.id.modifier_contact).setOnClickListener {
            val modifIntent = Intent(context, FormActivity::class.java)
                .putExtra("action","update")
                .putExtra("img",linkimage)
                .putExtra("lastName",nom)
                .putExtra("firstName",prenom)
                .putExtra("genre",genre)
                .putExtra("dateNaiss",date)
                .putExtra("num",telephone)
                .putExtra("mail",mail)
                .putExtra("favoris",fav)
                .putExtra("position",position)
            startActivityForResult(activity ,modifIntent,0,null)
        }


        // contact information
        view.setOnClickListener{
            //makeText(context, linkimage.toString(), Toast.LENGTH_SHORT).show()
            val modifIntent = Intent(context, ShowContactActivity::class.java)
                .putExtra("action","update")
                .putExtra("img",linkimage)
                .putExtra("lastName",nom)
                .putExtra("firstName",prenom)
                .putExtra("genre",genre)
                .putExtra("dateNaiss",date)
                .putExtra("num",telephone)
                .putExtra("mail",mail)
                .putExtra("favoris",fav)
                .putExtra("position",position)
            startActivityForResult(activity ,modifIntent,0,null)
        }

        // delete contatc
        view.setOnLongClickListener {
            onRemoved.invoke(personneCourrante)
            //FirstActivity().editNumberConatcts(listeContact.size.toString(),context)
            Log.e("size ", listeContact.size.toString())
            activity.findViewById<TextView>(R.id.nombreDeContacts).text = this.listeContact.size.toString() + " contacts"

            notifyDataSetChanged()
            true
        }
        return view
    }

    /**
     * set the current with the new list given
     * and notify the adapter
     */
    fun setListContact(listeContact: List<Person>){
        this.listeContact = listeContact
        activity.editNumberConatcts(count,"trouves")
        notifyDataSetChanged()
    }
}