package com.example.bookshelf.util

import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.os.LocaleList
import androidx.preference.PreferenceManager
import java.util.*

class LocaleHelper(base: Context?) : ContextWrapper(base) {


    companion object {
        fun wrap(context: Context): LocaleHelper {
            var _context = context
            val res = context.resources
            val config = res.configuration
            val prefManger = PreferenceManager.getDefaultSharedPreferences(context)
            val lan = prefManger.getString("language", "en-us")
            val locale = Locale(lan.toString())
            if (Build.VERSION.SDK_INT >= 24) {
                config.setLocale(locale)
                val localeList = LocaleList(locale)
                LocaleList.setDefault(localeList)
                config.setLocale(locale)
                _context = context.createConfigurationContext(config)

            } else if (Build.VERSION.SDK_INT >= 17) {
                config.setLocale(locale)
                _context = context.createConfigurationContext(config)
            } else {
                config.locale = locale
                res.updateConfiguration(
                    config,
                    res.displayMetrics
                )
            }
            return LocaleHelper(_context)

        }
    }
}