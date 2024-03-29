package com.example.bookshelf.bussiness.networkdata

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.bookshelf.bussiness.Result.Result
import com.example.bookshelf.bussiness.model.UserProfile
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import java.util.UUID

class FirestoreProfileDataSource(db : FirebaseFirestore,cloudStorage : FirebaseStorage) :
    ProfileDataSource {
    private val cloudRef = cloudStorage.reference
    private val profileRef = db.collection("profile")
    override suspend fun createProfile(profile: UserProfile): Flow<Result<Void>> {
        val flow = flow {
            try {
                emit(Result.Loading)
                val _profile = profileRef.document(profile.userUUID)
                    .set(
                        profile.map()
                    )
                    .await()
                emit(Result.Success(_profile))
            } catch (e: Exception) {
                emit(Result.Failure(e.message ?: e.toString()))
            }
        }
        return flow
    }

    override suspend fun updateUserName(
        firstName: String,
        lastName: String,
        uuid: String
    ): Flow<Result<Void>> {
        val flow = flow {
            try {
                emit(Result.Loading)
                val profile = profileRef.document(uuid.toString())
                    .update(
                        mapOf(
                            "firstName" to firstName,
                            "lastName" to lastName
                        )
                    )
                    .await()
                emit(Result.Success(profile))
            } catch (e: Exception) {
                emit(Result.Failure(e.message ?: e.toString()))
            }
        }
        return flow
    }

    override suspend fun updateUserPhoto(photoUri: String, uuid: String): Flow<Result<Void>> {
        val flow = flow {
            try {
                emit(Result.Loading)
                val profile = profileRef.document(uuid.toString())
                    .update(
                        mapOf(
                            "userPhotoUrl" to photoUri
                        )
                    ).await()
                emit(Result.Success(profile))
            } catch (e: Exception) {
                emit(Result.Failure(e.message ?: e.toString()))
            }
        }
        return flow
    }

    override suspend fun updateDisplayName(displayName: String, uuid: String): Flow<Result<Void>> {
        val flow = flow {
            try {
                emit(Result.Loading)
                val profile = profileRef.document(uuid.toString())
                    .update(
                        mapOf(
                            "displayName" to displayName
                        )
                    )
                    .await()
                emit(Result.Success(profile))
            } catch (e: Exception) {
                emit(Result.Failure(e.message ?: e.toString()))
            }
        }
        return flow
    }

    override suspend fun getProfile(uuid: String) = callbackFlow {
        val snapshot = profileRef.document(uuid)
            .addSnapshotListener { _snapshot, e ->
                val response = if (_snapshot != null) {
                    val _profile = _snapshot.toObject(UserProfile::class.java)
                    Log.d("ProfileRepo", "${_profile!!.displayName} ---Display Name")
                    Result.Success(_profile)
                } else {
                    Result.Failure(e?.message ?: e.toString())
                }
                trySend(response).isSuccess
            }
        awaitClose {
            snapshot.remove()
        }
    }

    override suspend fun uploadProfilePicture(uuid: String, uriFromFile: Uri) = callbackFlow {
        Result.Loading
        var downloadUri: Uri? = null
        val imageRef = cloudRef.child("profile/${uuid}/picture")
        val uploadTask = imageRef.putFile(uriFromFile)
        val loading = uploadTask.continueWithTask {
            imageRef.downloadUrl
        }.addOnSuccessListener {
            downloadUri = it
            trySend(Result.Success(it))
        }.addOnCompleteListener {
            if (it.isSuccessful) {
                Result.Success(it.result)
            } else {
                Result.Failure(it.exception?.message.toString())
            }
        }.addOnFailureListener {
            Result.Failure(it.message ?: it.toString())
        }
        awaitClose { loading.result }
    }

}