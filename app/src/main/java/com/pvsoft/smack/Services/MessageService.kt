package com.pvsoft.smack.Services

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.pvsoft.smack.Models.Channel
import com.pvsoft.smack.Models.Message
import com.pvsoft.smack.Utilities.AppUtils
import com.pvsoft.smack.Utilities.URL_GET_CHANNEL
import com.pvsoft.smack.Utilities.URL_GET_MESSAGE
import org.json.JSONException

/**
 * Created by minhp on 1/14/2018.
 */
object MessageService {
    val channels = ArrayList<Channel>()
    val messages = ArrayList<Message>()

    fun getChannels(context: Context, complete: (Boolean) -> Unit) {
        val url = URL_GET_CHANNEL
        val channelsRequest = object : JsonArrayRequest(Method.GET, url, null,
                Response.Listener { response ->
                    try {
                        for (x in 0 until response.length()) {
                            val channel = response.getJSONObject(x)
                            val name = channel.getString("name")
                            val description = channel.getString("description")
                            val id = channel.getString("_id")
                            val newChannel = Channel(name, description, id)
                            this.channels.add(newChannel)
                        }
                        complete(true)
                    } catch (ex: JSONException) {
                        Log.d(AuthService.TAG, "Get Channels Exception:${ex.localizedMessage}")
                        complete(false)
                    }
                }, Response.ErrorListener { error ->
            Log.d(AuthService.TAG, "Channels Request error:${error.localizedMessage}")
            complete(false)
        }) {
            override fun getBodyContentType(): String {
                return AuthService.getBodyContentTypeOfAuthService()
            }

            override fun getHeaders(): MutableMap<String, String> {
                return AuthService.getHeadersOfAuthService()
            }
        }

        Volley.newRequestQueue(context).add(channelsRequest)
    }

    fun getMessage(context: Context, channelId: String, complete: (Boolean) -> Unit) {
        val url = "$URL_GET_MESSAGE$channelId"
        val messageRequest = object : JsonArrayRequest(Method.GET, url, null,
                Response.Listener { response ->
                    clearMessage()
                    try {
                        for (x in 0 until response.length()) {
                            val message = response.getJSONObject(x)
                            val messageBody = message.getString("messageBody")
                            val channelId = message.getString("channelId")
                            val userName = message.getString("userName")
                            val userAvatarName = message.getString("userAvatar")
                            val userAvatarColor = message.getString("userAvatarColor")
                            val messageId = message.getString("_id")
                            val timeStamp = AppUtils.formatDateString(message.getString("timeStamp"))
                            val newMessage = Message(messageBody, channelId, userName, userAvatarName, userAvatarColor, messageId, timeStamp)
                            MessageService.messages.add(newMessage)
                        }
                        complete(true)
                    } catch (ex: JSONException) {
                        Log.d(AuthService.TAG, "Get Message Exception:${ex.localizedMessage}")
                        complete(false)
                    }
                },
                Response.ErrorListener { error ->
                    Log.d(AuthService.TAG, "Get Message error:${error.localizedMessage}")
                    complete(false)
                }) {
            override fun getBodyContentType(): String {
                return AuthService.getBodyContentTypeOfAuthService()
            }

            override fun getHeaders(): MutableMap<String, String> {
                return AuthService.getHeadersOfAuthService()
            }
        }
        Volley.newRequestQueue(context).add(messageRequest)
    }

    fun clearMessage() {
        messages.clear()
    }

    fun clearChannels() {
        messages.clear()
    }
}