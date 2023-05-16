package com.practicum.playlistmaker.search.ui.activity

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.search.domain.models.NetworkError
import com.practicum.playlistmaker.search.domain.models.TrackModel
import com.practicum.playlistmaker.search.ui.models.SearchContentState
import com.practicum.playlistmaker.search.ui.view_model.SearchViewModel
import com.practicum.playlistmaker.utils.router.HandlerRouter
import com.practicum.playlistmaker.utils.router.NavigationRouter
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {
    
    private lateinit var trackAdapter: TrackAdapter
    
    private val viewModel: SearchViewModel by viewModel()
    private val binding by lazy { ActivitySearchBinding.inflate(layoutInflater) }
    private val navigationRouter by lazy { NavigationRouter(activity = this@SearchActivity) }
    private val handlerRouter by lazy { HandlerRouter() }
    
    override fun onResume() {
        super.onResume()
        viewModel.onViewResume()
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    
        viewModel.apply {
            observeContentState().observe(this@SearchActivity) { searchScreenState ->
                render(searchScreenState)
            }
            observeClearIconState().observe(this@SearchActivity) { query ->
                clearIconVisibility(query)
            }
            observeSearchTextClearClicked().observe(this@SearchActivity) { isClicked ->
                if (isClicked) {
                    clearSearchText()
                    hideKeyboard()
                }
            }
        }
        initListeners()
        initAdapter()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        navigationRouter.activity = null
    }
    
    private fun initListeners() {
    
        binding.apply {
            inputEditText.setOnFocusChangeListener { _, hasFocus ->
                viewModel.searchFocusChanged(hasFocus, binding.inputEditText.text.toString())
            }
        
            inputEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int,) {} //empty
                override fun afterTextChanged(s: Editable?) {} //empty
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    viewModel.onSearchTextChanged(s.toString())
                }
            })
            clearIcon.setOnClickListener {
                viewModel.searchTextClearClicked()
            }
            clearHistory.setOnClickListener {
                viewModel.onHistoryClearedClicked()
            }
            updateButton.setOnClickListener {
                viewModel.loadTrackList(binding.inputEditText.text.toString())
            }
            navigationToolbar.setNavigationOnClickListener {
                navigationRouter.goBack()
            }
        }
    }
    
    private fun initAdapter() {
        trackAdapter = TrackAdapter { track ->
            if (handlerRouter.clickDebounce()) {
                viewModel.addTrackToHistoryList(track)
                navigationRouter.openAudioPlayer(track)
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
                        ContextCompat.getDrawable(
                            this@SearchActivity, R.drawable.search_error
                        )
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
                        ContextCompat.getDrawable(
                            this@SearchActivity, R.drawable.internet_error
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
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
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


