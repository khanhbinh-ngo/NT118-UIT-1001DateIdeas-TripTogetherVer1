package com.example.triptogether

import android.os.Bundle
import com.example.triptogether.databinding.ActivityImagePreviewBinding
import com.example.triptogether.ui.PlannerActivity

class ImagePreviewActivity : PlannerActivity() {

    private lateinit var binding: ActivityImagePreviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImagePreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageUrl = intent.getStringExtra("imageUrl")
        if (imageUrl != null) {
            com.example.triptogether.drive.DriveImages.load(
                binding.ivFullImage,
                imageUrl,
                R.drawable.ic_image_placeholder,
                circle = false,
                fit = true,
            )
        } else {
            finish()
        }

        binding.btnBack.setOnClickListener { finish() }
    }
}
