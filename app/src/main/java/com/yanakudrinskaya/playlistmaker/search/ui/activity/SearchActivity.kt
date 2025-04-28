package com.yanakudrinskaya.playlistmaker.search.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.yanakudrinskaya.playlistmaker.player.ui.activity.AudioPlayerActivity
import com.yanakudrinskaya.playlistmaker.databinding.ActivitySearchBinding
import com.yanakudrinskaya.playlistmaker.search.domain.models.Track
import com.yanakudrinskaya.playlistmaker.search.ui.SearchHistoryAdapter
import com.yanakudrinskaya.playlistmaker.search.ui.TrackListAdapter
import com.yanakudrinskaya.playlistmaker.search.ui.model.TrackState
import com.yanakudrinskaya.playlistmaker.search.ui.view_model.SearchViewModel

class SearchActivity : AppCompatActivity() {

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private var searchString: String = ""
    private val viewModel by viewModels<SearchViewModel> { SearchViewModel.getViewModelFactory() }
    private lateinit var binding: ActivitySearchBinding
    private lateinit var simpleTextWatcher: TextWatcher

    private val trackListAdapter = TrackListAdapter()
    private val searchHistoryAdapter = SearchHistoryAdapter()

    private val handler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.rvTracklist.adapter = trackListAdapter
        binding.rvSearchHistory.adapter = searchHistoryAdapter

        setupObserves()
        setupListeners()
        setupContent()
    }

    private fun setupObserves() {

        viewModel.getSearchLiveData().observe(this) {
            render(it)
        }

        viewModel.getHistoryLiveData().observe(this) {
            updateHistoryUi(it)
        }
    }

    private fun setupListeners() {

        trackListAdapter.onItemClick = { track -> openAudioPlayer(track) }
        searchHistoryAdapter.onItemClick = { track -> openAudioPlayer(track) }

        simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.clearIcon.visibility = clearButtonVisibility(s)
                binding.searchHistoryGroup.isVisible =
                    if (binding.inputEditText.hasFocus() && s?.isEmpty() == true && searchHistoryAdapter.searchHistoryTrackList.isNotEmpty()) true else false
                viewModel.searchDebounce(
                    s?.toString() ?: ""
                )
            }

            override fun afterTextChanged(s: Editable?) {
                searchString = binding.inputEditText.text.toString()

            }
        }

        simpleTextWatcher.let { binding.inputEditText.addTextChangedListener(it) }

        binding.inputEditText.setOnFocusChangeListener { view, hasFocus ->
            binding.searchHistoryGroup.isVisible =
                if (hasFocus && binding.inputEditText.text.isEmpty() && searchHistoryAdapter.searchHistoryTrackList.isNotEmpty()) true else false
        }

        binding.toolbarSearch.setNavigationOnClickListener {
            finish()
        }

        binding.clearHistoryButton.setOnClickListener {
            viewModel.clearSearchHistory()
            binding.searchHistoryGroup.isVisible = false
        }

        binding.updateButton.setOnClickListener {
            viewModel.searchDebounce(searchString)
            closeErrorMessage()
        }

        binding.clearIcon.setOnClickListener {
            binding.inputEditText.setText("")
            clearSearchList()
            closeErrorMessage()
            ///updateHistoryUi()
            hideKeyboard()
        }
    }

    private fun clearSearchList() {
        trackListAdapter.removeItems()
        trackListAdapter.notifyDataSetChanged()
    }

    private fun setupContent() {
        viewModel.getHistoryList()
        binding.inputEditText.setText(viewModel.getSearchTextLiveData().value)
    }

    private fun render(state: TrackState) {
        when (state) {
            is TrackState.Content -> showSearchContent(state.tracks)
            is TrackState.Empty -> showError(binding.errorNotFound)
            is TrackState.Error -> showError(binding.errorConnect)
            is TrackState.Loading -> showLoading()
        }
    }

    private fun showLoading() {
        closeErrorMessage()
        binding.rvTracklist.visibility = View.GONE
        binding.searchHistoryGroup.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun showSearchContent(tracks: List<Track>) {
        closeErrorMessage()
        binding.progressBar.visibility = View.GONE
        binding.searchHistoryGroup.visibility = View.GONE
        binding.rvTracklist.visibility = View.VISIBLE
        trackListAdapter.removeItems()
        trackListAdapter.trackList.addAll(tracks)
        trackListAdapter.notifyDataSetChanged()
    }


    private fun showError(view: LinearLayout) {
        binding.progressBar.visibility = View.GONE
        binding.searchHistoryGroup.visibility = View.GONE
        binding.rvTracklist.visibility = View.GONE
        view.isVisible = true
        hideKeyboard()
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun openAudioPlayer(track: Track) {
        if (clickDebounce()) {
            viewModel.addTrackToHistory(track)
            startActivity(Intent(this, AudioPlayerActivity::class.java))
        }
    }

    private fun updateHistoryUi(list: MutableList<Track>) {
        searchHistoryAdapter.searchHistoryTrackList = list
        searchHistoryAdapter.notifyDataSetChanged()
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(binding.inputEditText.windowToken, 0)
    }


    private fun closeErrorMessage() {
        binding.errorNotFound.isVisible = false
        binding.errorConnect.isVisible = false
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        simpleTextWatcher.let { binding.inputEditText.removeTextChangedListener(it) }
    }
}

