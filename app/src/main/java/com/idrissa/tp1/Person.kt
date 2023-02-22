package com.idrissa.tp1

import android.net.Uri

class Person(
    nom :String,
    prenom : String,
    /*var genre : GenreType,*/
    dateNaiss: String,
    telephone : String,
    mail : String,
    favoris : Boolean,
    linkImage : String) {

    private var nom : String
    private var prenom : String
    private var dateNaiss : String
    private var telephone : String
    private var mail : String
    private var favoris : Boolean
    private var linkImage : String

    init {
        this.nom = nom
        this.prenom = prenom
        this.dateNaiss = dateNaiss
        this.telephone = telephone
        this.mail = mail
        this.favoris = favoris
        this.linkImage = linkImage

    }
    fun getNom() : String{ return this.nom }
    fun getPrenom() : String { return this.prenom }
    fun getDateNaiss() : String {
        if(this.dateNaiss == null) return "Ajoutez une date de naissance"
        return this.dateNaiss }
    fun getTelephne() : String { return this.telephone }
    fun getMail() : String {
        if(this.mail == null) return "Ajoutez une adresse mail"
        return this.mail}
    fun isFavoris() : Boolean { return this.favoris}

    fun getLinkImage() : String{ return this.linkImage }

    fun setNom(nom: String) { this.nom = nom }
    fun setPrenom(prenom: String) { this.prenom = prenom }
    fun setDateNaiss(dateNaiss: String) {this.dateNaiss = dateNaiss }
    fun setTelephone(telephone: String) { this.telephone = telephone }
    fun setMail(mail: String) { this.mail = mail }
    fun setFavoris(favoris : Boolean) { this.favoris = favoris }
    fun setLinkImage(linkImage: String){ this.linkImage = linkImage }


    /*override fun equals(other: Any?): Boolean {
        if(this.nom == other)
            return true
        if(this.prenom.contentEquals(other.toString()))
            return true
        return super.equals(other)
    }*/

}

