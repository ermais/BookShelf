package com.example.bookshelf.ui.recent

import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.bookshelf.bussiness.db.BookDatabase
import com.example.bookshelf.bussiness.db.asDomainModel
import com.example.bookshelf.bussiness.networkdata.FirestoreBookDataSource
import com.example.bookshelf.bussiness.repository.book.BookListRepository
import com.example.bookshelf.databinding.FragmentRecentBinding
import com.example.bookshelf.ui.booklist.BookListAdapter
import com.example.bookshelf.ui.booklist.BookListViewModel
import com.example.bookshelf.ui.booklist.BookListViewModelFactory
import com.example.bookshelf.util.NetworkStatus
import com.example.bookshelf.util.getConnMgr
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class RecentFragment : Fragment() {

    private lateinit var recentModel: RecentViewModel
    private var _binding: FragmentRecentBinding? = null
    private lateinit var recentViewModelFactory: RecentViewModelFactory
    private lateinit var recentViewModel: RecentViewModel
    private lateinit var bookListRepository: BookListRepository
    private lateinit var auth : FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseFirestore
    private lateinit var cloudStorage : FirebaseStorage
    private lateinit var firestoreBookDataSource : FirestoreBookDataSource
    private lateinit var _connMgr : ConnectivityManager
    private lateinit var bookShelDb : BookDatabase
    private var isConnected = false
    private lateinit var adapter : RecentAdapter
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecentBinding.inflate(inflater, container, false)

        firebaseDatabase = Firebase.firestore
        cloudStorage = FirebaseStorage.getInstance()
        firestoreBookDataSource = FirestoreBookDataSource(firebaseDatabase, cloudStorage)
        activity?.let {
            _connMgr = getConnMgr(requireContext(), it::getSystemService)
        }
        isConnected = NetworkStatus.isConnected(_connMgr)
        bookShelDb = BookDatabase.getDatabase(requireContext())
        bookListRepository = BookListRepository(firestoreBookDataSource, bookShelDb, _connMgr)

        recentViewModelFactory = RecentViewModelFactory(bookListRepository,isConnected, requireNotNull(activity).application)
        recentViewModel = ViewModelProvider(
            this,
            recentViewModelFactory
        )[RecentViewModel::class.java]
        binding.lifecycleOwner = this.viewLifecycleOwner
        adapter = RecentAdapter(requireContext()) { view, downloadUri, bookTitle, bookId ->
            onDownloadBook(downloadUri, bookTitle, bookId)
        }
        recentViewModel.recentBooks.observe(viewLifecycleOwner){
            Log.d("GETRecentO","${it}------ListEntity")
            adapter.data = it.asDomainModel()
        }
        binding.rvRecentBookList.adapter = adapter



        binding.lifecycleOwner = this
        val root: View = binding.root
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        firebaseDatabase = Firebase.firestore
        cloudStorage = FirebaseStorage.getInstance()
        firestoreBookDataSource = FirestoreBookDataSource(firebaseDatabase, cloudStorage)
        activity?.let {
            _connMgr = getConnMgr(requireContext(), it::getSystemService)
        }
        isConnected = NetworkStatus.isConnected(_connMgr)
        bookShelDb = BookDatabase.getDatabase(requireContext())
        bookListRepository = BookListRepository(firestoreBookDataSource, bookShelDb, _connMgr)

        recentViewModelFactory = RecentViewModelFactory(bookListRepository,isConnected, requireNotNull(activity).application)
        recentViewModel = ViewModelProvider(
            this,
            recentViewModelFactory
        )[RecentViewModel::class.java]

    }


    private fun onDownloadBook(uri: Uri, title: String, bookId: String) {
        recentViewModel.downloadBook(uri, title, bookId)
    }


}