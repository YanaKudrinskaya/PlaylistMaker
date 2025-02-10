package com.yanakudrinskaya.playlistmaker

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
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
    private lateinit var rvTrackList: RecyclerView
    private lateinit var errorNotFound: LinearLayout
    private lateinit var errorConnect: LinearLayout
    private lateinit var updateButton: Button

    private val trackList = ArrayList<Track>()

    private val adapter = TrackListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        toolbar = findViewById(R.id.toolbar_search)
        inputEditText = findViewById(R.id.input_edit_text)
        clearButton = findViewById(R.id.clear_icon)
        rvTrackList = findViewById(R.id.rv_tracklist)
        errorConnect = findViewById(R.id.error_connect)
        errorNotFound = findViewById(R.id.error_not_found)
        updateButton = findViewById(R.id.update_button)

        adapter.trackList = trackList

        rvTrackList.adapter = adapter

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE && inputEditText.text.trim().length!=0) {
                searchTrack()
                true
            }
            false
        }

        toolbar.setNavigationOnClickListener {
            finish()
        }

        clearButton.setOnClickListener {
            inputEditText.setText("")
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)
            adapter.removeItems()
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
            }

            override fun afterTextChanged(s: Editable?) {
                searchString = inputEditText.text.toString()
            }
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)
    }

    private fun searchTrack() {
        errorClear()
        adapter.removeItems()
        iTunesService.getTrackList(inputEditText.text.toString())
            .enqueue(object : Callback<TracksResponse> {
                override fun onResponse(call: Call<TracksResponse>,
                                        response: Response<TracksResponse>
                ) {
                    when (response.code()) {
                        200 -> {
                            trackList.clear()
                            if (response.body()?.results?.isNotEmpty() == true) {
                                trackList.addAll(response.body()?.results!!)
                                adapter.notifyDataSetChanged()
                            }
                            if (trackList.isEmpty()) {
                                errorNotFound.isVisible = true
                            }
                        }
                        else -> errorConnect.isVisible = true
                    }
                }
                override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
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