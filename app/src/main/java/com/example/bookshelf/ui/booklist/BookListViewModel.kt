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
import java.util.Collections.copy

class BookListViewModel(private val bookListRepository: BookListRepository, application:Application) : ViewModel(){
    var _books = MutableLiveData<MutableList<Book>>()
    var filteredBooks =  MutableLiveData<MutableList<Book>>()

    var books = _books
    init {
        getBooks()
        filteredBooks = books
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

     fun filter(query:String?) = viewModelScope.launch {
         if (!query.isNullOrEmpty()){
             val _filteredBooks = books.value?.filter { book:Book-> query.let {
                 book.title?.contains(
                     it
                 )
             }
                 ?: true }
             filteredBooks.value = (_filteredBooks as MutableList<Book>?)!!
         }else{
             filteredBooks = books
             println("Check out the list of books ---------------------------")
             println(books)
         }

    }


}


