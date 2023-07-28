package com.practicum.playlistmaker.player.ui.bottom_sheet

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior

class ExpandableBottomSheetBehavior<V : View> : BottomSheetBehavior<V> {
    constructor() : super()
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    
    override fun onLayoutChild(parent: CoordinatorLayout, child: V, layoutDirection: Int): Boolean {
        val superReturnValue = super.onLayoutChild(parent, child, layoutDirection)
        if (state == STATE_EXPANDED) {
            // Растягиваем на весь экран при создании
            parent.post { setState(STATE_EXPANDED) }
        }
        return superReturnValue
    }
}
