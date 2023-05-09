package com.practicum.playlistmaker.search.ui.activity

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.search.domain.models.NetworkError
import com.practicum.playlistmaker.search.domain.models.TrackModel
import com.practicum.playlistmaker.search.ui.models.SearchContentState
import com.practicum.playlistmaker.search.ui.view_model.SearchViewModel
import com.practicum.playlistmaker.utils.router.HandlerRouter
import com.practicum.playlistmaker.utils.router.NavigationRouter

class SearchActivity : AppCompatActivity() {
    
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var binding: ActivitySearchBinding
    private lateinit var viewModel: SearchViewModel
    private lateinit var navigationRouter: NavigationRouter
    private lateinit var handlerRouter: HandlerRouter
    
    override fun onResume() {
        super.onResume()
        viewModel.onViewResume()
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        viewModel = ViewModelProvider(
            this, SearchViewModel.getViewModelFactory()
        )[SearchViewModel::class.java]
        
        viewModel.observeContentState().observe(this) { searchScreenState ->
            render(searchScreenState)
        }
        viewModel.observeClearIconState().observe(this) { query ->
            clearIconVisibility(query)
        }
        viewModel.observeSearchTextClearClicked().observe(this) { isClicked ->
            if (isClicked) {
                clearSearchText()
                hideKeyboard()
            }
        }
        
        navigationRouter = NavigationRouter(activity = this@SearchActivity)
        handlerRouter = HandlerRouter()
        
        initListeners()
        initAdapter()
        
    }
    
    override fun onDestroy() {
        super.onDestroy()
        navigationRouter.activity = null
    }
    
    private fun initListeners() {
        
        binding.inputEditText.setOnFocusChangeListener { _, hasFocus ->
            viewModel.searchFocusChanged(hasFocus, binding.inputEditText.text.toString())
        }
        binding.inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {} //empty
            override fun afterTextChanged(s: Editable?) {} //empty
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onSearchTextChanged(s.toString())
            }
        })
        
        binding.clearIcon.setOnClickListener {
            viewModel.searchTextClearClicked()
        }
        
        binding.clearHistory.setOnClickListener {
            viewModel.onHistoryClearedClicked()
        }
        
        binding.updateButton.setOnClickListener {
            viewModel.loadTrackList(binding.inputEditText.text.toString())
        }
        
        binding.navigationToolbar.setNavigationOnClickListener {
            navigationRouter.goBack()
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
        binding.placeholderImage.visibility = View.GONE
        binding.placeholderMessage.visibility = View.GONE
        binding.updateButton.visibility = View.GONE
        binding.youSearched.visibility = View.GONE
        binding.clearHistory.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
    
        trackAdapter.trackList.clear()
        trackAdapter.trackList.addAll(list)
        trackAdapter.notifyDataSetChanged()
    
    }

    private fun showHistoryList(list: List<TrackModel>) {
    
        binding.placeholderImage.visibility = View.GONE
        binding.placeholderMessage.visibility = View.GONE
        binding.updateButton.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        
        if (list.isNotEmpty()) {
            binding.youSearched.visibility = View.VISIBLE
            binding.clearHistory.visibility = View.VISIBLE
        } else {
            binding.youSearched.visibility = View.GONE
            binding.clearHistory.visibility = View.GONE
        }

        trackAdapter.trackList.clear()
        trackAdapter.trackList.addAll(list)
        trackAdapter.notifyDataSetChanged()

    }

    private fun showMessage(error: NetworkError) {
        when (error) {
            NetworkError.SEARCH_ERROR -> {
                trackAdapter.trackList.clear()
                trackAdapter.notifyDataSetChanged()
    
                binding.placeholderImage.visibility = View.VISIBLE
                binding.placeholderMessage.visibility = View.VISIBLE
                binding.updateButton.visibility = View.GONE
                binding.youSearched.visibility = View.GONE
                binding.clearHistory.visibility = View.GONE
                binding.progressBar.visibility = View.GONE
    
                binding.placeholderMessage.text = getString(R.string.search_error)
                binding.placeholderImage.setImageDrawable(
                    ContextCompat.getDrawable(
                        this, R.drawable.search_error
                    )
                )
            }

            NetworkError.CONNECTION_ERROR -> {
                hideKeyboard()
    
                trackAdapter.trackList.clear()
                trackAdapter.notifyDataSetChanged()
    
                binding.placeholderImage.visibility = View.VISIBLE
                binding.placeholderMessage.visibility = View.VISIBLE
                binding.updateButton.visibility = View.VISIBLE
                binding.youSearched.visibility = View.GONE
                binding.clearHistory.visibility = View.GONE
                binding.progressBar.visibility = View.GONE
    
                binding.placeholderMessage.text = getString(R.string.internet_error)
                binding.placeholderImage.setImageDrawable(
                    ContextCompat.getDrawable(
                        this, R.drawable.internet_error
                    )
                )
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
        trackAdapter.trackList.clear()
        trackAdapter.notifyDataSetChanged()
        
        binding.placeholderImage.visibility = View.GONE
        binding.placeholderMessage.visibility = View.GONE
        binding.updateButton.visibility = View.GONE
        binding.youSearched.visibility = View.GONE
        binding.clearHistory.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
    }
}


