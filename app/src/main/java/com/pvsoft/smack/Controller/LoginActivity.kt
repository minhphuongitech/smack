package com.pvsoft.smack.Controller

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import com.pvsoft.smack.R
import com.pvsoft.smack.Services.AuthService
import com.pvsoft.smack.Utilities.AppUtils
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginSpinner.visibility = ProgressBar.INVISIBLE
    }

    fun signupBtnClicked(view: View) {
        val createAccountIntent = Intent(this, CreateUserActivity::class.java)
        startActivity(createAccountIntent)
        finish()
    }

    fun loginBtnClicked(view: View) {
        enableSpinner(true)
        val email = emailEdt.text.toString()
        val password = passwordEdt.text.toString()
        AppUtils.hideKeyboard(this, this)
        if (email.isNotEmpty() && password.isNotEmpty()) {
            AuthService.loginAccount(this, emailEdt.text.toString(), passwordEdt.text.toString()) { loginSuccess ->
                if (loginSuccess) {
                    AuthService.findUserByEmail(this) { hasUser ->
                        if (hasUser) {
                            finish()
                        } else {
                            AppUtils.errorToast(this, "Find User By Email has something went wrong, please try again.")
                            enableSpinner(false)
                        }
                    }
                } else {
                    AppUtils.errorToast(this, "Login Account has something went wrong, please try again.")
                    enableSpinner(false)
                }
            }
        } else {
            AppUtils.errorToast(this, "Make sure email and password are filled in.")
            enableSpinner(false)
        }
    }

    fun enableSpinner(enable: Boolean) {
        if (enable) {
            loginSpinner.visibility = ProgressBar.VISIBLE
        } else {
            loginSpinner.visibility = ProgressBar.INVISIBLE
        }

        loginAccountBtn.isEnabled = !enable
        signupBtn.isEnabled = !enable
    }
}
