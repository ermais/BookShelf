package com.example.bookshelf.ui.main

import android.app.SearchManager
import android.app.UiModeManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.NightMode
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.bookshelf.R
import com.example.bookshelf.databinding.ActivityMainBinding
import com.example.bookshelf.ui.login.LoginActivity
import com.example.bookshelf.util.getConnMgr
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity(), OnQueryTextListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var layout: View
    private var _binding: ActivityMainBinding? = null
    val binding get() = _binding!!
    lateinit var viewModel: MainViewModel
    private lateinit var viewModelFactory: MainViewModelFactory
    private lateinit var connMgr  : ConnectivityManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
//        setNightMode(applicationContext,true)
        setContentView(binding.root)
//        setNightMode(applicationContext,true)
//        initSearchView()
        layout = binding.root
        viewModelFactory = MainViewModelFactory(application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

        if (actionBar != null) {

            val drawerLayout = binding.drawerLayout
            val navView = binding.navView
            val navController = findNavController(R.id.nav_host_fragment_content_main)
            appBarConfiguration = AppBarConfiguration(
                navController.graph,
                drawerLayout
            )
            setupActionBarWithNavController(navController, appBarConfiguration)
            navView.setupWithNavController(navController)

        }
        connMgr = getConnMgr(applicationContext,::getSystemService)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                Firebase.auth.signOut()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()

            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu.findItem(R.id.action_search).actionView as SearchView).apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            maxWidth = Int.MAX_VALUE
            setIconifiedByDefault(false)
            setOnQueryTextListener(this@MainActivity)
        }
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        viewModel.setQuery(query)
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        viewModel.setQuery(newText)
        return false
    }

//    fun initSearchView(){
//        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
//        binding.appBarMain.actionSearch.setSearchableInfo(searchManager.getSearchableInfo(this.componentName))
//        binding.appBarMain.actionSearch.maxWidth = Int.MAX_VALUE
//        binding.appBarMain.actionSearch.setOnQueryTextListener(this)
//
//    }

//        override fun attachTabWithViewPager(viewPager: ViewPager2, listOfTabs: List<String>) {
//        supportFragmentManager.fragments.forEach{
//            if (it is BookListFragment || it is RecentFragment || it is TopRatedFragment){
//                TabLayoutMediator(binding.appBarMain.homeTabs,viewPager) { tab, position ->
//                    tab.text = listOfTabs[position]
//
//                }.attach()
//            }
//        }

//        }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    @Deprecated("Deprecated in Java")
    override fun onAttachFragment(fragment: android.app.Fragment?) {
        super.onAttachFragment(fragment)
        println("Get attaching fragment ------------------------------------")
        println(fragment)
        if (actionBar != null) {
            val drawerLayout = binding.drawerLayout
            val navView = binding.navView
            val navController = findNavController(R.id.nav_host_fragment_content_main)
            appBarConfiguration = AppBarConfiguration(
                navController.graph,
                drawerLayout
            )
            setupActionBarWithNavController(navController, appBarConfiguration)
            navView.setupWithNavController(navController)
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        if (actionBar != null) {
            println("onResume fragment -------------------------------------------------")
            val drawerLayout = binding.drawerLayout
            val navView = binding.navView
            val navController = findNavController(R.id.nav_host_fragment_content_main)
            appBarConfiguration = AppBarConfiguration(
                navController.graph,
                drawerLayout
            )
            setupActionBarWithNavController(navController, appBarConfiguration)
            navView.setupWithNavController(navController)
        }
    }

    override fun onResume() {
        super.onResume()
        if (actionBar != null) {
            println("onResume-------------------------------------------------")
            val drawerLayout = binding.drawerLayout
            val navView = binding.navView
            val navController = findNavController(R.id.nav_host_fragment_content_main)
            appBarConfiguration = AppBarConfiguration(
                navController.graph,
                drawerLayout
            )
            setupActionBarWithNavController(navController, appBarConfiguration)
            navView.setupWithNavController(navController)
        }
    }

    override fun onStart() {
        super.onStart()
//        try {
//            if (actionBar != null) {
//                println("onStart -------------------------------------------------")
//                val drawerLayout = binding.drawerLayout
//                val navView = binding.navView
//                val navController = findNavController(R.id.nav_host_fragment_content_main)
//                appBarConfiguration = AppBarConfiguration(
//                    navController.graph,
//                    drawerLayout
//                )
//                setupActionBarWithNavController(navController, appBarConfiguration)
//                navView.setupWithNavController(navController)
//            }
//        }catch (e : Exception){
//            Log.d("MAIN_ACTIVITY",e?.message.toString() ?: e.toString())
//        }
    }

    companion object {

        fun setNightMode(context: Context,isNight  : Boolean){
            if (isNight){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }



    }

}