package com.pvsoft.smack.Services

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.pvsoft.smack.Utilities.*
import org.json.JSONException
import org.json.JSONObject
import java.lang.reflect.Method

/**
 * Created by minhp on 1/11/2018.
 */
object AuthService {
    val TAG: String = "AuthService"
    var isLoggedIn = false
    var userEmail = ""
    var authToken = ""

    fun getBodyContentTypeOfAuthService(): String {
        return "application/json; charset=utf-8"
    }

    fun getHeadersOfAuthService(): MutableMap<String, String>{
        val headers: HashMap<String, String> = HashMap()
        headers.put("Authorization", "Bearer ${authToken}")
        return headers
    }

    fun registerUser(context: Context, email: String, password: String, complete: (Boolean) -> Unit) {

        val url = URL_REGISTER
        val jsonBody = JSONObject()
        jsonBody.put("email", email)
        jsonBody.put("password", password)
        val requestBody = jsonBody.toString()

        val registerRequest = object : StringRequest(Method.POST, url,
                Response.Listener { response ->
                    Log.d(TAG, "REGISTER USER RESPONSE : ${response}")
                    complete(true)
                },
                Response.ErrorListener { error ->
                    Log.d(TAG, "REGISTER USER ERROR : ${error}")
                    complete(false)
                }) {
            override fun getBodyContentType(): String {
                return getBodyContentTypeOfAuthService()
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }
        }

        Volley.newRequestQueue(context).add(registerRequest)
    }

    fun loginAccount(context: Context, email: String, password: String, complete: (Boolean) -> Unit) {
        val url = URL_LOGIN
        val jsonBody = JSONObject()
        jsonBody.put("email", email)
        jsonBody.put("password", password)
        val requestBody = jsonBody.toString()

        val loginRequest = object : JsonObjectRequest(Method.POST, url, null,
                Response.Listener { response ->
                    Log.d(TAG, "LOGIN ACCOUNT RESPONSE :${response}")
                    try {
                        userEmail = response.getString("user")
                        authToken = response.getString("token")
                        isLoggedIn = true
                        complete(true)
                    } catch (e: JSONException) {
                        Log.d(TAG, "LOGIN ACCOUNT EXCEPTION : ${e.localizedMessage}")
                        complete(false)
                    }
                },
                Response.ErrorListener { error ->
                    Log.d(TAG, "LOGIN ACCOUNT ERROR: ${error}")
                    complete(false)
                }) {
            override fun getBodyContentType(): String {
                return getBodyContentTypeOfAuthService()
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }
        }

        Volley.newRequestQueue(context).add(loginRequest)
    }

    fun createUser(context: Context, name: String, email: String, avatarName: String, avatarColor: String, complete: (Boolean) -> Unit) {
        val url = URL_CREATE_USER
        val jsonBody = JSONObject()
        jsonBody.put("name", name)
        jsonBody.put("email", email)
        jsonBody.put("avatarName", avatarName)
        jsonBody.put("avatarColor", avatarColor)
        val requestBody = jsonBody.toString()

        val createUserRequest = object : JsonObjectRequest(Request.Method.POST, url, null,
                Response.Listener { response ->
                    try {
                        UserDataService.id = response.getString("_id")
                        UserDataService.name = response.getString("name")
                        UserDataService.email = response.getString("email")
                        UserDataService.avatarColor = response.getString("avatarColor")
                        UserDataService.avatarName = response.getString("avatarName")
                        complete(true)
                    } catch (ex: JSONException) {
                        Log.d(TAG, "CREATE USER REQUEST EXCEPTION : ${ex.localizedMessage}")
                        complete(false)
                    }

                },
                Response.ErrorListener { error ->
                    Log.d(TAG, "CREATE USER ERROR: ${error}")
                    complete(false)
                }) {
            override fun getBodyContentType(): String {
                return getBodyContentTypeOfAuthService()
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }

            override fun getHeaders(): MutableMap<String, String> {
                return getHeadersOfAuthService()
            }
        }

        Volley.newRequestQueue(context).add(createUserRequest)
    }

}