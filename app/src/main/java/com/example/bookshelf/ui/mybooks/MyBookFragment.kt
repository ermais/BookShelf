package com.example.bookshelf.ui.mybooks

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.bookshelf.R
import com.example.bookshelf.bussiness.db.BookDatabase
import com.example.bookshelf.bussiness.networkdata.FirestoreMyBooksDataSource
import com.example.bookshelf.bussiness.repository.book.MyBooksRepository
import com.example.bookshelf.databinding.FragmentMyBookBinding
import com.example.bookshelf.ui.main.MainActivity
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class MyBookFragment : Fragment() {
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navView: NavigationView
    private lateinit var layout: CollapsingToolbarLayout
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var mainActivity: MainActivity
    private lateinit var drawerLayout: DrawerLayout
    private var _binding: FragmentMyBookBinding? = null
    private val binding get() = _binding!!
    private lateinit var myBooksViewModel: MyBooksViewModel
    private lateinit var myBooksViewModelFactory: MyBooksViewModelFactory
    private lateinit var db: BookDatabase
    private lateinit var myBooksRepository: MyBooksRepository
    private lateinit var firestoreMyBooksDataSource: FirestoreMyBooksDataSource
    private lateinit var myBooksAdapter: MyBooksAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMyBookBinding.inflate(inflater, container, false)
        layout = binding.clvMyBooks
        toolbar = binding.toolbarMybooks
        mainActivity = activity as MainActivity
        navHostFragment =
            mainActivity.supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        navController = navHostFragment.findNavController()
        drawerLayout = mainActivity.findViewById(R.id.drawer_layout)
        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
        navView = mainActivity.findViewById(R.id.nav_view)
        val application = requireNotNull(activity).application
        firestoreMyBooksDataSource = FirestoreMyBooksDataSource(FirebaseFirestore.getInstance())
        db = BookDatabase.getDatabase(application)
        myBooksRepository = MyBooksRepository(db, firestoreMyBooksDataSource)
        myBooksViewModelFactory = MyBooksViewModelFactory(myBooksRepository, application)
        myBooksViewModel =
            ViewModelProvider(this, myBooksViewModelFactory)[MyBooksViewModel::class.java]
        myBooksAdapter = MyBooksAdapter()

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

        drawerLayout = mainActivity.findViewById(R.id.drawer_layout)
        toolbar.setNavigationIcon(R.drawable.ic_baseline_menu_24)
        layout.isTitleEnabled = true
        layout.title = "My Books"
        toolbar.setTitle(getString(R.string.book_list_app_bar_title))
        mainActivity.setSupportActionBar(toolbar)
        mainActivity.actionBar?.setDisplayHomeAsUpEnabled(true)
        layout.setupWithNavController(toolbar, navController)
        NavigationUI.setupWithNavController(
            layout,
            toolbar,
            findNavController(),
            appBarConfiguration
        )
        val toggle = ActionBarDrawerToggle(
            mainActivity,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    override fun onStart() {
        super.onStart()
        val homeActivity = activity as MainActivity
        val drawerHome = homeActivity.findViewById<DrawerLayout>(R.id.drawer_layout)
        if (drawerHome != null) {
            Log.d("HOME_FRAGMENT", "ON-START$drawerHome")
        }
        mainActivity = requireContext() as MainActivity
        navHostFragment =
            mainActivity.supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        navController = navHostFragment.findNavController()
        navView = mainActivity.findViewById(R.id.nav_view)
        drawerLayout = mainActivity.findViewById(R.id.drawer_layout)
        appBarConfiguration = AppBarConfiguration(
            navController.graph,
            drawerLayout
        )

        mainActivity.setSupportActionBar(toolbar)
        mainActivity.actionBar?.title = getString(R.string.book_list_app_bar_title)
        mainActivity.actionBar?.setDisplayHomeAsUpEnabled(true)
        layout.setupWithNavController(toolbar, navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        toolbar.title = "My Books"
        layout.title = "My Books"
        myBooksViewModel.myBooks.observe(viewLifecycleOwner){
            myBooksAdapter.myBooks = it
        }

    }
}