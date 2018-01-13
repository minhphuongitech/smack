package com.pvsoft.smack.Utilities

/**
 * Created by minhp on 1/11/2018.
 */

const val BASE_URL = "https://chatpvsoft.herokuapp.com/v1/"
//const val BASE_URL = "http://localhost:3005/v1/"
//const val BASE_URL = "http://127.0.0.1:3005/v1/"  // Ip này chỉ dùng trên máy tính, trên emulator thì chuyển sang 10.0.2.2
//const val BASE_URL = "http://10.0.0.2:3005/v1/"
const val URL_REGISTER = "${BASE_URL}account/register"
const val URL_LOGIN ="${BASE_URL}account/login"
const val URL_CREATE_USER = "${BASE_URL}user/add"
const val BROADCAST_DATA_USER_CHANGE = "BROADCAST_DATA_USER_CHANGE"