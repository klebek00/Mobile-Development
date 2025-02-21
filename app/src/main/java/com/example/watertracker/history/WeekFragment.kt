package com.example.watertracker.history

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.watertracker.R
import com.example.watertracker.history.adapters.RecordAdapter
import com.example.watertracker.model.HistoryData
import com.example.watertracker.repository.HistoryRepository
import com.example.watertracker.repository.WaterRepository
import com.example.watertracker.utility.RecordAdapterWeek


class WeekFragment : Fragment() {
    private lateinit var totalTextView: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var recordAdapter: RecordAdapterWeek
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

        val view = inflater.inflate(R.layout.fragment_week, container, false)

        historyRepository = HistoryRepository(requireContext())  // Инициализация репозитория

        totalTextView = view.findViewById(R.id.textViewTotal) // Тотал для объема
        recyclerView = view.findViewById(R.id.recordsRecyclerView) // RecyclerView для истории

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recordAdapter = RecordAdapterWeek(emptyList()) // Инициализируем адаптер пустым списком
        recyclerView.adapter = recordAdapter

        historyRepository.loadUserData { data ->
            if (data != null) {
                historyRepository.getWeekData { weekData ->
                    val dataList = weekData.toList() // Преобразуем Map в List<Pair<String, Double>>
                    recordAdapter.updateData(dataList) // Передаем список в адаптер

                    // Вычисляем общее количество выпитой воды за неделю
                    val totalWeekIntake = weekData.values.sum()
                    val formattedTotal = String.format("%.2f", totalWeekIntake) // Округляем до 2 знаков

                    Log.d("WaterIntake", "За неделю выпито: $formattedTotal L")
                    totalTextView.text = "$formattedTotal L"
                }
            } else {
                Log.e("WeekFragment", "Не удалось получить данные пользователя")
            }
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() = WeekFragment()
    }
}