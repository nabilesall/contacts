package com.idrissa.tp1.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.*
import com.idrissa.tp1.Person
import com.idrissa.tp1.R
import com.idrissa.tp1.custom.PersonAdapater
import kotlinx.android.synthetic.main.activity_form.*
import kotlinx.android.synthetic.main.adapter_contact.*
import kotlinx.android.synthetic.main.first_activity.*
import java.io.*
import java.lang.reflect.Type


@Suppress("DEPRECATION")
class FirstActivity : AppCompatActivity() {
    private var listeDeContact = mutableListOf<Person>()
    private var filteredContactList  = emptyList<Person>()
    private val spname : String = "myContatcs"
    private lateinit var adapater: PersonAdapater

    private var search = ""
        set(value) {
            field = value
            filterListe()
            if (value == "") displayItems("show")
            else displayItems("hide")
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.first_activity)

        loadData()
        adapater =  PersonAdapater(this@FirstActivity,applicationContext,filteredContactList) {
            listeDeContact.remove(it)
            filterListe()
        }
        liste_de_contact.adapter = adapater

        /**
         * search bar on contact list
         */
        barre_de_recherche.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                search = query ?: ""
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                search = query ?: ""
                return false
            }
        })

        /**
         * favorite option
         */
        switch_favoris.setOnCheckedChangeListener { _, _ ->
            filterListe()
        }

        /**
         * button to the form activity
         */
        toForm.setOnClickListener{
            val int = Intent(this, FormActivity::class.java)
            int.putExtra("action","add")
            int.putExtra("firstName","Idrissa")
            startActivityForResult(int,0)
        }

    }

    /**
     * this function filter the base list to display
     * the search results or the favorite contacts
     */
    private fun filterListe(){
        listeDeContact.sortBy {
            it.getPrenom()
        }
        this.filteredContactList = listeDeContact
            .filter { if(switch_favoris.isChecked){
                displayItems("hide"); it.isFavoris()
            }
            else{ displayItems("show") ; true}
            }

            .filter {if(search.isNotEmpty()){
                it.getPrenom().contains(search,true) ||
                        it.nom.contains(search,true) ||
                        it.getTelephne().contains(search,true)
            }
            else true }

        adapater.setListContact(filteredContactList)
    }

    /**
     * this function reprsente the activity result.
     * it restotres the data from the FormActivity
     */
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == RESULT_OK){
            // Get the result from data
            val action  = data?.getStringExtra("action")
            val nom = data?.getStringExtra("nom").toString()
            val prenom = data?.getStringExtra("prenom").toString()
            val genre = data?.getStringExtra("genre").toString()
            val datenaiss = data?.getStringExtra("dateNaiss").toString()
            val telephone = data?.getStringExtra("telephone").toString()
            val mail = data?.getStringExtra("mail").toString()
            val fav = data?.getBooleanExtra("favoris",true)
            val imageuri : Uri? = data?.data
            Log.e("image lien", "$imageuri")

            //We check if the origin action is for add a new person
            if(action == "add"){
                val person = Person(nom, prenom,genre, datenaiss, telephone, mail, fav!!,"$imageuri")
                addListeDeContact(person)
            }

            //for update
            else if (action == "update"){
                val pos= data.getStringExtra("index").toString()

                val currPerson = this.listeDeContact[pos.toInt()]
                currPerson.nom = nom
                currPerson.setPrenom(prenom)
                currPerson.setGenre(genre)
                currPerson.setDateNaiss(datenaiss)
                currPerson.setTelephone(telephone)
                currPerson.setMail(mail)
                currPerson.setFavoris(fav.toString().toBoolean())
                currPerson.setLinkImage(imageuri.toString())
            }
            filterListe()
            Toast.makeText(this, "Enregistré", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * save data to sharedpreferences
     */
    @SuppressLint("CommitPrefEdits")
    fun saveData(){
        val sp = this@FirstActivity.getSharedPreferences(spname, MODE_PRIVATE)
        val editor = sp.edit()

        val gson = Gson()

        val json : String = gson.toJson(this.listeDeContact)
        editor.putString("liste_contact",json)
        editor.apply()
        loadData()
    }

    /**
     * load the data from sharedpreferences
     */
    private fun loadData() {
        this.listeDeContact.clear()
        val sp = applicationContext.getSharedPreferences(spname, MODE_PRIVATE)
        val gson = Gson()
        val json : String? = sp.getString("liste_contact",null)
        val type : Type = object : TypeToken<ArrayList<Person>>(){
        }.type

        if(json != null){
            this.listeDeContact = gson.fromJson(json,type)
        }
        //this.listeDeContact = gson.fromJson(json,type)

        if (listeDeContact.size == 0){
            this.listeDeContact = ArrayList()
            //editNumberConatcts(0)
        }
        else{
            listeDeContact.sortBy {
                it.getPrenom()
            }
            filteredContactList = listeDeContact
            editNumberConatcts(listeDeContact.size,"")
        }
    }

    /**
     * add a new person in our list contact
     */
    private fun addListeDeContact(person: Person){
        listeDeContact.add(person)
    }

    /**
     * display the number of registred contacts
     */
    @SuppressLint("SetTextI18n")
    fun editNumberConatcts(nb: Int,option : String){
        if (option == "trouves"){
            if(nb != 0){
                contacts_trouves.text = "$nb contacts trouvés"
            }else contacts_trouves.text = "Aucun contact trouvé"
        }else nombreDeContacts.text = this.listeDeContact.size.toString() + " contacts"
    }

    /**
     * display or hide the items
     */
    private fun displayItems(visibilityOption : String){
        if(visibilityOption == "show"){
            contacts_trouves.visibility = View.GONE
        }
        else if (visibilityOption == "hide"){
            contacts_trouves.visibility = View.VISIBLE
        }
    }

    /**
     * this function is called when the activity is stopped
     */
    override fun onStop() {
        super.onStop()
        this.saveData()
    }

    /**
     * this function is called when the activity is resumed
     */
    override fun onResume() {
        super.onResume()
        filterListe()
    }
}


