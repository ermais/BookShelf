package com.example.bookshelf.ui.create

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.view.children
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.bookshelf.R
import com.example.bookshelf.bussiness.networkdata.FirestoreBookDataSource
import com.example.bookshelf.bussiness.networkdata.FirestoreMyBooksDataSource
import com.example.bookshelf.bussiness.repository.book.CreateBookRepository
import com.example.bookshelf.databinding.FragmentCreateBookBinding
import com.example.bookshelf.util.isPermissionGranted
import com.example.bookshelf.util.requestPermission
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage


@Suppress("DEPRECATION")
class CreateBookFragment : Fragment() {

    private lateinit var toolbar: Toolbar
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var firestoreBookDataSource: FirestoreBookDataSource
    private lateinit var db: FirebaseFirestore
    private lateinit var cloudStorage: FirebaseStorage
    private var _binding: FragmentCreateBookBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var repository: CreateBookRepository
    private lateinit var createBookViewModelFactory: CreateBookViewModelFactory
    private lateinit var createBookViewModel: CreateBookViewModel
    private lateinit var firebaseBooksDataSource: FirestoreMyBooksDataSource


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateBookBinding.inflate(inflater, container, false)
        db = FirebaseFirestore.getInstance()
        cloudStorage = FirebaseStorage.getInstance()
        firestoreBookDataSource = FirestoreBookDataSource(db, cloudStorage)
        requireNotNull(activity).application
        firebaseBooksDataSource = FirestoreMyBooksDataSource(Firebase.firestore)
        repository = CreateBookRepository(firestoreBookDataSource, FirebaseStorage.getInstance())
        auth = FirebaseAuth.getInstance()
        createBookViewModelFactory = CreateBookViewModelFactory(auth, repository)
        createBookViewModel = ViewModelProvider(
            this,
            createBookViewModelFactory
        )[CreateBookViewModel::class.java]
        binding.viewModel = createBookViewModel
        binding.layoutBookCreate.viewModel = createBookViewModel
        binding.layoutBookCreate.lifecycleOwner = this
        binding.lifecycleOwner = this
        with(binding.layoutBookCreate) {
            btnUploadBook.visibility = View.VISIBLE
            btnBookCover.visibility = View.GONE
            btnAddBook.visibility = View.GONE
            pbAddBook.visibility = View.GONE
        }



        createBookViewModel.apply {

            bookTitle.observe(viewLifecycleOwner) { title ->
                if (title.isNullOrEmpty()) {
                    binding.layoutBookCreate.btnBookCover.visibility = View.GONE
                    binding.layoutBookCreate.btnUploadBook.visibility = View.GONE
                    binding.layoutBookCreate.btnAddBook.visibility = View.GONE
                    createBookViewModel.bookTitleError.value = "Title is required!"
                } else {
                    binding.layoutBookCreate.btnUploadBook.visibility = View.VISIBLE
                    binding.layoutBookCreate.btnBookCover.visibility = View.VISIBLE
                    binding.layoutBookCreate.btnAddBook.visibility = View.VISIBLE
                    createBookViewModel.bookTitleError.value = ""
                }
            }

            bookDocUriFromFirebase.observe(viewLifecycleOwner) { uri ->
                if (uri.isNullOrEmpty()) {
                    createBookViewModel.bookDocUriError.value = "You must upload book document!"
                } else {
                    binding.layoutBookCreate.btnBookCover.visibility = View.VISIBLE
                    binding.layoutBookCreate.btnAddBook.visibility = View.VISIBLE
                }
            }
            bookCoverUriFromFirebase.observe(viewLifecycleOwner) {
                if (it.isNullOrEmpty()) {
                    with(binding.layoutBookCreate) {
                        btnAddBook.visibility = View.GONE
                    }
                } else {
                    with(binding.layoutBookCreate) {
                        btnAddBook.visibility = View.VISIBLE
                    }
                }
            }

            loading.observe(viewLifecycleOwner) {
                if (it as Boolean) {
                    binding.layoutBookCreate.pbAddBook.visibility = View.VISIBLE
                    for (view in binding.layoutBookCreate.bookCreateContainer.children){
                        if (view !is ProgressBar){
                            view.alpha = 0.23f
                            view.isActivated = false
                            Log.d("ProgressBar",view.id.toString())
                        }
                    }
                } else {
                    binding.layoutBookCreate.pbAddBook.visibility = View.GONE
                    for (view in binding.layoutBookCreate.bookCreateContainer.children){
                            view.alpha = 1.0f
                            Log.d("ProgressBar",view.id.toString())
                    }
                }
            }

            bookCreated.observe(viewLifecycleOwner) {
                with(binding.layoutBookCreate) {
                    if (it as Boolean) {
                        btnBookCover.visibility = View.VISIBLE
                        pbAddBook.visibility = View.VISIBLE
                        btnAddBook.visibility = View.VISIBLE
                    } else {
                        btnBookCover.visibility = View.GONE
                        pbAddBook.visibility = View.GONE
                        pbAddBook.visibility = View.GONE
                    }
                }
            }
            bookDocUriFromFile.observe(viewLifecycleOwner) {
                it.let {
                    if (!it.isNullOrEmpty()) {
                        binding.layoutBookCreate.tvUploadedBook.visibility = View.VISIBLE
                        binding.layoutBookCreate.tvUploadedBook.text = it
                    }else{
                        binding.layoutBookCreate.tvUploadedBook.visibility = View.GONE
                    }
                }
            }

            bookCoverUriFromFile.observe(viewLifecycleOwner) {
                it.let {
                    if (!it.isNullOrEmpty()) {
                        binding.layoutBookCreate.tvUploadedBookCover.text = it
                        binding.layoutBookCreate.tvUploadedBookCover.visibility = View.VISIBLE

                    }else{
                        binding.layoutBookCreate.tvUploadedBookCover.visibility =  View.GONE
                    }
                }
            }
        }


        /**
         * Handling event listener
         * Just put all listener, start here and forward
         */

        /**
         * set SET BOOK COVER button click listener
         */

        binding.layoutBookCreate.apply {


            btnBookCover.setOnClickListener {
                if (createBookViewModel.canUploadBookCover()) {
                    val toast = Toast.makeText(requireContext(), "loading ...", Toast.LENGTH_SHORT)
                    toast.show()
                    if (isPermissionGranted(
                            requireContext(),
                            PHOTO_READ_PERMISSION
                        )
                    ) {
                        getImageIntent()
                    } else {
                        requestPermission(
                            requireActivity(),
                            PHOTO_READ_PERMISSION,
                            BOOK_COVER_RE_CODE
                        )
                    }
                } else {
                    val toast =
                        Toast.makeText(requireContext(), "Title is required!", Toast.LENGTH_LONG)
                    toast.show()
                }
            }

            /**
             * Handling UPLOAD BOOK DOC
             * It is the event listener that handle get uri of book to uploaded and return it uri
             * @param savedInstanceState the returned  data and run  bookWorker manager
             *
             */

            btnUploadBook.setOnClickListener {
                if (createBookViewModel.canUploadBookDoc()) {
                    val toast = Toast.makeText(requireContext(),"Upload book ...",Toast.LENGTH_SHORT)
                    toast.show()
                    if (isPermissionGranted(
                            requireContext(),
                            BOOK_DOCUMENT_PERMISSION
                        )
                    ) {
                        getBookDoc()
                    } else {
                        requestPermission(
                            requireActivity(),
                            BOOK_DOCUMENT_PERMISSION,
                            BOOK_DOC_RE_CODE
                        )
                    }
                } else {
                    val toast =
                        Toast.makeText(requireContext(), "Title is required!", Toast.LENGTH_LONG)
                    toast.show()
                }
            }
            /**
             * Handling Save button onClick
             * It get all book attribute from view model and pass to book object creator
             */

            btnAddBook.setOnClickListener {
                if (createBookViewModel.canSaveBook()) {
                    createBookViewModel.publishBook(
                        this@CreateBookFragment::successCallback,
                        this@CreateBookFragment::failureCallback,
                        this@CreateBookFragment::networkFailureCallback
                    )
                    findNavController().navigate(R.id.action_createBookFragment_to_nav_home)
                } else {
                    val toast = Toast.makeText(
                        requireContext(),
                        "Creating, But We need to fix up something",
                        Toast.LENGTH_LONG
                    )
                    toast.show()
                }
            }

            svCategory.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        val selectedItem = p0!!.getItemAtPosition(p2)
                        if (selectedItem != null) {
                            val toast = Toast.makeText(
                                requireNotNull(activity).application,
                                selectedItem.toString(),
                                Toast.LENGTH_LONG
                            )
                            toast.show()
                            createBookViewModel.bookCategory.postValue(selectedItem.toString())
                        }
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {
                        val selectedItem = p0!!.getItemAtPosition(0).toString()
                        createBookViewModel.bookCategory.postValue(selectedItem)
                    }

                }
        }


        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.categories,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.layoutBookCreate.svCategory.adapter = adapter
        }


        toolbar = binding.bookCreateToolbar

        return binding.root
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GET_BCI && resultCode == Activity.RESULT_OK) {
            val imageUri = data?.data
            val toast = Toast.makeText(requireContext(), imageUri.toString(), Toast.LENGTH_LONG)
            toast.show()
            createBookViewModel.bookCoverUriFromFile.value = imageUri.toString()
            createBookViewModel.uploadBookCover(
                this::successCallback,
                this::failureCallback,
                this::networkFailureCallback
            )
        }
        if (requestCode == GET_BOOK_DOC && resultCode == Activity.RESULT_OK) {
            val bookDoc: Uri? = data?.data
            val toast = Toast.makeText(requireContext(), bookDoc.toString(), Toast.LENGTH_LONG)
            toast.show()
            createBookViewModel.bookDocUriFromFile.value = bookDoc.toString()
            val toastViewModel = Toast.makeText(
                requireContext(),
                createBookViewModel.bookDocUriFromFile.value,
                Toast.LENGTH_LONG
            )
            toastViewModel.show()
            createBookViewModel.uploadBookDoc(
                this::successCallback,
                this::failureCallback,
                this::networkFailureCallback
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.title = getString(R.string.create_app_bar_title_string)
        val drawerLayout = view.findViewById<DrawerLayout>(R.id.drawer_layout)
        appBarConfiguration = AppBarConfiguration(setOf(), drawerLayout)
        NavigationUI.setupWithNavController(
            toolbar,
            navController = findNavController(),
            appBarConfiguration
        )
    }

    override fun onResume() {
        super.onResume()
        toolbar.title = getString(R.string.create_app_bar_title_string)
    }


    override fun onStart() {
        super.onStart()
        toolbar.title = getString(R.string.create_app_bar_title_string)
    }


    /**
     * Set of private function goes here forward
     */


    private fun getImageIntent() {
        val toast = Toast.makeText(requireContext(), "getting image ...", Toast.LENGTH_LONG)
        toast.show()
        val imageIntent = Intent(Intent.ACTION_GET_CONTENT)
        imageIntent.type = "image/*"
        imageIntent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(imageIntent, GET_BCI)

    }

    private fun getBookDoc() {
        val toast = Toast.makeText(requireContext(),"Get book ...",Toast.LENGTH_LONG)
        toast.show()
        val bookDocIntent = Intent(Intent.ACTION_GET_CONTENT)
        bookDocIntent.type = "application/pdf*"
        bookDocIntent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(bookDocIntent, GET_BOOK_DOC)
    }

    companion object {
        const val GET_BCI: Int = 233
        const val GET_BOOK_DOC = 443
        const val PHOTO_READ_PERMISSION = android.Manifest.permission.READ_EXTERNAL_STORAGE
        const val BOOK_DOCUMENT_PERMISSION = android.Manifest.permission.READ_EXTERNAL_STORAGE
        const val BOOK_COVER_RE_CODE = 453
        const val BOOK_DOC_RE_CODE = 786
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun successCallback() {
        val toast =
            Toast.makeText(requireContext(), "upload completed successfully!", Toast.LENGTH_LONG)
        toast.show()
    }

    private fun failureCallback() {
        val toast =
            Toast.makeText(requireContext(), "upload completed successfully!", Toast.LENGTH_LONG)
        toast.show()
    }

    private fun networkFailureCallback() {
        val toast = Toast.makeText(
            requireContext(),
            "lost connection, check your connection!",
            Toast.LENGTH_LONG
        )
        toast.show()
    }
}