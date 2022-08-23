package com.example.bookshelf.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.bookshelf.HomeTabAdapter
import com.example.bookshelf.databinding.FragmentHomeBinding
import com.example.bookshelf.ui.main.IMainInterface
import com.example.bookshelf.ui.main.MainActivity
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment : Fragment() {


    private lateinit var mInterface : IMainInterface
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
        mInterface = requireContext() as IMainInterface
        homeTabAdapter = HomeTabAdapter(requireActivity().supportFragmentManager,lifecycle)
        binding.homeViewPager.adapter = homeTabAdapter
        mInterface.attachTabWithViewPager(binding.homeViewPager,tabList)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

