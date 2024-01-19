package com.example.bookshelf.ui.booklist

import android.net.ConnectivityManager
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
import com.example.bookshelf.ui.home.HomeViewModel
import com.example.bookshelf.ui.main.MainActivity
import com.example.bookshelf.ui.main.MainViewModel
import com.example.bookshelf.util.NetworkStatus
import com.example.bookshelf.util.NotificationService
import com.example.bookshelf.util.callActionWithPermission
import com.example.bookshelf.util.getConnMgr
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
    private var isConnected = false
    private lateinit var _connMgr: ConnectivityManager
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookListBinding.inflate(inflater, container, false)
        NotificationService.createNotificationChannel(requireContext().applicationContext)

        val prefManager = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val lan = prefManager.getString("language", "english")
        Log.d("LANGUAGE", lan.toString())

        layout = binding.rvBookList
        db = Firebase.firestore
        cloudStorage = FirebaseStorage.getInstance()
        firestoreBookDataSource = FirestoreBookDataSource(db, cloudStorage)
        activity?.let {
            _connMgr = getConnMgr(requireContext(), it::getSystemService)
        }
        isConnected = NetworkStatus.isConnected(_connMgr)
        bookShelDb = BookDatabase.getDatabase(requireContext())
        bookListRepository = BookListRepository(firestoreBookDataSource, bookShelDb, _connMgr)
        val bookListViewModelFactory =
            BookListViewModelFactory(
                bookListRepository,
                isConnected,
                requireNotNull(activity).application
            )
        bookListModel =
            ViewModelProvider(this, bookListViewModelFactory)[BookListViewModel::class.java]

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
        val adapter = BookListAdapter(requireContext()) { view, downloadUri, bookTitle, bookId ->
//            when {
//                ContextCompat.checkSelfPermission(
//                    requireContext(),
//                    android.Manifest.permission.MANAGE_EXTERNAL_STORAGE
//                ) ==
//                        PackageManager.PERMISSION_GRANTED -> {
//                    val toast =
//                        Toast.makeText(requireContext(), "Downloading ...", Toast.LENGTH_LONG)
//                    toast.show()
//                    println("Granted --------------")
//                    layout.showSnackBar(
//                        view,
//                        getString(R.string.write_external_storage_permission),
//                        Snackbar.LENGTH_SHORT,
//                        null
//                    ) {
//                        Log.d("BOOK-LIST", "I am from inside the snack-bar")
//                        onDownloadBook(downloadUri, bookTitle, bookId)
//                    }
//                    onDownloadBook(downloadUri, bookTitle, bookId)
//                }
//                ActivityCompat.shouldShowRequestPermissionRationale(
//                    requireActivity(),
//                    android.Manifest.permission.MANAGE_EXTERNAL_STORAGE
//                ) -> {
//                    layout.showSnackBar(
//                        view,
//                        getString(R.string.write_external_storage_permission),
//                        Snackbar.LENGTH_INDEFINITE,
//                        getString(R.string.ok),
//                    ) {
//                        requestPermissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                    }
//                }
//                else -> {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                        requestPermission(activity,android.Manifest.permission.MANAGE_EXTERNAL_STORAGE,300)
//                    }
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                        requestPermissionLauncher.launch(android.Manifest.permission.MANAGE_EXTERNAL_STORAGE)
//                    }
//                }
//            }

            val downloadBook : ()->Unit = {
                onDownloadBook(downloadUri, bookTitle, bookId)
            }
            callActionWithPermission(
                requireContext(),
                requireActivity(),
                POST_NOTIFICATION,
                POST_NOTIFICATION_PC,
                downloadBook
            )
        }
        activity?.let {
            mainViewModel = ViewModelProvider(it)[MainViewModel::class.java]
        }


        mainViewModel.query.observe(viewLifecycleOwner) { query ->
            bookListModel.filterBooks(query)
        }
        homeViewModel.filterByCategory.observe(viewLifecycleOwner) {
            if (it != "All") {
                bookListModel.filterByCategory(it)
            } else {
                bookListModel.getBooks()
            }
        }


        homeViewModel.sortBy.observe(viewLifecycleOwner) {
            Log.d("HOME_VIEW_MODEL", it)
            when (it) {
                "date" -> bookListModel.sortByPubDate()
                "author" -> bookListModel.sortByAuthor()
                "title" -> bookListModel.sortByTitle()
                else -> bookListModel.sortByPubDate()
            }
        }

        mainViewModel.query.observe(viewLifecycleOwner) {
            bookListModel.filterBooks(it)
        }


        bookListModel.filteredBooks.observe(viewLifecycleOwner) {
            adapter.data = it.asDomainModel()
            println("Filtered Book provided to List--------------------")
            println(it.asDomainModel())
        }
        binding.rvBookList.adapter = adapter


        with(binding.rvBookList) {
            this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    if (RecyclerView.SCROLL_STATE_IDLE == newState) {
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

                    if (dy < -1) {
                        homeViewModel.createBookFabBtnVisible.value = true
                    }
                }
            })
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mainActivity = requireActivity() as MainActivity
        toolbar = view.findViewById(R.id.toolbarBookList)
        val drawerLayout = mainActivity.findViewById<DrawerLayout>(R.id.drawer_layout)
        val appBarConfiguration = AppBarConfiguration(findNavController().graph, drawerLayout)
        if (toolbar != null) {
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


    override fun onStart() {
        super.onStart()
        toolbar?.title = "Book Shelf"
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
            Log.d("HOME_VIEW_MODEL", it)
            when (it) {
                "date" -> bookListModel.sortByPubDate()
                "author" -> bookListModel.sortByAuthor()
                "title" -> bookListModel.sortByTitle()
                else -> bookListModel.sortByPubDate()
            }
        }
    }

    private fun onDownloadBook(uri: Uri, title: String, bookId: String) {
        bookListModel.downloadBook(uri, title, bookId)
    }

    companion object {
        const val POST_NOTIFICATION = android.Manifest.permission.POST_NOTIFICATIONS
        const val POST_NOTIFICATION_PC = 1239
    }

}