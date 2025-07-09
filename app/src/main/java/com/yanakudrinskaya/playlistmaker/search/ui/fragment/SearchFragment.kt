package com.yanakudrinskaya.playlistmaker.search.ui.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.yanakudrinskaya.playlistmaker.R
import com.yanakudrinskaya.playlistmaker.databinding.FragmentSearchBinding
import com.yanakudrinskaya.playlistmaker.search.domain.models.Track
import com.yanakudrinskaya.playlistmaker.search.ui.SearchHistoryAdapter
import com.yanakudrinskaya.playlistmaker.search.ui.TrackListAdapter
import com.yanakudrinskaya.playlistmaker.search.ui.model.TrackState
import com.yanakudrinskaya.playlistmaker.search.ui.view_model.SearchViewModel
import com.yanakudrinskaya.playlistmaker.utils.debounce
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!


    private lateinit var onTrackClickDebounce: (Track) -> Unit

    private var searchString: String = ""
    private val viewModel by viewModel<SearchViewModel>()

    private lateinit var simpleTextWatcher: TextWatcher

    private val trackListAdapter = TrackListAdapter()
    private val searchHistoryAdapter = SearchHistoryAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvTracklist.adapter = trackListAdapter
        binding.rvSearchHistory.adapter = searchHistoryAdapter

        onTrackClickDebounce = debounce<Track>(CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false) { track ->
            viewModel.addTrackToHistory(track)
            val direction = SearchFragmentDirections.actionSearchFragmentToAudioPlayerFragment(track)
            findNavController().navigate(direction)
        }

        setupObserves()
        setupListeners()
        setupContent()
    }

    private fun setupObserves() {

        viewModel.getSearchLiveData().observe(viewLifecycleOwner) {
            render(it)
        }

        viewModel.getHistoryLiveData().observe(viewLifecycleOwner) {
            updateHistoryUi(it)
        }
    }

    private fun setupListeners() {

        trackListAdapter.onItemClick = { track -> onTrackClickDebounce(track) }
        searchHistoryAdapter.onItemClick = { track -> onTrackClickDebounce(track) }

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
            viewModel.clearSearchResults()
            clearSearchList()
            closeErrorMessage()
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

    private fun updateHistoryUi(list: MutableList<Track>) {
        searchHistoryAdapter.searchHistoryTrackList = list
        searchHistoryAdapter.notifyDataSetChanged()
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
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

    override fun onDestroyView() {
        super.onDestroyView()
        simpleTextWatcher.let { binding.inputEditText.removeTextChangedListener(it) }
        _binding = null

    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

}