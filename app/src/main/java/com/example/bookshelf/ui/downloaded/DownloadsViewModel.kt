package com.example.bookshelf.ui.downloaded

import android.app.Application
import androidx.lifecycle.*
import androidx.work.WorkInfo
import com.example.bookshelf.bussiness.db.BookEntity
import com.example.bookshelf.bussiness.db.DownloadAndBook
//import com.example.bookshelf.bussiness.db.BookAndDownloads
import com.example.bookshelf.bussiness.db.DownloadEntity
import com.example.bookshelf.bussiness.repository.book.DownloadRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Suppress("UNCHECKED_CAST")
class DownloadsViewModel(private val downloadRepository: DownloadRepository, application: Application) : ViewModel() {


     val downloads: MutableLiveData<List<DownloadAndBook>> by lazy {
         MutableLiveData<List<DownloadAndBook>>()
     }

//    internal var downloadsOnly : MutableLiveData<List<DownloadAndBook>> = downloadRepository.getDownloads().asLiveData() as MutableLiveData<List<DownloadAndBook>>

    init {
//        println("Downloads only ............................${downloadsOnly.value.toString()}")
        viewModelScope.launch {
            downloadRepository.getDownloads().collect(){
                println("Download only --------------------------------------")
                println(it)
            }
        }
        getDownloadsAndBooks()

    }

    private fun getDownloadsAndBooks() = viewModelScope.launch {
        downloadRepository.getDownloadsAndBooks().collect(){
            downloads.value = it
            println("Downloads -------------------------------------------------")
            println(it.size)
        }
    }
}