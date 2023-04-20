package com.example.bookshelf.ui.downloads

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.content.FileProvider
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.bookshelf.BuildConfig
import com.example.bookshelf.R
import com.example.bookshelf.bussiness.db.BookDatabase
import com.example.bookshelf.bussiness.repository.book.DownloadRepository
import com.example.bookshelf.databinding.FragmentDownloadsBinding
import java.io.File

class DownloadsFragment : Fragment() {
    private var _binding: FragmentDownloadsBinding? = null
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var toolbar: Toolbar
    private val binding get() = _binding!!

    private lateinit var db: BookDatabase
    private lateinit var downloadsViewModel: DownloadsViewModel
    private lateinit var downloadsRepository: DownloadRepository
    private lateinit var downloadsViewModelFactory: DownloadsViewModelFactory
    private lateinit var adapter: DownloadsAdapter

    @SuppressLint("IntentReset")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentDownloadsBinding.inflate(inflater, container, false)

        /**
         * Initialize lazy variables before they have been used!
         *
         */
        val _context = requireContext()
        val application = requireNotNull(activity).application
        db = BookDatabase.getDatabase(_context)
        downloadsRepository = DownloadRepository(db)
        downloadsViewModelFactory = DownloadsViewModelFactory(downloadsRepository)
        downloadsViewModel = ViewModelProvider(
            this,
            downloadsViewModelFactory
        )[DownloadsViewModel::class.java]
        adapter = DownloadsAdapter { bookUri ->


            val file = File(bookUri)
            val uri = FileProvider.getUriForFile(
                requireContext(),
                BuildConfig.APPLICATION_ID + ".provider",
                file
            )
            val intent = Intent(Intent.ACTION_VIEW).apply {
                type = "*/*"
                data = uri
            }
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            println("getting file ----------------------------------")
            println(uri)
            startActivity(intent)
        }
        /**
         * Setting up downloads adapter with providing data and
         * layout manager
         *
         */
        binding.recyclerViewDownloaded.adapter = adapter
        downloadsViewModel.downloads.observe(viewLifecycleOwner) {
            adapter.downloads = it
        }
//        downloadsViewModel.downloadsOnly.observe(viewLifecycleOwner){
//            println("Downloads only size ----------------------------------${it.size}")
//        }


        toolbar = binding.toolbarDownloaded
        toolbar.title = "Downloads"
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.title = "Downloads"
        val drawerLayout = view.findViewById<DrawerLayout>(R.id.drawer_layout)
        appBarConfiguration = AppBarConfiguration(setOf(), drawerLayout)
        NavigationUI.setupWithNavController(
            toolbar,
            navController = findNavController(),
            appBarConfiguration
        )

    }

    override fun onStart() {
        super.onStart()
        toolbar.title = "Downloads"
    }

}