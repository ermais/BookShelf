package com.example.bookshelf.ui.createaccount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth

class CreateAccountViewModelFactory(private val auth: FirebaseAuth) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateAccountViewModel::class.java)) {
            return CreateAccountViewModel(auth) as T
        } else {
            throw IllegalAccessException("Unknown model class access")
        }
        return super.create(modelClass)
    }
}