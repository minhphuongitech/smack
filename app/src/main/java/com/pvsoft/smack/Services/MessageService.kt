package com.pvsoft.smack.Services

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.pvsoft.smack.Models.Channel
import com.pvsoft.smack.Utilities.URL_GET_CHANNEL
import org.json.JSONException

/**
 * Created by minhp on 1/14/2018.
 */
object MessageService {
    val channels = ArrayList<Channel>()

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
}