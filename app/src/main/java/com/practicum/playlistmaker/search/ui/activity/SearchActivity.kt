package com.practicum.playlistmaker.search.ui.activity

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.search.domain.models.TrackModel
import com.practicum.playlistmaker.search.domain.models.NetworkError
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.ui.models.SearchContentState
import com.practicum.playlistmaker.search.ui.view_model.SearchViewModel

class SearchActivity : ComponentActivity() {

    private lateinit var inputEditText: EditText
    private lateinit var clearIcon: ImageView
    private lateinit var navigationToolbar: androidx.appcompat.widget.Toolbar
    private lateinit var searchList: RecyclerView
    private lateinit var placeholderImage: ImageView
    private lateinit var placeholderMessage: TextView
    private lateinit var updateButton: Button
    private lateinit var youSearched: TextView
    private lateinit var clearHistory: Button
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var progressBar: ProgressBar

    private lateinit var viewModel: SearchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        viewModel = ViewModelProvider(this, SearchViewModel.getViewModelFactory())[SearchViewModel::class.java]
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

        initViews()
        initListeners()
        initAdapter()

    }

    override fun onStop() {
        super.onStop()
        viewModel.onViewStopped()
    }

    private fun initViews() {

        inputEditText = findViewById(R.id.input_edit_text)
        clearIcon = findViewById(R.id.clear_icon)
        navigationToolbar = findViewById(R.id.navigation_toolbar)
        placeholderImage = findViewById(R.id.placeholder_image)
        placeholderMessage = findViewById(R.id.placeholder_message)
        updateButton = findViewById(R.id.update_button)
        youSearched = findViewById(R.id.you_searched)
        clearHistory = findViewById(R.id.clear_history)
        searchList = findViewById(R.id.search_list)
        progressBar = findViewById(R.id.progress_bar)

    }

    private fun initAdapter() {

        trackAdapter = TrackAdapter {
            viewModel.onTrackClicked(it)
        }
        searchList.adapter = trackAdapter
    }

    private fun initListeners() {

        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            viewModel.searchFocusChanged(hasFocus, inputEditText.text.toString())
        }
        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {} //empty
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
            viewModel.loadTrackList(inputEditText.text.toString())
        }

        navigationToolbar.setNavigationOnClickListener {
            finish()
        }
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
        placeholderImage.visibility = View.GONE
        placeholderMessage.visibility = View.GONE
        updateButton.visibility = View.GONE
        youSearched.visibility = View.GONE
        clearHistory.visibility = View.GONE
        progressBar.visibility = View.GONE

        trackAdapter.trackList.clear()
        trackAdapter.trackList.addAll(list)
        trackAdapter.notifyDataSetChanged()

    }

    private fun showHistoryList(list: List<TrackModel>) {

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

        trackAdapter.trackList.clear()
        trackAdapter.trackList.addAll(list)
        trackAdapter.notifyDataSetChanged()

    }

    private fun showMessage(error: NetworkError) {
        when (error) {
            NetworkError.SEARCH_ERROR -> {
                trackAdapter.trackList.clear()
                trackAdapter.notifyDataSetChanged()

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

            NetworkError.CONNECTION_ERROR -> {
                hideKeyboard()

                trackAdapter.trackList.clear()
                trackAdapter.notifyDataSetChanged()

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

    fun clearSearchText() {
        inputEditText.setText("")
    }

    fun clearIconVisibility(query: String?) {
        if (query.isNullOrEmpty()) clearIcon.visibility = View.GONE
        else clearIcon.visibility = View.VISIBLE
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    private fun showLoading() {
        trackAdapter.trackList.clear()
        trackAdapter.notifyDataSetChanged()

        placeholderImage.visibility = View.GONE
        placeholderMessage.visibility = View.GONE
        updateButton.visibility = View.GONE
        youSearched.visibility = View.GONE
        clearHistory.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
    }
}


