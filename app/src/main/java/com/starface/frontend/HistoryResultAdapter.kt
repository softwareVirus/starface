package com.starface.frontend

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.starface.frontend.databinding.ItemHistoryResultBinding
import com.starface.frontend.models.HistoryActor

class HistoryResultAdapter(
    private val historyResults: List<HistoryActor>
) : RecyclerView.Adapter<HistoryResultAdapter.HistoryResultViewHolder>() {

    inner class HistoryResultViewHolder(private val binding: ItemHistoryResultBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(actor: HistoryActor) {
            with(binding) {
                Glide.with(imageView.context)
                    .load(actor.imdb_link)
                    .into(imageView)
                titleTextView.text = "${actor.first_name} ${actor.last_name}"
                // Optional: Handle click events for each item
                root.setOnClickListener {
                    // Perform action when item is clicked
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryResultViewHolder {
        val binding = ItemHistoryResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryResultViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryResultViewHolder, position: Int) {
        holder.bind(historyResults[position])
    }

    override fun getItemCount(): Int {
        return historyResults.size
    }
}
