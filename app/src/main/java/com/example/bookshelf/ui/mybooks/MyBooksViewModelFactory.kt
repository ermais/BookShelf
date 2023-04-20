package com.example.bookshelf.ui.mybooks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bookshelf.bussiness.repository.book.MyBooksRepository

@Suppress("UNCHECKED_CAST")
class MyBooksViewModelFactory(
    private val myBooksRepository: MyBooksRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyBooksViewModel::class.java)) {
            return MyBooksViewModel(myBooksRepository) as T
        } else {
            throw IllegalAccessException("No Defined ViewModel Class")
        }

        return super.create(modelClass)
    }
}