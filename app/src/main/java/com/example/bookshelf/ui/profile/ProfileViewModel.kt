package com.example.bookshelf.ui.profile

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.databinding.InverseMethod
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.bookshelf.bussiness.Result.data
import com.example.bookshelf.bussiness.model.User
import com.example.bookshelf.bussiness.model.UserProfile
import com.example.bookshelf.bussiness.repository.profile.UserProfileRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.util.UUID

class ProfileViewModel(
    private val repository: UserProfileRepository,
    private val user:FirebaseAuth,
    private val application: Application
    ) :
    ViewModel() {
//
//    internal val data = flow {
//        repository.getProfile(user.currentUser!!.uid).collect{
//            emit(it.data)
//        }
//    }
//    internal val profile = data.asLiveData() as MutableLiveData<UserProfile>
    private val _profile = MutableLiveData<UserProfile>()
    val profile : LiveData<UserProfile> get() = _profile
    private val _firstName = MutableLiveData<String>()
    val firstName : LiveData<String> get() = _firstName
    private val _lastName = MutableLiveData<String>()
    val lastName : LiveData<String> = _lastName
     private val _displayName = MutableLiveData<String>()

    val displayName : LiveData<String> = _displayName

    private val _userPhotoUrl = MutableLiveData<String>()
     val userPhotoUrl : LiveData<String> = _userPhotoUrl


    private val _email = MutableLiveData<String>()
     val email : MutableLiveData<String> = _email


    init {

//        viewModelScope.launch {
//            getProfile()
//        }
        viewModelScope.launch(Dispatchers.IO) {
            getProfile()
            Log.d("ProfileI","${profile.value} -----Init")
        }

    }

    fun updateName() = viewModelScope.launch {
        repository.updateName(firstName.value.toString(),lastName.value.toString(),
            user.currentUser!!.uid ).collect{

        }
    }

    fun updateDisplayName() = viewModelScope.launch {
        repository.updateDisplayName(displayName.value.toString(),user.currentUser!!.uid)
            .collect{

            }
    }
    fun updateUserPhotoUrl() = viewModelScope.launch {
        userPhotoUrl.value?.let {
            repository.updatePhotoUrl(it as Uri,user.currentUser!!.uid)
                .collect{

                }
        }
    }


    suspend fun getProfile() = viewModelScope.launch(Dispatchers.IO) {
        Log.d("Profile","getting 0-----")
        repository.getProfile(user.currentUser!!.uid)
            .collect{data->
                val userProfile = data.data
                viewModelScope.launch(Dispatchers.Main) {
                    userProfile?.let {
                        _profile.value = it
                        _displayName.value = it.displayName
                        _lastName.value = it.lastName
                        _email.value= it.email
                        _userPhotoUrl.value= it.userPhotoUrl
                    }
                    Log.d("ProfileM","${profile.value} ---UserProfile")
                }

            }
    }
}