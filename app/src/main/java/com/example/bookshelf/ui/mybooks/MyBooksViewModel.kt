package com.example.bookshelf.ui.mybooks

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.bookshelf.bussiness.db.MyBooksQuery
import com.example.bookshelf.bussiness.repository.book.MyBooksRepository
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

@Suppress("UNCHECKED_CAST")
class MyBooksViewModel(private val myBooksRepository: MyBooksRepository, application: Application) :
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