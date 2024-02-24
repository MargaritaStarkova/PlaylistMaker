package com.practicum.playlistmaker.player.ui.view

import android.animation.ValueAnimator
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
import com.practicum.playlistmaker.player.ui.models.PlayStatus

class PlaybackButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0,
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private var imageAlpha: Int = MIN_ALPHA
    private val paint = Paint()
    private var currentImageBitmap: Bitmap? = null
    private var playImageBitmap: Bitmap? = null
    private var pauseImageBitmap: Bitmap? = null
    private var imageRect: RectF = RectF(0f, 0f, 0f, 0f)

    init {
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
        imageRect = RectF(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat())
    }

    override fun onDraw(canvas: Canvas) {
        paint.alpha = imageAlpha
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
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    fun refreshImage(status: PlayStatus) {
        when (status) {
            is PlayStatus.Playing -> currentImageBitmap = pauseImageBitmap
            is PlayStatus.Ready -> animateImageVisibility()
            else -> currentImageBitmap = playImageBitmap
        }

        invalidate()
    }

    private fun animateImageVisibility() {
        val animator = ValueAnimator.ofInt(MIN_ALPHA, MAX_ALPHA)
        animator.duration = DURATION_ANIMATION_MILLIS
        animator.addUpdateListener { animation ->
            imageAlpha = animation.animatedValue as Int
            invalidate()
        }
        animator.start()
    }

    companion object {
        private const val MIN_ALPHA = 120
        private const val MAX_ALPHA = 255
        private const val DURATION_ANIMATION_MILLIS = 500L
    }
}
