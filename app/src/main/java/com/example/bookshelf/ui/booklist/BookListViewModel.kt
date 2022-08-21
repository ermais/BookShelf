package com.example.bookshelf.ui.booklist

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookshelf.bussiness.model.Book
import com.example.bookshelf.bussiness.Result.data
import com.example.bookshelf.bussiness.db.asDomainModel
import com.example.bookshelf.bussiness.repository.book.BookListRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BookListViewModel(
    private val bookListRepository: BookListRepository,
    application:Application) : ViewModel(){
    var _books = MutableLiveData<MutableList<Book>>()
    var filteredBooks =  MutableLiveData<MutableList<Book>>()

    var books = _books
    init {
        refreshBooks()
        getBooks()
        filteredBooks = books
    }


    private fun getBooks() = viewModelScope.launch {
        bookListRepository.getOfflineBooks().collect{
            Log.d("BookListViewModel",it.toString())
             books.value = it.asDomainModel().toMutableList()
        }
    }

    private fun refreshBooks() = viewModelScope.launch {
        bookListRepository.refreshBooks()
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
             val _filteredBooks = books.value?.filter { book: Book -> query.let {
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


