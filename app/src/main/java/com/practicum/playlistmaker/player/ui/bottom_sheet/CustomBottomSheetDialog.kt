package com.practicum.playlistmaker.player.ui.bottom_sheet

import android.content.Context
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetDialog

class CustomBottomSheetDialog(context: Context) : BottomSheetDialog(context) {
    
    init {
        // Устанавливаем кастомный BottomSheetBehavior
        val behavior = ExpandableBottomSheetBehavior<View>()
        val bottomSheetContainer = findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        behavior.peekHeight = 100 // Устанавливаем высоту по умолчанию, можно изменить по вашему желанию
       // BottomSheetDialogUtils.setupCustomBottomSheetBehavior(bottomSheetContainer, behavior)
    }
}
