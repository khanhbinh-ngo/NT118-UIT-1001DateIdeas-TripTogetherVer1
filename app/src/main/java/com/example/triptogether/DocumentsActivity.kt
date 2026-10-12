package com.example.triptogether

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.triptogether.adapters.DocumentAdapter
import com.example.triptogether.databinding.ActivityDocumentsBinding
import com.example.triptogether.ui.PlannerActivity
import com.example.triptogether.ui.state.FilesViewModel
import com.example.triptogether.ui.state.UiState
import com.example.triptogether.ui.state.observe
import com.example.triptogether.utilities.openSharedDocument

class DocumentsActivity : PlannerActivity() {
    private lateinit var model: FilesViewModel
    private var tripId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityDocumentsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        tripId = intent.getStringExtra("tripId").orEmpty()
        if (tripId.isBlank()) {
            finish()
            return
        }
        model = ViewModelProvider(this)[FilesViewModel::class.java]
        val adapter = DocumentAdapter(onOpen = { openSharedDocument(it) })
        binding.rvDocuments.layoutManager = LinearLayoutManager(this)
        binding.rvDocuments.adapter = adapter
        model.items.observe(this) { state ->
            when (state) {
                UiState.Loading -> showState(loading = true)
                UiState.Error ->
                    showState(
                        message = getString(R.string.load_error),
                        retry = { model.load(tripId, false) },
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
        if (::model.isInitialized) model.load(tripId, false)
    }

    override fun onStop() {
        if (::model.isInitialized) model.stop()
        super.onStop()
    }
}
