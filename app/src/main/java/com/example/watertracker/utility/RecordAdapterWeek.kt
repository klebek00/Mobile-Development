package com.example.watertracker.utility

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.watertracker.R
import com.example.watertracker.history.adapters.RecordAdapter

class RecordAdapterWeek(private var records: List<Pair<String, Double>>) :
    RecyclerView.Adapter<RecordAdapterWeek.RecordViewHolder>() {

    inner class RecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTextView: TextView = itemView.findViewById(R.id.textViewTime)  // Теперь отображает дату
        val volumeTextView: TextView = itemView.findViewById(R.id.textViewVolume)  // Отображает объем воды
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_record, parent, false)
        return RecordViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        val (date, amount) = records[position]
        val formattedCounter = String.format("%.2f", amount)
        holder.dateTextView.text = date  // Отображаем название дня (например, "Понедельник")
        holder.volumeTextView.text = "$formattedCounter L"  // Отображаем количество выпитой воды
    }

    override fun getItemCount(): Int {
        return records.size
    }

    fun updateData(newRecords: List<Pair<String, Double>>) {
        records = newRecords
        notifyDataSetChanged()
    }
}