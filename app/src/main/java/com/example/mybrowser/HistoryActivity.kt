package com.example.mybrowser

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class HistoryActivity : AppCompatActivity() {

    private lateinit var dbHelper: HistoryDbHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnClear: ExtendedFloatingActionButton
    private lateinit var emptyText: View

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        
        setupStatusBar()

        dbHelper = HistoryDbHelper.getInstance(this)
        
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        ViewCompat.setOnApplyWindowInsetsListener(toolbar) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, systemBars.top, 0, 0)
            insets
        }

        recyclerView = findViewById(R.id.historyRecyclerView)
        btnClear = findViewById(R.id.btnClearHistory)
        emptyText = findViewById(R.id.emptyHistoryText)

        ViewCompat.setOnApplyWindowInsetsListener(btnClear) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val params = v.layoutParams as android.view.ViewGroup.MarginLayoutParams
            params.bottomMargin = (32 * resources.displayMetrics.density).toInt() + systemBars.bottom
            v.layoutParams = params
            insets
        }

        loadHistory()

        btnClear.setOnClickListener {
            dbHelper.clearHistory()
            loadHistory()
        }
    }

    private fun setupStatusBar() {
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        val isNightMode = (resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK) == android.content.res.Configuration.UI_MODE_NIGHT_YES
        windowInsetsController.isAppearanceLightStatusBars = !isNightMode
    }

    private fun loadHistory() {
        val history = dbHelper.getAllHistory()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = HistoryAdapter(history) { item ->
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("load_url", item.url)
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
            finish()
        }
        
        val isEmpty = history.isEmpty()
        btnClear.visibility = if (isEmpty) View.GONE else View.VISIBLE
        emptyText.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }
}