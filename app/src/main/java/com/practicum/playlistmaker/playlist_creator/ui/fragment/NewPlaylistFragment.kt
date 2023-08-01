package com.practicum.playlistmaker.playlist_creator.ui.fragment

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.core.utils.setImage
import com.practicum.playlistmaker.core.utils.viewBinding
import com.practicum.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.practicum.playlistmaker.playlist_creator.domain.models.PermissionResultState
import com.practicum.playlistmaker.playlist_creator.ui.models.CreateBtnState
import com.practicum.playlistmaker.playlist_creator.ui.models.ScreenState
import com.practicum.playlistmaker.playlist_creator.ui.view_model.NewPlaylistViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class NewPlaylistFragment : Fragment(R.layout.fragment_new_playlist) {
    
    private val binding by viewBinding<FragmentNewPlaylistBinding>()
    private val viewModel by viewModel<NewPlaylistViewModel>()
    
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        return super.onCreateView(inflater, container, savedInstanceState)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        initPickMediaRegister()
        initObserver()
        initListeners()
        
    }
    
    private fun initPickMediaRegister() {
        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                
                val cornerRadius =
                    requireContext().resources.getDimensionPixelSize(R.dimen.corner_radius_8dp)
                
                binding.playlistCoverImage.setImage(uri, cornerRadius)
                saveImageToPrivateStorage(uri)
            }
        }
    }
    
    private fun initObserver() {
        lifecycleScope.launch {
            viewModel.screenStateFlow.collect { state ->
                when (state) {
                    is ScreenState.AllowedToGoOut -> goBack()
                    is ScreenState.Empty, is ScreenState.HasContent -> renderCreateBtn(state.createBtnState)
                    is ScreenState.NeedsToAsk -> showDialog()
                }
            }
        }
        
        lifecycleScope.launch {
            viewModel.permissionStateFlow.collect { state ->
                when (state) {
                    
                    PermissionResultState.NEEDS_RATIONALE -> {
                        
                        Toast
                            .makeText(
                                requireContext(),
                                getString(R.string.rationale_permission_message),
                                Toast.LENGTH_SHORT
                            )
                            .show()
                    }
                    
                    PermissionResultState.GRANTED -> {
                        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }
    
                    PermissionResultState.DENIED_PERMANENTLY -> {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.data = Uri.fromParts("package", requireContext().packageName, null)
                        requireContext().startActivity(intent)
                    }
                }
            }
        }
    }
    
    private fun initListeners() {
        
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    viewModel.onBackPressed()
                }
            })
        
        binding.apply {
            
            navigationToolbar.setNavigationOnClickListener {
                viewModel.onBackPressed()
            }
            
            playlistCoverImage.setOnClickListener {
                viewModel.onPlaylistCoverClicked()
            }
            
            playlistName.doOnTextChanged { text, _, _, _ ->
    
                renderBoxStrokeEditTextColor(binding.playlistNameContainer, text)
                viewModel.onPlaylistNameChanged(text.toString())
                
            }
            
            playlistDescription.doOnTextChanged { text, _, _, _ ->
                renderBoxStrokeEditTextColor(binding.playlistDescriptionContainer, text)
                viewModel.onPlaylistDescriptionChanged(text.toString())
            }
            
            buttonCreate.setOnClickListener {
                viewModel.onCreateBtnClicked()
                showAndroidXSnackbar(playlistName.text.toString())
            }
        }
    }
    
    private fun goBack() {
        findNavController().navigateUp()
    }
    
    private fun renderCreateBtn(state: CreateBtnState) {
        when (state) {
            CreateBtnState.ENABLED -> binding.buttonCreate.isEnabled = true
            CreateBtnState.DISABLED -> binding.buttonCreate.isEnabled = false
        }
    }
    
    private fun renderBoxStrokeEditTextColor(view: TextInputLayout, text: CharSequence?) {
        if (!text.isNullOrEmpty()) {
            view.defaultHintTextColor = ContextCompat.getColorStateList(
                requireContext(),
                R.color.new_playlist_edittext_blue
            )
            ContextCompat
                .getColorStateList(requireContext(), R.color.new_playlist_edittext_blue)
                ?.let { view.setBoxStrokeColorStateList(it) }
        }
        
        else {
            view.defaultHintTextColor = ContextCompat.getColorStateList(
                requireContext(),
                R.color.new_playlist_edittext_color
            )
            ContextCompat
                .getColorStateList(requireContext(), R.color.new_playlist_edittext_color)
                ?.let { view.setBoxStrokeColorStateList(it) }
        }
    }
    
    private fun showDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.title_playlist_dialog))
            .setMessage(getString(R.string.message_playlist_dialog))
            .setNeutralButton(getString(R.string.cancel)) { _, _ -> }
            .setPositiveButton(getString(R.string.complete)) { _, _ -> goBack() }
            .show()
    }
    
    private fun saveImageToPrivateStorage(uri: Uri) {
        val filePath = File(
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            getString(R.string.my_playlists)
        )
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val file = File(filePath, uri.lastPathSegment ?: "image")
        val inputStream = requireActivity().contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, QUALITY_IMAGE, outputStream)
        
        viewModel.saveImageUri(file.toURI())
    }
    
    private fun showAndroidXSnackbar(playlistName: String) {
        val message =
            getString(R.string.playlist) + " \"" + playlistName + "\" " + getString(R.string.created)
        Snackbar
            .make(requireContext(), binding.containerLayout, message, Snackbar.LENGTH_SHORT)
            .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.blue))
            .setTextColor(ContextCompat.getColor(requireContext(), R.color.deep_white))
            .setDuration(MESSAGE_DURATION)
            .show()
    }
    
    companion object {
        private const val QUALITY_IMAGE = 30
        private const val MESSAGE_DURATION = 4000
    }
    
}