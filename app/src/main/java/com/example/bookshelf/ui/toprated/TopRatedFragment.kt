package com.example.bookshelf.ui.toprated

import android.net.ConnectivityManager
import android.net.NetworkInfo
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
import com.example.bookshelf.bussiness.repository.book.TopRatedBookRepository
import com.example.bookshelf.databinding.FragmentTopRatedBinding
import com.example.bookshelf.ui.recent.RecentAdapter
import com.example.bookshelf.util.NetworkStatus
import com.example.bookshelf.util.getConnMgr
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class TopRatedFragment : Fragment() {

    private var _binding: FragmentTopRatedBinding? = null
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var cloudStorage : FirebaseStorage
    private lateinit var firestoreBookDataSource: FirestoreBookDataSource
    private lateinit var bookshelfDb : BookDatabase
    private lateinit var topRatedBookRepository: TopRatedBookRepository
    private lateinit var topRatedViewModel: TopRatedViewModel
    private lateinit var topRatedBookViewModelFactory: TopRatedBookViewModelFactory
    private var isConnected = false
    private lateinit var _connMgr : ConnectivityManager

    private lateinit var adapter : TopRatedBookAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTopRatedBinding.inflate(inflater, container, false)
        val root: View = binding.root
        firebaseFirestore = Firebase.firestore
        cloudStorage = FirebaseStorage.getInstance()
        firestoreBookDataSource = FirestoreBookDataSource(firebaseFirestore,cloudStorage)
        bookshelfDb = BookDatabase.getDatabase(requireContext())
        activity?.let {
            _connMgr = getConnMgr(requireContext(),it::getSystemService)
        }
        topRatedBookRepository = TopRatedBookRepository(firestoreBookDataSource,bookshelfDb,_connMgr)
        isConnected = NetworkStatus.isConnected(_connMgr)
        topRatedBookViewModelFactory = TopRatedBookViewModelFactory(
            topRatedBookRepository,
            isConnected,
            requireNotNull(activity).application
        )
        topRatedViewModel = ViewModelProvider(
            this,
            topRatedBookViewModelFactory
        )[TopRatedViewModel::class.java]
        binding.lifecycleOwner = this.viewLifecycleOwner

        adapter = TopRatedBookAdapter(requireContext()) { view, downloadUri, bookTitle, bookId ->
            onDownloadBook(downloadUri, bookTitle, bookId)
        }

        topRatedViewModel.topRatedBooks.observe(viewLifecycleOwner){
            Log.d("GETTop","${it}----------------GET-TOP")
            adapter.data = it.asDomainModel()
        }
        binding.rvTopRatedBookList.adapter = adapter


        return root
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        firebaseFirestore = Firebase.firestore
        cloudStorage = FirebaseStorage.getInstance()
        firestoreBookDataSource = FirestoreBookDataSource(firebaseFirestore,cloudStorage)
        bookshelfDb = BookDatabase.getDatabase(requireContext())
        activity?.let {
            _connMgr = getConnMgr(requireContext(),it::getSystemService)
        }
        topRatedBookRepository = TopRatedBookRepository(firestoreBookDataSource,bookshelfDb,_connMgr)
        isConnected = NetworkStatus.isConnected(_connMgr)
        topRatedBookViewModelFactory = TopRatedBookViewModelFactory(
            topRatedBookRepository,
            isConnected,
            requireNotNull(activity).application
        )
        topRatedViewModel = ViewModelProvider(
            this,
            topRatedBookViewModelFactory
        )[TopRatedViewModel::class.java]
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onDownloadBook(uri: Uri, title: String, bookId: String) {
        topRatedViewModel.downloadBook(uri, title, bookId)
    }
}