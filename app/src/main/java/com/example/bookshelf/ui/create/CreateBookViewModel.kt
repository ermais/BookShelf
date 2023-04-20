package com.example.bookshelf.ui.create

import android.net.Uri
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookshelf.bussiness.Result.data
import com.example.bookshelf.bussiness.model.Book
import com.example.bookshelf.bussiness.repository.book.CreateBookRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

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
    var bookCreated = MutableLiveData(false)

    //     val bookDocUploadWorkInfo  : LiveData<List<WorkInfo>> by lazy {
//         workManager.getWorkInfosByTagLiveData(UPLOAD_BOOK_DOC_WORKER_TAG)
//     }
//     val bookCoverUploadWorkInfo  : LiveData<List<WorkInfo>> by lazy {
//         workManager.getWorkInfosByTagLiveData(UPLOAD_BOOK_COVER_WORKER_TAG)
//     }
    val bookTitleError: MutableLiveData<String> by lazy {
        MutableLiveData<String>("")
    }

    val bookDocUriError: MutableLiveData<String> by lazy {
        MutableLiveData<String>("")
    }

    val loading: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }


    fun publishBook() = viewModelScope.launch {
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


    fun uploadBookDoc() = viewModelScope.launch(Dispatchers.IO) {
        loading.postValue(true)
        createBookRepository.uploadBookDoc(
            Uri.parse(bookDocUriFromFile.value),
            bookTitle.value.toString()
        )
            .collect { result ->
                bookDocUriFromFirebase.postValue(result.data.toString())
                loading.postValue(false)
            }
    }

    fun uploadBookCover() = viewModelScope.launch(Dispatchers.IO) {
        loading.postValue(true)
        createBookRepository.uploadBookCover(
            Uri.parse(bookCoverUriFromFile.value),
            bookTitle.value.toString()
        )
            .collect { result ->
                bookCoverUriFromFirebase.postValue(result.data.toString())
                loading.postValue(false)
            }
    }
}


