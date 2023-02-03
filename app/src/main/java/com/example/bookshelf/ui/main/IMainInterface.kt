package com.example.bookshelf.ui.main

import androidx.viewpager2.widget.ViewPager2

interface IMainInterface {

    fun attachTabWithViewPager(viewPager: ViewPager2, listOfTabs: List<String>)
}