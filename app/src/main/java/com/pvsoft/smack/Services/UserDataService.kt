package com.pvsoft.smack.Services

import android.graphics.Color
import java.util.*

/**
 * Created by minhp on 1/12/2018.
 */
object UserDataService {
    var id = ""
    var avatarName = ""
    var avatarColor = ""
    var email = ""
    var name = ""

    fun logout() {
        id = ""
        avatarColor = ""
        avatarName = ""
        email = ""
        name = ""
        AuthService.authToken = ""
        AuthService.isLoggedIn = false
        AuthService.userEmail = ""
    }

    fun returnAvatarColor(components: String): Int {
        val strippedColor = components.replace("[", "").replace("]", "").replace(",", "")
        val scanner = Scanner(strippedColor)
        var alpha = 0
        var red = 0
        var green = 0
        var blue = 0
        if (scanner.hasNext()) {
            alpha = (scanner.nextDouble() * 255).toInt()
            red = (scanner.nextDouble() * 255).toInt()
            green = (scanner.nextDouble() * 255).toInt()
            blue = (scanner.nextDouble() * 255).toInt()
        }
        return Color.argb(alpha, red, green, blue)
    }
}