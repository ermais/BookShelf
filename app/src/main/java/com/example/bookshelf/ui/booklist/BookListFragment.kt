package com.example.bookshelf.ui.booklist

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.bookshelf.R
import com.example.bookshelf.ui.main.MainViewModel
import com.example.bookshelf.databinding.FragmentBookListBinding
import com.example.bookshelf.bussiness.model.Book
import com.example.bookshelf.bussiness.FirestoreBookDataSource
import com.example.bookshelf.bussiness.db.BookDatabase
import com.example.bookshelf.bussiness.db.asDomainModel
import com.example.bookshelf.bussiness.repository.book.BookListRepository
import com.example.bookshelf.isPermissionGranted
import com.example.bookshelf.requestPermission
import com.example.bookshelf.ui.Utils.showSnackBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class BookListFragment : Fragment() {

    private lateinit var layout:View
    private lateinit var db: FirebaseFirestore
    private lateinit var firestoreBookDataSource: FirestoreBookDataSource
    private lateinit var bookListRepository: BookListRepository
    private lateinit var bookListModel: BookListViewModel
    private var _binding: FragmentBookListBinding? = null
    private lateinit var cloudStorage: FirebaseStorage
    private lateinit var mainViewModel : MainViewModel
    private lateinit var bookShelDb : BookDatabase
    private lateinit var requestPermissionLauncher : ActivityResultLauncher<String>


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBookListBinding.inflate(inflater, container, false)
        layout = binding.rvBookList
        db = Firebase.firestore
        cloudStorage = FirebaseStorage.getInstance()
        firestoreBookDataSource = FirestoreBookDataSource(db, cloudStorage)
        bookShelDb = BookDatabase.getDatabase(requireContext())
        bookListRepository = BookListRepository(firestoreBookDataSource,bookShelDb)
        val bookListViewModelFactory = BookListViewModelFactory(bookListRepository, requireNotNull(activity).application)
        bookListModel = ViewModelProvider(this,bookListViewModelFactory).get(BookListViewModel::class.java)
        val adapter = BookListAdapter(requireContext()){view, downloadUri , bookTitle ->
            when{
                ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.WRITE_EXTERNAL_STORAGE)==
                        PackageManager.PERMISSION_GRANTED-> {
                    val toast = Toast.makeText(requireContext(),"Downloading ...",Toast.LENGTH_LONG)
                    toast.show()
                    println("Granted --------------")
                    layout.showSnackBar(view,
                        getString(R.string.write_external_storage_permission),
                        Snackbar.LENGTH_SHORT,
                        null){
                        Log.d("BOOKLIST","I am from inside the snakckbar")
                       onDownloadBook(view,downloadUri,bookTitle)
                    }
                    onDownloadBook(view,downloadUri,bookTitle)
                }
                ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)->{
                    layout.showSnackBar(
                        view,
                        getString(R.string.write_external_storage_permission),
                        Snackbar.LENGTH_INDEFINITE,
                        getString(R.string.ok),
                    ){
                        requestPermissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    }
                }
                else->{
                    requestPermissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }

        }
        activity?.let {
            mainViewModel = ViewModelProvider(it).get(MainViewModel::class.java)
        }
//        mainViewModel.query.observe(viewLifecycleOwner){query->
//            bookListModel.filter(query)
//        }
        bookListModel.filteredBooks.observe(viewLifecycleOwner) {
            adapter.data = it.asDomainModel()
        }

        mainViewModel.filterByCategory.observe(viewLifecycleOwner){
            if (it != "All"){
                bookListModel.filterByCategory(it)
            }else{
                bookListModel.getBooks()
            }


        }

        requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()){ isGranted:Boolean->
                if (isGranted){
                    val toast = Toast.makeText(activity,"Permission Granted",Toast.LENGTH_LONG)
                    toast.show()
                }else {
                    val toast = Toast.makeText(activity,"Permission Denied",Toast.LENGTH_LONG)
                    toast.show()
                }
            }

        mainViewModel.query.observe(viewLifecycleOwner){
            bookListModel.filterBooks(it)
        }

        mainViewModel.sortBy.observe(viewLifecycleOwner){
            when(it){
                "date"->bookListModel.sortByPubDate()
                "author"->bookListModel.sortByAuthor()
                "title"->bookListModel.sortByTitle()
                else->bookListModel.sortByPubDate()
            }
        }


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

    companion object {
        const val WRITE_PERMISSION = android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        const val WRITE_PERMISSION_CODE = 211
    }




    fun onDownloadBook(view:View,uri:Uri,title:String){
        bookListModel.downloadBook(uri,title)
    }

}