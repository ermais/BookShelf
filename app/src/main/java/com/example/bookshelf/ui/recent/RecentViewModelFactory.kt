package com.example.bookshelf.ui.recent

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bookshelf.bussiness.repository.book.BookListRepository

class RecentViewModelFactory(
    val bookListRepository: BookListRepository,
    val isConnected : Boolean,
    val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecentViewModel::class.java)){
            return RecentViewModel(bookListRepository,isConnected,application) as T
        }else{
            throw IllegalAccessException("${modelClass} model is not found")
        }
    }
}