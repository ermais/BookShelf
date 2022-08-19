package com.example.bookshelf.ui.create

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import java.lang.IllegalArgumentException

class CreateBookViewModelFactory(
    private val auth : FirebaseAuth,
    private val repo: CreateBookRepository,
    private val app: Context
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
       if(modelClass.isAssignableFrom(CreateBookViewModel::class.java)){
           return CreateBookViewModel(auth,repo,app) as T
       }

        throw IllegalArgumentException("Unknown view model class")
    }
}