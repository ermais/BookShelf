package com.example.bookshelf.bussiness.networkdata

import android.net.Uri
import com.example.bookshelf.bussiness.Result.Result
import com.example.bookshelf.bussiness.model.UserProfile
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface ProfileDataSource {

    suspend fun createProfile(profile: UserProfile) : Flow<Result<Void>>
    suspend fun updateUserName(firstName:String,lastName:String,uuid: String) : Flow<Result<Void>>
    suspend fun updateUserPhoto(photoUri:Uri,uuid: String) : Flow<Result<Void>>
    suspend fun updateDisplayName(displayName : String,uuid: String) : Flow<Result<Void>>
    suspend fun getProfile(uuid: String) : Flow<Result<UserProfile?>>

}