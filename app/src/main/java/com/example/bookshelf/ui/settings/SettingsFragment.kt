package com.example.bookshelf.ui.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.example.bookshelf.R
import java.util.*

@Suppress("DEPRECATION")
class SettingsFragment : PreferenceFragmentCompat() {

    private lateinit var locale: Locale
    private lateinit var prefManager: SharedPreferences
    private lateinit var prefListener: SharedPreferences.OnSharedPreferenceChangeListener

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        prefManager = PreferenceManager.getDefaultSharedPreferences(requireContext())
        prefListener = SharedPreferences.OnSharedPreferenceChangeListener { p0, _ ->
            val lan = p0?.getString("language", "en-us")
            val theme = p0?.getString("theme_color", "bookshelf")
            val fontSize: Float? = p0?.getInt("text_font_size", 1)?.div(12)?.toFloat()
            val isNight: Boolean? = p0?.getBoolean("night_mode", false)

            lan?.let {
                setupLan(it)
            }
            theme?.let {
                setCustomTheme(it)
            }
            fontSize?.let {
                setCustomFontSize(it)
            }
            isNight?.let {
                _setNightMode(it)
            }
        }

        prefManager.registerOnSharedPreferenceChangeListener(prefListener)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val defaultView = super.onCreateView(inflater, container, savedInstanceState)
        val _view = inflater.inflate(R.layout.pre_template, container, false) as ViewGroup
        val _container = _view.findViewById<FrameLayout>(R.id.setting_container)
        _container.addView(defaultView)

        return _view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar = view.findViewById<Toolbar>(R.id.setting_toolbar)
        toolbar.apply {
            title = "Settings"
        }
        val drawerLayout = view.findViewById<DrawerLayout>(R.id.drawer_layout)
        val appBarConfiguration = AppBarConfiguration(setOf(), drawerLayout)
        NavigationUI.setupWithNavController(
            toolbar as Toolbar,
            navController = findNavController(),
            appBarConfiguration
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefManager.registerOnSharedPreferenceChangeListener(prefListener)
    }

    override fun onPause() {
        super.onPause()
        prefManager.unregisterOnSharedPreferenceChangeListener(prefListener)
    }

    override fun onResume() {
        super.onResume()
        prefManager.registerOnSharedPreferenceChangeListener(prefListener)
    }

    fun setupLan(lan: String?) {
        if (lan.equals("not-set")) locale = Locale.getDefault()
        else
            locale = Locale(lan.toString())
        Locale.setDefault(locale)
        val context = requireNotNull(activity).baseContext
        val config = context.resources.configuration
        config?.locale = locale
        context.resources.updateConfiguration(
            config,
            context.resources.displayMetrics
        )

    }

    private fun setCustomTheme(theme: String) {
        when (theme) {
            "bookshelf" -> {
                requireActivity().baseContext.setTheme(R.style.Theme_BookShelf)
            }
            "bookshelf_green_dark" -> {
                requireActivity().baseContext.setTheme(R.style.Theme_BookShelf_GreenDark)
            }
            "bookshelf_light_dark" -> {
                requireActivity().baseContext.setTheme(R.style.Theme_BookShelf_LightDark)
            }
            "bookshelf_orange_dark" -> {
                requireActivity().baseContext.setTheme(R.style.Theme_BookShelf_OrangeDark)
            }
            "bookshelf_white" -> {
                requireActivity().baseContext.setTheme(R.style.Theme_BookShelf_White)
            }
            else -> {
                requireActivity().baseContext.setTheme(R.style.Theme_BookShelf)
            }
        }
    }

    private fun setCustomFontSize(fontSize: Float) {
        val _context = requireActivity().baseContext
        val config = _context.resources.configuration
        config.fontScale = fontSize
        val metrics = resources.displayMetrics
        val wm: WindowManager =
            requireActivity().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.defaultDisplay.getMetrics(metrics)
        metrics.scaledDensity = config.fontScale * metrics.density
        _context.createConfigurationContext(config)
        _context.resources.displayMetrics.setTo(metrics)
    }

    private fun _setNightMode(is_night: Boolean) {
        if (is_night) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }


}