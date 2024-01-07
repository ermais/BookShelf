package com.example.bookshelf.bussiness.model

import android.net.Uri
import java.util.UUID

data class UserProfile(
    val userUUID : String,
    val displayName : String,
    val firstName : String,
    val lastName : String,
    val userPhotoUrl : String,
    val email : String
) {

    constructor() : this(
        userUUID = "",
        displayName = "",
        firstName = "",
        lastName = "",
        userPhotoUrl = "",
        email = ""
    ){

    }

    fun map() : Map<String,Any>{
        return mapOf(
            "userUUID" to userUUID,
            "displayName" to displayName,
            "firstName" to firstName,
            "lastName" to lastName,
            "userPhotoUrl" to userPhotoUrl,
            "email" to email
        )
    }
}