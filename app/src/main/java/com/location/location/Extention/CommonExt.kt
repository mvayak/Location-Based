package com.location.location.Extention

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import com.location.location.R

private var alertDialog: AlertDialog? = null

// For Show Toast message
fun String.showToast(context: Context){
    Toast.makeText(context,this,Toast.LENGTH_SHORT).show()
}

// For Show Alert Dialog
fun Context.makeAlertDialog(
    isCancelable: Boolean,
    stringTitle: String?,
    stringMessage: String?,
    stringPositiveButton: String?,
    onClickListener: DialogInterface.OnClickListener,
    stringNegativeButton: String?,
    onNoClickListener: DialogInterface.OnClickListener?
) {
    val alertDialogBuilder = AlertDialog.Builder(this)
    alertDialogBuilder.setCancelable(isCancelable)

    if (stringTitle != null && stringTitle != "") {
        alertDialogBuilder.setTitle(stringTitle)
    }


    if (stringMessage != null && stringMessage != "") {
        alertDialogBuilder.setMessage(stringMessage)
    }

    if (stringPositiveButton != null && stringPositiveButton != "") {
        alertDialogBuilder.setPositiveButton(stringPositiveButton, onClickListener)
    } else {
        alertDialogBuilder.setPositiveButton(stringPositiveButton) { dialog, _ -> dialog.dismiss() }
    }

    if (stringNegativeButton != null && stringNegativeButton != "") {
        alertDialogBuilder.setNegativeButton(stringNegativeButton, onNoClickListener)
    }

    if (!(this as Activity).isFinishing) {
        alertDialog = alertDialogBuilder.create()
        alertDialog!!.show()



    }
}