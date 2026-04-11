package com.blacksmith.quranlib.domain

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken


inline fun <reified T> parseFromJson(response: String?): T? {
    return try {
        val gson = Gson()
        val type = object : TypeToken<T>() {

        }.type
        gson.fromJson(response,type)

//        val b = GsonBuilder()
//        val serializer = BooleanSerializer()
//        b.registerTypeAdapter(Boolean::class.javaPrimitiveType, serializer)
//        val gson = b.create()
//        gson.fromJson(response,T::class.java)
    } catch (e1: Exception) {
        Log.i("JsonParser", "error Json: ${e1.message ?: ""}")
        e1.printStackTrace()
        null
    }
}

fun <T : Any> parseToJson(
    model: T
): String? {
    return try {
        var bodyjson = ""
        val gson = Gson()
        bodyjson = gson.toJson(model)
        bodyjson
    } catch (e1: Exception) {
        e1.printStackTrace()
        null
    }
}