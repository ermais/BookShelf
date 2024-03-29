package com.example.bookshelf.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text
    val filterByCategory: MutableLiveData<String> by lazy {
        MutableLiveData<String>("")
    }

    val sortBy: MutableLiveData<String> by lazy {
        MutableLiveData<String>("")
    }

    val createBookFabBtnVisible: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }
}