package com.practicum.playlistmaker.sharing.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.sharing.domain.api.IExternalNavigator
import com.practicum.playlistmaker.sharing.domain.models.EmailData

class ExternalNavigator(private val context: Context) : IExternalNavigator {
    
    private val chooserTitle = context.getText(R.string.chooser_title)
    private val supportTitle = context.getText(R.string.support_title)
    private val supportMessage = context.getText(R.string.support_message)
    
    override fun shareLink(shareAppLink: String) {
        val intent = Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, shareAppLink)
            type = "text/plain"
        }, chooserTitle)
        
        context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }
    
    override fun openLink(termsLink: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(termsLink)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
    
    override fun openEmail(supportEmail: EmailData) {
        
        val intent = Intent.createChooser(Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse(supportEmail.mailTo)
            putExtra(Intent.EXTRA_EMAIL, supportEmail.mail)
            putExtra(Intent.EXTRA_SUBJECT, supportTitle)
            putExtra(Intent.EXTRA_TEXT, supportMessage)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }, chooserTitle)
        
        context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }
}