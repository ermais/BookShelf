package com.example.bookshelf.ui.profile

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.bookshelf.R
import com.example.bookshelf.bussiness.networkdata.FirestoreProfileDataSource
import com.example.bookshelf.bussiness.repository.profile.UserProfileRepository
import com.example.bookshelf.databinding.FragmentProfileBinding
import com.example.bookshelf.ui.main.MainActivity
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage


@Suppress("DEPRECATION")
class ProfileFragment : Fragment() {
    private  var _binding : FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var profileViewModelFactory: ProfileViewModelFactory
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var cloudStorage : FirebaseStorage
    private lateinit var dataSource: FirestoreProfileDataSource
    private lateinit var repository: UserProfileRepository
    private lateinit var toolbar : Toolbar
    private lateinit var layout : CollapsingToolbarLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navView : NavigationView
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater,container,false)

        db = FirebaseFirestore.getInstance()
        cloudStorage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        dataSource = FirestoreProfileDataSource(db,cloudStorage)
        repository = UserProfileRepository(dataSource)
        profileViewModelFactory = ProfileViewModelFactory(repository,auth,
            requireNotNull(activity).application)
        profileViewModel = ViewModelProvider(
            this,
            profileViewModelFactory
        )[ProfileViewModel::class.java]
        binding.viewModel = profileViewModel
        toolbar = binding.profileToolbar
        layout = binding.layoutProfileCollapsable
        navController = findNavController()
        val _drawer = view?.findViewById<DrawerLayout>(R.id.drawer_layout)
        appBarConfiguration = AppBarConfiguration(setOf(),_drawer)
        NavigationUI.setupWithNavController(layout,toolbar,navController,appBarConfiguration)


        return binding.root
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.title = "profile page"
        val drawerLayout = view.findViewById<DrawerLayout>(R.id.drawer_layout)
        val appBarConfiguration = AppBarConfiguration(setOf(), drawerLayout)
        layout.isTitleEnabled = false
        layout.title = "profile name"
//        layout.setupWithNavController(toolbar,navController,appBarConfiguration)
        NavigationUI.setupWithNavController(
            toolbar as Toolbar,
            navController = findNavController(),
            appBarConfiguration
        )
    }
}