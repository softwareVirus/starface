package com.starface.frontend

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.starface.frontend.databinding.FragmentActorBinding
import com.starface.frontend.models.ActorResponse
import com.starface.frontend.utils.NetworkResult
import com.starface.frontend.utils.NetworkUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ActorFragment : Fragment() {

    private val actorViewModel: ActorViewModel by viewModels()

    private var _binding: FragmentActorBinding? = null
    private val binding get() = _binding!!
    private lateinit var actorPhotoImageView: ImageView
    private lateinit var actorNameTextView: TextView
    private lateinit var actorBioTextView: TextView
    private lateinit var actorUrlTextView: TextView
    private lateinit var moviesSeriesListLayout: LinearLayout
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentActorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        actorPhotoImageView = view.findViewById(R.id.actor_photo)
        actorNameTextView = view.findViewById(R.id.actor_name)
        actorBioTextView = view.findViewById(R.id.actor_bio)
        actorUrlTextView = view.findViewById(R.id.actor_url)
        moviesSeriesListLayout = view.findViewById(R.id.movies_series_list)
        // Observe the actor details
        actorViewModel.actorResponseLiveData.observe(viewLifecycleOwner, Observer { actor ->
            actor?.let { handleActorResult(it) }
        })

        // Load actor details
        loadActorDetails()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun handleActorResult(result: NetworkResult<*>) {
        when (result) {
            is NetworkResult.Success<*> -> {
                binding.progressBar.visibility = View.GONE
                displayActorDetails(result.data)
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

    private fun loadActorDetails() {
        val actorId = arguments?.getInt("id")
        actorId?.let {
            actorViewModel.findActor(it)
            if (NetworkUtil.isNetworkAvailable(requireContext())) {
                // Call your function if the internet is available
                actorViewModel.saveHistory(it)
            }
        }
    }

    private fun displayActorDetails(searchResponse: Any?) {
        when (searchResponse) {
            is ActorResponse -> {
                searchResponse.actor.let { actor ->
                    actorNameTextView.text = "${actor.firstname} ${actor.lastname}"
                    actorBioTextView.text = actor.biography
                    actorUrlTextView.text = actor.img_url

                    Glide.with(actorPhotoImageView.context)
                        .load(actor.img_url)
                        .into(actorPhotoImageView)
                }

                // Clear previous movie and series views
                moviesSeriesListLayout.removeAllViews()

                // Display movies
                searchResponse.movies.forEach { movie ->
                    val movieView = layoutInflater.inflate(R.layout.item_movie, moviesSeriesListLayout, false) as LinearLayout
                    val movieNameTextView = movieView.findViewById<TextView>(R.id.movie_name)
                    val movieRatingTextView = movieView.findViewById<TextView>(R.id.movie_rating)
                    val movieImageView = movieView.findViewById<ImageView>(R.id.movie_image)

                    movieNameTextView.text = movie.movie_name
                    movieRatingTextView.text = "Rating: ${movie.imdb_rating}"

                    Glide.with(movieImageView.context)
                        .load(movie.img_url)
                        .into(movieImageView)

                    moviesSeriesListLayout.addView(movieView)
                }

                // Display series
                searchResponse.series.forEach { series ->
                    val seriesView = layoutInflater.inflate(R.layout.item_series, moviesSeriesListLayout, false) as LinearLayout
                    val seriesNameTextView = seriesView.findViewById<TextView>(R.id.series_name)
                    val seriesRatingTextView = seriesView.findViewById<TextView>(R.id.series_rating)
                    val seriesImageView = seriesView.findViewById<ImageView>(R.id.series_image)

                    seriesNameTextView.text = series.series_name
                    seriesRatingTextView.text = "Rating: ${series.imdb_rating}"

                    Glide.with(seriesImageView.context)
                        .load(series.img_url)
                        .into(seriesImageView)

                    moviesSeriesListLayout.addView(seriesView)
                }
            }
        }
    }


}
