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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class BookListFragment : Fragment() {
    val books by lazy {
        arrayListOf<Book>(
            Book(
                "The Green Spot in your face",
                "USERUID RANDOM ",
                "Fiction",
                "as soon as I see the post in ur face.",
                null,
                null,
                "4.5"
            ),
            Book(
                "The Green Spot in your face",
                "USER ID UID",
                "Fiction",
                "as soon as I see the post in ur face.",
                null,
                null,
                "4.3"
            )

        )
    }
    private lateinit var db: FirebaseFirestore
    private lateinit var firestoreBookDataSource: FirestoreBookDataSource
    private lateinit var bookListRepository: BookListRepository
    private lateinit var bookListModel: BookListViewModel
    private var _binding: FragmentBookListBinding? = null
    private lateinit var cloudStorage: FirebaseStorage
    private lateinit var mainViewModel : MainViewModel

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
        bookListRepository = BookListRepository(firestoreBookDataSource)
        val bookListViewModelFactory = BookListViewModelFactory(bookListRepository, requireNotNull(activity).application)
        bookListModel = ViewModelProvider(this,bookListViewModelFactory).get(BookListViewModel::class.java)
        val adapter = BookListAdapter(requireContext())
        activity?.let {
            mainViewModel = ViewModelProviders.of(it)[MainViewModel::class.java]
        }
        mainViewModel.query.observe(viewLifecycleOwner){query->
            bookListModel.filter(query)
        }
        bookListModel.filteredBooks?.observe(viewLifecycleOwner) {
            adapter.data = it
        }


        adapter.data = books
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