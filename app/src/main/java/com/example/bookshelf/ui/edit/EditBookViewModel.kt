package com.example.bookshelf.ui.edit

import android.app.Application
import android.util.Log
import androidx.collection.arrayMapOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookshelf.bussiness.db.MyBooksEntity
import com.example.bookshelf.bussiness.model.Book
import com.example.bookshelf.bussiness.repository.book.BookDetailRepository
import com.example.bookshelf.bussiness.repository.book.BookEditRepository
import com.example.bookshelf.bussiness.repository.book.MyBooksRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class EditBookViewModel(
    private val bookEditRepository : BookEditRepository,
    private val application: Application,
    private val titleAsId : String,
) : ViewModel() {

    val book: MutableLiveData<Book> by lazy {
        MutableLiveData<Book>(Book())
    }

    val openBookMenu : MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }

    val title : MutableLiveData<String> by lazy {
        MutableLiveData<String>("")
    }

    val category : MutableLiveData<String> by lazy {
        MutableLiveData<String>("")
    }

    val desc : MutableLiveData<String> by lazy {
        MutableLiveData<String>("")
    }

    val bookCover : MutableLiveData<String> by lazy {
        MutableLiveData<String>("")
    }

    val bookDoc : MutableLiveData<String> by lazy {
        MutableLiveData<String>("")
    }




    init {
        getBook(titleAsId)
    }

     internal lateinit var oldTitle :String
    internal lateinit var oldCategory : String
    internal lateinit var oldDesc : String
    internal lateinit var oldBookCover : String
    internal lateinit var oldDocUrl : String

    fun getBook(titleId: String) = viewModelScope.launch(Dispatchers.IO) {
        bookEditRepository.getBookById(titleId).collect {
            println("Get Book -----------------------")
            println(it.asDomainModel())
            val _book = it.asDomainModel()
            book.postValue(it.asDomainModel())
            title.postValue(_book.title)
            category.postValue(_book.category)
            desc.postValue(_book.desc)
            bookCover.postValue(_book.bookCover)
            bookDoc.postValue(_book.bookUri)
            oldTitle = _book.title
            oldCategory = _book.category
            oldDesc = _book.desc ?: ""
            oldBookCover = _book.bookCover ?: ""
            oldDocUrl = _book.bookUri
        }
    }

    fun editBook(bookId : String) =
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("OBJ_UpdateO","${getUpdateObj().size}")
            bookEditRepository.editBook(bookId,*getUpdateObj())
                .collect{

                }
    }

    fun getUpdateObj() : Array<Map<String,String?>>{
        var obj = arrayListOf<Map<String,String?>>()
        Log.d("OBJ_TOUpdateN","${title.value} ----New value")
        Log.d("OBJ_TOUpdateO","${oldTitle}")
        if (oldTitle != title.value){
            Log.d("OBJ_NEW","${oldTitle}, ${title.value} ----New value must have")
            obj.add(mapOf("title" to title.value))
        }
        if (oldCategory != category.value){
            obj.add(mapOf("category" to category.value))
        }
        if (oldDesc != desc.value){
            obj.add(mapOf("desc" to desc.value))
        }
        if (bookCover.value != oldBookCover){
            obj.add(mapOf("bookCover" to bookCover.value))
        }
        if (oldDocUrl != bookCover.value){
            obj.add(mapOf("bookUri" to bookDoc.value))
        }
        Log.d("OBJ_B","${obj.size}")
        return obj.toTypedArray()
    }

}