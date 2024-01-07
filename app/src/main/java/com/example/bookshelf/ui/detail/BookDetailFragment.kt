package com.example.bookshelf.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.bumptech.glide.Glide
import com.example.bookshelf.R
import com.example.bookshelf.bussiness.db.BookDao
import com.example.bookshelf.bussiness.db.BookDatabase
import com.example.bookshelf.bussiness.networkdata.FirestoreMyBooksDataSource
import com.example.bookshelf.bussiness.repository.book.BookDetailRepository
import com.example.bookshelf.bussiness.repository.book.MyBooksRepository
import com.example.bookshelf.databinding.FragmentBookDetailBinding
import com.google.firebase.firestore.FirebaseFirestore

class BookDetailFragment : Fragment() {
    private var _binding: FragmentBookDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var bookDetailViewModel: BookDetailViewModel
    private lateinit var bookDetailViewModelFactory: BookDetailViewModelFactory
    private lateinit var bookDetailRepository: BookDetailRepository
    private lateinit var myBooksRepository: MyBooksRepository
    private lateinit var firestoreMyBooksDataSource: FirestoreMyBooksDataSource
    private lateinit var bookDao: BookDao
    val args: BookDetailFragmentArgs by navArgs()
    private var toolbar: Toolbar? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookDetailBinding.inflate(inflater, container, false)
        val db = BookDatabase.getDatabase(requireContext())
        bookDao = db.bookDao()
        bookDetailRepository = BookDetailRepository(bookDao)
        firestoreMyBooksDataSource = FirestoreMyBooksDataSource(FirebaseFirestore.getInstance())
        myBooksRepository = MyBooksRepository(db, firestoreMyBooksDataSource)
        toolbar = binding.toolbarBookDetail
        val application = requireNotNull(activity).application
        bookDetailViewModelFactory =
            BookDetailViewModelFactory(bookDetailRepository, myBooksRepository, application)
        bookDetailViewModel = ViewModelProvider(
            this,
            bookDetailViewModelFactory
        )
            .get(BookDetailViewModel::class.java)
        bookDetailViewModel.getBook(args.title)
        bookDetailViewModel.book.observe(viewLifecycleOwner) {
            binding.bookDetail = it
            toolbar?.title = it.title
            Glide.with(requireContext())
                .load(it.bookCover)
                .into(binding.imgBookCover)
        }

        binding.btnBuyBook.isEnabled = true
        binding.btnBuyBook.setOnClickListener {
            if (bookDetailViewModel.book.value != null) {
                bookDetailViewModel.buyBook()
                val toast = Toast.makeText(
                    requireContext(),
                    "Transaction completed successfully!",
                    Toast.LENGTH_LONG
                )
                toast.show()
                findNavController().navigate(R.id.nav_my_books)
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar?.title = binding.bookDetail?.title
        val drawerLayout = view.findViewById<DrawerLayout>(R.id.drawer_layout)
        val appBarConfiguration = AppBarConfiguration(setOf(), drawerLayout)
        NavigationUI.setupWithNavController(
            toolbar as Toolbar,
            navController = findNavController(),
            appBarConfiguration
        )
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}