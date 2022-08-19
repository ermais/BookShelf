package com.example.bookshelf.ui.main

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.bookshelf.ui.create.CreateBookActivity
import com.example.bookshelf.databinding.ActivityMainBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import androidx.appcompat.widget.SearchView.*
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.bookshelf.R
import com.example.bookshelf.ui.booklist.BookListFragment
import com.example.bookshelf.ui.login.LoginActivity
import com.example.bookshelf.ui.recent.RecentFragment
import com.example.bookshelf.ui.toprated.TopRatedFragment
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() , OnQueryTextListener,IMainInterface{

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    lateinit var viewModel: MainViewModel
    private lateinit var viewModelFactory: MainViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initSearchView()
        viewModelFactory = MainViewModelFactory(application)
        viewModel = ViewModelProvider(this,viewModelFactory).get(MainViewModel::class.java)
        setSupportActionBar(binding.appBarMain.toolbar)
        binding.appBarMain.fabCreateBook.setOnClickListener{
            val createBookIntent = Intent(this, CreateBookActivity::class.java)
            startActivity(createBookIntent)
        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_settings ->{

                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                Firebase.auth.signOut()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
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

    fun initSearchView(){
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        binding.appBarMain.actionSearch.setSearchableInfo(searchManager.getSearchableInfo(this.componentName))
        binding.appBarMain.actionSearch.maxWidth = Int.MAX_VALUE
        binding.appBarMain.actionSearch.setOnQueryTextListener(this)

    }

    override fun attachTabWithViewPager(viewPager: ViewPager2, listOfTabs: List<String>) {
        supportFragmentManager.fragments.forEach{
            if (it is BookListFragment || it is RecentFragment || it is TopRatedFragment){
                TabLayoutMediator(binding.appBarMain.homeTabs,viewPager) { tab, position ->
                    tab.text = listOfTabs[position]

                }.attach()
            }
        }

    }
}