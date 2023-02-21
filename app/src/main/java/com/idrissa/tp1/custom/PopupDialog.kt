package com.idrissa.tp1.custom

import android.app.Dialog
import android.content.Context
import android.widget.Button
import android.widget.TextView
import com.idrissa.tp1.R
import kotlinx.android.synthetic.main.popup_layout.*

class PopupDialog(context: Context) : Dialog(context) {

    private var titrePopupDialog : String
    private var messagePopupDialog : String
    private var leftButtonPopup : Button
    private var rightButtonPopup : Button
    private var titreView : TextView
    private var messageView : TextView


    init {
        setContentView(R.layout.popup_layout)

        this.titrePopupDialog = "Titre"
        this.messagePopupDialog ="msg"

        this.leftButtonPopup = leftButton
        this.rightButtonPopup = rightButton
        this.titreView = titrePopUp
        this.messageView = messagePopUp
    }

    fun getLeftButtonPopup(): Button { return this.leftButtonPopup }

    fun setTextLeftButtonPopup(textButton : String){ this.leftButtonPopup.text = textButton }

    fun getRightButtonPopup() : Button { return this.rightButtonPopup }

    fun setTextRightButtonPopup(textButton: String){ this.rightButtonPopup.text = textButton }

    fun setTitrePopupDialog(titrePopupDialog : String){
        this.titrePopupDialog = titrePopupDialog
    }

    fun setMessagePopupDialog(messagePopupDialog : String){
        this.messagePopupDialog = messagePopupDialog
    }

    fun build(){
        this.titreView.text = this.titrePopupDialog
        this.messageView.text = this.messagePopupDialog
        this.show()
    }

}