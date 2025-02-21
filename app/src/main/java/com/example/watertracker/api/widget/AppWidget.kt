package com.example.watertracker.api.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.watertracker.R
import com.example.watertracker.api.widget.AppWidget.Companion.ACTION_UPDATE_INCREMENT_WIDGET
import com.example.watertracker.model.HistoryData
import com.example.watertracker.repository.HistoryRepository
import com.example.watertracker.repository.WaterRepository


class AppWidget : AppWidgetProvider() {
    companion object {
        const val ACTION_UPDATE_WIDGET = "com.example.watertracker.UPDATE_WIDGET"
        const val ACTION_UPDATE_INCREMENT_WIDGET = "com.example.watertracker.UPDATE_INCREMENT_WIDGET"
    }


    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        if (context != null && intent?.action == ACTION_UPDATE_WIDGET) {



            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = context.packageName.let {
                android.content.ComponentName(it, AppWidget::class.java.name)
            }
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
            for (appWidgetId in appWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId)
            }
        }
        if (context != null && intent?.action == ACTION_UPDATE_INCREMENT_WIDGET) {

            val waterRepository = WaterRepository(context)
            val historyRepository = HistoryRepository(context)

            var counter = waterRepository.loadUserData()?.counter ?: 0.0
            counter += 0.2

            waterRepository.updateCounter(counter)

            val date = System.currentTimeMillis()
            val historyData = HistoryData(0.2, date)
            historyRepository.saveUserData(historyData)

            val appUpdateIntent = Intent("com.example.watertracker.ACTION_UPDATE_APP")
            context.sendBroadcast(appUpdateIntent)

            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = context.packageName.let {
                android.content.ComponentName(it, AppWidget::class.java.name)
            }
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
            for (appWidgetId in appWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId)
            }
        }
    }
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

}

internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
    val waterRepository = WaterRepository(context)

    val counter = waterRepository.loadUserData()?.counter ?: 0.0
    val formattedCounter = String.format("%.2f", counter)
    val widgetText = "$formattedCounter L"
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.app_widget)
    views.setTextViewText(R.id.appwidget_text, widgetText)

    val incrementIntent = Intent(context, AppWidget::class.java).apply{
        action = ACTION_UPDATE_INCREMENT_WIDGET
    }

    val incrementPendingIntent = PendingIntent.getBroadcast(
        context,
        0,
        incrementIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE


    )
    views.setOnClickPendingIntent(R.id.increment_button, incrementPendingIntent)

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}