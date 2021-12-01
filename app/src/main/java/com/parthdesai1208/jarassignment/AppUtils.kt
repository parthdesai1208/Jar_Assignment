package com.parthdesai1208.jarassignment

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CompletableDeferred
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

fun <T : Number> T?.orZero(): T = this ?: (0 as T)

fun <T : Number> T?.orOne(): T = this ?: (1 as T)

fun Boolean?.orFalse(): Boolean = this ?: false

fun Boolean?.orTrue(): Boolean = this ?: true

fun <T> Call<T>.enqueueDeferredResponse(
    lifeCycleOwner: LifecycleOwner? = null,
    callback: Callback<T>? = null
): CompletableDeferred<Response<T>> {

    val deferred = CompletableDeferred<Response<T>>()

    deferred.invokeOnCompletion {
        if (deferred.isCancelled) {
            this.cancel()
        }
    }

    lifeCycleOwner?.lifecycle?.addObserver(object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun cancelCalls() {
            this@enqueueDeferredResponse.cancel()
        }
    })

    this.enqueue(object : Callback<T> {
        override fun onFailure(call: Call<T>, t: Throwable) {
            callback?.onFailure(call, t)
            deferred.completeExceptionally(t)
        }

        override fun onResponse(call: Call<T>, response: Response<T>) {
            callback?.onResponse(call, response)
            deferred.complete(response)
        }

    })

    return deferred
}

fun ImageView.loadImage(t: Any?, id: Int?) {
    if (t is File || t is String) {
        id?.let {
            Glide.with(this.context)
                .asBitmap()
                .load(t)
                .format(DecodeFormat.PREFER_RGB_565)
                .centerCrop()
                .placeholder(it)
                .error(it)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                // .signature(ObjectKey(System.currentTimeMillis()))  //for load every time with latest images
                .timeout(60000)
                .into(this)
        }
    }
}

fun Context.isInternetAvailable(view: View?, anchorView: View? = view): Boolean {

    return if (checkInternet()) {
        true
    } else {
        view?.also {
            var returnSnackBar: Snackbar? = null
            returnSnackBar = view.let { it1 ->
                Snackbar.make(
                    it1,
                    getString(R.string.no_internet_connection),
                    Snackbar.LENGTH_LONG
                )
            }
            returnSnackBar.context.let { it1 -> ContextCompat.getColor(it1, R.color.error) }
                .let { it1 -> returnSnackBar.view.setBackgroundColor(it1) }
            returnSnackBar.show()
            return false
        } ?: run {
            Toast.makeText(this, getString(R.string.no_internet_connection), Toast.LENGTH_LONG)
                .show()
            return false
        }
        return false


    }
}

private fun Context.checkInternet(): Boolean {
    val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val cap: NetworkCapabilities = cm.getNetworkCapabilities(cm.activeNetwork) ?: return false
        return cap.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    } else {
        val networks: Array<Network> = cm.allNetworks
        for (n in networks) {
            @Suppress("DEPRECATION") //don't worry This method was deprecated in API level 29.
            val nInfo = cm.getNetworkInfo(n)
            @Suppress("DEPRECATION")  //don't worry This method was deprecated in API level 29.
            if (nInfo != null && nInfo.isConnected) return true
        }
    }
    return false
}