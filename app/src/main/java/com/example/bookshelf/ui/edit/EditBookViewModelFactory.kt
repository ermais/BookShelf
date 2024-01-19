package com.example.bookshelf.ui.edit

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bookshelf.bussiness.repository.book.BookDetailRepository
import com.example.bookshelf.bussiness.repository.book.BookEditRepository
import com.example.bookshelf.bussiness.repository.book.MyBooksRepository

class EditBookViewModelFactory(
    private val bookEditRepository: BookEditRepository,
    private val application: Application,
    private val titleAsId : String,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditBookViewModel::class.java)) {
            return EditBookViewModel(bookEditRepository,application,titleAsId) as T
        }else{
            throw IllegalAccessException("${modelClass} class is not found!")
        }
    }
}