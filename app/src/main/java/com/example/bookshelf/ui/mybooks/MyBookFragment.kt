package com.example.bookshelf.ui.mybooks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.bookshelf.R
import com.example.bookshelf.bussiness.db.BookDatabase
import com.example.bookshelf.bussiness.networkdata.FirestoreMyBooksDataSource
import com.example.bookshelf.bussiness.repository.book.MyBooksRepository
import com.example.bookshelf.databinding.FragmentMyBookBinding
import com.example.bookshelf.ui.main.MainActivity
import com.google.firebase.firestore.FirebaseFirestore


class MyBookFragment : Fragment() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var toolbar: Toolbar
    private var _binding: FragmentMyBookBinding? = null
    private val binding get() = _binding!!
    private lateinit var myBooksViewModel: MyBooksViewModel
    private lateinit var myBooksViewModelFactory: MyBooksViewModelFactory
    private lateinit var db: BookDatabase
    private lateinit var myBooksRepository: MyBooksRepository
    private lateinit var firestoreMyBooksDataSource: FirestoreMyBooksDataSource
    private lateinit var myBooksAdapter: MyBooksAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMyBookBinding.inflate(inflater, container, false)
//        layout = binding.clvMyBooks
        toolbar = binding.toolbarMybooks
        val application = requireNotNull(activity).application
        firestoreMyBooksDataSource = FirestoreMyBooksDataSource(FirebaseFirestore.getInstance())
        db = BookDatabase.getDatabase(application)
        myBooksRepository = MyBooksRepository(db, firestoreMyBooksDataSource)
        myBooksViewModelFactory = MyBooksViewModelFactory(myBooksRepository)
        myBooksViewModel =
            ViewModelProvider(this, myBooksViewModelFactory)[MyBooksViewModel::class.java]
        myBooksAdapter = MyBooksAdapter(activity as MainActivity)

        myBooksViewModel.myBooks.observe(viewLifecycleOwner) {
            myBooksAdapter.myBooks = it
            println("exposed data by view-model")
            println(it)
        }
        binding.rvMyBooks.adapter = myBooksAdapter



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val _drawerLayout = view.findViewById<DrawerLayout>(R.id.drawer_layout)
        appBarConfiguration = AppBarConfiguration(setOf(), _drawerLayout)
        toolbar.title = getString(R.string.my_books_app_bar_title_string)
        NavigationUI.setupWithNavController(
            toolbar,
            navController = findNavController(),
            appBarConfiguration
        )
    }

    override fun onStart() {
        super.onStart()
        toolbar.title = "My Books"
        myBooksViewModel.myBooks.observe(viewLifecycleOwner) {
            myBooksAdapter.myBooks = it
        }

    }
}