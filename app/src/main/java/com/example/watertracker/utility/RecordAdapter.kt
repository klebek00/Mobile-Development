package com.example.watertracker.history.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.watertracker.R
import com.example.watertracker.model.HistoryData

class RecordAdapter(private var records: List<HistoryData>) : RecyclerView.Adapter<RecordAdapter.RecordViewHolder>() {

    // Внутренний класс для ViewHolder
    inner class RecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val volumeTextView: TextView = itemView.findViewById(R.id.textViewVolume)
        val timeTextView: TextView = itemView.findViewById(R.id.textViewTime)
    }

    // Метод для создания нового элемента в RecyclerView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_record, parent, false)
        return RecordViewHolder(view)
    }

    // Метод для связывания данных с ViewHolder
    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        val record = records[position]

        // Преобразуем дату в удобочитаемый формат
        val time = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
            .format(java.util.Date(record.date))

        holder.timeTextView.text = time
        holder.volumeTextView.text = "${record.amount} L"
    }

    // Метод для получения количества элементов
    override fun getItemCount(): Int {
        return records.size
    }

    // Метод для обновления данных адаптера
    fun updateData(newRecords: List<HistoryData>) {
        records = newRecords
        notifyDataSetChanged()
    }
}
