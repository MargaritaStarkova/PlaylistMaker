package com.practicum.playlistmaker

import android.content.Context
import android.graphics.drawable.Drawable
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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory


class SearchActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var placeholderImage: ImageView
    private lateinit var placeholderMessage: TextView
    private lateinit var updateButton: Button

    private val baseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val trackService = retrofit.create(SearchApi::class.java)

    private val trackDataList = ArrayList<TrackData>()


    private var countValue = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val inputEditText = findViewById<EditText>(R.id.inputEditText)
        val clearButton = findViewById<ImageView>(R.id.clearIcon)
        val backButton = findViewById<androidx.appcompat.widget.Toolbar>(R.id.backIcon)
        placeholderImage = findViewById(R.id.placeholderImage)
        placeholderMessage = findViewById(R.id.placeholderMessage)
        updateButton = findViewById(R.id.updateButton)

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)


        val trackAdapter = TrackAdapter(trackDataList)
        recyclerView.adapter = trackAdapter

        clearButton.setOnClickListener {
            inputEditText.setText("")
            trackDataList.clear()
            recyclerView.adapter?.notifyDataSetChanged()

            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

        }
        updateButton.setOnClickListener{
            search()
        }

        backButton.setNavigationOnClickListener {
            finish()
        }

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

    }

    private fun search() {
        trackService.search(countValue)
            .enqueue(object : Callback<SearchResponse> {
                override fun onResponse(
                    call: Call<SearchResponse>,
                    response: Response<SearchResponse>
                ) {
                    if (response.isSuccessful) {
                        if(response.body()?.results?.isNotEmpty() == true) {
                            trackDataList.clear()
                            trackDataList.addAll(response.body()?.results!!)
                            recyclerView.adapter?.notifyDataSetChanged()
                            showMessage("", null, false)
                        } else
                            showMessage(getString(R.string.search_error), ContextCompat.getDrawable(this@SearchActivity, R.drawable.search_error), false)

                    } else
                        showMessage(getString(R.string.internet_error), ContextCompat.getDrawable(this@SearchActivity, R.drawable.internet_error), true)
                }

                override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                    showMessage(getString(R.string.internet_error), ContextCompat.getDrawable(this@SearchActivity, R.drawable.internet_error), true)
                }

            })
    }

    private fun showMessage(text: String, image: Drawable?, isInternetError: Boolean){
        if (text.isNotEmpty() && isInternetError) {

            trackDataList.clear()
            recyclerView.adapter?.notifyDataSetChanged()

            placeholderMessage.visibility = View.VISIBLE
            placeholderImage.visibility = View.VISIBLE
            updateButton.visibility = View.VISIBLE

            placeholderMessage.text = text
            placeholderImage.setImageDrawable(image)

        } else if (text.isNotEmpty() && !isInternetError) {
            trackDataList.clear()
            recyclerView.adapter?.notifyDataSetChanged()

            updateButton.visibility = View.GONE
            placeholderMessage.visibility = View.VISIBLE
            placeholderImage.visibility = View.VISIBLE

            placeholderMessage.text = text
            placeholderImage.setImageDrawable(image)
        } else {

            placeholderMessage.visibility = View.GONE
            placeholderImage.visibility = View.GONE
            updateButton.visibility = View.GONE

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
    }
}

