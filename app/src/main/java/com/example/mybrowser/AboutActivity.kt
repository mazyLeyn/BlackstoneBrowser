package com.example.mybrowser

import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        ViewCompat.setOnApplyWindowInsetsListener(toolbar) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, systemBars.top, 0, 0)
            insets
        }
        
        findViewById<TextView>(R.id.versionText).text = "v1.2.2"
        
        val detailsText = StringBuilder()
            .append("Powered by Mozilla GeckoView\n\n")
            .append("Developer: Onur Karatas\n")
            .append("GitHub: https://github.com/mazyLeyn/BlackstoneBrowser\n\n")
            .append("Android Version: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})\n")
            .append("Build Number: ${BuildConfig.VERSION_CODE}")
            .toString()
            
        findViewById<TextView>(R.id.detailsText).text = detailsText
    }
}