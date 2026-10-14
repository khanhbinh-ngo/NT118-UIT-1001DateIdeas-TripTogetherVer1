package com.example.triptogether

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.example.triptogether.ui.PlannerActivity

class HelpSupportActivity : PlannerActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help_support)
        val content = findViewById<LinearLayout>(R.id.faqContent)
        listOf(
                R.string.faq_trip to R.string.faq_trip_answer,
                R.string.faq_invite to R.string.faq_invite_answer,
                R.string.faq_files to R.string.faq_files_answer,
                R.string.faq_reset to R.string.faq_reset_answer,
                R.string.faq_help to R.string.faq_help_answer,
            )
            .forEachIndexed { index, (question, answer) ->
                val title =
                    TextView(this).apply {
                        setTextAppearance(R.style.TextAppearance_Trip_Subtitle)
                        text = getString(question)
                        minHeight = (48 * resources.displayMetrics.density).toInt()
                        gravity = android.view.Gravity.CENTER_VERTICAL
                        isClickable = true
                        isFocusable = true
                    }
                val body =
                    TextView(this).apply {
                        setTextAppearance(R.style.TextAppearance_Trip_Body)
                        text = getString(answer)
                        visibility =
                            if (savedInstanceState?.getBoolean("faq$index") == true) View.VISIBLE
                            else View.GONE
                        tag = "faq$index"
                    }
                title.setOnClickListener {
                    body.visibility =
                        if (body.visibility == View.VISIBLE) View.GONE else View.VISIBLE
                }
                content.addView(title)
                content.addView(body)
            }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val content = findViewById<LinearLayout>(R.id.faqContent)
        for (i in 0 until content.childCount) {
            val child = content.getChildAt(i)
            (child.tag as? String)?.let {
                outState.putBoolean(it, child.visibility == View.VISIBLE)
            }
        }
    }
}
