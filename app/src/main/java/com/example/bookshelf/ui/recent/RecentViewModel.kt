package com.example.bookshelf.ui.recent

import android.app.Application
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.bookshelf.bussiness.db.BookEntity
import com.example.bookshelf.bussiness.repository.book.BookListRepository
import com.example.bookshelf.data.DOWNLOAD_BOOK_WORKER_TAG
import com.example.bookshelf.data.KEY_BOOK_ID
import com.example.bookshelf.data.KEY_BOOK_TITLE
import com.example.bookshelf.data.KEY_DOWNLOAD_BOOK_URI
import com.example.bookshelf.worker.DownloadBookWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecentViewModel(
    val bookListRepository: BookListRepository,
    val isConnected: Boolean,
    val application: Application) : ViewModel() {

    private val _recentBooks : MutableLiveData<List<BookEntity>>  = bookListRepository.getRecentBooks()
        .asLiveData(Dispatchers.IO) as MutableLiveData<List<BookEntity>>


    private var workManager = WorkManager.getInstance(application)


    val recentBooks : MutableLiveData<List<BookEntity>> get() {
        if (_recentBooks.value != null){
            _recentBooks.value?.let {
                Log.d("GET","GET----------------")
                _recentBooks.value = it.sortedBy { book->book.pubDate }.take(5)
                Log.d("GETRecent","${_recentBooks.value} ------------RecentBooks")
            }
        }
        return _recentBooks
    }

    init {
        refreshBooks()
    }


    private fun refreshCallback() {
        val toast = Toast.makeText(
            application,
            "lost connection !, check your connection",
            Toast.LENGTH_LONG
        )
        toast.show()
    }

    private fun refreshBooks() = viewModelScope.launch(Dispatchers.IO) {
        bookListRepository.refreshBooks(::refreshCallback)
    }

    fun downloadBook(downloadUri: Uri, bookTitle: String, bookId: String) = viewModelScope.launch {
        if (isConnected) {
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
        } else {

        }

    }


    private fun inputDownloadData(downloadUri: Uri, bookTitle: String, bookId: String): Data {
        val builder = Data.Builder()
        builder.putString(KEY_DOWNLOAD_BOOK_URI, downloadUri.toString())
        builder.putString(KEY_BOOK_TITLE, bookTitle)
        builder.putString(KEY_BOOK_ID, bookId)
        return builder.build()
    }


}

