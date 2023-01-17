package com.practicum.playlistmaker

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val share = findViewById<FrameLayout>(R.id.share)
        val support = findViewById<FrameLayout>(R.id.support)
        val termsOfUse = findViewById<FrameLayout>(R.id.terms_of_use)
        val backButton = findViewById<ImageView>(R.id.backIcon)

        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        share.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_url))
            intent.type = "text/plain"
            startActivity(Intent.createChooser(intent, getString(R.string.chooser_title)))
        }

        support.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:")
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("margo.ivi@yandex.ru"))
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_title))
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.support_message))

            startActivity(Intent.createChooser(intent, getString(R.string.chooser_title)))
        }

        termsOfUse.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(getString(R.string.offer_url))
            startActivity(intent)
        }




    }
}