package com.example.bookshelf.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bookshelf.bussiness.repository.profile.UserProfileRepository
import com.google.firebase.auth.FirebaseAuth

class LoginViewModelFactory(private val auth: FirebaseAuth,private val userProfileRepository: UserProfileRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(auth,userProfileRepository) as T
        } else {
            throw IllegalAccessException("Not Model found!")
        }
        return super.create(modelClass)
    }
}