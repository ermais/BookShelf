package com.example.bookshelf.ui.booklist

import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toolbar
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookshelf.R
import com.example.bookshelf.bussiness.db.BookDatabase
import com.example.bookshelf.bussiness.db.asDomainModel
import com.example.bookshelf.bussiness.networkdata.FirestoreBookDataSource
import com.example.bookshelf.bussiness.repository.book.BookListRepository
import com.example.bookshelf.databinding.FragmentBookListBinding
import com.example.bookshelf.ui.Utils.showSnackBar
import com.example.bookshelf.ui.home.HomeViewModel
import com.example.bookshelf.ui.main.MainActivity
import com.example.bookshelf.ui.main.MainViewModel
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class BookListFragment : Fragment() {

    private lateinit var layout: View
    private lateinit var db: FirebaseFirestore
    private lateinit var firestoreBookDataSource: FirestoreBookDataSource
    private lateinit var bookListRepository: BookListRepository
    private lateinit var bookListModel: BookListViewModel
    private var _binding: FragmentBookListBinding? = null
    private lateinit var cloudStorage: FirebaseStorage
    private lateinit var mainViewModel: MainViewModel
    private val homeViewModel: HomeViewModel by activityViewModels()
    private lateinit var bookShelDb: BookDatabase
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private var toolbar: Toolbar? = null


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBookListBinding.inflate(inflater, container, false)
        println("Book List -----------------------------------------------")

        val prefManager = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val lan = prefManager.getString("language","english")
        Log.d("LANGUAGE",lan.toString())

        layout = binding.rvBookList
        db = Firebase.firestore
        cloudStorage = FirebaseStorage.getInstance()
        firestoreBookDataSource = FirestoreBookDataSource(db, cloudStorage)
        bookShelDb = BookDatabase.getDatabase(requireContext())
        bookListRepository = BookListRepository(firestoreBookDataSource, bookShelDb)
        val bookListViewModelFactory =
            BookListViewModelFactory(bookListRepository, requireNotNull(activity).application)
        bookListModel =
            ViewModelProvider(this, bookListViewModelFactory).get(BookListViewModel::class.java)

        val adapter = BookListAdapter(requireContext()) { view, downloadUri, bookTitle, bookId ->
            println("onDownload callback---------------------------------- ${bookId} ")
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) ==
                        PackageManager.PERMISSION_GRANTED -> {
                    val toast =
                        Toast.makeText(requireContext(), "Downloading ...", Toast.LENGTH_LONG)
                    toast.show()
                    println("Granted --------------")
                    layout.showSnackBar(
                        view,
                        getString(R.string.write_external_storage_permission),
                        Snackbar.LENGTH_SHORT,
                        null
                    ) {
                        Log.d("BOOK-LIST", "I am from inside the snack-bar")
                        onDownloadBook(view, downloadUri, bookTitle, bookId)
                    }
                    onDownloadBook(view, downloadUri, bookTitle, bookId)
                }
                ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) -> {
                    layout.showSnackBar(
                        view,
                        getString(R.string.write_external_storage_permission),
                        Snackbar.LENGTH_INDEFINITE,
                        getString(R.string.ok),
                    ) {
                        requestPermissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    }
                }
                else -> {
                    requestPermissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }

        }
        activity?.let {
            mainViewModel = ViewModelProvider(it).get(MainViewModel::class.java)
        }


        mainViewModel.query.observe(viewLifecycleOwner) { query ->
            bookListModel.filterBooks(query)
        }
        bookListModel.filteredBooks.observe(viewLifecycleOwner) {
            adapter.data = it.asDomainModel()
            println("Filtered Book provided to List--------------------")
            println(it.asDomainModel())
        }

//        bookListModel.workInfo.observe(viewLifecycleOwner){
//            println("Work info size .......................................... ${it.size}")
//            Log.d("WORK_INFO","Size of work info ---------- ${it.size}")
//            try {
//                if(it.isNotEmpty()){
//                    val workInfo = it[0]
//                    if (workInfo.state == WorkInfo.State.SUCCEEDED ){
//                        println("Getting ..... work info-------------------------------------")
//                        val bookFileUri = workInfo.outputData.getString(KEY_DOWNLOAD_BOOK_URI)
//                        val bookTitle = workInfo.outputData.getString(KEY_BOOK_TITLE)
//                        val bookId = workInfo.outputData.getInt(KEY_BOOK_ID,0)
//                        println("Book Id ---------------------------------${bookId}")
//                        println("Book Uri .................................${bookFileUri}")
//                        if (bookFileUri != null) {
//                            val download = DownloadEntity(bookFileUri,bookId)
//                            println("Download instance ...............................${download.toString()}")
//                            bookListModel.addDownloads(download)
//                        }
//
//                    }
//                }
//            }catch (_: Exception){
//                println("Something goes wrong! ----------------------------")
//            }
//
//
//        }

        homeViewModel.filterByCategory.observe(viewLifecycleOwner) {
            if (it != "All") {
                bookListModel.filterByCategory(it)
            } else {
                bookListModel.getBooks()
            }
        }

        requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    val toast = Toast.makeText(activity, "Permission Granted", Toast.LENGTH_LONG)
                    toast.show()
                } else {
                    val toast = Toast.makeText(activity, "Permission Denied", Toast.LENGTH_LONG)
                    toast.show()
                }
            }

        mainViewModel.query.observe(viewLifecycleOwner) {
            bookListModel.filterBooks(it)
        }

        homeViewModel.sortBy.observe(viewLifecycleOwner) {
            Log.d("HOMEVIEWMODEL", it)
            when (it) {
                "date" -> bookListModel.sortByPubDate()
                "author" -> bookListModel.sortByAuthor()
                "title" -> bookListModel.sortByTitle()
                else -> bookListModel.sortByPubDate()
            }
        }




        binding.rvBookList.adapter = adapter
        with(binding.rvBookList) {
            this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    if(RecyclerView.SCROLL_STATE_IDLE == newState){
                        homeViewModel.createBookFabBtnVisible.value = true
                    }
                    super.onScrollStateChanged(recyclerView, newState)
                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 10) {
                        homeViewModel.createBookFabBtnVisible.value = false
                    }
                    if (dy < -10) {
                        homeViewModel.createBookFabBtnVisible.value = true
                    }
                }
            })
        }

        val root: View = binding.root

//        val textView: TextView = binding.book_list
//        bookListModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mainActivity = requireActivity() as MainActivity
        toolbar = view.findViewById<Toolbar>(R.id.toolbarBookList)
        val layout = view.findViewById<CollapsingToolbarLayout>(R.id.toolbarBookListLayout)
        val drawerLayout = mainActivity.findViewById<DrawerLayout>(R.id.drawer_layout)
        val appBarConfiguration = AppBarConfiguration(findNavController().graph, drawerLayout)
        if(toolbar != null){
            mainActivity.setupActionBarWithNavController(findNavController(), appBarConfiguration)

        }
        homeViewModel.filterByCategory.observe(viewLifecycleOwner) {
            if (it != "All") {
                bookListModel.filterByCategory(it)
            } else {
                bookListModel.getBooks()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val WRITE_PERMISSION = android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        const val WRITE_PERMISSION_CODE = 211
    }

    override fun onStart() {
        super.onStart()
        toolbar?.title = "Book Shelf"

//        activity?.let {
//            homeViewModel = ViewModelProvider(it).get(HomeViewModel::class.java)
//        }


        homeViewModel.filterByCategory.observe(viewLifecycleOwner) {
            if (it != "All") {
                bookListModel.filterByCategory(it)
            } else {
                bookListModel.getBooks()
            }
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        homeViewModel.sortBy.observe(viewLifecycleOwner) {
            Log.d("HOMEVIEWMODEL", it)
            when (it) {
                "date" -> bookListModel.sortByPubDate()
                "author" -> bookListModel.sortByAuthor()
                "title" -> bookListModel.sortByTitle()
                else -> bookListModel.sortByPubDate()
            }
        }
    }

    fun onDownloadBook(view: View, uri: Uri, title: String, bookId: String) {
        bookListModel.downloadBook(uri, title, bookId)
    }

}