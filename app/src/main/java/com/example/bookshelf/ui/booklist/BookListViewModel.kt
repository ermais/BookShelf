package com.example.bookshelf.ui.booklist

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookshelf.model.book.Book
import com.example.bookshelf.model.book.Result
import com.example.bookshelf.model.book.data
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class BookListViewModel(val bookListRepository: BookListRepository,application:Application) : ViewModel(){
    var _books = MutableLiveData<List<Book>>()

    var books = _books
    init {
        getBooks()
    }


    private fun getBooks() = viewModelScope.launch {
        bookListRepository.getBookList().collect{
            Log.d("BookListViewModel",it.toString())
             books.value = it.data

        }
    }

    private fun filterByCategory(category: String) = viewModelScope.launch(Dispatchers.Unconfined) {
        bookListRepository.filterByCategory(category).collect{
            books.value = it.data
        }
    }


    private fun filterByAuthor(author: String) = viewModelScope.launch {
        bookListRepository.filterByAuthor(author).collect{
            books.value = it.data
        }
    }


}


