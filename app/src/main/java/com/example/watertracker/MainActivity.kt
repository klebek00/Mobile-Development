package com.example.watertracker

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

class MainActivity : AppCompatActivity() {
    private var progress = 0
    private lateinit var button: Button
    private lateinit var buttonToday: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var textView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        button = findViewById(R.id.button7)
        buttonToday = findViewById(R.id.button11)

        progressBar = findViewById(R.id.progress_bar)
        textView = findViewById(R.id.text_view_progress)
        textView.text = progress.toString()
        textView.text = "${progress}%"

        button.setOnClickListener{
            if (progress <= 90) {
                progress += 10
                updateProgressBar()
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    }

    private fun updateProgressBar() {
        progressBar.progress = progress
        textView.text = progress.toString()
        textView.text = "${progress}%"

    }
}