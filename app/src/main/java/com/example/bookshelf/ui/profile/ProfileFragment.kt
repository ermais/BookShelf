package com.example.bookshelf.ui.profile

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.net.toUri
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bookshelf.R
import com.example.bookshelf.bussiness.networkdata.FirestoreProfileDataSource
import com.example.bookshelf.bussiness.repository.profile.UserProfileRepository
import com.example.bookshelf.databinding.FragmentProfileBinding
import com.example.bookshelf.ui.create.CreateBookFragment
import com.example.bookshelf.ui.main.MainActivity
import com.example.bookshelf.util.callActionWithPermission
import com.example.bookshelf.util.isPermissionGranted
import com.example.bookshelf.util.requestPermission
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
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navView : NavigationView
    private lateinit var navController: NavController
    private lateinit var mainActivity: MainActivity
    private lateinit var layout: CollapsingToolbarLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater,container,false)
        mainActivity = requireActivity() as MainActivity
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
        binding.lifecycleOwner = this.viewLifecycleOwner

        binding.viewModel = profileViewModel

        toolbar = binding.profileToolbar
        layout = binding.layoutProfileCollapsable
        layout.isTitleEnabled = false
        navController = findNavController()
        val _drawer = view?.findViewById<DrawerLayout>(R.id.drawer_layout)
        appBarConfiguration = AppBarConfiguration(setOf(),_drawer)
        NavigationUI.setupWithNavController(layout,toolbar,navController,appBarConfiguration)
        Log.d("ProfileF","${profileViewModel.profile.value} ---------Model")

        /**
         * Observer area
         */

//        profileViewModel.displayName.observe(viewLifecycleOwner){
//            Log.d("ProfileD","${it} ----DisplayName")
//            binding.tevDisplayName.setText(it)
//        }
//        profileViewModel.firstName.observe(viewLifecycleOwner){
//            Log.d("FirstNameS","${it},  ----FirstName")
//            binding.tevFirstName.setText(it)
//        }
//        profileViewModel.lastName.observe(viewLifecycleOwner){
//            binding.tevLastName.setText(it)
//        }
        profileViewModel.email.observe(viewLifecycleOwner){
            Log.d("ProfileE","${it} ----Email")
            binding.tvUserName.setText(it)

        }

        profileViewModel.changeName.observe(viewLifecycleOwner){
            with(binding){
                if (it == false){
                    btnDiscardNameChange.visibility = View.GONE
                    btnSaveName.visibility = View.GONE
                    btnEditName.visibility = View.VISIBLE
                    tevFirstName.isEnabled = false
                    tevLastName.isEnabled = false
                }else{
                    btnDiscardNameChange.visibility = View.VISIBLE
                    btnSaveName.visibility = View.VISIBLE
                    btnEditName.visibility = View.GONE
                    tevFirstName.isEnabled = true
                    tevLastName.isEnabled = true
                }
            }
        }

        profileViewModel.changeDisplayName.observe(viewLifecycleOwner){
            with(binding){
                if (it == true){
                    btnDiscardDisplayNameChange.visibility = View.VISIBLE
                    btnSaveDisplayName.visibility = View.VISIBLE
                    btnEditDisplayName.visibility = View.GONE
                    tevDisplayName.isEnabled = true
                }else{
                    btnDiscardDisplayNameChange.visibility = View.GONE
                    btnSaveDisplayName.visibility = View.GONE
                    btnEditDisplayName.visibility = View.VISIBLE
                    tevDisplayName.isEnabled = false
                }
            }
        }


        /**
         * action calling area
         */
        with(binding){
            btnEditName.setOnClickListener{
                profileViewModel.setChangeName(true)
            }
            btnDiscardNameChange.setOnClickListener{
                profileViewModel.setChangeName(false)
            }
            btnSaveName.setOnClickListener{
                onSaveName()
            }

            btnEditDisplayName.setOnClickListener{
                profileViewModel.setChangeDisplayName(true)
            }

            btnDiscardNameChange.setOnClickListener{
                profileViewModel.setChangeDisplayName(false)
            }

            btnSaveDisplayName.setOnClickListener{
                    onSaveDisplayName()
                }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                profileAppbar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
                    profileViewModel.showEditProfilePicFab.value = verticalOffset == 0
                }
            }

        }

        profileViewModel.userPhotoUrl.observe(viewLifecycleOwner){
            val requestOptions: RequestOptions =
                RequestOptions().placeholder(R.drawable.ic_launcher_background)
            Glide.with(requireActivity())
                .load(it.toUri())
                .apply(requestOptions)
                .into(binding.ivProfilePicture)
        }

        profileViewModel.showEditProfilePicFab.observe(viewLifecycleOwner){
            if (it){
                binding.fabEditProfilePicture.show()
            }else{
                binding.fabEditProfilePicture.hide()
            }
        }
        return binding.root
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layout.title = "Profile Setting"
        layout.isTitleEnabled = false
        Log.d("ProfileF","${profileViewModel.profile.value} ---------Model")

        binding.fabEditProfilePicture.setOnClickListener{
            try {
                if (Build.VERSION.SDK_INT >= 32) {
                    callActionWithPermission(
                        requireContext(),
                        requireActivity(),
                        REQUEST_PHOTO_PERMISSION,
                        GET_PROFILE_PICTURE_PR,
                        this::getProfileImageIntent
                    )
                } else {
                    callActionWithPermission(
                        requireContext(),
                        requireActivity(),
                        REQUEST_MANAGE_STORAGE,
                        GET_PROFILE_PICTURE_PR,
                        this::getProfileImageIntent
                    )
                }
            }catch (e : Exception){
                val toast = Toast.makeText(activity,"something wrong happened!",Toast.LENGTH_SHORT)
                toast.show()
            }
        }

    }

    override fun onStart() {
        super.onStart()
        Log.d("ProfileF","${profileViewModel.profile.value} ---------Model")

    }

    fun onSaveName(){
        profileViewModel.updateName()
        profileViewModel.setChangeName(false)

    }

    private fun onSaveDisplayName(){
        profileViewModel.updateDisplayName()
        profileViewModel.setChangeDisplayName(false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GET_PROFILE_PICTURE && Activity.RESULT_OK == resultCode){
            var imageuri : Uri? = Uri.EMPTY
            data?.let{
                imageuri = it.data
            }
            val toast = Toast.makeText(requireContext(), imageuri.toString(), Toast.LENGTH_SHORT)
            toast.show()
            profileViewModel.profileImageUri.value = imageuri.toString()
            profileViewModel.uploadProfilePicture(
                this::onSuccess,
                this::onFailure,
            )
        }
    }

    private fun getProfileImageIntent() {
        val toast = Toast.makeText(requireContext(), "getting profile picture ...",
            Toast.LENGTH_SHORT)
        toast.show()
        val imageIntent = Intent(Intent.ACTION_GET_CONTENT)
        imageIntent.type = "image/*"
        imageIntent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(imageIntent, GET_PROFILE_PICTURE )

    }

    fun onSuccess(){
        val toast = Toast.makeText(requireContext(),"successfully uploaded profile",Toast.LENGTH_SHORT)
        toast.show()
    }

    fun onFailure(){

    }


    companion object {
        val GET_PROFILE_PICTURE : Int = 989
        val GET_PROFILE_PICTURE_PR = 9899
        val REQUEST_PHOTO_PERMISSION = android.Manifest.permission.READ_MEDIA_IMAGES
        val REQUEST_MANAGE_STORAGE = android.Manifest.permission.MANAGE_EXTERNAL_STORAGE

    }
}