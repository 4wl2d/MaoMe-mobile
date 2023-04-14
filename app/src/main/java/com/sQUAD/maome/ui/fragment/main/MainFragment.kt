package com.sQUAD.maome.ui.fragment.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.sQUAD.maome.adapter.FragmentPagerAdapter
import com.sQUAD.maome.databinding.MainFragmentBinding
import com.sQUAD.maome.ui.fragment.extensions.isLocationPermissionGranted

class MainFragment : Fragment() {

    private lateinit var locLauncher: ActivityResultLauncher<String>
    private lateinit var binding: MainFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkLocationPermission()

        // Create a list of Fragments to display in the ViewPager2
        val fragmentList = listOf(MainMapFragment(), MainNotesFragment(), MainProfileFragment())

        binding.apply {
            // Set up the ViewPager2 Adapter
            val pagerAdapter = FragmentPagerAdapter(childFragmentManager, lifecycle, fragmentList)
            viewPager.adapter = pagerAdapter

            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    // Disable swiping in the first fragment
                    viewPager.isUserInputEnabled = position != 0
                }
            })

            // Link the TabLayout to the ViewPager2
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                when (position) {
                    0 -> tab.text = "Map"
                    1 -> tab.text = "Notes"
                    2 -> tab.text = "Profile"
                }
            }.attach()
        }
    }

    private fun locationPermissionListener() {
        locLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()){// granting permission and if not granted we can do something

        }
    }

    private fun checkLocationPermission() {
        if (!isLocationPermissionGranted(android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            locationPermissionListener()
            locLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION) // if we doesnt have location access than we requesting it another time
        }
    }

}