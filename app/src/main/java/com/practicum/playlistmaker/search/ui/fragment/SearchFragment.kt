package com.practicum.playlistmaker.search.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.core.root.HostActivity
import com.practicum.playlistmaker.core.utils.debounce
import com.practicum.playlistmaker.core.utils.viewBinding
import com.practicum.playlistmaker.databinding.FragmentSearchBinding
import com.practicum.playlistmaker.player.ui.fragment.AudioPlayerFragment
import com.practicum.playlistmaker.search.domain.models.NetworkError
import com.practicum.playlistmaker.search.domain.models.TrackModel
import com.practicum.playlistmaker.search.ui.models.SearchContentState
import com.practicum.playlistmaker.search.ui.view_model.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment(R.layout.fragment_search) {
    
    private val binding by viewBinding<FragmentSearchBinding>()
    private val viewModel by viewModel<SearchViewModel>()
    
    private var trackAdapter: TrackAdapter? = null
    private var onClickDebounce: ((TrackModel) -> Unit)? = null
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    
        onClickDebounce = debounce(delayMillis = CLICK_DEBOUNCE_DELAY,
            coroutineScope = viewLifecycleOwner.lifecycleScope,
            useLastParam = false,
            action = { track ->
                viewModel.addTrackToHistoryList(track)
                findNavController().navigate(
                    R.id.action_searchFragment_to_audioPlayerFragment,
                    AudioPlayerFragment.createArgs(track)
                )
            })
    
        viewModel.apply {
            observeContentState().observe(viewLifecycleOwner) { searchScreenState ->
                render(searchScreenState)
            }
            observeClearIconState().observe(viewLifecycleOwner) { query ->
                clearIconVisibility(query)
            }
            observeSearchTextClearClicked().observe(viewLifecycleOwner) { isClicked ->
                if (isClicked) {
                    clearSearchText()
                    hideKeyboard()
                    
                }
            }
        }
        
        initListeners()
        initAdapter()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        trackAdapter = null
    }
    
    private fun initListeners() {
        
        binding.apply {
    
            inputEditText.setOnFocusChangeListener { _, hasFocus ->
                viewModel.searchFocusChanged(hasFocus, binding.inputEditText.text.toString())
            }
            inputEditText.doOnTextChanged { text, _, _, _ ->
                viewModel.onSearchTextChanged(text.toString())
            }
            clearIcon.setOnClickListener {
                viewModel.searchTextClearClicked()
            }
            clearHistory.setOnClickListener {
                viewModel.onHistoryClearedClicked()
            }
            updateButton.setOnClickListener {
                viewModel.loadTrackList(binding.inputEditText.text.toString())
            }
        }
    }
    
    private fun initAdapter() {
        trackAdapter = TrackAdapter { track ->
            (activity as HostActivity).animateBottomNavigationView()
            onClickDebounce?.let { it(track) }
        }
        binding.searchList.adapter = trackAdapter
    }
    
    private fun render(state: SearchContentState) {
        when (state) {
            is SearchContentState.Loading ->
                showLoading()
            is SearchContentState.Error -> showMessage(state.error)
            is SearchContentState.SearchContent -> {
                showSearchList(state.trackList)
            }
            is SearchContentState.HistoryContent -> {
                showHistoryList(state.historyList)
            }
        }
    }

    private fun showSearchList(list: List<TrackModel>) {
    
        binding.apply {
            placeholderImage.visibility = View.GONE
            placeholderMessage.visibility = View.GONE
            updateButton.visibility = View.GONE
            youSearched.visibility = View.GONE
            clearHistory.visibility = View.GONE
            progressBar.visibility = View.GONE
        }
    
        trackAdapter?.apply {
            trackList.clear()
            trackList.addAll(list)
            notifyDataSetChanged()
        }
    }

    private fun showHistoryList(list: List<TrackModel>) {
    
        binding.apply {
            placeholderImage.visibility = View.GONE
            placeholderMessage.visibility = View.GONE
            updateButton.visibility = View.GONE
            progressBar.visibility = View.GONE
        
            if (list.isNotEmpty()) {
                youSearched.visibility = View.VISIBLE
                clearHistory.visibility = View.VISIBLE
            } else {
                youSearched.visibility = View.GONE
                clearHistory.visibility = View.GONE
            }
        }
        trackAdapter?.apply {
            trackList.clear()
            trackList.addAll(list)
            notifyDataSetChanged()
        }
    }

    private fun showMessage(error: NetworkError) {
        when (error) {
            NetworkError.SEARCH_ERROR -> {
    
                trackAdapter?.apply {
                    trackList.clear()
                    notifyDataSetChanged()
                }
    
                binding.apply {
                    placeholderImage.visibility = View.VISIBLE
                    placeholderMessage.visibility = View.VISIBLE
                    updateButton.visibility = View.GONE
                    youSearched.visibility = View.GONE
                    clearHistory.visibility = View.GONE
                    progressBar.visibility = View.GONE
    
                    placeholderMessage.text = getString(R.string.search_error)
                    placeholderImage.setImageDrawable(
                        AppCompatResources.getDrawable(requireContext(), R.drawable.error_search)
                    )
                }
            }
        
            NetworkError.CONNECTION_ERROR -> {
                hideKeyboard()
    
                trackAdapter?.apply {
                    trackList.clear()
                    notifyDataSetChanged()
                }
    
                binding.apply {
                    placeholderImage.visibility = View.VISIBLE
                    placeholderMessage.visibility = View.VISIBLE
                    updateButton.visibility = View.VISIBLE
                    youSearched.visibility = View.GONE
                    clearHistory.visibility = View.GONE
                    progressBar.visibility = View.GONE
                
                    placeholderMessage.text = getString(R.string.internet_error)
                    placeholderImage.setImageDrawable(
                        AppCompatResources.getDrawable(
                            requireContext(), R.drawable.error_internet
                        )
                    )
                }
            }
        }
    }
    
    private fun clearSearchText() {
        binding.inputEditText.setText("")
    }
    
    private fun clearIconVisibility(query: String?) {
        if (query.isNullOrEmpty()) binding.clearIcon.visibility = View.GONE
        else binding.clearIcon.visibility = View.VISIBLE
    }
    
    private fun hideKeyboard() {
        val inputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
    }
    
    private fun showLoading() {
        trackAdapter?.apply {
            trackList.clear()
            notifyDataSetChanged()
        }
        binding.apply {
            placeholderImage.visibility = View.GONE
            placeholderMessage.visibility = View.GONE
            updateButton.visibility = View.GONE
            youSearched.visibility = View.GONE
            clearHistory.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
        }
    }
    
    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 300L
    }
}


