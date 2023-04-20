package com.example.bookshelf.ui.downloads

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bookshelf.bussiness.repository.book.DownloadRepository

@Suppress("UNCHECKED_CAST", "UNREACHABLE_CODE")
class DownloadsViewModelFactory(
    private val downloadRepository: DownloadRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DownloadsViewModel::class.java)) {
            return DownloadsViewModel(downloadRepository) as T
        } else {
            throw IllegalAccessException("Unknown ViewModel Class Access !")
        }

        return super.create(modelClass)
    }
}