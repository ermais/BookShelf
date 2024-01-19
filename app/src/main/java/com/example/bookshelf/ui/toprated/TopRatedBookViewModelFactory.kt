package com.example.bookshelf.ui.toprated

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bookshelf.bussiness.repository.book.TopRatedBookRepository

class TopRatedBookViewModelFactory(
    private val topRatedBookRepository: TopRatedBookRepository,
    val isConnected : Boolean,
    val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TopRatedViewModel::class.java)){
            return TopRatedViewModel(topRatedBookRepository,isConnected,application) as T
        }
        throw IllegalAccessException("${modelClass} class not found!")
    }
}