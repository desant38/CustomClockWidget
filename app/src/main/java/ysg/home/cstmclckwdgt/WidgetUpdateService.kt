package ysg.home.cstmclckwdgt

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
        startForeground(NOTIFICATION_ID, notification)

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
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Канал сервиса виджета часов", // Это имя будет видно в настройках приложения
                NotificationManager.IMPORTANCE_LOW // Низкий приоритет, без звука
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }


    // --- НОВЫЙ КОД: Функция для создания самого уведомления ---
    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Виджет часов")
            .setContentText("Обновление времени активно")
            .setSmallIcon(R.mipmap.ic_launcher_round) // Важно! Нужна иконка
            .setOngoing(true) // Уведомление нельзя будет смахнуть
            .build()
    }
}




/** "Дворецкий" (Service).
    * Его задача — "оживлять" (включать и выключать) "стукача" TimeTickReceiver.
    * Это сердце нашего механизма. Он будет "жить" в фоне, только пока на экране есть хотя бы один наш виджет.


package ysg.home.cstmclckwdgt

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder

class WidgetUpdateService : Service() {

    // Создаём экземпляр нашего "стукача" или "посыльного"
    private val timeTickReceiver = TimeTickReceiver()

    override fun onCreate() {
        super.onCreate()
        // Когда сервис создаётся, он регистрирует "стукача",
        // чтобы тот начал слушать ежеминутные сигналы системы.
        registerReceiver(timeTickReceiver, IntentFilter(Intent.ACTION_TIME_TICK))
    }

    override fun onDestroy() {
        super.onDestroy()
        // Когда сервис уничтожается, он "увольняет" стукача,
        // чтобы перестать слушать сигналы и экономить батарею.
        unregisterReceiver(timeTickReceiver)
    }

    // Этот метод нам не нужен для такого простого сервиса, просто оставляем.
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}

 * Что он делает: Очень просто — при своём создании
 * он начинает слушать ежеминутный "стук в бубен от стукача, на которого мы подписались" (регистрирует
 * системный сигнал ACTION_TIME_TICK), а при уничтожении — перестаёт.
 * */