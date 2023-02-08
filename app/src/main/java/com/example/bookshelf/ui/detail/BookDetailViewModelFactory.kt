package com.example.bookshelf.ui.detail

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bookshelf.bussiness.repository.book.BookDetailRepository
import com.example.bookshelf.bussiness.repository.book.MyBooksRepository

@Suppress("UNCHECKED_CAST")
class BookDetailViewModelFactory(
    private val bookDetailRepository: BookDetailRepository,
    private val myBooksRepository: MyBooksRepository,
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookDetailViewModel::class.java)) {
            return BookDetailViewModel(bookDetailRepository, myBooksRepository, application) as T
        } else {
            IllegalAccessException("Unknown Viewmodel class")
        }
        return super.create(modelClass)
    }

}