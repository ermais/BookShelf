package com.example.bookshelf.data

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
    val firstName: String,
    val lastName: String,
    val userName: String,
    val email: String
) {
    override fun toString(): String {
        return "User $firstName $lastName"
    }

    @Exclude
    fun toMap(): Map<String, Any> {
        return mapOf(
            "firstName" to firstName,
            "lastName" to lastName,
            "userName" to userName,
            "email" to email
        )
    }
}





