package com.example.bookshelf.ui.mybooks

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.bookshelf.bussiness.db.MyBooksQuery
import com.example.bookshelf.bussiness.repository.book.MyBooksRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Suppress("UNCHECKED_CAST")
class MyBooksViewModel(private val myBooksRepository: MyBooksRepository) :
    ViewModel() {

    val myBooks: MutableLiveData<List<MyBooksQuery>> = myBooksRepository.getOfflineMyBooks()
        .asLiveData() as MutableLiveData<List<MyBooksQuery>>

    init {
        refreshMyBooks()
    }

    private fun refreshMyBooks() = viewModelScope.launch(Dispatchers.IO) {
        myBooksRepository.refreshMyBooks()
    }

}