package com.example.bookshelf.ui.booklist

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.bookshelf.bussiness.Result.data
import com.example.bookshelf.bussiness.db.BookEntity
import com.example.bookshelf.bussiness.model.asBookEntity
import com.example.bookshelf.bussiness.repository.book.BookListRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class BookListViewModel(
    private val bookListRepository: BookListRepository,
    application:Application) : ViewModel(){
    var books : LiveData<List<BookEntity>> = bookListRepository.getOfflineBooks().asLiveData()
    var filteredBooks = books

    init {
        refreshBooks()
    }


//    @SuppressLint("LongLogTag")
//    private  fun getBooks()  = viewModelScope.launch {
//       bookListRepository.getOfflineBooks().collect{
//           Log.d("BOOKLISTVIEWMODEL",it.toString())
//           books.value = it
//       }
//    }




    private fun refreshBooks() = viewModelScope.launch {
        bookListRepository.refreshBooks()
    }
//
//    private fun filterByCategory(category: String) = viewModelScope.launch(Dispatchers.Unconfined) {
//        bookListRepository.filterByCategory(category).collect{
//            books.value = it.data?.asBookEntity()
//        }
//    }
//
//
//    private fun filterByAuthor(author: String) = viewModelScope.launch {
//        bookListRepository.filterByAuthor(author).collect{
//            books.value = it.data?.asBookEntity()
//        }
//    }
//
//    fun filter(query:String?) = viewModelScope.launch {
//        if (!query.isNullOrEmpty()){
//            val _filteredBooks = books.value?.filter { book: BookEntity -> query.let {
//                book.title.contains(
//                    it
//                )
//            }
//                ?: true }
//            filteredBooks.value = (_filteredBooks as MutableList<BookEntity>?)!!
//        }else{
//            filteredBooks = books
//            println("Check out the list of books ---------------------------")
//            println(books)
//        }
//    }
}