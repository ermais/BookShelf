package com.example.bookshelf.ui.booklist

import android.app.Application
import android.net.Uri
import androidx.lifecycle.*
import androidx.work.*
import com.example.bookshelf.bussiness.db.BookEntity
import com.example.bookshelf.bussiness.db.DownloadEntity
import com.example.bookshelf.bussiness.repository.book.BookListRepository
import com.example.bookshelf.data.DOWNLOAD_BOOK_WORKER_TAG
import com.example.bookshelf.data.KEY_BOOK_ID
import com.example.bookshelf.data.KEY_BOOK_TITLE
import com.example.bookshelf.data.KEY_DOWNLOAD_BOOK_URI
import com.example.bookshelf.worker.DownloadBookWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BookListViewModel(
    private val bookListRepository: BookListRepository,
    application: Application
) : ViewModel() {
    internal var books: MutableLiveData<List<BookEntity>> =
        bookListRepository.getOfflineBooks().asLiveData() as MutableLiveData<List<BookEntity>>
    val filteredBooks get() = books
    private var workManager = WorkManager.getInstance(application)

    init {
        refreshBooks()
    }



    fun downloadBook(downloadUri: Uri, bookTitle: String, bookId: String) = viewModelScope.launch {

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .setRequiresStorageNotLow(true)
            .build()
        val downloadWorkRequest = OneTimeWorkRequestBuilder<DownloadBookWorker>()
            .setConstraints(constraints)
            .setInputData(inputDownloadData(downloadUri, bookTitle, bookId))
            .addTag(DOWNLOAD_BOOK_WORKER_TAG)
            .build()
        workManager.enqueue(downloadWorkRequest)
    }


    private fun refreshBooks() = viewModelScope.launch(Dispatchers.IO) {
        bookListRepository.refreshBooks()
    }


    fun filterByCategory(category: String) = viewModelScope.launch {
        bookListRepository.filterByCategoryOffline(category).collect {
            books.value = it
        }
    }

    fun filterBooks(query: String) = viewModelScope.launch {
        bookListRepository.filterBooks(query).collect {
            books.value = it
        }
    }

    fun getBooks() = viewModelScope.launch {
        bookListRepository.getOfflineBooks().collect {
            books.value = it
        }
    }

    fun sortByAuthor() = viewModelScope.launch {
        bookListRepository.sortByAuthor().collect {
            books.value = it
        }
    }

    fun sortByTitle() = viewModelScope.launch {
        bookListRepository.sortByTitle().collect {
            books.value = it
        }
    }

    fun sortByPubDate() = viewModelScope.launch {
        bookListRepository.sortByPubDate().collect {
            books.value = it
        }
    }


    fun addDownloads(_download: DownloadEntity) = viewModelScope.launch(Dispatchers.IO) {
        bookListRepository.addDownload(_download)
    }



    private fun inputDownloadData(downloadUri: Uri, bookTitle: String, bookId: String): Data {
        val builder = Data.Builder()
        builder.putString(KEY_DOWNLOAD_BOOK_URI, downloadUri.toString())
        builder.putString(KEY_BOOK_TITLE, bookTitle)
        builder.putString(KEY_BOOK_ID, bookId)
        return builder.build()
    }
}