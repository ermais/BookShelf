package com.example.bookshelf.ui.downloads

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bookshelf.bussiness.repository.book.DownloadRepository

@Suppress("UNCHECKED_CAST", "UNREACHABLE_CODE")
class DownloadsViewModelFactory(
    private val application: Application,
    private val downloadRepository: DownloadRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DownloadsViewModel::class.java)) {
            return DownloadsViewModel(downloadRepository, application) as T
        } else {
            throw IllegalAccessException("Unknown ViewModel Class Access !")
        }

        return super.create(modelClass)
    }
}