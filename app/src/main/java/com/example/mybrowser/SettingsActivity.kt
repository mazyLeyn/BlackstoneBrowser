package com.example.mybrowser

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowCompat
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import org.mozilla.geckoview.GeckoRuntime
import org.mozilla.geckoview.StorageController

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        
        setupStatusBar()

        val container = findViewById<android.view.View>(R.id.settings_container)
        ViewCompat.setOnApplyWindowInsetsListener(container) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, systemBars.top, 0, systemBars.bottom)
            insets
        }

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings_container, SettingsFragment())
                .commit()
        }
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "Settings"
    }

    private fun setupStatusBar() {
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        val isNightMode = (resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK) == android.content.res.Configuration.UI_MODE_NIGHT_YES
        windowInsetsController.isAppearanceLightStatusBars = !isNightMode
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        
        // Sürüm Bilgisi
        findPreference<Preference>("version")?.summary = "v${BuildConfig.VERSION_NAME}"

        // Geçmişi Temizle
        findPreference<Preference>("clear_history")?.setOnPreferenceClickListener {
            HistoryDbHelper.getInstance(requireContext()).clearHistory()
            Toast.makeText(context, "Geçmiş temizlendi", Toast.LENGTH_SHORT).show()
            true
        }

        // Çerezleri Temizle
        findPreference<Preference>("clear_cookies")?.setOnPreferenceClickListener {
            val runtime = GeckoRuntime.getDefault(requireContext())
            runtime.storageController.clearData(StorageController.ClearFlags.COOKIES)
            Toast.makeText(context, "Çerezler temizlendi", Toast.LENGTH_SHORT).show()
            true
        }

        // Önbelleği Temizle
        findPreference<Preference>("clear_cache")?.setOnPreferenceClickListener {
            val runtime = GeckoRuntime.getDefault(requireContext())
            runtime.storageController.clearData(StorageController.ClearFlags.ALL_CACHES)
            Toast.makeText(context, "Önbellek temizlendi", Toast.LENGTH_SHORT).show()
            true
        }

        // Lisanslar (About sayfasına yönlendir)
        findPreference<Preference>("licenses")?.setOnPreferenceClickListener {
            startActivity(Intent(context, AboutActivity::class.java))
            true
        }

        // GitHub Bağlantısı
        findPreference<Preference>("github")?.setOnPreferenceClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/mazyLeyn/WardenBrowser"))
            startActivity(intent)
            true
        }
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences?.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceManager.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            "theme" -> {
                val theme = sharedPreferences?.getString("theme", "dark")
                applyTheme(theme)
            }
        }
    }

    private fun applyTheme(theme: String?) {
        when (theme) {
            "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }
}