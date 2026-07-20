package com.example.mybrowser

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class HistoryActivity : AppCompatActivity() {

    private lateinit var dbHelper: HistoryDbHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnClear: ExtendedFloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        
        dbHelper = HistoryDbHelper(this)
        
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        ViewCompat.setOnApplyWindowInsetsListener(toolbar) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, systemBars.top, 0, 0)
            insets
        }

        recyclerView = findViewById(R.id.historyRecyclerView)
        btnClear = findViewById(R.id.btnClearHistory)

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
        
        btnClear.visibility = if (history.isEmpty()) View.GONE else View.VISIBLE
    }
}