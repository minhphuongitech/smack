package com.pvsoft.smack.Controller

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.pvsoft.smack.R
import com.pvsoft.smack.Services.AuthService
import com.pvsoft.smack.Utilities.BROADCAST_DATA_USER_CHANGE
import com.pvsoft.smack.Utilities.UserDataService
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : AppCompatActivity() {

    private val userDataChangeBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            if (AuthService.isLoggedIn) {
                usernameNavHeader.text = UserDataService.name
                emailNavHeader.text = UserDataService.email
                loginBtnNavHeader.text = "Logout"
                val idResource = resources.getIdentifier(UserDataService.avatarName, "drawable", packageName)
                userAvatarNavHeaderImg.setImageResource(idResource)
                userAvatarNavHeaderImg.setBackgroundColor(UserDataService.returnAvatarColor(UserDataService.avatarColor))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        LocalBroadcastManager.getInstance(this).registerReceiver(userDataChangeBroadcastReceiver, IntentFilter(BROADCAST_DATA_USER_CHANGE))
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun loginBtnClicked(view: View) {
        if(AuthService.isLoggedIn) {
            UserDataService.logout()
            emailNavHeader.text = ""
            usernameNavHeader.text = ""
            loginBtnNavHeader.text = "Login"
            userAvatarNavHeaderImg.setImageResource(R.drawable.profiledefault)
            userAvatarNavHeaderImg.setBackgroundColor(Color.TRANSPARENT)
        } else {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }

    }

    fun sendMessageBtnClicked(view: View) {

    }

    fun addChannelBtnClicked(view: View) {

    }
}
