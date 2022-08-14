package com.example.bookshelf.ui.booklist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.bookshelf.databinding.FragmentBookListBinding
import com.example.bookshelf.model.book.Book
import com.example.bookshelf.model.book.FirestoreBookDataSource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

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
        firestoreBookDataSource = FirestoreBookDataSource(db)
        bookListRepository = BookListRepository(firestoreBookDataSource)
        val bookListViewModelFactory = BookListViewModelFactory(bookListRepository, requireNotNull(activity).application)
        bookListModel = ViewModelProvider(this,bookListViewModelFactory).get(BookListViewModel::class.java)
        val adapter = BookListAdapter(requireContext())
        bookListModel.books.observe(viewLifecycleOwner) {
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