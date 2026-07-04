package com.neilb.nonocraft.common

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import com.neilb.nonocraft.R

fun Context.showToast(message: String?, isError: Boolean = true) {
    val inflater = LayoutInflater.from(this)
    val layout = inflater.inflate(R.layout.toast_snackbar, null)

    val text = layout.findViewById<TextView>(R.id.toast_text)
    text.text = message ?: "Bilinmeyen bir hata oluştu"

    val toast = Toast(this)
    toast.duration = Toast.LENGTH_SHORT
    toast.setGravity(Gravity.BOTTOM, 0, 200)
    toast.view = layout
    toast.show()
}