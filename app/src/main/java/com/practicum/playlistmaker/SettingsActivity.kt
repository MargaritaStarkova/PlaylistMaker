package com.practicum.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backButton = findViewById<ImageView>(R.id.backIcon)

        val share = findViewById<FrameLayout>(R.id.share)
        val support = findViewById<FrameLayout>(R.id.support)
        val termsOfUse = findViewById<FrameLayout>(R.id.terms_of_use)
        val themeMode = findViewById<FrameLayout>(R.id.theme_mode)
        val switch = findViewById<Switch>(R.id.switch_theme_mode)

        themeMode.setOnClickListener {
            switch.isChecked = !switch.isChecked

            if (switch.isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        switch.setOnCheckedChangeListener { button, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

        }

        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        share.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_TEXT, getString(R.string.share_url))
                type = "text/plain"
            }
            startActivity(Intent.createChooser(intent, getString(R.string.chooser_title)))
        }

        support.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(R.string.email))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_title))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.support_message))
            }

            startActivity(Intent.createChooser(intent, getString(R.string.chooser_title)))
        }

        termsOfUse.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(getString(R.string.offer_url))
            }
            startActivity(intent)
        }
    }
}