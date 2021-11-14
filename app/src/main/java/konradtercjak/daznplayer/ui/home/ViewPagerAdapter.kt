package konradtercjak.daznplayer.ui.home

import android.os.Bundle
import androidx.annotation.NonNull
import androidx.fragment.app.*
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fm: FragmentManager,   @NonNull  lifecycle: Lifecycle) : FragmentStateAdapter(fm,lifecycle){

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                val fragment = DaznFragment()
                val bundle = Bundle()
                bundle.putBoolean(DaznFragment.IS_EVENTS, true)
                fragment.arguments=bundle
                fragment

            }

            else -> {
                val fragment = DaznFragment()
                val bundle = Bundle()
                bundle.putBoolean(DaznFragment.IS_EVENTS, false)
                fragment.arguments=bundle
                fragment
            }
        }
    }

}