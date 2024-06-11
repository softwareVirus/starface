package com.starface.frontend

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.starface.frontend.databinding.FragmentSearchBinding
import com.starface.frontend.models.*
import com.starface.frontend.utils.NetworkResult
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val searchResults = mutableListOf<SearchResult>()
    private val searchViewModel by viewModels<SearchViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearchBar()
        setupCategoryRadioGroup()
        setupObservers()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = SearchResultAdapter(searchResults)
    }

    private fun setupSearchBar() {
        binding.searchButton.setOnClickListener {
            binding.txtError.text = "" // Clear error message on each search click
            val query = binding.searchBar.text.toString()
            val selectedCategory = view?.findViewById<RadioButton>(binding.categoryRadioGroup.checkedRadioButtonId)
                ?.text
                ?.toString()
                ?.toLowerCase()
                ?.split("/")
                ?.first()

            performSearch(query, selectedCategory!!)
        }
    }

    private fun setupCategoryRadioGroup() {
        binding.categoryRadioGroup.check(R.id.radio_actor)
    }

    private fun setupObservers() {
        searchViewModel.actorSearchResponseLiveData.observe(viewLifecycleOwner, Observer { result ->
            handleSearchResult(result)
        })
        searchViewModel.movieSearchResponseLiveData.observe(viewLifecycleOwner, Observer { result ->
            handleSearchResult(result)
        })
        searchViewModel.seriesSearchResponseLiveData.observe(viewLifecycleOwner, Observer { result ->
            handleSearchResult(result)
        })
    }

    private fun performSearch(query: String, category: String) {
        searchViewModel.search(category, query)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun handleSearchResult(result: NetworkResult<*>) {
        when (result) {
            is NetworkResult.Success<*> -> {
                binding.progressBar.visibility = View.GONE
                updateSearchResults(result.data)
            }
            is NetworkResult.Error<*> -> {
                binding.progressBar.visibility = View.GONE
                // Handle error, show error message
                binding.txtError.text = result.message
            }
            is NetworkResult.Loading<*> -> {
                binding.progressBar.visibility = View.VISIBLE
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateSearchResults(searchResponse: Any?) {
        searchResults.clear()
        when (searchResponse) {
            is ActorSearchResponse -> {
                if (searchResponse.actors.isEmpty()) {
                    binding.txtError.text = "No Items Found"
                } else {
                    searchResponse.actors.forEach { actor ->
                        searchResults.add(SearchResult(actor.img_url, actor.firstname + " " + actor.lastname, null, actor.actor_actress_id))
                    }
                }
            }
            is MovieSearchResponse -> {
                if (searchResponse.movies.isEmpty()) {
                    binding.txtError.text = "No Items Found"
                } else {
                    searchResponse.movies.forEach { movie ->
                        searchResults.add(SearchResult(movie.img_url, movie.movie_name, movie.imdb_rating.toFloat()))
                    }
                }
            }
            is SeriesSearchResponse -> {
                if (searchResponse.series.isEmpty()) {
                    binding.txtError.text = "No Items Found"
                } else {
                    searchResponse.series.forEach { series ->
                        searchResults.add(SearchResult(series.img_url, series.series_name, series.imdb_rating.toFloat()))
                    }
                }
            }
        }
        binding.recyclerView.adapter?.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
