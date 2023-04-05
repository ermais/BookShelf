package com.example.bookshelf.ui.main

import android.app.SearchManager
import android.app.UiModeManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
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
import androidx.preference.PreferenceManager
import com.example.bookshelf.R
import com.example.bookshelf.databinding.ActivityMainBinding
import com.example.bookshelf.ui.login.LoginActivity
import com.example.bookshelf.util.LocaleHelper
import com.example.bookshelf.util.getConnMgr
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.Locale
import java.util.Objects

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(), OnQueryTextListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var layout: View
    private var _binding: ActivityMainBinding? = null
    val binding get() = _binding!!
    lateinit var viewModel: MainViewModel
    private lateinit var viewModelFactory: MainViewModelFactory
    private lateinit var connMgr: ConnectivityManager
    private lateinit var locale: Locale
    private lateinit var prefListener: SharedPreferences.OnSharedPreferenceChangeListener
    private lateinit var prefManager : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        prefManager = PreferenceManager.getDefaultSharedPreferences(this)
        val theme = prefManager.getString("theme_color","bookshelf")
        setCustomTheme(theme.toString())
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
//        setNightMode(applicationContext,true)
        setContentView(binding.root)

        /**
         * set up language configuration here to localization and internalization
         */

        val lang = prefManager.getString("language","en-us")
        setupLan(lang)
        prefListener = SharedPreferences.OnSharedPreferenceChangeListener { p0, p1 ->
            val lan = p0?.getString("language", "en-us")
            val _theme = p0?.getString("theme_color","bookshelf")
            setupLan(lan)
            Log.d("THEME",theme.toString())
            setCustomTheme(_theme.toString())
            val fontSize : Float? = (p0?.getInt("text_font_size",1)?.div(12))?.toFloat()
            fontSize?.let {
                setCustomFontSize(it)
            }
            recreate()
        }
        prefManager.registerOnSharedPreferenceChangeListener(prefListener)

//        setNightMode(applicationContext,true)
//        initSearchView()
        layout = binding.root
        viewModelFactory = MainViewModelFactory(application)
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]

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
        connMgr = getConnMgr(applicationContext, ::getSystemService)
    }

    private fun setCustomTheme(theme: String) {
            when(theme){
                "bookshelf" -> {
                    setTheme(R.style.Theme_BookShelf)
                }
                "bookshelf_green_dark" -> {
                    setTheme(R.style.Theme_BookShelf_GreenDark)
                }
                "bookshelf_light_dark" -> {
                    setTheme(R.style.Theme_BookShelf_LightDark)
                }
                "bookshelf_orange_dark" -> {
                    setTheme(R.style.Theme_BookShelf_OrangeDark)
                }
                "bookshelf_white" ->{
                    setTheme(R.style.Theme_BookShelf_White)
                }
                else ->{
                    setTheme(R.style.Theme_BookShelf)
                }
            }
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
        prefManager.registerOnSharedPreferenceChangeListener(prefListener)
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

    override fun onPause() {
        super.onPause()
        prefManager.unregisterOnSharedPreferenceChangeListener(prefListener)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onRestoreInstanceState(
        savedInstanceState: Bundle?,
        persistentState: PersistableBundle?
    ) {
        prefManager.registerOnSharedPreferenceChangeListener(prefListener)
        super.onRestoreInstanceState(savedInstanceState, persistentState)
    }

    override fun attachBaseContext(newBase: Context?) {
        val context = LocaleHelper.wrap(newBase as Context)
        super.attachBaseContext(context)
    }

    override fun applyOverrideConfiguration(overrideConfiguration: Configuration?) {

        if (overrideConfiguration != null){
            val uiMode = overrideConfiguration.uiMode
            overrideConfiguration.setTo(baseContext.resources.configuration)
            overrideConfiguration.uiMode = uiMode
        }
        super.applyOverrideConfiguration(overrideConfiguration)
    }

    fun setupLan(lan:String?){
        if (lan.equals("not-set")) locale = Locale.getDefault()
        else
            locale = Locale(lan.toString())
        Locale.setDefault(locale)
        val _context = baseContext
        val config = _context.resources.configuration
        config.locale = locale
        _context.resources.updateConfiguration(config, _context.resources.displayMetrics)

    }

    private fun setCustomFontSize(fontSize : Float){
        val _context = baseContext
        val config = _context.resources.configuration
        config.fontScale = fontSize
        val metrics = resources.displayMetrics
        val wm : WindowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.defaultDisplay.getMetrics(metrics)
        metrics.scaledDensity  = config.fontScale * metrics.density
        _context.createConfigurationContext(config)
        _context.resources.displayMetrics.setTo(metrics)
    }
    companion object {

        fun setNightMode(context: Context, isNight: Boolean) {
            if (isNight) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }


    }

}