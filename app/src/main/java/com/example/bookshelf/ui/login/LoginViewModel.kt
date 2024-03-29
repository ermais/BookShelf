package com.example.bookshelf.ui.login

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookshelf.bussiness.Result.data
import com.example.bookshelf.bussiness.model.UserProfile
import com.example.bookshelf.bussiness.repository.profile.UserProfileRepository
import com.example.bookshelf.data.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LoginViewModel(private val auth: FirebaseAuth,private val userProfileRepository: UserProfileRepository) : ViewModel() {

    private val _user = MutableLiveData<User>().apply {
        value = null
    }

    val email: MutableLiveData<String> by lazy {
        MutableLiveData<String>("")
    }
    val password: MutableLiveData<String> by lazy {
        MutableLiveData<String>("")
    }

    val invalidPassword by lazy {
        MutableLiveData<Boolean>(true)
    }

    val invalidEmail by lazy {
        MutableLiveData<Boolean>(true)
    }

    val invalidPasswordOrEmail by lazy {
        MutableLiveData<Boolean>(false)
    }

    val showProgressBar by lazy {
        MutableLiveData<Boolean>(false)
    }

    val hasProfile : MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }


    fun createProfile(profile:UserProfile) = viewModelScope.launch(Dispatchers.IO) {
        userProfileRepository.getProfile(auth.currentUser!!.uid)
            .collect{
                if (it.data == null){
                    userProfileRepository.createProfile(profile)
                        .collect()
                }
            }

    }

    fun loginWithEmailPassword(loginSuccessCallback: () -> Unit, loginFailCallback: () -> Unit) {
        if (canILogin()) {
            Log.d("LOGIN", "CAN LOGIN")
            showProgressBar.postValue(true)
            viewModelScope.launch {
                delay(50000)
            }
            auth.signInWithEmailAndPassword(email.value!!, password.value!!)
                .addOnCompleteListener {
                    Log.d("LOGIN", "Here, I am")
                    if (it.isSuccessful) {
                        Log.d("LOGIN", "navigate to main screen")
                        showProgressBar.postValue(false)
                        invalidPasswordOrEmail.postValue(false)
                        loginSuccessCallback()
                    } else {
                        showProgressBar.postValue(false)
                        invalidPasswordOrEmail.postValue(true)
                        loginFailCallback()
                    }
                }
                .addOnFailureListener {
                    showProgressBar.postValue(false)
                    invalidPasswordOrEmail.postValue(true)
                    loginFailCallback()
                    Log.d("LOGIN_VIEWMODEL", "Login failed!")
                }
        }
    }

    private fun canILogin(): Boolean {
        return email.value!!.isNotEmpty() && password.value!!.isNotEmpty()
    }

    fun validPassword() {
        if (password.value!!.isNotEmpty()) {
            if (password.value!!.length >= 8) {
                invalidPassword.postValue(false)
            } else {
                invalidPassword.postValue(true)
            }
        } else {
            invalidPassword.postValue(true)
        }
    }

    fun validEmail() {
        if (email.value!!.isNotEmpty()) {
            if (email.value!!.length >= 8) {
                invalidEmail.postValue(false)
            } else {
                invalidEmail.postValue(true)
            }
        } else {
            invalidEmail.postValue(true)
        }
    }
}