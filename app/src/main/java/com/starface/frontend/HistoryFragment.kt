package com.starface.frontend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.starface.frontend.databinding.FragmentHistoryBinding
import com.starface.frontend.models.HistoryActor
import com.starface.frontend.models.HistoryResponse
import com.starface.frontend.utils.NetworkResult
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private val historyViewModel by viewModels<HistoryViewModel>()
    private val historyResults = mutableListOf<HistoryActor>()
    private lateinit var adapter: HistoryResultAdapter
    private var currentPage = 1
    private val limit = 20

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
        loadHistory()

        binding.btnLoadMore.setOnClickListener {
            currentPage++
            loadHistory()
        }
    }

    private fun setupRecyclerView() {
        adapter = HistoryResultAdapter(historyResults)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter
    }

    private fun setupObservers() {
        historyViewModel.historyResponseLiveData.observe(viewLifecycleOwner, Observer { result ->
            handleHistoryResult(result)
        })
    }

    private fun loadHistory() {
        historyViewModel.getHistory(currentPage, limit)
    }

    private fun handleHistoryResult(result: NetworkResult<HistoryResponse>) {
        when (result) {
            is NetworkResult.Success -> {
                binding.progressBar.visibility = View.GONE
                result.data?.actors?.let {
                    historyResults.addAll(it)
                    adapter.notifyDataSetChanged()
                }
            }
            is NetworkResult.Error -> {
                binding.progressBar.visibility = View.GONE
                // Handle error, show error message
                binding.txtError.text = result.message
            }
            is NetworkResult.Loading -> {
                binding.progressBar.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
