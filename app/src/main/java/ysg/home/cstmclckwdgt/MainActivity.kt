package ysg.home.cstmclckwdgt

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        /** --- НАЧАЛО НАШЕЙ ЛОГИКИ ---*/

        /** 1. Находим оба TextView на экране по их ID из activity_main.xml */
        val greetingTextView = findViewById<TextView>(R.id.TVgreeting)
        val revisionTextView = findViewById<TextView>(R.id.TVrevision)

        greetingTextView.text = getString(R.string.app_exclaimer)
        /** 2. Устанавливаем основной текст в первый TextView "greetingTextView" (о приложении и активити) */
        greetingTextView.text = getString(R.string.app_exclaimer)

        /** 3. Получаем версию из сгенерированного файла BuildConfig */
        val appVersionName = BuildConfig.APP_VERSION // <-- Та самая "волшебная" переменная из Gradle

        /** 4. Используем шаблон R.string.revision и вставляем в него версию  */
        val fullRevisionText = getString(R.string.revision, appVersionName)

        /** 5. Устанавливаем готовый текст с версией во второй TextView "revisionTextView" (о версии) */
        revisionTextView.text = fullRevisionText
    }
}
