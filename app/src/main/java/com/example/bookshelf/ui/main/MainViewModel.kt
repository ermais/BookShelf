package com.example.bookshelf.ui.main

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel(application: Application) : ViewModel() {
    val query: MutableLiveData<String> by lazy {
        MutableLiveData<String>("")
    }

    val filterByCategory : MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val sortBy : MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    fun setQuery(queryText:String?){
     query.value = queryText
    }
}