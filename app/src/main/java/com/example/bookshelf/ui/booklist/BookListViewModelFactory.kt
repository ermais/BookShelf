package com.example.bookshelf.ui.booklist

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bookshelf.bussiness.repository.book.BookListRepository


@Suppress("UNCHECKED_CAST")
class BookListViewModelFactory(
    private val bookListRepository: BookListRepository,
    private val application: Application)
    : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookListViewModel::class.java)){
            return BookListViewModel(bookListRepository,application) as T
        }else {
            IllegalAccessException("Unknown Viewmodel class")
        }

        return super.create(modelClass)
    }

}