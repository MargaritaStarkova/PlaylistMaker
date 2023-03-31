package com.practicum.playlistmaker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.practicum.api.SearchApi
import com.practicum.api.SearchResponse
import com.practicum.api.TrackData
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import com.practicum.recycler.TrackAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    companion object {
        private const val SEARCH_QUERY = "SEARCH_QUERY"
        private const val BASE_URL = "https://itunes.apple.com/"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L

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
    private lateinit var searchHistory: SearchHistory
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var progressBar: ProgressBar

    private val logging = HttpLoggingInterceptor().apply {
        setLevel(HttpLoggingInterceptor.Level.BODY)
    }
    private val okHttpClient = OkHttpClient.Builder().addInterceptor(logging).build()
    private val retrofit =
        Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient).build()

    private val trackService = retrofit.create(SearchApi::class.java)
    private val trackDataList = ArrayList<TrackData>()
    private val historyList = ArrayList<TrackData>()

    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { search() }

    private var countValue = ""
    private var isClickAllowed = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        viewSetting()
        settingListeners()
        historyList.addAll(searchHistory.readHistory())
        historyListVisibility(historyList, historyList.isNotEmpty())

    }

    override fun onStop() {
        super.onStop()
        searchHistory.saveHistory(historyList)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(searchRunnable)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_QUERY, countValue)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        countValue = savedInstanceState.getString(SEARCH_QUERY, "")
    }

    private fun viewSetting() {

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
        searchList.layoutManager = LinearLayoutManager(this)
        searchHistory = SearchHistory(getSharedPreferences(PREFERENCES, MODE_PRIVATE))

        trackAdapter = TrackAdapter {
            addTrackToSearchHistory(it)
            if (clickDebounce()) transitionToAudioPlayer(it)
        }

        searchList.adapter = trackAdapter

    }

    private fun settingListeners() {

        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && inputEditText.text.isEmpty()) historyListVisibility(
                historyList, historyList.isNotEmpty()
            )
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { //empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                countValue = s.toString()
                clearIcon.visibility = clearButtonVisibility(countValue)

                searchDebounce()
                if (countValue == "") {
                    trackDataList.clear()
                    showMessage(Success)
                    historyListVisibility(historyList, historyList.isNotEmpty())
                }
            }

            override fun afterTextChanged(s: Editable?) { //empty
            }
        }

        inputEditText.setText(countValue)
        inputEditText.addTextChangedListener(textWatcher)


        clearIcon.setOnClickListener {
            hideKeyboard()
            inputEditText.setText("")
            trackDataList.clear()
            showMessage(Success)
        }

        clearHistory.setOnClickListener {
            historyList.clear()
            historyListVisibility(historyList, historyList.isNotEmpty())
        }

        updateButton.setOnClickListener {
            search()
        }

        navigationToolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun search() {

        when (countValue) {
            "" -> {
                trackDataList.clear()
                showMessage(Success)
                historyListVisibility(historyList, historyList.isNotEmpty())
            }

            else -> {
                progressBar.visibility = View.VISIBLE
                historyListVisibility(trackDataList, false)

                trackService.search(countValue).enqueue(object : Callback<SearchResponse> {

                    override fun onResponse(
                        call: Call<SearchResponse>, response: Response<SearchResponse>
                    ) {

                        progressBar.visibility = View.GONE

                        when (response.code()) {
                            in 100..399 -> {
                                if (response.body()?.results?.isNotEmpty() == true) {
                                    trackDataList.clear()
                                    trackDataList.addAll(response.body()?.results!!.filter { it.previewUrl != null })
                                    trackAdapter.notifyDataSetChanged()
                                    showMessage(Success)
                                } else showMessage(SearchError)
                            }
                            in 400..499 -> {
                                showMessage(SearchError)
                            }
                            else -> showMessage(ConnectionError)
                        }
                    }

                    override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                        progressBar.visibility = View.GONE
                        showMessage(ConnectionError)
                    }

                })
            }
        }
    }

    private fun showMessage(error: Error) = when (error) {
        is Success -> {
            placeholderMessage.visibility = View.GONE
            placeholderImage.visibility = View.GONE
            updateButton.visibility = View.GONE
        }

        is SearchError -> {
            trackDataList.clear()
            trackAdapter.notifyDataSetChanged()

            updateButton.visibility = View.GONE
            placeholderMessage.visibility = View.VISIBLE
            placeholderImage.visibility = View.VISIBLE

            placeholderMessage.text = getString(R.string.search_error)
            placeholderImage.setImageDrawable(
                ContextCompat.getDrawable(
                    this@SearchActivity, R.drawable.search_error
                )
            )
        }

        is ConnectionError -> {

            trackDataList.clear()
            trackAdapter.notifyDataSetChanged()

            hideKeyboard()

            placeholderMessage.visibility = View.VISIBLE
            placeholderImage.visibility = View.VISIBLE
            updateButton.visibility = View.VISIBLE

            placeholderMessage.text = getString(R.string.internet_error)
            placeholderImage.setImageDrawable(
                ContextCompat.getDrawable(
                    this@SearchActivity, R.drawable.internet_error
                )
            )
        }
    }

    private fun addTrackToSearchHistory(track: TrackData) = when {

        historyList.contains(track) -> {

            val position = historyList.indexOf(track)

            historyList.remove(track)
            trackAdapter.notifyItemRemoved(position)
            trackAdapter.notifyItemRangeChanged(position, historyList.size)

            historyList.add(0, track)
            trackAdapter.notifyItemInserted(0)
            trackAdapter.notifyItemRangeChanged(0, historyList.size)
        }

        historyList.size < 10 -> {

            historyList.add(0, track)
            trackAdapter.notifyItemInserted(0)
            trackAdapter.notifyItemRangeChanged(0, historyList.size)
        }

        else -> {

            historyList.removeAt(9)
            trackAdapter.notifyItemRemoved(9)
            trackAdapter.notifyItemRangeChanged(9, historyList.size)

            historyList.add(0, track)
            trackAdapter.notifyItemInserted(0)
            trackAdapter.notifyItemRangeChanged(0, historyList.size)
        }
    }

    private fun transitionToAudioPlayer(track: TrackData) {
        val intent = Intent(this, AudioPlayerActivity::class.java).apply {
            putExtra(AudioPlayerActivity.TRACK_DATA, Gson().toJson(track))
        }
        startActivity(intent)
    }

    private fun historyListVisibility(array: ArrayList<TrackData>, isHistoryListNotEmpty: Boolean) {
        trackAdapter.trackList = array
        trackAdapter.notifyDataSetChanged()
        if (isHistoryListNotEmpty) {
            youSearched.visibility = View.VISIBLE
            clearHistory.visibility = View.VISIBLE
        } else {
            youSearched.visibility = View.GONE
            clearHistory.visibility = View.GONE
        }
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) View.GONE
        else View.VISIBLE
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }
}


