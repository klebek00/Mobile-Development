package com.example.watertracker.history

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.watertracker.R
import com.example.watertracker.history.adapters.RecordAdapter
import com.example.watertracker.model.HistoryData
import com.example.watertracker.repository.HistoryRepository
import com.example.watertracker.repository.WaterRepository
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class DayFragment : Fragment() {

    private lateinit var totalTextView: TextView
    private lateinit var goal: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var recordAdapter: RecordAdapter
    private var historyList: List<HistoryData> = listOf()
    private var todayHistory: List<HistoryData> = listOf()
    private lateinit var historyRepository: HistoryRepository
    private lateinit var waterRepository: WaterRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        waterRepository = WaterRepository(requireContext())

        val userData = waterRepository.loadUserData()

        val view = inflater.inflate(R.layout.fragment_day, container, false)

        historyRepository = HistoryRepository(requireContext())  // Инициализация репозитория

        totalTextView = view.findViewById(R.id.textViewTotal) // Тотал для объема
        recyclerView = view.findViewById(R.id.recordsRecyclerView) // RecyclerView для истории
        goal = view.findViewById(R.id.textViewGoal)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recordAdapter = RecordAdapter(todayHistory)
        recyclerView.adapter = recordAdapter

        historyRepository.loadUserData { data ->
            if (data != null) {
                historyList = data
                todayHistory = historyRepository.getTodayHistory(historyList)

                val totalTodayIntake = todayHistory.sumOf { it.amount }
                val formattedTotal = String.format("%.2f", totalTodayIntake) // Округляем до 2 знаков

                Log.d("WaterIntake", "Сегодня выпито: $formattedTotal L")
                totalTextView.text = "$formattedTotal L"

                todayHistory = todayHistory.sortedBy { it.date } // Сортировка по возрастанию

                recordAdapter.updateData(todayHistory)
            } else {
                Log.e("DayFragment", "Не удалось получить данные пользователя")
            }
        }
        if (userData != null) {
            var dailyWaterIntake = userData.dailyWaterIntake ?: 0.0
            val formattedCounter = String.format("%.2f", dailyWaterIntake)
            goal.text = "$formattedCounter L"
        }


        return view
    }


    companion object {
        @JvmStatic
        fun newInstance() = DayFragment()
    }
}
