package ysg.home.cstmclckwdgt

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    private lateinit var statusTextView: TextView
    private lateinit var grantButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        statusTextView = findViewById(R.id.TVgreeting)
        grantButton = findViewById(R.id.grantPermissionButton)
        val revisionTextView = findViewById<TextView>(R.id.TVrevision)

        revisionTextView.text = getString(R.string.revision, BuildConfig.APP_VERSION)

        grantButton.setOnClickListener {
            requestExactAlarmPermission()
        }
    }

    override fun onResume() {
        super.onResume()
        checkPermissionStatus()
    }

    private fun checkPermissionStatus() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                statusTextView.text = "Разрешение предоставлено. Теперь можно добавить виджет на главный экран."
                grantButton.visibility = View.GONE
            } else {
                statusTextView.text = "Для ежеминутных обновлений виджету нужно специальное разрешение. Нажмите кнопку ниже, чтобы его предоставить."
                grantButton.visibility = View.VISIBLE
            }
        } else {
            statusTextView.text = "На вашей версии Android все готово. Можно добавлять виджет на главный экран."
            grantButton.visibility = View.GONE
        }
    }

    private fun requestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                data = Uri.parse("package:$packageName")
            }
            startActivity(intent)
        }
    }
}