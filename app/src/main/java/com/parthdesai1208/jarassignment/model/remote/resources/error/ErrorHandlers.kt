package com.parthdesai1208.jarassignment.model.remote.resources.error

import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.parthdesai1208.jarassignment.model.remote.resources.APIError
import com.parthdesai1208.jarassignment.model.remote.resources.Resource
import com.google.android.material.snackbar.Snackbar

interface ErrorHandler {
    fun onError(resource: Resource<*, APIError>)
}

interface PaginatedErrorHandler {
    fun onInitialError(paginatedResource: Resource<*, APIError>)
    fun onPageLoadingError(paginatedResource: Resource<*, APIError>)
}


class SnackBarPaginatedErrorHandler(private val parentView: View) : PaginatedErrorHandler {
    override fun onInitialError(paginatedResource: Resource<*, APIError>) {
        showError(paginatedResource)
    }

    override fun onPageLoadingError(paginatedResource: Resource<*, APIError>) {
        showError(paginatedResource)
    }

    private fun showError(resource: Resource<*, APIError>) {
        val snackbar = Snackbar.make(
                parentView,
                resource.errorData?.message ?: "Unknown Error",
                Snackbar.LENGTH_INDEFINITE
        )
        snackbar.setAction("OK") {
            snackbar.dismiss()
        }
        snackbar.show()
    }
}

class SnackbarErrorHandler(private val parentView: View) : ErrorHandler {
    override fun onError(resource: Resource<*, APIError>) {
        val snackbar = Snackbar.make(
                parentView,
                resource.errorData?.message ?: "Unknown Error",
                Snackbar.LENGTH_INDEFINITE
        )
        val textView =
                snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        textView.maxLines = 5
        snackbar.setAction("OK") {
            snackbar.dismiss()
        }
        snackbar.show()
    }
}

class AlertErrorHandler(
        private val view: View,
        private val isCancelable: Boolean = true,
        private val func: (() -> Unit)? = null
) : ErrorHandler {
    override fun onError(resource: Resource<*, APIError>) {
        AlertDialog.Builder(view.context)
                .setMessage("Error")
                .setCancelable(isCancelable)
                .setMessage(resource.errorData?.message ?: "Unknown Error")
                .setPositiveButton("OK") { dialog, _ ->
                    if (func == null) dialog.dismiss() else func.invoke()
                }
                .create()
                .show()
    }
}

class ToastErrorHandler(private val view: View) : ErrorHandler {
    override fun onError(resource: Resource<*, APIError>) {
        Toast.makeText(
                view.context,
                resource.errorData?.message ?: "Unknown Error",
                Toast.LENGTH_SHORT
        ).show()
    }
}