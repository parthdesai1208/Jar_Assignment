package com.parthdesai1208.jarassignment.model.remote.resources

import android.annotation.SuppressLint
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.util.*
import kotlin.collections.HashMap

/**
 * Converts any object to the Map<String, Any> type
 * @return Map representing key value pairs of <String, Any>
 */
fun Any?.toMap(): HashMap<String, Any> {
    this?.apply {
        val gson = Gson()
        val type = object : TypeToken<HashMap<String, Any>>() {

        }.type
        return gson.fromJson(gson.toJson(this), type)
    }
    return HashMap<String, Any>()
}

fun <T : Any> Any?.toMapOfValue(): HashMap<String, T> {
    this?.apply {
        val gson = Gson()
        val type = object : TypeToken<HashMap<String, T>>() {

        }.type
        return gson.fromJson(gson.toJson(this), type)
    }
    return HashMap<String, T>()
}

/**
 * Converts any object to the Map<String, RequestBody> type
 * @return Map representing key value pairs of <String, RequestBody>
 */
fun Any.toRequestBodyMap(): HashMap<String, RequestBody> =
        this.toMap().mapValues {
            it.value.toString().toRequestBody()
        } as HashMap<String, RequestBody>


fun String?.toRequestBody(): RequestBody =
        RequestBody.create(MultipartBody.FORM, this ?: "")

@SuppressLint("DefaultLocale")
fun File.getMimeType(): String {
    val url = this.toURI().toURL().toString()
    val extension = url.substringAfterLast(".")
    when (extension.toLowerCase(Locale.getDefault())) {
        "jpg", "jpeg", "png" -> return "image/$extension"
        "mp4" -> return "video/$extension"
        "pdf" -> return "application/$extension"
        else -> return "image/*"
    }
}

fun File?.toMultipartBody(name: String): MultipartBody.Part? {
    this ?: return null
    return MultipartBody.Part.createFormData(
            name,
            this.name,
            RequestBody.create(MediaType.parse(getMimeType()), this)
    )
}