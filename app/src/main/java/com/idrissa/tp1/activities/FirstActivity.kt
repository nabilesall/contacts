package com.idrissa.tp1.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.*
import com.idrissa.tp1.Person
import com.idrissa.tp1.R
import com.idrissa.tp1.custom.PersonAdapater
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.adapter_contact.*
import kotlinx.android.synthetic.main.first_activity.*
import java.io.*
import java.lang.reflect.Type


@Suppress("DEPRECATION")
class FirstActivity : AppCompatActivity() {
    private var listeDeContact = mutableListOf<Person>()
    private var filteredContactList  = emptyList<Person>()
    private val FILENAME : String = "ListesContact.txt"
    private val spname : String = "myContatcs"
    private lateinit var adapater: PersonAdapater

    private var search = ""
        set(value) {
            filterListe()
            field = value
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

        barre_de_recherche.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            val listContactFound  = ArrayList<Person>()
            override fun onQueryTextSubmit(query: String?): Boolean {
                search = query ?: ""
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                search = query ?: ""
                return false
            }
        })

        toForm.setOnClickListener{
            val int = Intent(this, MainActivity::class.java)
            int.putExtra("action","add")
            int.putExtra("firstName","Idrissa")
            startActivityForResult(int,0)
        }

        switch_favoris.setOnCheckedChangeListener { buttonView, isChecked ->
            filterListe()
            Toast.makeText(this,"clik",Toast.LENGTH_SHORT).show()
        }

    }

    private fun filterListe(){
        val newList = listeDeContact
            .filter { if(switch_favoris.isChecked) it.isFavoris() else true }
            .filter {if(search.isNotEmpty()) it.getPrenom().contains(search) else true }

        filteredContactList.plus(newList)
        adapater.notifyDataSetChanged()
    }

    //save data to sharedpreferences
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

    //to load the data from sharedpreferences
    fun loadData() {
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
            editNumberConatcts(0)
        }
        else{
            filteredContactList = listeDeContact
            editNumberConatcts(listeDeContact.size)
        }
    }

    private fun addListeDeContact(person: Person){
        listeDeContact.add(person)
    }

    fun setListeDeContact(listeDeContact : ArrayList<Person>){
        this.listeDeContact = listeDeContact
        //this.saveData()
    }

    @SuppressLint("SetTextI18n")
    fun editNumberConatcts(nb: Any){
        textView.text = "$nb Contacts"
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == RESULT_OK){
            // Get the result from data
            val action  = data?.getStringExtra("action")
            val nom = data?.getStringExtra("nom").toString()
            val prenom = data?.getStringExtra("prenom").toString()
            val datenaiss = data?.getStringExtra("dateNaiss").toString()
            val telephone = data?.getStringExtra("telephone").toString()
            val mail = data?.getStringExtra("mail").toString()
            val fav = data?.getBooleanExtra("favoris",true)

            //We check if the origin action is for add a new person
            if(action == "add"){
                val person = Person(nom, prenom, datenaiss, telephone, mail, fav!!)
                addListeDeContact(person)
            }

            //for update
            else if (action == "update"){
                val pos= data.getStringExtra("index").toString()

                val currPerson = this.listeDeContact[pos.toInt()]
                currPerson.setNom(nom)
                currPerson.setPrenom(prenom)
                currPerson.setDateNaiss(datenaiss)
                currPerson.setTelephone(telephone)
                currPerson.setMail(mail)
                currPerson.setFavoris(fav.toString().toBoolean())
            }

            /*val personadap = PersonAdapater(this@FirstActivity,this,listeDeContact)
            liste_de_contact.adapter = personadap
            editNumberConatcts(personadap.count.toString())
            this.saveData()*/

            // set the result to the text view
            Toast.makeText(this, "Enregistré", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStop() {
        super.onStop()
        this.saveData()
    }
}


