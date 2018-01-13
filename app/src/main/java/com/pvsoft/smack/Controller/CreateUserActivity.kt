package com.pvsoft.smack.Controller

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.pvsoft.smack.R
import com.pvsoft.smack.Services.AuthService
import com.pvsoft.smack.Utilities.BROADCAST_DATA_USER_CHANGE
import kotlinx.android.synthetic.main.activity_create_user.*
import java.util.*

class CreateUserActivity : AppCompatActivity() {

    var userAvatarName = "profiledefault"
    var userAvatarColor = "[1,0.5,0.5,0.5]"
    var random = Random()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)
        createUserProgressSpinner.visibility = ProgressBar.INVISIBLE
    }

    fun userAvatarImgClicked(view: View) {
        var color = random.nextInt(2)
        var avatar = random.nextInt(28)
        if (color == 0) {
            userAvatarName = "light${avatar}"
        } else {
            userAvatarName = "dark${avatar}"
        }

        userAvatarImg.setImageResource(resources.getIdentifier(userAvatarName, "drawable", this.packageName))
    }

    fun generateBackgroundColorBtnClicked(view: View) {
        var alpha = random.nextInt(255)
        var red = random.nextInt(255)
        var green = random.nextInt(255)
        var blue = random.nextInt(255)
        userAvatarColor = "[${alpha.toDouble() / 255}, ${red.toDouble() / 255}, ${green.toDouble() / 255}, ${blue.toDouble() / 255}]"
        userAvatarImg.setBackgroundColor(Color.argb(alpha, red, green, blue))
    }

    fun createUserBtnClicked(view: View) {
        enableSpinner(true)

        var username = usernameEdt.text.toString()
        var email = emailEdt.text.toString()
        var password = passwordEdt.text.toString()


        if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
            AuthService.registerUser(this, email, password) { registerSuccess ->
                if (registerSuccess) {
//                Toast.makeText(this,"Create new user is success!", Toast.LENGTH_SHORT).show()
                    AuthService.loginAccount(this, email, password) { loginSuccess ->
                        if (loginSuccess) {
                            AuthService.createUser(this, username, email, userAvatarName, userAvatarColor) { createUserSuccess ->
                                if (createUserSuccess) {

                                    val userDataChange = Intent(BROADCAST_DATA_USER_CHANGE)
                                    LocalBroadcastManager.getInstance(this).sendBroadcast(userDataChange)
                                    enableSpinner(false)
                                    finish()
                                } else {
                                    errorToast("Create user has something went wrong, please try again.")
                                }
                            }
                        } else {
                            errorToast("Login has something went wrong, please try again.")
                        }
                    }
                } else {
                    errorToast("Register has something went wrong, please try again.")
                }
            }
        } else {
            Toast.makeText(this, "Make sure Username, email and password are filled in.", Toast.LENGTH_SHORT).show()
            enableSpinner(false)
        }
    }

    fun errorToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        enableSpinner(false)
    }

    fun enableSpinner(enable: Boolean) {
        if (enable) {
            createUserProgressSpinner.visibility = ProgressBar.VISIBLE
        } else {
            createUserProgressSpinner.visibility = ProgressBar.INVISIBLE
        }

        loginAccountBtn.isEnabled = !enable
        generateBackgroundColorBtn.isEnabled = !enable
        userAvatarImg.isEnabled = !enable

    }

}
