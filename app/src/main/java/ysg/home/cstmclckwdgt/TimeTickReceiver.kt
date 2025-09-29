package ysg.home.cstmclckwdgt

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent

/**
*   Создаём "Стукача" или "Посыльного" (BroadcastReceiver)
*   Возвращаемся к нашему плану по ежеминутному обновлению.
*   Первым делом создадим "стукача", который будет слушать системный сигнал.
*/

class TimeTickReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        // Проверяем, что мы получили именно тот сигнал, который ждали (раз в минуту)
        if (intent?.action == Intent.ACTION_TIME_TICK) {

            // Получаем список всех наших виджетов, которые есть на экране
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, ClockWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)

            // Создаём "поручение" на обновление для нашего основного класса
            val updateIntent = Intent(context, ClockWidgetProvider::class.java).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
            }

            // Отправляем это "поручение", чтобы ClockWidgetProvider его получил и обновил виджеты
            context.sendBroadcast(updateIntent)
        }
    }
}