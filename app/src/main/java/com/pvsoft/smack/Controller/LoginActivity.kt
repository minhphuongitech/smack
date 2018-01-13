package com.pvsoft.smack.Controller

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.pvsoft.smack.R
import com.pvsoft.smack.Services.AuthService
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun signupBtnClicked(view: View) {
        val createAccountIntent = Intent(this, CreateUserActivity::class.java)
        startActivity(createAccountIntent)
        finish()
    }

    fun loginBtnClicked(view: View){
        AuthService.loginAccount(this, emailEdt.text.toString(), passwordEdt.text.toString()) {
            loginSuccess ->
            if (loginSuccess) {
                AuthService.findUserByEmail(this) {
                    hasUser ->
                    if (hasUser) {
                        finish()
                    }
                }
            }
        }
    }
}
