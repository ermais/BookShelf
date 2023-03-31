package com.example.bookshelf.ui

import android.icu.text.DateFormat
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import com.google.android.material.snackbar.Snackbar
import java.util.*

object Utils {

    fun View.showSnackBar(
        view: View,
        msg: String,
        length: Int,
        actionMessage: CharSequence?,
        action: (View) -> Unit
    ) {
        val snackBar = Snackbar.make(view, msg, length)
        if (actionMessage != null) {
            snackBar.setAction(actionMessage) {
                action(this)
            }.show()
        } else {
            snackBar.show()
        }
    }


    @RequiresApi(Build.VERSION_CODES.N)
    fun bookShelfDateFormatter(date: Date):String{
        val df = DateFormat.getDateInstance(DateFormat.RELATIVE_MEDIUM,Locale.US)
        val stringDate = df.format(date)
        return stringDate
    }
}

