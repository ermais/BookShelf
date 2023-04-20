package com.example.bookshelf.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    val query: MutableLiveData<String> by lazy {
        MutableLiveData<String>("")
    }
    val sortBy: MutableLiveData<String> by lazy {
        MutableLiveData<String>("")
    }

    val filterByCategory: MutableLiveData<String> by lazy {
        MutableLiveData<String>("")
    }

    fun setQuery(queryText: String?) {
        query.value = queryText
    }
}