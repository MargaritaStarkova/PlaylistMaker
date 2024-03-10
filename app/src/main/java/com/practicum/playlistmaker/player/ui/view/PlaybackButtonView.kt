package com.practicum.playlistmaker.player.ui.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.graphics.drawable.toBitmap
import com.practicum.playlistmaker.R

class PlaybackButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0,
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private val paint = Paint()
    private var currentImageBitmap: Bitmap? = null
    private var playImageBitmap: Bitmap? = null
    private var pauseImageBitmap: Bitmap? = null
    private var imageRect: RectF = RectF(0f, 0f, 0f, 0f)

    init {
        isEnabled = false
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.PlaybackButtonView,
            defStyleAttr,
            defStyleRes,
        ).apply {
            try {
                playImageBitmap =
                    getDrawable(R.styleable.PlaybackButtonView_playImageResId)?.toBitmap()
                pauseImageBitmap =
                    getDrawable(R.styleable.PlaybackButtonView_pauseImageResId)?.toBitmap()

                currentImageBitmap = playImageBitmap

            } finally {
                recycle()
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        imageRect.set(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat())
    }

    override fun onDraw(canvas: Canvas) {
        paint.alpha = if (!isEnabled) MIN_ALPHA else MAX_ALPHA
        currentImageBitmap?.let { canvas.drawBitmap(it, null, imageRect, paint) }
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                return true
            }

            MotionEvent.ACTION_UP -> {
                performClick()
                if (!isEnabled) return false
                changeImage()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    fun setInitStatus() {
        isEnabled = true
        currentImageBitmap = playImageBitmap
        invalidate()
    }

    private fun changeImage() {
        currentImageBitmap = when (currentImageBitmap) {
            playImageBitmap -> pauseImageBitmap
            else -> playImageBitmap
        }
        invalidate()
    }

    companion object {
        private const val MIN_ALPHA = 120
        private const val MAX_ALPHA = 255
    }
}
