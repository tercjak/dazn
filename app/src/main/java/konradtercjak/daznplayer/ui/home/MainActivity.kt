package konradtercjak.daznplayer.ui.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.google.android.material.tabs.TabLayoutMediator
import konradtercjak.daznplayer.R
import konradtercjak.daznplayer.databinding.MainActivityBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

       val  binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
        binding.pager.adapter = viewPagerAdapter
        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
            val isEvents = position == 0
            val text = if (isEvents) R.string.events else R.string.schedule
            val icon = if (isEvents) R.drawable.baseline_tv else R.drawable.ic_history

            tab.text = getString(text)
            tab.icon = AppCompatResources.getDrawable(this, icon)
        }.attach()
    }


}
