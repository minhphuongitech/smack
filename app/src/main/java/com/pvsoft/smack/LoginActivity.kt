package com.pvsoft.smack

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun signupBtnClicked(view: View) {
        val createAccountIntent = Intent(this, CreateUserActivity::class.java)
        startActivity(createAccountIntent)
    }
}
