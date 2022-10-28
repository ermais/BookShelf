package com.example.bookshelf.ui

import android.content.pm.PackageManager
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.bookshelf.R
import com.example.bookshelf.ui.Utils.showSnackBar
import com.google.android.material.snackbar.Snackbar

object Utils{

    fun View.showSnackBar(view:View,msg:String,length:Int,actionMessage:CharSequence?,action:(View)->Unit){
        val snackBar = Snackbar.make(view,msg,length)
        if (actionMessage != null){
            snackBar.setAction(actionMessage){
                action(this)
            }.show()
        }else {
            snackBar.show()
        }
    }
}