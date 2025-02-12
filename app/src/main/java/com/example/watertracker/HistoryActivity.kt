package com.example.watertracker

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.watertracker.databinding.ActivityHistoryBinding
import com.example.watertracker.history.DayFragment
import com.example.watertracker.history.MonthFragment
import com.example.watertracker.history.WeekFragment
import com.example.watertracker.utility.ViewpagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator

class HistoryActivity : AppCompatActivity() {
    private  lateinit var binding: ActivityHistoryBinding
    private lateinit var buttonToday: Button
    private lateinit var buttonHistory: Button
    private lateinit var buttonMe: Button
    private var fragList = listOf(
        DayFragment.newInstance(),
        WeekFragment.newInstance(),
        MonthFragment.newInstance()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val adapter = ViewpagerAdapter(this, fragList)
        binding.placeholder!!.adapter = adapter
        TabLayoutMediator(binding.history!!, binding.placeholder!!){
            tab, pos -> tab.text = resources.getStringArray(R.array.title)[pos]
        }.attach()

        buttonToday = findViewById(R.id.button11)
        buttonHistory = findViewById(R.id.button12)
        buttonMe = findViewById(R.id.button13)

        buttonToday.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            val options = ActivityOptions.makeCustomAnimation(this, 0, 0)
            startActivity(intent, options.toBundle())
            finish()

        }
        buttonMe.setOnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            val options = ActivityOptions.makeCustomAnimation(this, 0, 0)
            startActivity(intent, options.toBundle())
            finish()
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}