package com.example.bookshelf.ui.edit

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.bumptech.glide.Glide
import com.example.bookshelf.R
import com.example.bookshelf.bussiness.db.BookDao
import com.example.bookshelf.bussiness.db.BookDatabase
import com.example.bookshelf.bussiness.networkdata.FirestoreBookDataSource
import com.example.bookshelf.bussiness.networkdata.FirestoreMyBooksDataSource
import com.example.bookshelf.bussiness.repository.book.BookDetailRepository
import com.example.bookshelf.bussiness.repository.book.BookEditRepository
import com.example.bookshelf.bussiness.repository.book.MyBooksRepository
import com.example.bookshelf.databinding.FragmentBookDetailBinding
import com.example.bookshelf.databinding.FragmentEditBookBinding
import com.example.bookshelf.ui.detail.BookDetailFragmentArgs
import com.example.bookshelf.ui.detail.BookDetailViewModel
import com.example.bookshelf.ui.detail.BookDetailViewModelFactory
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class EditBookFragment : Fragment() {
    private var _binding: FragmentEditBookBinding? = null

    private lateinit var  db: BookDatabase
    private val binding get() = _binding!!
    private lateinit var bookEditViewModel: EditBookViewModel
    private lateinit var bookEditViewModelFactory: EditBookViewModelFactory
    private lateinit var bookDao: BookDao
    private lateinit var firestore: FirebaseFirestore
    private lateinit var cloudStorage : FirebaseStorage
    private lateinit var firestoreBookDataSource: FirestoreBookDataSource
    private lateinit var bookEditRepository : BookEditRepository
    val args: EditBookFragmentArgs by navArgs()
    private var toolbar: Toolbar? = null
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditBookBinding.inflate(inflater,container,false)

        db = BookDatabase.getDatabase(requireContext())
        bookDao = db.bookDao()
        firestore = FirebaseFirestore.getInstance()
        cloudStorage = FirebaseStorage.getInstance()
        firestoreBookDataSource = FirestoreBookDataSource(firestore,cloudStorage)
        bookEditRepository = BookEditRepository(bookDao,firestoreBookDataSource)
        val application = requireNotNull(activity).application
        bookEditViewModelFactory =
            EditBookViewModelFactory(bookEditRepository,application,args.bookId)
        bookEditViewModel = ViewModelProvider(
            this,
            bookEditViewModelFactory
        )[EditBookViewModel::class.java]
        toolbar = binding.tbEditBook
        toolbar!!.title = null

        binding.lifecycleOwner = this.viewLifecycleOwner
        binding.viewModel = bookEditViewModel

        bookEditViewModel.book.observe(viewLifecycleOwner){
            bookEditViewModel.title.value = it.title
            bookEditViewModel.category.value = it.category
            bookEditViewModel.desc.value = it.desc
            bookEditViewModel.bookCover.value = it.bookCover
            bookEditViewModel.bookDoc.value = it.bookUri
            Glide.with(requireContext())
                .load(it.bookCover)
                .into(binding.ivEditBookCover)
        }




        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val drawer = view.findViewById<DrawerLayout>(R.id.drawer_layout)
        val appBarConfiguration = AppBarConfiguration(setOf(),drawer)
        NavigationUI.setupWithNavController(
            toolbar as Toolbar,
            findNavController(),
            appBarConfiguration
        )
        toolbar!!.title = null
        toolbar!!.navigationIcon = resources.getDrawable(R.drawable.arrow_back)
        binding.btnEditBook.setOnClickListener {
            onEditBtnClick()
        }

        binding.btnSaveChange.setOnClickListener {
            onSaveEditedBtnClick()
        }

        binding.btnDiscardChange.setOnClickListener {
            onDiscardEditedBtnClick()
        }
    }

    fun onEditBtnClick(){
        binding.btnEditBook.visibility = View.GONE
        binding.btnSaveChange.visibility = View.VISIBLE
        binding.btnDiscardChange.visibility = View.VISIBLE
        with(binding){
            tevBookTitle.isEnabled = true
            tevBookCategory.isEnabled = true
            tevBookDesc.isEnabled = true
        }
    }


    fun onSaveEditedBtnClick(){
        binding.btnEditBook.visibility = View.VISIBLE
        binding.btnSaveChange.visibility = View.GONE
        binding.btnDiscardChange.visibility = View.GONE
        with(binding){
            tevBookTitle.isEnabled = false
            tevBookCategory.isEnabled = false
            tevBookDesc.isEnabled = false
        }
        saveEdited()
    }

    fun onDiscardEditedBtnClick(){
        binding.btnEditBook.visibility = View.VISIBLE
        binding.btnSaveChange.visibility = View.GONE
        binding.btnDiscardChange.visibility = View.GONE
        with(binding){
            tevBookTitle.isEnabled = false
            tevBookCategory.isEnabled = false
            tevBookDesc.isEnabled = false
        }
    }


    fun saveEdited(){
        try {
            bookEditViewModel.editBook(args.bookId)
        }catch (e : Exception){

        }
    }

}