package com.practicum.playlistmaker.presentation.ui.search

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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.domain.models.TrackModel
import com.practicum.playlistmaker.domain.models.NetworkError
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.data.network.SearchApi
import com.practicum.playlistmaker.presentation.presenters.router.NavigationRouter
import com.practicum.playlistmaker.presentation.presenters.search.SearchPresenter
import com.practicum.playlistmaker.presentation.presenters.search.SearchView
import com.practicum.playlistmaker.presentation.ui.App
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity(), SearchView {

    companion object {
        private const val BASE_URL = "https://itunes.apple.com/"
    }

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
    private lateinit var presenter: SearchPresenter

    private val logging = HttpLoggingInterceptor().apply {
        setLevel(HttpLoggingInterceptor.Level.BODY)
    }
    private val okHttpClient = OkHttpClient.Builder().addInterceptor(logging).build()
    private val retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient).build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initViews()
        initListeners()
        initAdapter()

        presenter = Creator.provideSearchPresenter(
            view = this,
            sharedPrefs = getSharedPreferences(App.PREFERENCES, MODE_PRIVATE),
            api = retrofit.create(SearchApi::class.java),
            router = NavigationRouter(this)
        )
    }

    override fun onStop() {
        super.onStop()
        presenter.onViewStopped()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onViewDestroyed()
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
            presenter.onTrackClicked(it)
        }
        searchList.adapter = trackAdapter
    }

    private fun initListeners() {

        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            presenter.searchFocusChanged(hasFocus, inputEditText.text.toString())
        }
        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {} //empty
            override fun afterTextChanged(s: Editable?) {} //empty
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                presenter.onSearchTextChanged(s.toString())
            }
        })

        clearIcon.setOnClickListener {
            presenter.searchTextClearClicked()
        }

        clearHistory.setOnClickListener {
            presenter.onHistoryClearedClicked()
        }

        updateButton.setOnClickListener {
            presenter.loadTrackList(inputEditText.text.toString())
        }

        navigationToolbar.setNavigationOnClickListener {
            presenter.backButtonClicked()
        }
    }

    override fun showSearchList(list: List<TrackModel>) {
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

    override fun showHistoryList(list: ArrayList<TrackModel>) {

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

    override fun showMessage(error: NetworkError) {
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

    override fun clearSearchText() {
        inputEditText.setText("")
    }

    override fun clearButtonVisibility(query: String?) {
        if (query.isNullOrEmpty()) clearIcon.visibility = View.GONE
        else clearIcon.visibility = View.VISIBLE
    }

    override fun hideKeyboard() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    override fun showLoading() {
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


