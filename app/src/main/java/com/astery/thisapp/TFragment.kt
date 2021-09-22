package com.astery.thisapp

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.transition.MaterialSharedAxis

abstract class TFragment : Fragment() {

    fun setTransition(first: Boolean){
                if (first) {
                    exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
                    reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
                } else {
                    enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z,  false)
                    returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
                }
            }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).showLogout(this.javaClass == MainFragment::class.java);
    }
}