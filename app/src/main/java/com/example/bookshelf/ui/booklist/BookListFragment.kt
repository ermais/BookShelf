package com.example.bookshelf.ui.booklist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.bookshelf.ui.main.MainViewModel
import com.example.bookshelf.databinding.FragmentBookListBinding
import com.example.bookshelf.bussiness.model.Book
import com.example.bookshelf.bussiness.FirestoreBookDataSource
import com.example.bookshelf.bussiness.db.BookDatabase
import com.example.bookshelf.bussiness.db.asDomainModel
import com.example.bookshelf.bussiness.repository.book.BookListRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class BookListFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var firestoreBookDataSource: FirestoreBookDataSource
    private lateinit var bookListRepository: BookListRepository
    private lateinit var bookListModel: BookListViewModel
    private var _binding: FragmentBookListBinding? = null
    private lateinit var cloudStorage: FirebaseStorage
    private lateinit var mainViewModel : MainViewModel
    private lateinit var bookShelDb : BookDatabase

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBookListBinding.inflate(inflater, container, false)
        db = Firebase.firestore
        cloudStorage = FirebaseStorage.getInstance()
        firestoreBookDataSource = FirestoreBookDataSource(db, cloudStorage)
        bookShelDb = BookDatabase.getDatabase(requireContext())
        bookListRepository = BookListRepository(firestoreBookDataSource,bookShelDb)
        val bookListViewModelFactory = BookListViewModelFactory(bookListRepository, requireNotNull(activity).application)
        bookListModel = ViewModelProvider(this,bookListViewModelFactory).get(BookListViewModel::class.java)
        val adapter = BookListAdapter(requireContext())
        activity?.let {
            mainViewModel = ViewModelProviders.of(it)[MainViewModel::class.java]
        }
//        mainViewModel.query.observe(viewLifecycleOwner){query->
//            bookListModel.filter(query)
//        }
        bookListModel.filteredBooks.observe(viewLifecycleOwner) {
            adapter.data = it.asDomainModel()
        }

        mainViewModel.filterByCategory.observe(viewLifecycleOwner){
            if (it != "All"){
                bookListModel.filterByCategory(it)
            }else{
                bookListModel.getBooks()
            }


        }

        mainViewModel.query.observe(viewLifecycleOwner){
            bookListModel.filterBooks(it)
        }


        binding.rvBookList.adapter = adapter
        val root: View = binding.root

//        val textView: TextView = binding.book_list
//        bookListModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}