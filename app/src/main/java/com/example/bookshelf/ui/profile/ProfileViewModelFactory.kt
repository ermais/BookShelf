package com.example.bookshelf.ui.profile

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bookshelf.bussiness.repository.profile.UserProfileRepository
import com.google.firebase.auth.FirebaseAuth
import java.security.PrivateKey

class ProfileViewModelFactory(
    private val repository: UserProfileRepository,
    private val user:FirebaseAuth,
    private val application: Application
    ) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)){
            return ProfileViewModel(repository, user,application) as T
        }else{
            IllegalAccessException("unknown view Model class")
        }
        return super.create(modelClass)
    }
}