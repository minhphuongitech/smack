package com.pvsoft.smack.Controller

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.os.Message
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import com.pvsoft.smack.Models.Channel
import com.pvsoft.smack.R
import com.pvsoft.smack.Services.AuthService
import com.pvsoft.smack.Services.MessageService
import com.pvsoft.smack.Utilities.AppUtils
import com.pvsoft.smack.Utilities.BROADCAST_DATA_USER_CHANGE
import com.pvsoft.smack.Utilities.SOCKET_URL
import com.pvsoft.smack.Services.UserDataService
import io.socket.client.IO
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : AppCompatActivity() {

    val socket = IO.socket(SOCKET_URL)
    lateinit var channelAdapter: ArrayAdapter<Channel>
    var selectedChannel: Channel? = null

    private fun setupAdapters() {
        channelAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, MessageService.channels)
        channel_list.adapter = channelAdapter
    }

    //For Update navigate layout after logged in
    private val userDataChangeBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, p1: Intent?) {
//            if (AuthService.isLoggedIn) {
            if (App.prefs.isLoggedIn) {
                usernameNavHeader.text = UserDataService.name
                emailNavHeader.text = UserDataService.email
                loginBtnNavHeader.text = "Logout"
                val idResource = resources.getIdentifier(UserDataService.avatarName, "drawable", packageName)
                userAvatarNavHeaderImg.setImageResource(idResource)
                userAvatarNavHeaderImg.setBackgroundColor(UserDataService.returnAvatarColor(UserDataService.avatarColor))

                //Get channels list from server
                MessageService.getChannels(context) { complete ->
                    if (complete) {
                        if(MessageService.channels.count() > 0) {
                            selectedChannel = MessageService.channels[0]
                            channelAdapter.notifyDataSetChanged()
                            updateChannel()
                        }
                    }
                }
            }
        }
    }

    fun updateChannel() {
        mainChannelNameTxt.text = selectedChannel.toString()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        socket.connect()
        socket.on("channelCreated", onNewChannel)
        setupAdapters()

        if (App.prefs.isLoggedIn) {
            AuthService.findUserByEmail(this) { complete -> }
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(userDataChangeBroadcastReceiver, IntentFilter(BROADCAST_DATA_USER_CHANGE))
        
        channel_list.setOnItemClickListener { adapterView, view, i, l ->
            selectedChannel = MessageService.channels[i]
            drawer_layout.closeDrawer(GravityCompat.START)
            updateChannel()
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(userDataChangeBroadcastReceiver)
        socket.disconnect()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun loginBtnClicked(view: View) {
//        if (AuthService.isLoggedIn) {
        if (App.prefs.isLoggedIn) {
            UserDataService.logout()
            emailNavHeader.text = ""
            usernameNavHeader.text = ""
            loginBtnNavHeader.text = "Login"
            userAvatarNavHeaderImg.setImageResource(R.drawable.profiledefault)
            userAvatarNavHeaderImg.setBackgroundColor(Color.TRANSPARENT)
            channelAdapter.clear()
            App.prefs.isLoggedIn = false
            MessageService.channels.clear()
        } else {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }

    }

    fun sendMessageBtnClicked(view: View) {

    }

    fun addChannelBtnClicked(view: View) {
//        if (AuthService.isLoggedIn) {
        if (App.prefs.isLoggedIn) {
            AppUtils.showAlertDialog(this, this, "Add", "Cancel") { isPositiveButton, dialogView ->
                if (isPositiveButton) {
                    val channelName = dialogView.findViewById<EditText>(R.id.channelNameEdt).text.toString()
                    val channelDesc = dialogView.findViewById<EditText>(R.id.channelDescEdt).text.toString()
                    socket.emit("newChannel", channelName, channelDesc)
                } else {

                }
                AppUtils.hideKeyboard(this, this)
            }
        } else {

        }
    }

    private val onNewChannel = Emitter.Listener { args ->
        runOnUiThread {
            val channelName = args[0] as String
            val channelDescription = args[1] as String
            val channelId = args[2] as String

            val newChannel = Channel(channelName, channelDescription, channelId)
            MessageService.channels.add(newChannel)
            channelAdapter.notifyDataSetChanged()
        }
    }
}
