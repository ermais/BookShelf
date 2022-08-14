package com.example.bookshelf.create

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.ui.AppBarConfiguration
import androidx.work.Data
import androidx.work.WorkInfo
import com.example.bookshelf.MainActivity
import com.example.bookshelf.R
import com.example.bookshelf.data.KEY_BOOK_COVER_URI
import com.example.bookshelf.data.KEY_BOOK_URI
import com.example.bookshelf.data.UPLOAD_BOOK_COVER_PROGRESS
import com.example.bookshelf.databinding.ActivityCreateBookBinding
import com.example.bookshelf.isPermissionGranted
import com.example.bookshelf.model.book.FirestoreBookDataSource
import com.example.bookshelf.requestPermission
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CreateBookActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityCreateBookBinding
    private lateinit var firestoreBookDataSource: FirestoreBookDataSource
    private lateinit var db: FirebaseFirestore
    private lateinit var viewModel: CreateBookViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateBookBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val application = requireNotNull(this).application
        val auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        firestoreBookDataSource = FirestoreBookDataSource(db)
        val repo = CreateBookRepository(firestoreBookDataSource)
        var coverUploaded: Boolean = false

        val createBookViewModelFactory = CreateBookViewModelFactory(auth,repo, application)
        viewModel = ViewModelProvider(
            this,
            createBookViewModelFactory
        )[CreateBookViewModel::class.java]

        binding.viewModel = viewModel
        binding.layoutBookCreate.viewModel = viewModel
        binding.layoutBookCreate.lifecycleOwner = this
        binding.lifecycleOwner = this


        /*
            Disable and enable  Save Button
            Where All Required field is fulfilled enable button else
            disable button till they fulfilled

         */


//        val bookCoverObserver = Observer<Uri> {bookCover:Uri->
//            binding.layoutBookCreate.tvShow.text = bookCover.toString()
//        }
//
//        viewModel.bookCover.observe(this,bookCoverObserver
//
//        )


        /**
         * Binding element
         */

        binding.layoutBookCreate.btnUploadBook.visibility = View.GONE
        binding.layoutBookCreate.btnBookCover.visibility = View.GONE


        binding.layoutBookCreate.pbAddBook.visibility = View.GONE
        binding.layoutBookCreate.btnBookCover.visibility = View.GONE
        binding.layoutBookCreate.btnUploadBook.visibility = View.VISIBLE
        if (viewModel.canCreateBook()){
            binding.layoutBookCreate.btnAddBook.visibility = View.VISIBLE
            binding.layoutBookCreate.pbAddBook.visibility = View.GONE
        }else {
            binding.layoutBookCreate.btnAddBook.visibility = View.GONE
        }



        /**
         *Observe book document upload work info
         * @param viewModel.bookDocUploadWorkInfo
         * If WorkInfo state is succeed, retrieve outputdata and
         * return the result uri
         */

        viewModel.bookDocUploadWorkInfo?.observe(this, Observer {
            if(it.isNullOrEmpty()){
                return@Observer
            }
            it.forEach{workInfo ->
                if (WorkInfo.State.SUCCEEDED == workInfo.state){
                    val bookDocUri = workInfo.outputData.getString(KEY_BOOK_URI)
                    viewModel.bookDocUri.value = bookDocUri
                    uploadBookOnFinished()
                }
                if (WorkInfo.State.RUNNING == workInfo.state){
                    uploadBookOnProgress()
                }

            }

            val workInfo = it[0]
            if (workInfo.state.isFinished){
                val bookDocUri = workInfo.outputData.getString(KEY_BOOK_URI)
                println("Printing book doc uri -----------------------------------")
                println(bookDocUri)
            }
        })

        viewModel.bookCoverUploadWorkInfo.observe(this, Observer {

            if (it.isNullOrEmpty()){
                return@Observer
            }
            it.forEach{workInfo ->
                if (WorkInfo.State.SUCCEEDED == workInfo.state){

                    val bookCoverUri = workInfo.outputData.getString(KEY_BOOK_COVER_URI)
                    viewModel.bookCover.value = bookCoverUri
                    uploadBookCoverOnFinished()
                }
                if (WorkInfo.State.RUNNING == workInfo.state){
                    uploadBookCoverInProgress()
                }
            }
        })


        /**
         * Observe book title
         * and set @bookTitle observer here
         *
         */

        viewModel.bookTitle.observe(this) { title ->

            if (title.isNullOrEmpty()) {
                binding.layoutBookCreate.btnBookCover.visibility = View.GONE
                binding.layoutBookCreate.btnUploadBook.visibility = View.GONE
            }else {
                binding.layoutBookCreate.btnBookCover.visibility = View.VISIBLE
                binding.layoutBookCreate.btnUploadBook.visibility = View.VISIBLE
            }

        }

        /**
         * Handling event listener
         * Just put all listener, start here and forward
         */

        /**
         * set SET BOOK COVER button click listener
         */

        binding.layoutBookCreate.btnBookCover.setOnClickListener {
            if (isPermissionGranted(applicationContext, PHOTO_READ_PERMISSION)) {
                getImageIntent()
            } else {
                requestPermission(requireNotNull(this), PHOTO_READ_PERMISSION, BOOK_COVER_RE_CODE)
            }
        }

        /**
         * Handling UPLOAD BOOK DOC
         * It is the event listener that handle get uri of book to uploaded and return it uri
         * @param onActivityResult process the returned  data and run bookworker manager
         *
         */
        binding.layoutBookCreate.btnUploadBook.setOnClickListener{
            if (isPermissionGranted(applicationContext, PHOTO_READ_PERMISSION)) {
                getBookDoc()
            } else {
                requestPermission(requireNotNull(this), PHOTO_READ_PERMISSION, BOOK_DOC_RE_CODE)
            }
        }

        /**
         * Handling Save button onClick
         * It get all book attribute from view model and pass to book object creator
         */

        binding.layoutBookCreate.btnAddBook.setOnClickListener { view ->

            viewModel.publishBook()
            val bookUriToast = Toast.makeText(this,"Book Successfully Created!",Toast.LENGTH_LONG)
            bookUriToast.show()
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }




//
//        val bookCoverUploadedObserver = Observer<Boolean> {
//            binding.layoutBookCreate.tvShow.text  = it
//        }

//        val addBookBtnEnabledObserver = Observer<Boolean> {
//            binding.layoutBookCreate.btnAddBook.isClickable = it
//        }
//
//        val pbAddBookVisibilityObserver = Observer<Int> {
//            binding.layoutBookCreate.pbAddBook.visibility = it
//        }

//        viewModel.bookCoverUploaded.observe(this,bookCoverUploadedObserver)
//        viewModel.createBookBtnEnabled.observe(this, addBookBtnEnabledObserver)
//        viewModel.pbAddBookVisibility.observe(this, pbAddBookVisibilityObserver)

//        val bookCoverUploadedObserver = Observer<Boolean> { bookCoverUploaded:Boolean->
//            coverUploaded = bookCoverUploaded
//        }

//        binding.viewModel.bookCoverUploaded.observe(this,bookCoverUploadedObserver)




        binding.layoutBookCreate.svCategory.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    val selectedItem = p0!!.getItemAtPosition(p2)
                    if (selectedItem != null) {
                        val toast = Toast.makeText(
                            getApplication(),
                            selectedItem.toString(),
                            Toast.LENGTH_LONG
                        )
                        toast.show()
                        with(binding) {
                            toast.show()
                            viewModel?.bookCategory?.postValue(selectedItem.toString())
                        }
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    val selectedItem = p0!!.getItemAtPosition(0).toString()
                    with(binding) {
                        viewModel?.bookCategory?.postValue(selectedItem)
                    }
                }

            }

//
//        val navController = findNavController(R.id.nav_host_fragment_content_main)
//        appBarConfiguration = AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController, appBarConfiguration)

        ArrayAdapter.createFromResource(
            this,
            R.array.categories,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.layoutBookCreate.svCategory.adapter = adapter
        }
    }
//
//    override fun onSupportNavigateUp(): Boolean {
//        val navController = findNavController(R.id.nav_host_fragment_content_main)
//        return navController.navigateUp(appBarConfiguration)
//                || super.onSupportNavigateUp()
//    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GET_BCI && resultCode == Activity.RESULT_OK) {
            val imageUri = data?.data
            val toast = Toast.makeText(applicationContext, imageUri.toString(), Toast.LENGTH_LONG)
            toast.show()
            viewModel.bookCover.value = imageUri.toString()
            viewModel.uploadBookCover()
        }
        if (requestCode == GET_BOOK_DOC && resultCode == Activity.RESULT_OK) {
            val bookDoc: Uri? = data?.data
            val toast = Toast.makeText(applicationContext, bookDoc.toString(), Toast.LENGTH_LONG)
            toast.show()
            viewModel.bookDocUri.value = bookDoc.toString()
            val toastViewModel = Toast.makeText(applicationContext, viewModel.bookDocUri.value, Toast.LENGTH_LONG)
            toastViewModel.show()
            viewModel.uploadBookDoc()

        }
    }

    /**
     * Set of private function goes here forward
     *
     */
    private fun getImageIntent() {
        val imageIntent = Intent(Intent.ACTION_PICK)
        imageIntent.type = "image/*"
        startActivityForResult(imageIntent, GET_BCI)

    }
    private fun getBookDoc() {
        val bookDocIntent = Intent(Intent.ACTION_PICK)
        bookDocIntent.type = "*/*"
        startActivityForResult(bookDocIntent, GET_BOOK_DOC)
    }

    private fun uploadBookOnProgress(){
        binding.layoutBookCreate.btnAddBook.visibility = View.GONE
        binding.layoutBookCreate.btnBookCover.visibility = View.GONE
        binding.layoutBookCreate.pbAddBook.visibility = View.VISIBLE
    }


    private fun uploadBookOnFinished(){
        binding.layoutBookCreate.btnBookCover.visibility = View.VISIBLE
        binding.layoutBookCreate.pbAddBook.visibility = View.GONE
    }

    private fun uploadBookCoverInProgress(){
        binding.layoutBookCreate.btnAddBook.visibility = View.GONE
        binding.layoutBookCreate.pbAddBook.visibility = View.VISIBLE

    }

    private fun uploadBookCoverOnFinished(){
        binding.layoutBookCreate.btnAddBook.visibility = View.VISIBLE
        binding.layoutBookCreate.pbAddBook.visibility = View.GONE
        binding.layoutBookCreate.btnBookCover.visibility = View.VISIBLE
    }
    companion object {
        val GET_BCI: Int = 233
        const val BOOK_COVER_RE_CODE = 232
        const val BOOK_DOC_RE_CODE = 343
        const val GET_BOOK_DOC = 443
        const val PHOTO_READ_PERMISSION = android.Manifest.permission.READ_EXTERNAL_STORAGE
    }


    fun getOUtPutData(key:String): String? {
        val data = Data.Builder().build()
        return data.getString(key)
    }
}