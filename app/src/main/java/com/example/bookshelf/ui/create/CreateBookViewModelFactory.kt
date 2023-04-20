package com.example.bookshelf.ui.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bookshelf.bussiness.repository.book.CreateBookRepository
import com.google.firebase.auth.FirebaseAuth

@Suppress("UNCHECKED_CAST")
class CreateBookViewModelFactory(
    private val auth: FirebaseAuth,
    private val repo: CreateBookRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateBookViewModel::class.java)) {
            return CreateBookViewModel(auth, repo) as T
        }

        throw IllegalArgumentException("Unknown view model class")
    }
}