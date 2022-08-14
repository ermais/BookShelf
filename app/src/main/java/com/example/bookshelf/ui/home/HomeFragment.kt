package com.example.bookshelf.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.bookshelf.HomeTabAdapter
import com.example.bookshelf.databinding.FragmentHomeBinding
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment : Fragment() {


    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null
    private lateinit var homeTabAdapter: HomeTabAdapter
    private lateinit var fragment: Fragment
    private var tabList = arrayListOf<String>("Books","Top Rated","Recent")

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(
                this,
                ViewModelProvider.NewInstanceFactory()
            ).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)



//        val textView: TextView = binding.textHome
//        homeViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeTabAdapter = HomeTabAdapter(requireActivity().supportFragmentManager,lifecycle)
        binding.homeViewPager.adapter = homeTabAdapter
        TabLayoutMediator(binding.homeTabLayout,binding.homeViewPager) { tab, position ->
            tab.text = tabList[position]

        }.attach()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

