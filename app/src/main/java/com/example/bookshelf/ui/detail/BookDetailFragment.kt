package com.example.bookshelf.ui.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavArgs
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.example.bookshelf.R
import com.example.bookshelf.bussiness.db.BookDao
import com.example.bookshelf.bussiness.db.BookDatabase
import com.example.bookshelf.bussiness.model.Book
import com.example.bookshelf.bussiness.repository.book.BookDetailRepository
import com.example.bookshelf.databinding.FragmentBookDetailBinding
import com.google.android.material.navigation.NavigationView

class BookDetailFragment : Fragment() {
    private  var _binding : FragmentBookDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var bookDetailViewModel: BookDetailViewModel
    private lateinit var bookDetailViewModelFactory: BookDetailViewModelFactory
    private lateinit var bookDetailRepository: BookDetailRepository
    private lateinit var bookDao: BookDao
    val args : BookDetailFragmentArgs by navArgs()
     private lateinit var book: Book
     private var toolbar : Toolbar?  = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentBookDetailBinding.inflate(inflater,container,false)
//        val navView  = requireActivity().findViewById<NavigationView>(R.id.nav_view)
//        navView.setupWithNavController(findNavController())
        val db = BookDatabase.getDatabase(requireContext())
        bookDao = db.bookDao()
        bookDetailRepository = BookDetailRepository(bookDao)
        toolbar = binding.toolbarBookDetail
        val application = requireNotNull(activity).application
        bookDetailViewModelFactory = BookDetailViewModelFactory(bookDetailRepository,application)
        bookDetailViewModel = ViewModelProvider(
            this,
            bookDetailViewModelFactory)
            .get(BookDetailViewModel::class.java)
        bookDetailViewModel.getBook(args.title)
        bookDetailViewModel.book.observe(viewLifecycleOwner){
            binding.bookDetail = it
            toolbar?.setTitle(it.title)
            Glide.with(requireContext())
                .load(it.bookCover)
                .into(binding.imgBookCover)
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar?.setTitle(binding.bookDetail?.title)
        val drawerLayout = view.findViewById<DrawerLayout>(R.id.drawer_layout)
        val appBarConfiguration = AppBarConfiguration(setOf(),drawerLayout)
        NavigationUI.setupWithNavController(toolbar as Toolbar, navController = findNavController(),appBarConfiguration)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}