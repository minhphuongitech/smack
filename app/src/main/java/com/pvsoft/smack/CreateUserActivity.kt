package com.pvsoft.smack

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class CreateUserActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)
    }

    fun generateUserAvatarTxtClicked(view: View){}
    fun generateBackgroundColorBtnClicked(view: View){}
    fun createUserBtnClicked(view: View){}

}
