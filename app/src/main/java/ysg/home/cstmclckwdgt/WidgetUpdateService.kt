package ysg.home.cstmclckwdgt

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class WidgetUpdateService : Service() {

    private var timeReceiver: TimeTickReceiver? = null

    // --- НОВЫЙ КОД: ID для канала и уведомления ---
    companion object {
        private const val CHANNEL_ID = "ClockWidgetServiceChannel"
        private const val NOTIFICATION_ID = 1
    }

    override fun onCreate() {
        super.onCreate()

        // --- НОВЫЙ КОД: Создаем канал и уведомление при создании сервиса ---
        createNotificationChannel()
        val notification = createNotification()

        // --- ГЛАВНОЕ ИЗМЕНЕНИЕ: Запускаем сервис в приоритетном режиме ---
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // UPSIDE_DOWN_CAKE это Android 14
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC // <-- Вот оно, подтверждение типа
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }


        // Регистрируем наш "стукач", как и раньше
        timeReceiver = TimeTickReceiver()
        val filter = IntentFilter(Intent.ACTION_TIME_TICK)
        registerReceiver(timeReceiver, filter)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Сервис будет перезапущен, если система его убьет
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        // Увольняем "стукача", если сервис уничтожается
        if (timeReceiver != null) {
            unregisterReceiver(timeReceiver)
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    // --- НОВЫЙ КОД: Функция для создания канала уведомлений ---
    private fun createNotificationChannel() {
        // Проверяем, является ли версия Android 8.0 (Oreo) или выше
        val serviceChannel = NotificationChannel(
            CHANNEL_ID,
            "Канал сервиса виджета часов", // Это имя будет видно в настройках приложения
            NotificationManager.IMPORTANCE_LOW // Низкий приоритет, без звука
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(serviceChannel)
    }


    // --- НОВЫЙ КОД: Функция для создания самого уведомления ---
    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Виджет часов")
            .setContentText("Обновление времени активно")
            .setSmallIcon(android.R.drawable.ic_menu_recent_history) // Важно! Нужна иконка
            .setOngoing(true) // Уведомление нельзя будет смахнуть
            .build()
    }
}
