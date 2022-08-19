package com.example.bookshelf.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bookshelf.data.User
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel(auth: FirebaseAuth) : ViewModel() {

    private val _user = MutableLiveData<User>().apply {
        value = null
    }
//    val user = _user


//    fun createUserAccount(
//        firstName : String,
//        lastName: String,
//        email: String,
//        password: String,
//        userName: String
//    ) {
//        val auth = Firebase.auth
//        var currentUser = auth.currentUser
//        if (currentUser != null){
//            auth.createUserWithEmailAndPassword(email,password)
//                .addOnCompleteListener() {
//                    if (it.isSuccessful){
//                        currentUser = auth.currentUser
////                        user = currentUser
//                    }else{
//
//                    }
//                }
//        }
//    }
}