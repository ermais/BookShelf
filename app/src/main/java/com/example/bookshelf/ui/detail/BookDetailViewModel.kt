package com.example.bookshelf.ui.detail

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookshelf.bussiness.db.MyBooksEntity
import com.example.bookshelf.bussiness.model.Book
import com.example.bookshelf.bussiness.repository.book.BookDetailRepository
import com.example.bookshelf.bussiness.repository.book.MyBooksRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class BookDetailViewModel(
    private val bookDetailRepository: BookDetailRepository,
    private val myBooksRepository: MyBooksRepository,
    application: Application
) : ViewModel() {

    val book: MutableLiveData<Book> by lazy {
        MutableLiveData<Book>(Book())
    }

    init {
    }

    fun getBook(title: String) = viewModelScope.launch {
        bookDetailRepository.getBook(title).collect {
            println("Get Book -----------------------")
            println(it.asDomainModel())
            book.value = it.asDomainModel()
        }
    }

    fun buyBook() = viewModelScope.launch {
        val myBook = MyBooksEntity(FirebaseAuth.getInstance().currentUser!!.uid,book.value!!.bookId,)
        myBooksRepository.buyBook(myBook).collect{
            println("Book successfully created ++++++++++++++++++++++++++++++++++++")
        }
    }

}