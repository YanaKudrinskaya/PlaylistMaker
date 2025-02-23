package com.yanakudrinskaya.playlistmaker

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.telecom.Call
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SearchActivity : AppCompatActivity() {

    private var searchString: String = SEARCH

    private val iTunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(iTunesApi::class.java)

    private lateinit var toolbar: Toolbar
    private lateinit var inputEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var errorNotFound: LinearLayout
    private lateinit var errorConnect: LinearLayout
    private lateinit var updateButton: Button
    private lateinit var clearHistoryButton: Button
    private lateinit var searchHistoryGroup: LinearLayout

    private lateinit var rvTrackList: RecyclerView
    private lateinit var rvSearchHistory: RecyclerView
    private val trackListAdapter = TrackListAdapter()
    private val searchHistoryAdapter = SearchHistoryAdapter()
    private lateinit var listener: SharedPreferences.OnSharedPreferenceChangeListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_search)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        toolbar = findViewById(R.id.toolbar_search)
        inputEditText = findViewById(R.id.input_edit_text)
        clearButton = findViewById(R.id.clear_icon)
        rvTrackList = findViewById(R.id.rv_tracklist)
        rvSearchHistory = findViewById(R.id.rv_search_history)
        errorConnect = findViewById(R.id.error_connect)
        errorNotFound = findViewById(R.id.error_not_found)
        updateButton = findViewById(R.id.update_button)
        clearHistoryButton = findViewById(R.id.clear_history_button)
        searchHistoryGroup = findViewById(R.id.search_history_group)

        rvTrackList.adapter = trackListAdapter
        rvSearchHistory.adapter = searchHistoryAdapter

        val sharedPreferences = getSharedPreferences(EXAMPLE_PREFERENCES, MODE_PRIVATE)
        val searchHistory = SearchHistory(sharedPreferences)

        var historyList = searchHistory.read().toList()
        searchHistoryAdapter.searchHistoryTrackList = historyList
        searchHistoryAdapter.notifyDataSetChanged()

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE && inputEditText.text.trim().length!=0) {
                searchTrack()
                true
            }
            false
        }

        inputEditText.setOnFocusChangeListener { view, hasFocus ->
            searchHistoryGroup.isVisible = if (hasFocus && inputEditText.text.isEmpty() && searchHistoryAdapter.searchHistoryTrackList.isNotEmpty()) true else false
        }

        listener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (key == HISTORY_LIST_KEY) {
                historyList = searchHistory.read().toList()
                searchHistoryAdapter.searchHistoryTrackList = historyList
                searchHistoryAdapter.notifyDataSetChanged()
            }
        }

        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        clearButton.setOnClickListener {
            inputEditText.setText("")
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)
            trackListAdapter.removeItems()
            errorClear()
        }

        updateButton.setOnClickListener {
            searchTrack()
            errorClear()
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
                if (s?.isEmpty() == true) trackListAdapter.removeItems()
                searchHistoryGroup.isVisible = if (inputEditText.hasFocus() && s?.isEmpty() == true && searchHistoryAdapter.searchHistoryTrackList.isNotEmpty()) true else false
            }

            override fun afterTextChanged(s: Editable?) {
                searchString = inputEditText.text.toString()

            }
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)

        clearHistoryButton.setOnClickListener {
            searchHistory.clearSearchHistory()
            searchHistoryGroup.isVisible = false
        }
    }

    private fun searchTrack() {
        errorClear()
        trackListAdapter.removeItems()
        iTunesService.getTrackList(inputEditText.text.toString())
            .enqueue(object : Callback<TracksResponse> {
                override fun onResponse(call: retrofit2.Call<TracksResponse>,
                                        response: Response<TracksResponse>
                ) {
                    val body = response.body()?.results
                    if (response.isSuccessful) {
                        if (body?.isNotEmpty() == true) {
                            trackListAdapter.trackList.addAll(body)
                            trackListAdapter.notifyDataSetChanged()
                        }
                        if (trackListAdapter.trackList.isEmpty()) {
                            errorNotFound.isVisible = true
                        }
                    }
                    else errorConnect.isVisible = true
                }
                override fun onFailure(call: retrofit2.Call<TracksResponse>, t: Throwable) {
                    errorConnect.isVisible = true
                }
            })
    }

    private fun errorClear() {
        errorNotFound.isVisible = false
        errorConnect.isVisible = false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_STRING, searchString)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchString = savedInstanceState.getString(SEARCH_STRING, SEARCH)
    }

    override fun onResume() {
        super.onResume()
        inputEditText.setText(searchString)
    }

    companion object {
        const val SEARCH_STRING = "SEARCH_STRING"
        const val SEARCH = ""
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

}