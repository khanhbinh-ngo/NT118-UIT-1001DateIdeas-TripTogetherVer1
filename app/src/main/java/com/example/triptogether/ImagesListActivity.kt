package com.example.triptogether

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.triptogether.adapters.ImageAdapter
import com.example.triptogether.databinding.ActivityImagesListBinding
import com.example.triptogether.ui.PlannerActivity
import com.example.triptogether.ui.state.FilesViewModel
import com.example.triptogether.ui.state.UiState
import com.example.triptogether.ui.state.observe

class ImagesListActivity : PlannerActivity() {
    private lateinit var model: FilesViewModel
    private var tripId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityImagesListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        tripId = intent.getStringExtra("tripId").orEmpty()
        if (tripId.isBlank()) {
            finish()
            return
        }
        model = ViewModelProvider(this)[FilesViewModel::class.java]
        val adapter = ImageAdapter { url ->
            startActivity(Intent(this, ImagePreviewActivity::class.java).putExtra("imageUrl", url))
        }
        binding.rvImages.layoutManager = GridLayoutManager(this, 2)
        binding.rvImages.adapter = adapter
        model.items.observe(this) { state ->
            when (state) {
                UiState.Loading -> showState(loading = true)
                UiState.Error ->
                    showState(
                        message = getString(R.string.load_error),
                        retry = { model.load(tripId, true) },
                    )
                is UiState.Content -> {
                    adapter.submitList(state.data)
                    showState(
                        message = if (state.data.isEmpty()) getString(R.string.no_files) else null
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (::model.isInitialized) model.load(tripId, true)
    }

    override fun onStop() {
        if (::model.isInitialized) model.stop()
        super.onStop()
    }
}
