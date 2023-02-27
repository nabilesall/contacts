package com.idrissa.tp1.custom

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat.startActivity
import com.idrissa.tp1.Person
import com.idrissa.tp1.R
import com.idrissa.tp1.activities.*


class PersonAdapater(private val activity: FirstActivity,
                     private val context : Context,
                     private var listeContact : List<Person>,
                     private val onRemoved: (Person) -> Unit) : BaseAdapter() {

    private var inflater: LayoutInflater = LayoutInflater.from(context)
    
    override fun getCount(): Int {
        return  listeContact.size
    }

    override fun getItem(position: Int): Person {
        return listeContact[position]
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    @SuppressLint("SetTextI18n", "ViewHolder", "InflateParams")
    override fun getView(position: Int, p1: View?, p2: ViewGroup?): View {
        val view = this.inflater.inflate(R.layout.adapter_contact,null)

        val personneCourrante = getItem(position)
        val nom = personneCourrante.nom
        val prenom = personneCourrante.prenom
        val genre = personneCourrante.genre.lowercase()
        val date = personneCourrante.dateNaiss
        val telephone = personneCourrante.telephone
        val mail = personneCourrante.mail
        val fav = personneCourrante.favoris
        val linkimage = Uri.parse(personneCourrante.linkImage)

        view.findViewById<TextView>(R.id.nom_contact).text = "$prenom $nom"
        view.findViewById<TextView>(R.id.telephone_contact).text = telephone

        if(linkimage.toString() != "null"){
            try {
                Log.e("linkimage","$linkimage")
                view.findViewById<ImageView>(R.id.photo_contact).setImageURI(Uri.parse(linkimage.toString()))
            }catch (ex : Exception){
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

        //data contact
        val dataContactIntent = Intent(context, ShowContactActivity::class.java)
            .putExtra("action","update")
            .putExtra("img",linkimage.toString())
            .putExtra("lastName",nom)
            .putExtra("firstName",prenom)
            .putExtra("genre",genre)
            .putExtra("dateNaiss",date)
            .putExtra("num",telephone)
            .putExtra("mail",mail)
            .putExtra("favoris",fav)
            .putExtra("position",position)
        // modify button
        view.findViewById<Button>(R.id.modifier_contact).setOnClickListener {
            dataContactIntent.setClass(context, FormActivity::class.java)
            startActivityForResult(activity ,dataContactIntent,0,null)
        }

        // contact information
        view.setOnClickListener{
            dataContactIntent.setClass(context, ShowContactActivity::class.java)
            startActivityForResult(activity ,dataContactIntent,0,null)
        }

        // delete contatc
        view.setOnLongClickListener {
            onRemoved.invoke(personneCourrante)
            Log.e("size ", listeContact.size.toString())
            activity.findViewById<TextView>(R.id.nombreDeContacts).text = this.listeContact.size.toString() + " contacts"

            notifyDataSetChanged()
            true
        }
        return view
    }

    private fun getPathFromURI(context: Context, linkimage: Uri): String? {
        var mediaCursor: Cursor? = null
        return try {
            val dataPath = arrayOf(MediaStore.Images.Media.DATA)
            mediaCursor = context.contentResolver.query(linkimage, dataPath, null, null, null)
            val column_index: Int = mediaCursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            mediaCursor.moveToFirst()
            mediaCursor.getString(column_index)
        } finally {
            if (mediaCursor != null) {
                mediaCursor.close()
            }
        }
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