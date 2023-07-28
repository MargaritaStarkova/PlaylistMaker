package com.practicum.playlistmaker.library.ui.bottom_sheet

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.R.id.design_bottom_sheet
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.core.utils.viewBinding
import com.practicum.playlistmaker.databinding.BottomSheetBinding
import com.practicum.playlistmaker.library.ui.models.ScreenState
import com.practicum.playlistmaker.library.ui.view_model.PlaylistsViewModel
import com.practicum.playlistmaker.playlist_creator.domain.models.PlaylistModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val TAG = "MyLog"

class BottomSheet : BottomSheetDialogFragment(R.layout.bottom_sheet) {
    
    private val binding by viewBinding<BottomSheetBinding>()
    private val viewModel by viewModel<PlaylistsViewModel>()
    
    private var job: Job? = null
    private lateinit var playlistsAdapter: BottomSheetAdapter
    
    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: ")
        
         setupRatio(requireContext(), dialog as BottomSheetDialog, 100)
    }
    
    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "onAttach: ")
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: ")
    }
    
    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        Log.d(TAG, "onViewStateRestored: ")
    }
    
    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
    }
    
    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: ")
    }
    
    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: ")
    }
    
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(TAG, "onSaveInstanceState: ")
    }
    
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView: ")
        return super.onCreateView(inflater, container, savedInstanceState)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: ")
        
        initAdapter()
        
        binding.createPlaylistBtn.setOnClickListener {
            findNavController().navigate(
                R.id.action_bottomSheet_to_newPlaylistFragment
            )
        }
        
        job = lifecycleScope.launch {
            viewModel.contentFlow.collect { screenState ->
                render(screenState)
                
            }
        }
    }
    
    override fun onDestroyView() {
        Log.d(TAG, "onDestroyView: ")
        super.onDestroyView()
        job?.cancel()
    }
    
    private fun initAdapter() {
        playlistsAdapter = BottomSheetAdapter { playlist ->
            
            Toast
                .makeText(requireContext(), "Clicked", Toast.LENGTH_SHORT)
                .show()
            
        }
        binding.playlistsRecycler.adapter = playlistsAdapter
    }
    
    private fun render(state: ScreenState) {
        when (state) {
            is ScreenState.Content -> showContent(state.content)
            ScreenState.Empty -> showContent(emptyList())
        }
    }
    
    private fun showContent(content: List<PlaylistModel>) {
        binding.playlistsRecycler.visibility = View.VISIBLE
        playlistsAdapter.apply {
            list.clear()
            list.addAll(content)
            notifyDataSetChanged()
            
        }
    
        Log.d("MyLog", playlistsAdapter.list.joinToString())
    }
    
    private fun setupRatio(context: Context, bottomSheetDialog: BottomSheetDialog, percetage: Int) {
        
        val bottomSheet = bottomSheetDialog.findViewById<View>(design_bottom_sheet) as FrameLayout
        val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet)
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = getBottomSheetDialogDefaultHeight(context, percetage)
        bottomSheet.layoutParams = layoutParams
        behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        
        /*       val bottomSheetContainer = binding.bottomSheetLayout
          
              val bottomSheetBehavior = BottomSheetBehavior
                  .from(bottomSheetContainer).apply {
                      state = BottomSheetBehavior.STATE_COLLAPSED
                  } */
    }
    
    private fun getBottomSheetDialogDefaultHeight(context: Context, percetage: Int): Int {
        return getWindowHeight(context) * percetage / 100
    }
    
    private fun getWindowHeight(context: Context): Int {
        // Calculate window height for fullscreen use
        val displayMetrics = DisplayMetrics()
        (context as Activity?)!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }
}