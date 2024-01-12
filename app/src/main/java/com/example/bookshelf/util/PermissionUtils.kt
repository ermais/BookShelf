package com.example.bookshelf.util

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.bookshelf.ui.contactus.ContactUsFragment

fun isPermissionGranted(context: Context, permission: String): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        permission
    ) == PackageManager.PERMISSION_GRANTED
}

fun requestPermission(activity: Activity?, permission: String, requestCode: Int) {
    ActivityCompat.requestPermissions(activity!!, arrayOf(permission), requestCode)
}

fun callActionWithPermission(context: Context,activity: Activity?,permission: String,permissionCode:Int, callback:()->Unit){
    if (isPermissionGranted(
            context,
            permission
        )){
        callback()
    }else{
        requestPermission(
            activity,
          permission,
            permissionCode
        )
       callback()
    }
}



