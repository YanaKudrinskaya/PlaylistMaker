package com.yanakudrinskaya.playlistmaker

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CompoundButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        val toolbar = findViewById<Toolbar>(R.id.toolbar_settings)
        val shareButton = findViewById<MaterialTextView>(R.id.settings_share)
        val supportButton = findViewById<MaterialTextView>(R.id.settings_support)
        val userAgreementButton = findViewById<MaterialTextView>(R.id.settings_user_agreement)
        val themeSwitch = findViewById<SwitchMaterial>(R.id.switch_durk_themes)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            themeSwitch.isChecked = true
        }

        themeSwitch.setOnCheckedChangeListener {
                _, isChecked
            ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        shareButton.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.setType("text/plain")
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_message))
            try {
                startActivity(shareIntent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(
                    this,
                    getString(R.string.share_toast),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        supportButton.setOnClickListener {
            val supportIntent = Intent(Intent.ACTION_SENDTO)
            supportIntent.data = Uri.parse("mailto:")
            supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_message_email)))
            supportIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_message_subject))
            supportIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.support_message))
            try {
                startActivity(supportIntent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(
                    this,
                    getString(R.string.support_toast),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        userAgreementButton.setOnClickListener {
            val url = Uri.parse(getString(R.string.agreement_address))
            val agreementIntent = Intent(Intent.ACTION_VIEW, url)
            try {
                startActivity(agreementIntent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(
                    this,
                    getString(R.string.agreement_toast),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}