package com.example.bookshelf.ui.contactus

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ContactUsViewModel : ViewModel() {

    private val _phoneNumberContact : MutableLiveData<String> =
        MutableLiveData<String>("251925017182")
    val phoneNumber : MutableLiveData<String> get() = _phoneNumberContact

}