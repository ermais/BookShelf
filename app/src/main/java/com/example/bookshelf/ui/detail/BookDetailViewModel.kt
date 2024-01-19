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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BookDetailViewModel(
    private val bookDetailRepository: BookDetailRepository,
    private val myBooksRepository: MyBooksRepository,
    application: Application
) : ViewModel() {

    val book: MutableLiveData<Book> by lazy {
        MutableLiveData<Book>(Book())
    }

    val openBookMenu : MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }

    init {
    }

    fun getBook(title: String) = viewModelScope.launch(Dispatchers.IO) {
        bookDetailRepository.getBook(title).collect {
            println("Get Book -----------------------")
            println(it?.asDomainModel())
            book?.postValue(it?.asDomainModel())
        }
    }

    fun buyBook() = viewModelScope.launch {
        val myBook =
            MyBooksEntity(FirebaseAuth.getInstance().currentUser!!.uid, book.value!!.bookId)
        myBooksRepository.buyBook(myBook).collect {
            println("Book successfully created ++++++++++++++++++++++++++++++++++++")
        }
    }

}