package com.example.bookshelf.ui.booklist

import android.annotation.SuppressLint
import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.bookshelf.bussiness.Result.data
import com.example.bookshelf.bussiness.db.BookEntity
import com.example.bookshelf.bussiness.model.asBookEntity
import com.example.bookshelf.bussiness.repository.book.BookListRepository
import com.example.bookshelf.data.DOWNLOAD_BOOK_WORKER_TAG
import com.example.bookshelf.data.KEY_BOOK_TITLE
import com.example.bookshelf.data.KEY_DOWNLOAD_BOOK_URI
import com.example.bookshelf.worker.DownloadBookWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class BookListViewModel(
    private val bookListRepository: BookListRepository,
    application:Application) : ViewModel(){
    internal var books : MutableLiveData<List<BookEntity>> = bookListRepository.getOfflineBooks().asLiveData() as MutableLiveData<List<BookEntity>>
    var filteredBooks = books
    val workManager = WorkManager.getInstance(application)
    val downloadWorkerInfo = workManager.getWorkInfosByTag(DOWNLOAD_BOOK_WORKER_TAG)


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


    fun downloadBook(downloadUri:Uri,bookTitle:String) = viewModelScope.launch {
        val downloadWorkRequest = OneTimeWorkRequestBuilder<DownloadBookWorker>()
            .setInputData(inputDownloadData(downloadUri,bookTitle))
            .addTag(KEY_DOWNLOAD_BOOK_URI)
            .build()
        workManager.enqueue(downloadWorkRequest)
    }




    private fun refreshBooks() = viewModelScope.launch(Dispatchers.IO) {
        bookListRepository.refreshBooks()
    }


    fun filterByCategory(category:String) = viewModelScope.launch {
        bookListRepository.filterByCategoryOffline(category).collect{
            books.value = it
        }
    }

    fun filterBooks(query:String) = viewModelScope.launch {
        bookListRepository.filterBooks(query).collect{
            books.value = it
        }
    }

    fun getBooks() = viewModelScope.launch {
        bookListRepository.getOfflineBooks().collect(){
            books.value = it
        }
    }

    fun sortByAuthor() = viewModelScope.launch {
        bookListRepository.sortByAuthor().collect(){
            books.value = it
        }
    }

    fun sortByTitle() = viewModelScope.launch {
        bookListRepository.sortByTitle().collect(){
            books.value = it
        }
    }

    fun sortByPubDate() = viewModelScope.launch {
        bookListRepository.sortByPubDate().collect(){
            books.value = it
        }
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


    fun inputDownloadData(downloadUri:Uri,bookTitle:String) : Data {
        val builder = Data.Builder()
        builder.putString(KEY_DOWNLOAD_BOOK_URI, downloadUri.toString())
        builder.putString(KEY_BOOK_TITLE,bookTitle)
        return builder.build()
    }
}