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
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import com.pvsoft.smack.MessageAdapter
import com.pvsoft.smack.Models.Channel
import com.pvsoft.smack.Models.Message
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
    lateinit var messageAdapter: MessageAdapter
    var selectedChannel: Channel? = null

    private fun setupAdapters() {
        channelAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, MessageService.channels)
        channel_list.adapter = channelAdapter

        messageAdapter = MessageAdapter(this, MessageService.messages)
        message_list.adapter = messageAdapter
        val layoutManager = LinearLayoutManager(this)
        message_list.layoutManager = layoutManager
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
                        if (MessageService.channels.count() > 0) {
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
        if (selectedChannel != null) {
            MessageService.getMessage(this, selectedChannel!!.id) { complete ->
                if (complete) {
                    updateMessageLayout()
                }
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
        socket.connect()
        socket.on("channelCreated", onNewChannel)
        socket.on("messageCreated", onNewMessage)
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
            App.prefs.isLoggedIn = false
            mainChannelNameTxt.text = "Please Log In"
            MessageService.channels.clear()
            MessageService.messages.clear()
            channelAdapter.clear()
            messageAdapter.messageList.clear()
            channelAdapter.notifyDataSetChanged()
            messageAdapter.notifyDataSetChanged()
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }

    }

    fun sendMessageBtnClicked(view: View) {
        val typingMessage = messageEdt.text.toString()
        if (App.prefs.isLoggedIn && typingMessage.isNotEmpty() && selectedChannel != null) {
            socket.emit("newMessage", typingMessage, UserDataService.id, selectedChannel!!.id, UserDataService.name, UserDataService.avatarName, UserDataService.avatarColor)
            messageEdt.text.clear()
            AppUtils.hideKeyboard(this, this)
        }
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
        if (App.prefs.isLoggedIn) {
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

    private val onNewMessage = Emitter.Listener { args ->
        if (App.prefs.isLoggedIn) {
            runOnUiThread {
                val channelId: String = args[2] as String
                if (channelId == selectedChannel?.id) {
                    val message: String = args[0] as String

                    val userName: String = args[3] as String
                    val userAvatarName: String = args[4] as String
                    val userAvatarColor: String = args[5] as String
                    val messageId: String = args[6] as String
                    val timeStamp: String = args[7] as String

                    val newMessage = Message(message, channelId, userName, userAvatarName, userAvatarColor, messageId, AppUtils.formatDateString(timeStamp))
                    MessageService.messages.add(newMessage)
                    updateMessageLayout()
                }
            }
        }
    }

    fun updateMessageLayout() {
        messageAdapter.notifyDataSetChanged()
        if (messageAdapter.itemCount > 0) {
            message_list.smoothScrollToPosition(messageAdapter.itemCount - 1)
        }
    }
}
