package com.example.triptogether

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.MimeTypeMap
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.triptogether.adapters.DocumentAdapter
import com.example.triptogether.adapters.ImageAdapter
import com.example.triptogether.databinding.ActivityDocumentsHomeBinding
import com.example.triptogether.ui.PlannerActivity
import com.example.triptogether.ui.state.FileUploadViewModel
import com.example.triptogether.ui.state.FilesViewModel
import com.example.triptogether.ui.state.UiState
import com.example.triptogether.ui.state.observe
import com.example.triptogether.utilities.openSharedDocument

class DocumentsHomeActivity : PlannerActivity() {
    private lateinit var binding: ActivityDocumentsHomeBinding
    private lateinit var model: FilesViewModel
    private lateinit var uploadModel: FileUploadViewModel
    private var tripId = ""
    private var creator = ""
    private val documents = DocumentAdapter(onOpen = { openSharedDocument(it) })
    private val images = ImageAdapter { url ->
        startActivity(Intent(this, ImagePreviewActivity::class.java).putExtra("imageUrl", url))
    }
    private val picker =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null) {
                runCatching {
                    contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION,
                    )
                }
                val mime = contentResolver.getType(uri).orEmpty()
                uploadModel.upload(
                    uri,
                    mime,
                    MimeTypeMap.getSingleton().getExtensionFromMimeType(mime) ?: "file",
                    creator,
                    tripId,
                )
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDocumentsHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        tripId = intent.getStringExtra("tripId").orEmpty()
        creator = intent.getStringExtra("createdBy").orEmpty()
        if (tripId.isBlank() || creator.isBlank()) {
            finish()
            return
        }
        model = ViewModelProvider(this)[FilesViewModel::class.java]
        uploadModel = ViewModelProvider(this)[FileUploadViewModel::class.java]
        model.photos = savedInstanceState?.getBoolean("photos") ?: model.photos
        binding.btnFileTab.setOnClickListener { selectTab(false) }
        binding.btnImagesTab.setOnClickListener { selectTab(true) }
        binding.btnUploadFile.setOnClickListener {
            withDriveAccess {
                picker.launch(arrayOf(if (model.photos) "image/*" else "application/pdf"))
            }
        }
        model.items.observe(this) { state ->
            when (state) {
                UiState.Loading -> showState(loading = true)
                UiState.Error ->
                    showState(
                        message = getString(R.string.load_error),
                        retry = { selectTab(model.photos) },
                    )
                is UiState.Content -> {
                    if (model.photos) images.submitList(state.data)
                    else documents.submitList(state.data)
                    showState(
                        message = if (state.data.isEmpty()) getString(R.string.no_files) else null
                    )
                }
            }
        }
        uploadModel.state.observe(this) { state ->
            val pending = state is FileUploadViewModel.State.Uploading
            binding.btnUploadFile.isEnabled = !pending
            binding.uploadProgress.visibility = if (pending) View.VISIBLE else View.GONE
            binding.uploadStatus.visibility = if (pending) View.VISIBLE else View.GONE
            if (state is FileUploadViewModel.State.Uploading) {
                binding.uploadProgress.progress = state.percent
                binding.uploadStatus.text = getString(R.string.upload_percent, state.percent)
            }
            when (state) {
                is FileUploadViewModel.State.Saved -> {
                    uploadModel.acknowledge()
                    selectTab(state.image)
                }
                is FileUploadViewModel.State.Failed -> {
                    toast(state.message)
                    uploadModel.acknowledge()
                }
                else -> Unit
            }
        }
    }

    private fun selectTab(photos: Boolean) {
        binding.btnFileTab.isSelected = !photos
        binding.btnImagesTab.isSelected = photos
        binding.rvSharedFiles.layoutManager =
            if (photos) GridLayoutManager(this, 2) else LinearLayoutManager(this)
        binding.rvSharedFiles.adapter = if (photos) images else documents
        model.load(tripId, photos)
    }

    override fun onStart() {
        super.onStart()
        if (::model.isInitialized) selectTab(model.photos)
    }

    override fun onStop() {
        if (::model.isInitialized) model.stop()
        super.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (::model.isInitialized) outState.putBoolean("photos", model.photos)
        super.onSaveInstanceState(outState)
    }
}
