package konradtercjak.daznplayer.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.recyclerview.widget.*
import com.google.android.material.snackbar.Snackbar
import konradtercjak.daznplayer.R
import konradtercjak.daznplayer.databinding.ListFragmentBinding
import konradtercjak.daznplayer.model.*
import konradtercjak.daznplayer.network.FakeRepo
import konradtercjak.daznplayer.ui.PlayerActivity
import konradtercjak.daznplayer.util.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.IOException


class DaznFragment : Fragment(R.layout.list_fragment) {
    companion object {
        const val IS_EVENTS = "IS_EVENTS"
    }

    private val binding: ListFragmentBinding by viewBinding(ListFragmentBinding::bind)
    private var viewModel: DaznViewModel by autoCleaned { ViewModelProvider(this).get(DaznViewModel::class.java) }
    private var adapter: DaznAdapter by autoCleaned { DaznAdapter(viewModel) }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = binding
        val isEvents = arguments?.getBoolean(IS_EVENTS, true) as Boolean
        initRecyclerView()

        if (isInternetConnection(requireContext())) {
            lifecycleScope.launch(Dispatchers.Main) {
                onConnected(isEvents)
            }
        } else {
            var snackbar = showErrorSnackbar("No internet connection")
            val connectionLiveData = ConnectionLiveData(requireContext())

            connectionLiveData.observe(viewLifecycleOwner) {
                lifecycleScope.launch(Dispatchers.Main) {
                    if (it) {
                        connectionLiveData.removeObservers { lifecycle }
                        onConnected(isEvents)
                        snackbar.dismiss()
                    } else {
                        snackbar = showErrorSnackbar("No internet connection")
                        snackbar.show()
                    }
                }
            }
        }

        viewModel.clickedUrl.observe(viewLifecycleOwner) { url ->
            Log.e("AAA", "observed")
            lifecycleScope.launch {

                val intent = Intent(requireContext(), PlayerActivity::class.java)
                intent.putExtra(PlayerActivity.URL, url)
                startActivity(intent)

            }

        }
    }

    private suspend fun onConnected(isEvents: Boolean) {
        if (isEvents)
            initEvents()
        else
            initSchedules()
    }

    private suspend fun initSchedules() {
        viewModel.getSchedule().flowOn(Dispatchers.IO).retryWhen { e, attempt -> e is IOException && attempt < 3 }
            .collectLatest { response ->
                when (response) {
                    is NetworkResponse.Success -> updateAdapter(response.data)
                    is NetworkResponse.HttpError -> {
                        if (response.statusCode == 404 || response.statusCode == 500) {
                            showErrorSnackbar("Connection error ${response.statusCode}")
                        }

                    }
                    is NetworkResponse.Error -> {
                        binding.searchLoading.visibility = View.GONE
                        showErrorSnackbar("Conversion Exception ${response.exception.message}")
                    }
                }
            }
    }

    private suspend fun initEvents() {
        viewModel.getEvents().retryWhen { e, attempt -> e is IOException && attempt < 3 }.flowOn(Dispatchers.IO).collectLatest { response ->
            when (response) {
                is NetworkResponse.Success -> updateAdapter(response.data)
                is NetworkResponse.HttpError -> {
                    if (response.statusCode == 404 || response.statusCode == 500) {
                        showErrorSnackbar("Connection error ${response.statusCode}")
                    }

                }

                is NetworkResponse.Error -> {
                    binding.searchLoading.visibility = View.GONE
                    showErrorSnackbar("Conversion Exception ${response.exception.message}")
                }
            }
        }
    }

    private suspend fun initFake404() {
        FakeRepo<List<DaznEvent>>().get404().flowOn(Dispatchers.IO).collectLatest { response ->
            when (response) {
                is NetworkResponse.Success -> updateAdapter(response.data)
                is NetworkResponse.HttpError -> {
                    if (response.statusCode == 404 || response.statusCode == 500) {
                        showErrorSnackbar("Connection error ${response.statusCode}")
                    }

                }

                is NetworkResponse.Error -> {
                    binding.searchLoading.visibility = View.GONE
                    showErrorSnackbar("Conversion Exception ${response.exception.message}")
                }
            }
        }
    }

    private fun showErrorSnackbar(message: String): Snackbar {
        val snackbar = Snackbar.make(requireContext(), binding.root, message, Snackbar.LENGTH_INDEFINITE)
        snackbar.view.setOnClickListener {
            snackbar.dismiss()
        }
        snackbar.show()
        return snackbar
    }

    private fun initRecyclerView() {
        adapter = DaznAdapter(viewModel)

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
            val dividerItemDecoration = DividerItemDecoration(recyclerView.context, LinearLayoutManager.VERTICAL)

            recyclerView.addItemDecoration(dividerItemDecoration)
            searchLoading.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE

        }
    }

}