package com.starface.frontend

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.starface.frontend.databinding.ItemSearchResultBinding

data class SearchResult(val imageUrl: String, val title: String, val imdbRating: Float?, val id: Int? = null)

class SearchResultAdapter(
    private val searchResults: List<SearchResult>
) : RecyclerView.Adapter<SearchResultAdapter.SearchResultViewHolder>() {

    inner class SearchResultViewHolder(private val binding: ItemSearchResultBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(searchResult: SearchResult) {

            with(binding) {
                Glide.with(imageView.context)
                    .load(searchResult.imageUrl)
                    .into(imageView)
                titleTextView.text = searchResult.title
                Log.d("idss", searchResult.id.toString())
                searchResult.imdbRating?.let {
                    binding.imdbRatingTextView.text = it.toString()
                    binding.imdbRatingTextView.visibility = View.VISIBLE
                } ?: run {
                    binding.imdbRatingTextView.visibility = View.GONE
                }
                // Set click listener for the root view
                root.setOnClickListener {
                    searchResult.id?.let { id ->
                        val bundle = Bundle().apply { putInt("id", id) }
                        root.findNavController().navigate(R.id.action_searchFragment_to_actorFragment, bundle)
                    }
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        val binding = ItemSearchResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchResultViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        holder.bind(searchResults[position])
    }

    override fun getItemCount(): Int {
        return searchResults.size
    }
}
