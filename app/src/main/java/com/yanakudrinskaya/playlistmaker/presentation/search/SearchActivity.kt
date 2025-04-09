package com.yanakudrinskaya.playlistmaker.presentation.search

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.yanakudrinskaya.playlistmaker.Creator
import com.yanakudrinskaya.playlistmaker.presentation.audioplayer.AudioPlayerActivity
import com.yanakudrinskaya.playlistmaker.R
import com.yanakudrinskaya.playlistmaker.data.EXAMPLE_PREFERENCES
import com.yanakudrinskaya.playlistmaker.data.HISTORY_LIST_KEY
import com.yanakudrinskaya.playlistmaker.data.dto.ResponseStatus
import com.yanakudrinskaya.playlistmaker.domain.api.SearchHistoryInteractor
import com.yanakudrinskaya.playlistmaker.domain.api.TracksInteractor
import com.yanakudrinskaya.playlistmaker.domain.models.Track
import com.yanakudrinskaya.playlistmaker.domain.models.TrackList


class SearchActivity : AppCompatActivity() {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        const val SEARCH_STRING = "SEARCH_STRING"
        const val SEARCH = ""
    }

    private var searchString: String = SEARCH

    private lateinit var toolbar: Toolbar
    private lateinit var inputEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var errorNotFound: LinearLayout
    private lateinit var errorConnect: LinearLayout
    private lateinit var updateButton: Button
    private lateinit var clearHistoryButton: Button
    private lateinit var searchHistoryGroup: LinearLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var searchHistoryInteractor: SearchHistoryInteractor
    private lateinit var tracksInteractor: TracksInteractor

    private lateinit var rvTrackList: RecyclerView
    private lateinit var rvSearchHistory: RecyclerView
    private val trackListAdapter = TrackListAdapter()
    private val searchHistoryAdapter = SearchHistoryAdapter()
    private lateinit var listener: SharedPreferences.OnSharedPreferenceChangeListener

    private val searchRunnable = Runnable { searchTrack() }
    private val handler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true

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
        progressBar = findViewById(R.id.progress_bar)
        clearHistoryButton = findViewById(R.id.clear_history_button)
        searchHistoryGroup = findViewById(R.id.search_history_group)

        rvTrackList.adapter = trackListAdapter
        rvSearchHistory.adapter = searchHistoryAdapter

        searchHistoryInteractor = Creator.provideSearchHistoryInteractor(this)
        tracksInteractor = Creator.provideTracksInteractor()

        updateHistoryUi()

        trackListAdapter.onItemClick = { track -> openAudioPlayer(track) }
        searchHistoryAdapter.onItemClick = { track -> openAudioPlayer(track) }


        inputEditText.setOnFocusChangeListener { view, hasFocus ->
            searchHistoryGroup.isVisible =
                if (hasFocus && inputEditText.text.isEmpty() && searchHistoryAdapter.searchHistoryTrackList.isNotEmpty()) true else false
        }

        val sharedPreferences = getSharedPreferences(EXAMPLE_PREFERENCES, MODE_PRIVATE)
        listener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (key == HISTORY_LIST_KEY) {
                updateHistoryUi()
            }
        }

        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        clearButton.setOnClickListener {
            inputEditText.setText("")
            hideKeyboard()
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
                searchHistoryGroup.isVisible =
                    if (inputEditText.hasFocus() && s?.isEmpty() == true && searchHistoryAdapter.searchHistoryTrackList.isNotEmpty()) true else false
                if (s?.isNotEmpty()!!) searchDebounce()
            }

            override fun afterTextChanged(s: Editable?) {
                searchString = inputEditText.text.toString()

            }
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)

        clearHistoryButton.setOnClickListener {
            searchHistoryInteractor.clearSearchHistory()
            searchHistoryGroup.isVisible = false
        }
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

    private fun openAudioPlayer(track: Track) {
        if (clickDebounce()) {
            searchHistoryInteractor.addTrackToHistory(track)
            val intent = Intent(this, AudioPlayerActivity::class.java)
            intent.putExtra("track", track)
            startActivity(intent)
        }
    }

    private fun searchTrack() {
        if (inputEditText.text.toString().isNotEmpty()) {
            errorClear()
            trackListAdapter.removeItems()
            progressBar.visibility = View.VISIBLE
            tracksInteractor.searchTracks(inputEditText.text.toString(),
                object : TracksInteractor.TracksConsumer {
                    override fun consume(result: TrackList) {
                        runOnUiThread {
                            progressBar.visibility = View.GONE
                            updateUi(result)
                        }
                    }
                })
        }
    }

    private fun updateHistoryUi() {
        searchHistoryInteractor.getHistoryList(
            object : SearchHistoryInteractor.SearchHistoryConsumer {
                override fun consume(history: MutableList<Track>) {
                    searchHistoryAdapter.searchHistoryTrackList = history
                }
            }
        )
        searchHistoryAdapter.notifyDataSetChanged()
    }

    private fun updateUi(result: TrackList) {
        when (result.status) {
            ResponseStatus.SUCCESS -> updateSearchUi(result.list!!)
            ResponseStatus.NO_INTERNET -> showError(errorConnect)
            ResponseStatus.BAD_REQUEST -> Log.d("MyTestSearch", "Неверный запрос")
            ResponseStatus.SERVER_ERROR, ResponseStatus.UNKNOWN_ERROR -> Log.d(
                "MyTestSearch",
                "Ошибка сервера"
            )
        }
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)
    }

    private fun showError(error: LinearLayout) {
        error.isVisible = true
        hideKeyboard()
    }

    private fun updateSearchUi(list: List<Track>) {
        progressBar.visibility = View.GONE
        if (list.isNotEmpty() == true) {
            trackListAdapter.trackList.addAll(list)
            trackListAdapter.notifyDataSetChanged()
        } else showError(errorNotFound)
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

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

}