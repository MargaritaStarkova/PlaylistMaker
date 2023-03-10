package com.practicum.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backButton = findViewById<androidx.appcompat.widget.Toolbar>(R.id.back_icon)
        val share = findViewById<FrameLayout>(R.id.share)
        val support = findViewById<FrameLayout>(R.id.support)
        val termsOfUse = findViewById<FrameLayout>(R.id.terms_of_use)
        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)

        themeSwitcher.isChecked = getSharedPreferences(PREFERENCES, MODE_PRIVATE)
            .getBoolean(SWITCH_THEME_KEY, false)

        themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            (applicationContext as App).switchTheme(isChecked)
            (applicationContext as App).saveTheme(isChecked)
        }

        backButton.setNavigationOnClickListener {
            finish()
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