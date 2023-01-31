package com.example.bookshelf.ui.detail

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.bookshelf.bussiness.db.BookEntity
import com.example.bookshelf.bussiness.model.Book
import com.example.bookshelf.bussiness.repository.book.BookDetailRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class BookDetailViewModel(private val bookDetailRepository: BookDetailRepository, application: Application)
    : ViewModel() {

      val book : MutableLiveData<Book> by lazy {
          MutableLiveData<Book>(Book())
      }

    init {
    }

    fun getBook(title:String) = viewModelScope.launch {
        bookDetailRepository.getBook(title).collect{
            println("Get Book -----------------------")
            println(it.asDomainModel())
            book.value = it.asDomainModel()
        }
    }
}