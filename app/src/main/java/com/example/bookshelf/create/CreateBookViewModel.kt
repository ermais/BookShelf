package com.example.bookshelf.create

import android.app.Application
import android.content.Context
import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.bookshelf.data.*
import com.example.bookshelf.model.book.Book
import com.example.bookshelf.model.book.data
import com.example.bookshelf.worker.UploadBookCover
import com.example.bookshelf.worker.UploadBookWorker
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.util.*

class CreateBookViewModel(
    private val auth: FirebaseAuth,
    private val createBookRepository: CreateBookRepository,
    application: Context,
)
    : ViewModel(){

    val bookTitle by lazy { MutableLiveData<String>() }
    val bookDesc by lazy {MutableLiveData<String>()}
    val bookCategory by lazy { MutableLiveData<String>() }
    var bookCover = MutableLiveData<String>()
    var bookDocUri = MutableLiveData<String>()
    var bookCreated = MutableLiveData<Void>()
     val bookDocUploadWorkInfo  : LiveData<List<WorkInfo>> by lazy {
         workManager.getWorkInfosByTagLiveData(UPLOAD_BOOK_DOC_WORKER_TAG)
     }
     val bookCoverUploadWorkInfo  : LiveData<List<WorkInfo>> by lazy {
         workManager.getWorkInfosByTagLiveData(UPLOAD_BOOK_COVER_WORKER_TAG)
     }
    val bookTitleError : MutableLiveData<String> by lazy {
        MutableLiveData<String>("")
    }

    val bookDocUriError : MutableLiveData<String> by lazy {
        MutableLiveData<String>("")
    }
    var workManager: WorkManager = WorkManager.getInstance(application)


    init {

    }

         fun publishBook() = viewModelScope.launch {
            val book = Book(
                auth.currentUser?.uid,
                bookTitle.value,
                auth.currentUser?.displayName,
                bookCategory.value,
                bookDesc.value,Date(),
                bookDocUri.value,
                bookCover.value,
                "0.0"
            )
            createBookRepository.publishBook(book).collect{
                bookCreated.value = it.data
            }
        }




    fun uploadBookDoc() = viewModelScope.launch{
        val uploadBookWorker = OneTimeWorkRequestBuilder<UploadBookWorker>()
            .setInputData(createBookURIInputData())
            .addTag(UPLOAD_BOOK_DOC_WORKER_TAG)
            .build()
        workManager?.enqueue(uploadBookWorker)
    }

    fun uploadBookCover()= viewModelScope.launch{
        val uploadBookCoverRequest = OneTimeWorkRequestBuilder<UploadBookCover>()
            .setInputData(createBookCoverURInputData())
            .addTag(UPLOAD_BOOK_COVER_WORKER_TAG)
            .build()
        workManager?.enqueue(uploadBookCoverRequest)
    }

    private fun createBookURIInputData():Data {
        val dataBuilder = Data.Builder()
        println("Printing from createBookURIInputData-------------------------------")
        println(bookDocUri.value.toString())
        bookDocUri.let {
            dataBuilder.putString(KEY_BOOK_URI,it.value.toString())
            dataBuilder.putString(KEY_BOOK_TITLE, bookTitle.value)
        }
        return dataBuilder.build()
    }


    private fun createBookCoverURInputData():Data {
        val dataBuilder = Data.Builder()
        bookCover.let {
            dataBuilder.putString(KEY_BOOK_COVER_URI,it.value)
            dataBuilder.putString(KEY_BOOK_TITLE,bookTitle.value)
        }
        return dataBuilder.build()
    }

    fun canCreateBook() : Boolean {
        return bookDocUploadWorkInfo?.value?.get(0)?.state?.isFinished  == true &&
                bookCoverUploadWorkInfo?.value?.get(0)?.state?.isFinished ==true
    }

    fun titleNotNullOREmpty():Boolean {
        return (!TextUtils.isEmpty(bookTitle.value)) && (bookTitle.value != null)
    }

    fun bookDocNotNullOREmpty():Boolean {
        return (!TextUtils.isEmpty(bookDocUri.value) && bookDocUri.value != null)
    }


    fun canUploadBookDoc():Boolean {
        return titleNotNullOREmpty()
    }

    fun canUploadBookCover():Boolean {
        return titleNotNullOREmpty() && bookDocNotNullOREmpty()
    }

    fun canSaveBook() : Boolean{
        return titleNotNullOREmpty() && bookDocNotNullOREmpty()
    }

    fun cancelUploadBookDocWorker(){
        workManager?.cancelAllWorkByTag(UPLOAD_BOOK_DOC_WORKER_TAG)
    }

    fun cancelUploadBookCoverWorker() {
        workManager?.cancelAllWorkByTag(UPLOAD_BOOK_DOC_WORKER_TAG)
    }
}























//
//class CreateBookViewModel(private val user:FirebaseAuth,private val repo: CreateBookRepository,application:Application) : AndroidViewModel(application) {
//    private val _bookTitle : MutableLiveData<String> by lazy {
//        MutableLiveData<String>()
//    }
//
//    private val _bookCategory : MutableLiveData<String> by lazy {
//        MutableLiveData<String>()
//    }
//
//    private val _bookDesc : MutableLiveData<String> by lazy {
//        MutableLiveData<String>()
//    }
//
//    private val _bookCover : MutableLiveData<Uri> by lazy {
//        MutableLiveData<Uri>()
//    }
//
//    private val _bookCoverUploaded : MutableLiveData<Boolean> by lazy {
//        MutableLiveData<Boolean>(false)
//    }
//    private val _createBookBtnEnabled : MutableLiveData<Boolean> by lazy {
//        MutableLiveData<Boolean>(true)
//    }
//
//    private val _onCreatingBook : MutableLiveData<Boolean> by lazy {
//        MutableLiveData<Boolean>(false)
//    }
//
//    private val _pbAddBookVisibility : MutableLiveData<Int> by lazy {
//        MutableLiveData<Int>(View.GONE)
//    }
//    val bookTitle = _bookTitle
//    val bookCategory = _bookCategory
//    val bookDesc = _bookDesc
//    val bookCover = _bookCover
//    val bookCoverUploaded = _bookCoverUploaded
//    val createBookBtnEnabled = _createBookBtnEnabled
//    val onCreatingBook  = _onCreatingBook
//    val pbAddBookVisibility = _pbAddBookVisibility
//
//    fun createBook(navigateUp: () -> Unit){
//        val book = Book(
//            bookTitle.value.toString(),
//            user.currentUser!!.uid,
//            bookCategory.value.toString(),
//            bookDesc.value.toString(),
//            Date(),
//            null,
//            "0.0"
//            )
//        viewModelScope.launch(Dispatchers.IO) {
//            _createBook(book,bookCover.value as Uri,navigateUp)
//        }
//    }
//
//    fun isBookCoverUploaded():Boolean{
//        return bookCoverUploaded.value as Boolean
//    }
//
//    fun setBookCoverUri(imageUri: Uri){
//        bookCover.value = imageUri
//
//    }
//
//    fun updateBookCoverUploaded(_bookUri : Uri?){
//        if (_bookUri != null){
//            bookCoverUploaded.postValue(true)
//        }
//    }
//
//
//
//    suspend fun _createBook(book:Book,uri:Uri?,navigateUp: () -> Unit) {
//        withContext(Dispatchers.IO) {
//        val _book = repo.createBook(book,uri)
//        val _uri = repo.uploadBookCover(_book, uri)
//        val _bookUri = repo.updateBookCover(_uri,_book)
//        navigateUp()
//        }
//    }
//
//}
//
//
//
