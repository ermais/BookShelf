package com.example.bookshelf.bussiness.repository.profile

import android.net.Uri
import com.example.bookshelf.bussiness.model.UserProfile
import com.example.bookshelf.bussiness.networkdata.FirestoreProfileDataSource
import java.util.UUID

class UserProfileRepository(val dataSource: FirestoreProfileDataSource) {

    suspend fun createProfile(profile : UserProfile) = dataSource.createProfile(profile)
     suspend fun getProfile(uuid: String) = dataSource.getProfile(uuid)
    suspend fun updateName(firstName : String, lastName:String,uuid: String) =
        dataSource.updateUserName(firstName,lastName,uuid)
    suspend fun updateDisplayName(displayName : String,uuid: String) =
        dataSource.updateDisplayName(displayName,uuid)

    suspend fun updatePhotoUrl(photoUrl : String,uuid: String) =
        dataSource.updateUserPhoto(photoUrl,uuid)

    suspend fun uploadProfilePicture(uuid: String,uri:Uri) =
        dataSource.uploadProfilePicture(uuid,uri)
}