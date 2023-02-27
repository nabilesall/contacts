package com.idrissa.tp1

import android.net.Uri

class Person(
    var nom :String,
    prenom : String,
    genre : String,
    dateNaiss: String,
    telephone : String,
    mail : String,
    favoris : Boolean,
    linkImage : String) : java.io.Serializable {

    //private var nom : String
    private var prenom : String
    private var genre : String ="femme"
    private var dateNaiss : String
    private var telephone : String
    private var mail : String
    private var favoris : Boolean
    private var linkImage : String = "empty"

    init {
        //this.nom = nom
        this.prenom = prenom
        this.genre = genre
        this.dateNaiss = dateNaiss
        this.telephone = telephone
        this.mail = mail
        this.favoris = favoris
        this.linkImage = linkImage

    }
    //fun getNom() : String{ return this.nom }
    fun getPrenom() : String { return this.prenom }
    fun getGenre() : String {
        if (this.genre == null || this.genre == "Autre") return "femme"
        return this.genre.lowercase()}
    fun getDateNaiss() : String {
        if(this.dateNaiss == null) return "Ajoutez une date de naissance"
        return this.dateNaiss }
    fun getTelephne() : String { return this.telephone }
    fun getMail() : String {
        if(this.mail == null) return "Ajoutez une adresse mail"
        return this.mail}
    fun isFavoris() : Boolean { return this.favoris}

    fun getLinkImageUri() : Uri{ return Uri.parse(this.linkImage) }

    //fun setNom(nom: String) { this.nom = nom }
    fun setPrenom(prenom: String) { this.prenom = prenom }
    fun setGenre(genre: String) { this.genre = genre}
    fun setDateNaiss(dateNaiss: String) {this.dateNaiss = dateNaiss }
    fun setTelephone(telephone: String) { this.telephone = telephone }
    fun setMail(mail: String) { this.mail = mail }
    fun setFavoris(favoris : Boolean) { this.favoris = favoris }
    fun setLinkImage(linkImage: String){ this.linkImage = linkImage }
}

