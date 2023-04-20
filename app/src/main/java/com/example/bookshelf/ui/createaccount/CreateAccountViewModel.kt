package com.example.bookshelf.ui.createaccount

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class CreateAccountViewModel(private val auth:FirebaseAuth):ViewModel() {
    val displayName  by lazy {
        MutableLiveData<String>("")
    }

    val email by lazy {
        MutableLiveData<String>("")
    }

    val password by lazy {
        MutableLiveData<String>("")
    }

    val confirmPassword by lazy {
        MutableLiveData<String>("")
    }

    val showDisplayError by lazy {
        MutableLiveData<Boolean>(false)
    }

    val showEmailError by lazy {
        MutableLiveData<Boolean>(false)
    }

    val showPasswordError by lazy {
        MutableLiveData<Boolean>(false)
    }

    val showConfirmPasswordError by lazy {
        MutableLiveData<Boolean>(false)
    }

    val showCreateAccountError by lazy {
        MutableLiveData<Boolean>(false)
    }

    fun validDisplayName(){
        if (isValidDisplayName()){
            showDisplayError.postValue(false)
        }else{
            showDisplayError.postValue(true)
        }
    }

    fun validEmail(){
        if (isValidEmail()){
            showEmailError.postValue(false)
        }else{
            showEmailError.postValue(true)
        }
    }

    fun validPassword(){
        if (isValidPassword()){
            showPasswordError.postValue(false)
        }else{
            showPasswordError.postValue(true)
        }
    }

    fun validConfirmPassword(){
        if (isPasswordMatch()){
            showConfirmPasswordError.postValue(false)
        }else{
            showConfirmPasswordError.postValue(true)
        }
    }

    fun isPasswordMatch():Boolean{
        return password.value!!.length == confirmPassword.value!!.length
    }

    fun isValidPassword():Boolean{
        return password.value!!.length >= 8
    }

    fun isValidDisplayName():Boolean{
        return displayName.value!!.length >=4 || displayName.value!!.isEmpty()
    }

    fun isValidEmail():Boolean{
        return email.value!!.contains("@gmail.com")
    }

    fun canICreate():Boolean{
        return isPasswordMatch() && isValidDisplayName() && isValidEmail() && isValidPassword()
    }

    fun createUserEmailAndPassword(callbackOnSuccess:()->Unit,callbackOnFailure:()->Unit){
        if (canICreate()){
            auth.createUserWithEmailAndPassword(email.value!!,password.value!!)
                .addOnCompleteListener {
                    if (it.isSuccessful){
                        showCreateAccountError.postValue(false)
                        callbackOnSuccess()
                    }else{
                        showCreateAccountError.postValue(true)
                        callbackOnFailure()
                    }
                }
                .addOnFailureListener {
                    showCreateAccountError.postValue(true)
                    callbackOnFailure()
                }
        }
    }
}