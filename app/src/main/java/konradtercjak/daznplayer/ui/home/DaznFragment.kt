package konradtercjak.daznplayer.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import konradtercjak.daznplayer.R
import konradtercjak.daznplayer.databinding.ListFragmentBinding
import konradtercjak.daznplayer.model.*
import konradtercjak.daznplayer.ui.PlayerActivity
import konradtercjak.daznplayer.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DaznFragment : Fragment(R.layout.list_fragment) {
    companion object {
        const val IS_EVENTS = "IS_EVENTS"
    }

    private val binding: ListFragmentBinding by viewBinding(ListFragmentBinding::bind)

    @Inject
    lateinit var viewInteractor: ViewInteractor

    @Inject
    lateinit var networkUsecase: NetworkUsecase

    private var adapter: DaznAdapter by autoCleaned { DaznAdapter(viewInteractor) }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = binding
        val isEvents = arguments?.getBoolean(IS_EVENTS, true) as Boolean
        initRecyclerView()

        if (isInternetConnection(requireContext())) {
            lifecycleScope.launch(Dispatchers.Main) {
                networkUsecase.onConnected(isEvents)
            }
        } else {
            var snackbar = showErrorSnackbar("No internet connection")
            val connectionLiveData = ConnectionLiveData(requireContext())

            connectionLiveData.observe(viewLifecycleOwner, {
                lifecycleScope.launch(Dispatchers.Main) {
                    if (it) {
                        connectionLiveData.removeObservers { lifecycle }
                        networkUsecase.onConnected(isEvents)
                        snackbar.dismiss()
                    } else {
                        snackbar = showErrorSnackbar("No internet connection")
                        snackbar.show()
                    }
                }
            })
        }

        viewInteractor.clickedUrl.observe(viewLifecycleOwner, { url ->
            lifecycleScope.launch {

                val intent = Intent(requireContext(), PlayerActivity::class.java)
                intent.putExtra(PlayerActivity.URL, url)
                startActivity(intent)

            }
        })

        lifecycleScope.launch {
            viewInteractor.state.collect {
                when (it) {
                    is ERROR -> showErrorSnackbar(it.message)
                    is ERROR_DIALOG -> showErrorSnackbar(it.message)
                    is LOADING -> {
                        with(binding) {
                            searchLoading.visibility = View.VISIBLE
                            recyclerView.visibility = View.GONE
                        }
                    }
                    is SUCCESS -> {
                        updateAdapter(it.data)
                    }
                }
            }
        }
    }


    private fun showErrorSnackbar(message: String): Snackbar {
       return requireContext().showSnackbar(message,binding.root)
    }

    private fun initRecyclerView() {
        adapter = DaznAdapter(viewInteractor)

        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)
    }

    private fun updateAdapter(items: List<DaznItem>) {
        with(binding) {

            searchLoading.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE

            adapter.submitList(items)

            searchLoading.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE

        }
    }

}