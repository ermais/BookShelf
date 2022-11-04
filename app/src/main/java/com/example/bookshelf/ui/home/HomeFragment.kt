package com.example.bookshelf.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.bookshelf.HomeTabAdapter
import com.example.bookshelf.R
import com.example.bookshelf.databinding.FragmentHomeBinding
import com.example.bookshelf.ui.booklist.BookListFragment
import com.example.bookshelf.ui.main.IMainInterface
import com.example.bookshelf.ui.main.MainActivity
import com.example.bookshelf.ui.recent.RecentFragment
import com.example.bookshelf.ui.toprated.TopRatedFragment
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
//        mInterface = requireContext() as IMainInterface
        homeTabAdapter = HomeTabAdapter(requireActivity().supportFragmentManager,lifecycle)
        binding.homeViewPager.adapter = homeTabAdapter
//        mInterface.attachTabWithViewPager(binding.homeViewPager,tabList)
                fragmentManager?.fragments?.forEach{
                    if (it is BookListFragment || it is RecentFragment || it is TopRatedFragment){
                        TabLayoutMediator(binding.homeTabs,binding.homeViewPager) { tab, position ->
                            tab.text = tabList[position]

                        }.attach()
                }


            }
//        TabLayoutMediator(binding.homeTabs,binding.homeViewPager){tab,position->
//            tab.text = tabList[position]
//        }.attach()

        val sortByAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.sort_by,
            android.R.layout.simple_spinner_item
        )
        sortByAdapter.also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.sortBy.adapter = it
        }

        val filterByAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.filter_by,
            android.R.layout.simple_spinner_item
        )

        filterByAdapter.also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.filterBy.adapter = it
        }

        binding.sortBy.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val itemSelected = p0?.getItemAtPosition(p2)
                homeViewModel.sortBy.value = itemSelected.toString()
                val toast = Toast.makeText(requireActivity(),itemSelected.toString(),Toast.LENGTH_LONG)
                toast.show()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                val itemSelected  = p0?.getItemAtPosition(0)
                homeViewModel.sortBy.value = itemSelected.toString()
            }


        }


        binding.filterBy.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val itemSelected = p0?.getItemAtPosition(p2)
                homeViewModel.filterByCategory.value = itemSelected.toString()
                val toast = Toast.makeText(requireContext(),itemSelected.toString(),Toast.LENGTH_LONG)
                toast.show()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                val itemSelected  = p0?.getItemAtPosition(0)
            }
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

