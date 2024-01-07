package com.example.bookshelf.ui.profile

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookshelf.bussiness.Result.data
import com.example.bookshelf.bussiness.model.UserProfile
import com.example.bookshelf.bussiness.repository.profile.UserProfileRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.UUID

class ProfileViewModel(
    private val repository: UserProfileRepository,
    private val user:FirebaseAuth,
    private val application: Application
    ) :
    ViewModel() {

        private val _profile  : MutableLiveData<UserProfile> =
            MutableLiveData<UserProfile>().apply {
            value = null
        }
    private val _firstName : MutableLiveData<String> by lazy {
        MutableLiveData<String>("")
    }
    private val _lastName : MutableLiveData<String> by lazy {
        MutableLiveData<String>("")
    }
    private val _displayName : MutableLiveData<String> by lazy {
        MutableLiveData<String>("")
    }
    private val _userPhotoUrl : MutableLiveData<Uri> by lazy {
        MutableLiveData<Uri>(Uri.EMPTY)
    }

    private val _email : MutableLiveData<String> by lazy {
        MutableLiveData<String>("")
    }
    val firstName get() = _firstName
    val lastName get() = _lastName
    val displayName get() = _displayName
    val userPhotoUrl get() = _userPhotoUrl
    val email get() = _email


    init {
        getProfile()
//        _lastName.value = _profile.value!!.lastName
//        _firstName.value = _profile.value!!.firstName
//        _email.value = _profile.value!!.email
//        _displayName.value = _profile.value!!.displayName
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
            repository.updatePhotoUrl(it,user.currentUser!!.uid)
                .collect{

                }
        }
    }

    fun getProfile() = viewModelScope.launch(Dispatchers.IO) {
        repository.getProfile(user.currentUser!!.uid)
            .collect{
                viewModelScope.launch {
                    Log.d("Profile","getting ")
                    _profile.value = it.data
                    val profile = _profile.value
                }

            }
    }
}