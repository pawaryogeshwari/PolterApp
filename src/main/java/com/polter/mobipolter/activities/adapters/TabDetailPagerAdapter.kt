package com.polter.mobipolter.activities.adapters

import android.content.Context;
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.polter.mobipolter.activities.fragments.BasicFragment
import com.polter.mobipolter.activities.fragments.LocationFragment
import com.polter.mobipolter.activities.fragments.MeasurementFragment
import com.polter.mobipolter.activities.fragments.PhotosFragment

class TabDetailPagerAdapter(private val myContext: Context, fm: FragmentManager, internal var totalTabs: Int, list:BasicFragment.saveListener) : FragmentStatePagerAdapter(fm) {

    var basicFragment : BasicFragment ?= null
    lateinit var liste : BasicFragment.saveListener
    init {
        liste = list
    }
    // this is for fragment tabs
    override fun getItem(position: Int): Fragment? {
        when (position) {
            0 -> {
                 basicFragment = BasicFragment(liste)
                 return basicFragment
            }
            1 -> {
                return MeasurementFragment()
            }
            2 -> {
                return PhotosFragment()
            }
            3 -> { return LocationFragment()
            }
            else -> return BasicFragment(liste)
        }
    }

    // this counts total number of tabs
    override fun getCount(): Int {
        return totalTabs
    }

    fun onBackPresssed(){
        basicFragment?.onChange()
    }

}