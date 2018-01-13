package com.pvsoft.smack.Models

/**
 * Created by minhp on 1/14/2018.
 */
class Channel(val name: String, val description: String, val id: String) {
    override fun toString(): String {
        return "#$name"
    }
}