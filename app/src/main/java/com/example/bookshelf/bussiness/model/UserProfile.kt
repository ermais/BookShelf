package com.example.bookshelf.bussiness.model

import android.net.Uri
import java.util.UUID

data class UserProfile(
    val userUUID : String,
    val displayName : String,
    val firstName : String,
    val lastName : String,
    val userPhotoUrl : Uri,
    val email : String
) {

    constructor() : this(
        userUUID = "",
        displayName = "",
        firstName = "",
        lastName = "",
        userPhotoUrl = Uri.EMPTY,
        email = ""
    ){

    }

    fun map() : Map<String,Any>{
        return mapOf(
            "user_uuid" to userUUID,
            "display_name" to displayName,
            "first_name" to firstName,
            "last_name" to lastName,
            "user_photo_url" to userPhotoUrl,
            "email" to email
        )
    }
}