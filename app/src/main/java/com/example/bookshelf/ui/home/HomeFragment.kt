package com.example.bookshelf.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.bookshelf.HomeTabAdapter
import com.example.bookshelf.R
import com.example.bookshelf.databinding.FragmentHomeBinding
import com.example.bookshelf.ui.booklist.BookListFragment
import com.example.bookshelf.ui.main.MainActivity
import com.example.bookshelf.ui.recent.RecentFragment
import com.example.bookshelf.ui.toprated.TopRatedFragment
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


@Suppress("DEPRECATION")
class HomeFragment : Fragment() {


    private lateinit var layout: CollapsingToolbarLayout
    private val sharedViewModel: HomeViewModel by activityViewModels()
    private var _binding: FragmentHomeBinding? = null
    private lateinit var homeTabAdapter: HomeTabAdapter
    private var tabList = arrayListOf("Books", "Top Rated", "Recent")
    private var drawerLayout: DrawerLayout? = null
    private lateinit var mainActivity: MainActivity
    private var navView: NavigationView? = null
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        mainActivity = requireContext() as MainActivity
        toolbar = binding.appBarBookList.toolbarBookList
        navHostFragment =
            mainActivity.supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        navController = navHostFragment.findNavController()
        navView = mainActivity.findViewById(R.id.nav_view)
        layout = binding.appBarBookList.toolbarBookListLayout
        drawerLayout = mainActivity.findViewById(R.id.drawer_layout)
        appBarConfiguration = AppBarConfiguration(
            findNavController().graph,
            drawerLayout
        )


        val homeActivity = activity as MainActivity
        val drawerHome = homeActivity.findViewById<DrawerLayout>(R.id.drawer_layout)
        if (drawerHome != null) {
            Log.d("HOME_FRAGMENT", "ONVIEWCREATE$drawerHome")
        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        drawerLayout = mainActivity.findViewById(R.id.drawer_layout)
        toolbar.setNavigationIcon(R.drawable.ic_baseline_menu_24)
        toolbar.title = getString(R.string.book_list_app_bar_title)
        mainActivity.setSupportActionBar(toolbar)
        mainActivity.actionBar?.setDisplayHomeAsUpEnabled(true)
        layout.isTitleEnabled = false
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
        drawerLayout?.addDrawerListener(toggle)
        if (drawerLayout != null) {
            toggle.syncState()
        }



        sharedViewModel.createBookFabBtnVisible.observe(viewLifecycleOwner) {
            if (it == true && !binding.fabCreateBook.isShown) {
                binding.fabCreateBook.show()
            }
            if (it == false && binding.fabCreateBook.isShown) {
                binding.fabCreateBook.hide()
            }
        }


        homeTabAdapter = HomeTabAdapter(requireActivity().supportFragmentManager, lifecycle)
        binding.homeViewPager.adapter = homeTabAdapter
        fragmentManager?.fragments?.forEach {
            if (it is BookListFragment || it is RecentFragment || it is TopRatedFragment) {
                TabLayoutMediator(
                    binding.appBarBookList.homeTabs,
                    binding.homeViewPager
                ) { tab, position ->
                    tab.text = tabList[position].toString()
                }.attach()
            }
        }
        val homeTabs = binding.appBarBookList.homeTabs
        homeTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.position?.let { binding.homeViewPager.currentItem = it }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.position?.let {
                    binding.homeViewPager.isSelected = false
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                tab?.position?.let {
                    binding.homeViewPager.currentItem = it
                }
            }
        })

        binding.fabCreateBook.setOnClickListener {
            findNavController().navigate(R.id.nav_create_book)
        }


        val sortByAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.sort_by,
            android.R.layout.simple_spinner_item
        )
        sortByAdapter.also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.appBarBookList.sortBy.adapter = it
        }

        val filterByAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.filter_by,
            android.R.layout.simple_spinner_item
        )

        filterByAdapter.also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.appBarBookList.filterBy.adapter = it
        }

        binding.appBarBookList.sortBy.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    val itemSelected = p0?.getItemAtPosition(p2)
                    sharedViewModel.sortBy.value = itemSelected.toString()
                    val toast = Toast.makeText(
                        requireActivity(),
                        itemSelected.toString(),
                        Toast.LENGTH_LONG
                    )
                    toast.show()
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    val itemSelected = p0?.getItemAtPosition(0)
                    sharedViewModel.sortBy.value = itemSelected.toString()
                }


            }


        binding.appBarBookList.filterBy.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    val itemSelected = p0?.getItemAtPosition(p2)
                    sharedViewModel.filterByCategory.value = itemSelected.toString()
                    val toast = Toast.makeText(
                        requireContext(),
                        itemSelected.toString(),
                        Toast.LENGTH_LONG
                    )
                    toast.show()
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    val itemSelected = p0?.getItemAtPosition(0)
                    sharedViewModel.filterByCategory.value = itemSelected.toString()
                }
            }

        val homeActivity = activity as MainActivity
        val drawerHome = homeActivity.findViewById<DrawerLayout>(R.id.drawer_layout)
        if (drawerHome != null) {
            Log.d("HOME_FRAGMENT", "ONVIEWCTEATED$drawerHome")
        }

    }

    override fun onStart() {
        super.onStart()
        super.onResume()
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
        navView?.setupWithNavController(navController)
        toolbar.title = getString(R.string.book_list_app_bar_title)
    }


    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        binding.appBarBookList.toolbarBookList.title = getString(R.string.book_list_app_bar_title)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}

