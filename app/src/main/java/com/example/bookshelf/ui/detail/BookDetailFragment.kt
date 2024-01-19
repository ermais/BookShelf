package com.example.bookshelf.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
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
    private lateinit var navHost : NavHostFragment
    private lateinit var navController: NavController


    private val rotateOpen : Animation by lazy {
        AnimationUtils.loadAnimation(requireContext(),R.anim.rotate_open_anim)
    }
    private val rotateClose : Animation by lazy {
        AnimationUtils.loadAnimation(requireContext(),R.anim.roatate_close_anim)
    }

    private val fromBottom : Animation by lazy {
        AnimationUtils.loadAnimation(requireContext(),R.anim.from_bottom_anim)
    }
    private val toBottom : Animation  by lazy {
        AnimationUtils.loadAnimation(requireContext(),R.anim.to_bottom_anim)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookDetailBinding.inflate(inflater, container, false)
        activity?.let {
            navHost = it.supportFragmentManager.
            findFragmentById(R.id.nav_host_fragment_content_main)
                    as NavHostFragment
        }
        navController = navHost.navController
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
        )[BookDetailViewModel::class.java]
        bookDetailViewModel.getBook(args.title)
        bookDetailViewModel.openBookMenu.observe(viewLifecycleOwner){
            if (it){
                onOpenAnimation()
            }else{
                onCloseAnimation()
            }
        }
        binding.fabBookDetailMenu.setOnClickListener{
            onfabEditBookMenuClick()
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
//        binding.fabEditBook.setOnClickListener {
//             bookDetailViewModel.book.value?.let { it1 ->
//                val action =
//                    BookDetailFragmentDirections.actionBookDetailFragmentToNavEditBook(it1.bookId)
//                it.findNavController().navigate(action)
//            }
//
//        }

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
        bookDetailViewModel.book.observe(viewLifecycleOwner) {
            binding.bookDetail = it
            toolbar?.title = it?.title
            Glide.with(requireContext())
                .load(it?.bookCover)
                .into(binding.imgBookCover)
        }


        binding.fabEditBook.setOnClickListener {
            bookDetailViewModel.book.value?.let { it1 ->
                val action =
                    BookDetailFragmentDirections.actionBookDetailFragmentToNavEditBook(it1.bookId)
                navController.navigate(action)
            }
        }

    }

    override fun onStart() {
        super.onStart()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    fun onfabEditBookMenuClick(){
        bookDetailViewModel.openBookMenu.value.let {
            bookDetailViewModel.openBookMenu.value = it?.not() ?: false
        }
    }

    fun onOpenAnimation(){
        binding.fabBookDetailMenu.visibility = View.INVISIBLE
        binding.fabEditBook.visibility = View.VISIBLE
        binding.fabDeleteBook.visibility = View.VISIBLE
        binding.fabShareBook.visibility = View.VISIBLE
        binding.fabBookDetailMenu.startAnimation(rotateOpen)
        binding.fabEditBook.startAnimation(fromBottom)
        binding.fabDeleteBook.startAnimation(fromBottom)
        binding.fabShareBook.startAnimation(fromBottom)
    }

    fun onCloseAnimation(){
        binding.fabBookDetailMenu.visibility = View.VISIBLE
        binding.fabEditBook.visibility = View.INVISIBLE
        binding.fabDeleteBook.visibility = View.INVISIBLE
        binding.fabShareBook.visibility = View.INVISIBLE
        binding.fabBookDetailMenu.startAnimation(rotateClose)
        binding.fabEditBook.startAnimation(toBottom)
        binding.fabDeleteBook.startAnimation(toBottom)
        binding.fabShareBook.startAnimation(toBottom)
    }
}