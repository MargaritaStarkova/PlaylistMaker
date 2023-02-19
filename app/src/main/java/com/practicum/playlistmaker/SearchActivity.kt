package com.practicum.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import api.SearchApi
import api.SearchResponse
import api.TrackData
import recycler.TrackAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SearchActivity : AppCompatActivity() {

    private lateinit var inputEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var backButton: androidx.appcompat.widget.Toolbar
    private lateinit var recyclerView: RecyclerView
    private lateinit var placeholderImage: ImageView
    private lateinit var placeholderMessage: TextView
    private lateinit var updateButton: Button

    private val retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    private val trackService = retrofit.create(SearchApi::class.java)
    private val trackDataList = ArrayList<TrackData>()
    private var countValue = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        viewSetting()
        settingListeners()
    }

    private fun viewSetting() {

        inputEditText = findViewById(R.id.inputEditText)
        clearButton = findViewById(R.id.clearIcon)
        backButton = findViewById(R.id.backIcon)
        placeholderImage = findViewById(R.id.placeholderImage)
        placeholderMessage = findViewById(R.id.placeholderMessage)
        updateButton = findViewById(R.id.updateButton)

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)


        val trackAdapter = TrackAdapter(trackDataList)
        recyclerView.adapter = trackAdapter
    }

    private fun settingListeners() {

        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                //empty
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //empty
            }
            override fun onTextChanged(p0: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(p0)
                countValue = p0.toString()
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

        clearButton.setOnClickListener {

            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

            inputEditText.setText("")
            trackDataList.clear()
            recyclerView.adapter?.notifyDataSetChanged()

            placeholderMessage.visibility = View.GONE
            placeholderImage.visibility = View.GONE
            updateButton.visibility = View.GONE

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
                        recyclerView.adapter?.notifyDataSetChanged()
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
            recyclerView.adapter?.notifyDataSetChanged()

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
            recyclerView.adapter?.notifyDataSetChanged()

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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_QUERY, countValue)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        countValue = savedInstanceState.getString(SEARCH_QUERY, "")
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


