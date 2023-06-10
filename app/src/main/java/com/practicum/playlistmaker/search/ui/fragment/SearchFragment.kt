package com.practicum.playlistmaker.search.ui.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.core.utils.router.HandlerRouter
import com.practicum.playlistmaker.databinding.FragmentSearchBinding
import com.practicum.playlistmaker.search.domain.models.NetworkError
import com.practicum.playlistmaker.search.domain.models.TrackModel
import com.practicum.playlistmaker.search.ui.models.SearchContentState
import com.practicum.playlistmaker.search.ui.view_model.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment(R.layout.fragment_search) {
    
    private lateinit var trackAdapter: TrackAdapter
    
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: SearchViewModel by viewModel()
    private val handlerRouter by lazy(LazyThreadSafetyMode.NONE) { HandlerRouter() }
    
    private var textWatcher: TextWatcher? = null
    
    override fun onResume() {
        super.onResume()
        viewModel.onViewResume()
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSearchBinding.bind(view)
        
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
        textWatcher?.let { binding.inputEditText.removeTextChangedListener(it) }
        _binding = null
    }
    
    private fun initListeners() {
        
        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(
                p0: CharSequence?, p1: Int, p2: Int, p3: Int,
            ) {
            } //empty
            
            override fun afterTextChanged(s: Editable?) {} //empty
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onSearchTextChanged(s.toString())
            }
        }
        
        binding.apply {
            
            inputEditText.setOnFocusChangeListener { _, hasFocus ->
                viewModel.searchFocusChanged(hasFocus, binding.inputEditText.text.toString())
            }
            textWatcher?.let { inputEditText.addTextChangedListener(it) }
            
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
            if (handlerRouter.clickDebounce()) {
                viewModel.addTrackToHistoryList(track)
                findNavController().navigate(
                    R.id.action_searchFragment_to_audioPlayerFragment,
                )
    
            }
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
    
        trackAdapter.apply {
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
        trackAdapter.apply {
            trackList.clear()
            trackList.addAll(list)
            notifyDataSetChanged()
        }
    }

    private fun showMessage(error: NetworkError) {
        when (error) {
            NetworkError.SEARCH_ERROR -> {
            
                trackAdapter.apply {
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
            
                trackAdapter.apply {
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
        trackAdapter.apply {
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
}


