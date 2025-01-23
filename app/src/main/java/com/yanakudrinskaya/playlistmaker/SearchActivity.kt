package com.yanakudrinskaya.playlistmaker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView

class SearchActivity : AppCompatActivity() {

    private var searchString: String = SEARCH

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        val toolbar = findViewById<Toolbar>(R.id.toolbar_search)
        val inputEditText = findViewById<EditText>(R.id.input_edit_text)
        val clearButton = findViewById<ImageView>(R.id.clear_icon)

        val trackList = TrackListAdapter(
            listOf(
                Track(getString(R.string.trackName1), getString(R.string.artistName1), getString(R.string.trackTime1), getString(R.string.artworkUrl1)),
                Track(getString(R.string.trackName2), getString(R.string.artistName2), getString(R.string.trackTime2), getString(R.string.artworkUrl2)),
                Track(getString(R.string.trackName3), getString(R.string.artistName3), getString(R.string.trackTime3), getString(R.string.artworkUrl3)),
                Track(getString(R.string.trackName4), getString(R.string.artistName4), getString(R.string.trackTime4), getString(R.string.artworkUrl4)),
                Track(getString(R.string.trackName5), getString(R.string.artistName5), getString(R.string.trackTime5), getString(R.string.artworkUrl5)),
            )
        )

        val rvTrackList = findViewById<RecyclerView>(R.id.rv_tracklist)
        rvTrackList.adapter = trackList

        toolbar.setNavigationOnClickListener {
            val displayIntent = Intent(this, MainActivity::class.java)
            startActivity(displayIntent)
            finish()
        }

        clearButton.setOnClickListener {
            inputEditText.setText("")
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
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
        val inputEditText = findViewById<EditText>(R.id.input_edit_text)
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