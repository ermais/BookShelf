package com.example.bookshelf

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.bookshelf.ui.booklist.BookListFragment
import com.example.bookshelf.ui.recent.RecentFragment
import com.example.bookshelf.ui.toprated.TopRatedFragment

class HomeTabAdapter(fm: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fm, lifecycle) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> BookListFragment()
            1 -> TopRatedFragment()
            2 -> RecentFragment()
            else -> {
                return BookListFragment()
            }
        }
    }
}