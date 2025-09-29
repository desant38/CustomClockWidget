package ysg.home.cstmclckwdgt // Твой пакет

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RemoteViews
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ClockWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle?
    ) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
        updateAppWidget(context, appWidgetManager, appWidgetId)
    }

    // НОВОЕ: Эта функция вызывается, когда на экран добавляют САМЫЙ ПЕРВЫЙ виджет
    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        // "Нанимаем дворецкого" - запускаем наш сервис
        context.startService(Intent(context, WidgetUpdateService::class.java))
    }

    // НОВОЕ: Эта функция вызывается, когда с экрана удаляют САМЫЙ ПОСЛЕДНИЙ виджет
    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        // "Увольняем дворецкого" - останавливаем наш сервис
        context.stopService(Intent(context, WidgetUpdateService::class.java))
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val options = appWidgetManager.getAppWidgetOptions(appWidgetId)
        val minHeight = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 90)
        val minWidth = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 90)

        val views = RemoteViews(context.packageName, R.layout.widget_layout)

        // ИЗМЕНЕНИЕ: Убрали секунды из формата времени, чтоб не садить батарею и не просить пермита у системы
        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

        if (minHeight < 90 || minWidth < 90) {
            // --- РЕЖИМ МАЛЕНЬКОГО ВИДЖЕТА ---
            val shortDateFormat = SimpleDateFormat("E, dd.MM", Locale.getDefault()).format(Date())
            val combinedText = "$shortDateFormat  $currentTime"

            views.setViewVisibility(R.id.widget_date, View.GONE)
            views.setTextViewText(R.id.widget_time, combinedText)
            views.setViewVisibility(R.id.widget_time, View.VISIBLE)
            views.setFloat(R.id.widget_time, "setTextSize", 12f)

        } else {
            // --- РЕЖИМ БОЛЬШОГО ВИДЖЕТА ---
            val longDateFormat = SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()).format(Date())

            views.setViewVisibility(R.id.widget_date, View.VISIBLE)
            views.setViewVisibility(R.id.widget_time, View.VISIBLE)
            views.setTextViewText(R.id.widget_date, longDateFormat)
            views.setTextViewText(R.id.widget_time, currentTime)
            views.setFloat(R.id.widget_date, "setTextSize", 16f)
            views.setFloat(R.id.widget_time, "setTextSize", 32f)
        }

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}