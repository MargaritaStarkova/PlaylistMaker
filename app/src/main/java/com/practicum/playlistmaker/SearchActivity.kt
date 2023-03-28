package com.practicum.playlistmaker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
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

    private lateinit var inputEditText: EditText
    private lateinit var clearIcon: ImageView
    private lateinit var backButton: androidx.appcompat.widget.Toolbar
    private lateinit var searchList: RecyclerView
    private lateinit var placeholderImage: ImageView
    private lateinit var placeholderMessage: TextView
    private lateinit var updateButton: Button
    private lateinit var searchHistoryViewGroup: LinearLayout
    private lateinit var historySearchList: RecyclerView
    private lateinit var clearHistory: Button
    private lateinit var searchHistory: SearchHistory

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

    private var countValue = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        viewSetting()
        settingListeners()
        historyList.addAll(searchHistory.readHistory())
    }

    override fun onStop() {
        super.onStop()
        searchHistory.saveHistory(historyList)
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
        backButton = findViewById(R.id.back_icon)
        placeholderImage = findViewById(R.id.placeholder_image)
        placeholderMessage = findViewById(R.id.placeholder_message)
        updateButton = findViewById(R.id.update_button)
        searchHistoryViewGroup = findViewById(R.id.search_history)
        clearHistory = findViewById(R.id.clear_history)

        historySearchList = findViewById(R.id.history_search_list)
        historySearchList.layoutManager = LinearLayoutManager(this)

        searchList = findViewById(R.id.search_list)
        searchList.layoutManager = LinearLayoutManager(this)

        searchHistory = SearchHistory(getSharedPreferences(PREFERENCES, MODE_PRIVATE))

        val trackSearchAdapter = TrackAdapter {
            addTrackToSearchHistory(it)
            transitionToAudioPlayer(it)
        }

        trackSearchAdapter.trackDataList = trackDataList
        searchList.adapter = trackSearchAdapter

        val trackHistoryAdapter = TrackAdapter {
            addTrackToSearchHistory(it)
            transitionToAudioPlayer(it)
        }

        trackHistoryAdapter.trackDataList = historyList
        historySearchList.adapter = trackHistoryAdapter
    }

    private fun settingListeners() {

        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            historyListVisibility(hasFocus && inputEditText.text.isEmpty() && historyList.isNotEmpty())
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { //empty
            }

            override fun onTextChanged(p0: CharSequence?, start: Int, before: Int, count: Int) {
                clearIcon.visibility = clearButtonVisibility(p0)
                countValue = p0.toString()
                historyListVisibility(inputEditText.text.isEmpty())
                if (inputEditText.text.isEmpty()) showMessage(Success)
            }

            override fun afterTextChanged(p0: Editable?) { //empty
            }
        }

        inputEditText.setText(countValue)
        inputEditText.addTextChangedListener(textWatcher)

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search()
            }
            false
        }

        clearIcon.setOnClickListener {

            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

            inputEditText.setText("")
            trackDataList.clear()
            searchList.adapter?.notifyDataSetChanged()
            showMessage(Success)
        }

        clearHistory.setOnClickListener {
            historyList.clear()
            historySearchList.adapter?.notifyDataSetChanged()
            historyListVisibility(historyList.isNotEmpty())
        }

        updateButton.setOnClickListener {
            search()
        }

        backButton.setNavigationOnClickListener {
            finish()
        }
    }

    private fun search() {
        trackService.search(countValue).enqueue(object : Callback<SearchResponse> {

            override fun onResponse(
                call: Call<SearchResponse>, response: Response<SearchResponse>
            ) {
                if (response.isSuccessful) {
                    if (response.body()?.results?.isNotEmpty() == true) {
                        trackDataList.clear()
                        trackDataList.addAll(response.body()?.results!!)
                        searchList.adapter?.notifyDataSetChanged()
                        showMessage(Success)
                    } else showMessage(SearchError)

                } else showMessage(ConnectionError)
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                showMessage(ConnectionError)
            }

        })
    }

    private fun showMessage(error: Error) = when (error) {
        is Success -> {
            placeholderMessage.visibility = View.GONE
            placeholderImage.visibility = View.GONE
            updateButton.visibility = View.GONE
        }

        is SearchError -> {
            trackDataList.clear()
            searchList.adapter?.notifyDataSetChanged()

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
            searchList.adapter?.notifyDataSetChanged()

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
            historySearchList.adapter?.notifyItemRemoved(position)
            historySearchList.adapter?.notifyItemRangeChanged(position, historyList.size)

            historyList.add(0, track)
            historySearchList.adapter?.notifyItemInserted(0)
            historySearchList.adapter?.notifyItemRangeChanged(0, historyList.size)
        }

        historyList.size < 10 -> {

            historyList.add(0, track)
            historySearchList.adapter?.notifyItemInserted(0)
            historySearchList.adapter?.notifyItemRangeChanged(0, historyList.size)
        }

        else -> {

            historyList.removeAt(9)
            historySearchList.adapter?.notifyItemRemoved(9)
            historySearchList.adapter?.notifyItemRangeChanged(9, historyList.size)

            historyList.add(0, track)
            historySearchList.adapter?.notifyItemInserted(0)
            historySearchList.adapter?.notifyItemRangeChanged(0, historyList.size)
        }
    }

    private fun transitionToAudioPlayer(track: TrackData) {
        val intent = Intent(this, AudioPlayerActivity::class.java).apply {
            putExtra(AudioPlayerActivity.TRACK_DATA, Gson().toJson(track))
        }
        startActivity(intent)
    }

    private fun historyListVisibility(b: Boolean) {
        if (b) searchHistoryViewGroup.visibility = View.VISIBLE
        else searchHistoryViewGroup.visibility = View.GONE
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) View.GONE
        else View.VISIBLE
    }

    companion object {
        private const val SEARCH_QUERY = "SEARCH_QUERY"
        private const val BASE_URL = "https://itunes.apple.com/"
    }
}


