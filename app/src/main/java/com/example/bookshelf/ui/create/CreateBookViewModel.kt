package com.example.bookshelf.ui.create

import android.net.Uri
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookshelf.bussiness.Result.data
import com.example.bookshelf.bussiness.model.Book
import com.example.bookshelf.bussiness.repository.book.CreateBookRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class CreateBookViewModel(
    private val auth: FirebaseAuth,
    private val createBookRepository: CreateBookRepository,
) : ViewModel() {
    val bookTitle by lazy { MutableLiveData("") }
    val bookDesc by lazy { MutableLiveData<String>() }
    val bookCategory by lazy { MutableLiveData<String>() }
    var bookCoverUriFromFile = MutableLiveData<String>()
    var bookDocUriFromFile = MutableLiveData<String>()
    val bookDocUriFromFirebase: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val bookCoverUriFromFirebase: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    private var isConnected = MutableLiveData(true)
    var bookCreated = MutableLiveData(false)

    val bookTitleError: MutableLiveData<String> by lazy {
        MutableLiveData<String>("")
    }

    val bookDocUriError: MutableLiveData<String> by lazy {
        MutableLiveData<String>("")
    }

    val loading: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }


    fun publishBook(
        successCallback: () -> Unit, failureCallback: () -> Unit,
        networkFailureCallback: () -> Unit
    ) = viewModelScope.launch(Dispatchers.IO) {
        if (isConnected.value == true) {
            try {
                val book = Book(
                    "",
                    auth.currentUser?.uid.toString(),
                    bookTitle.value.toString(),
                    auth.currentUser?.displayName.toString(),
                    bookCategory.value.toString(),
                    bookDesc.value,
                    Calendar.getInstance().timeInMillis,
                    bookCoverUriFromFirebase.value.toString(),
                    bookDocUriFromFirebase.value.toString(),
                    "0.0",
                    downloadCount = 0
                )
                createBookRepository.publishBook(book).collect {
                    bookCreated.postValue(true)
                    loading.postValue(false)
                    viewModelScope.launch(Dispatchers.Main) {
                        successCallback()
                    }

                }
            } catch (e: Exception) {
                loading.postValue(false)
                viewModelScope.launch {
                    failureCallback()
                }


            }
        } else {
            viewModelScope.launch {
                networkFailureCallback()
            }
        }
    }

    private fun titleNotNullOREmpty(): Boolean {
        return (!TextUtils.isEmpty(bookTitle.value)) && (bookTitle.value != null)
    }

    private fun bookDocNotNullOREmpty(): Boolean {
        return (!TextUtils.isEmpty(bookDocUriFromFirebase.value) && bookDocUriFromFirebase.value != null)
    }


    fun canUploadBookDoc(): Boolean {
        return titleNotNullOREmpty()
    }

    fun canUploadBookCover(): Boolean {
        return titleNotNullOREmpty() && bookDocNotNullOREmpty()
    }

    fun canSaveBook(): Boolean {
        return titleNotNullOREmpty() && bookDocNotNullOREmpty()
    }


    fun uploadBookDoc(
        successCallback: () -> Unit, failureCallback: () -> Unit,
        networkFailureCallback: () -> Unit
    ) = viewModelScope.launch(Dispatchers.IO) {
        if (isConnected.value == true) {
            try {
                loading.postValue(true)
                createBookRepository.uploadBookDoc(
                    Uri.parse(bookDocUriFromFile.value),
                    bookTitle.value.toString()
                )
                    .collect { result ->
                        bookDocUriFromFirebase.postValue(result.data.toString())
                        loading.postValue(false)
                        viewModelScope.launch(Dispatchers.Main) {
                            successCallback()
                        }

                    }
            } catch (e: Exception) {
                viewModelScope.launch(Dispatchers.Main) {
                    failureCallback()
                }

                loading.postValue(true)
            }

        } else networkFailureCallback()

    }

    fun uploadBookCover(
        successCallback: () -> Unit,
        failureCallback: () -> Unit,
        networkFailureCallback: () -> Unit
    ) = viewModelScope.launch(Dispatchers.IO) {
        if (isConnected.value == true) {
            try {
                loading.postValue(true)
                createBookRepository.uploadBookCover(
                    Uri.parse(bookCoverUriFromFile.value),
                    bookTitle.value.toString()
                )
                    .collect { result ->
                        bookCoverUriFromFirebase.postValue(result.data.toString())
                        loading.value = false

                        Log.d("Upload", loading.value.toString())
                        viewModelScope.launch(Dispatchers.Main) {
                            successCallback()
                        }

                    }
            } catch (e: Exception) {
                viewModelScope.launch(Dispatchers.Main) {
                    failureCallback()
                }

                loading.postValue(false)
            }
        } else networkFailureCallback()
    }
}


