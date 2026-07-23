package com.example.mybrowser

import android.content.Intent
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.core.view.WindowInsetsCompat
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.mozilla.geckoview.GeckoResult
import org.mozilla.geckoview.GeckoRuntime
import org.mozilla.geckoview.GeckoSession
import org.mozilla.geckoview.GeckoSessionSettings
import org.mozilla.geckoview.GeckoView
import org.mozilla.geckoview.StorageController
import org.mozilla.geckoview.WebRequestError

class MainActivity : AppCompatActivity() {

    private lateinit var geckoView: GeckoView
    private lateinit var session: GeckoSession
    private lateinit var homepageContainer: RelativeLayout
    private lateinit var browserContainer: SwipeRefreshLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var appBarLayout: View
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var toolbarSearchBar: EditText
    private lateinit var homeSearchBar: EditText
    private lateinit var btnMenu: ImageButton
    private lateinit var btnPrivateMode: ImageButton
    private lateinit var privateModeIndicator: View
    private lateinit var speedDialRecyclerView: RecyclerView
    private lateinit var glowView: View
    private lateinit var addressBarContainer: View
    private lateinit var homeSearchBarContainer: View

    private lateinit var dbHelper: HistoryDbHelper
    private var currentTitle: String? = null
    private var isPrivateMode: Boolean = false
    private var canGoBack: Boolean = false
    private var canGoForward: Boolean = false

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("isPrivateMode", isPrivateMode)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        
        isPrivateMode = savedInstanceState?.getBoolean("isPrivateMode", false) ?: false
        
        dbHelper = HistoryDbHelper.getInstance(this)
        applySavedTheme()
        
        setContentView(R.layout.activity_main)

        initViews()
        setupSystemBars()
        applyPrivateModeUI()
        setupGeckoView()
        setupListeners()
        setupSpeedDial()
        setupKeyboardAnimation()
        setupBackNavigation()
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val url = intent?.getStringExtra("load_url")
        if (!url.isNullOrEmpty()) {
            performSearch(url)
        }
    }

    override fun onResume() {
        super.onResume()
        // Settings'den gelen değişiklikleri uygula
        applySettings()
    }

    private fun initViews() {
        geckoView = findViewById(R.id.geckoview)
        progressBar = findViewById(R.id.progressBar)
        appBarLayout = findViewById(R.id.appBarLayout)
        homepageContainer = findViewById(R.id.homepageContainer)
        browserContainer = findViewById(R.id.browserContainer)
        homeSearchBar = findViewById(R.id.homeSearchBar)
        toolbarSearchBar = findViewById(R.id.toolbarSearchBar)
        bottomNavigation = findViewById(R.id.bottomNavigation)
        
        // Material 3 seçili öğe vurgusunu (oval arkaplan) kaldır
        bottomNavigation.isItemActiveIndicatorEnabled = false
        
        btnMenu = findViewById(R.id.btnMenu)
        btnPrivateMode = findViewById(R.id.btnPrivateMode)
        privateModeIndicator = findViewById(R.id.privateModeIndicator)
        speedDialRecyclerView = findViewById(R.id.speedDialRecyclerView)
        glowView = findViewById(R.id.glowView)
        addressBarContainer = findViewById(R.id.addressBarContainer)
        homeSearchBarContainer = findViewById(R.id.homeSearchBarContainer)
    }

    private fun applySavedTheme() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val theme = prefs.getString("theme", "dark")
        applyTheme(theme)
    }

    private fun applySettings() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        
        // JavaScript Ayarı
        session.settings.allowJavascript = prefs.getBoolean("javascript_enabled", true)
        
        // Masaüstü Modu Ayarı
        val isDesktopMode = prefs.getBoolean("desktop_mode", false)
        session.settings.userAgentMode = if (isDesktopMode) {
            GeckoSessionSettings.USER_AGENT_MODE_DESKTOP
        } else {
            GeckoSessionSettings.USER_AGENT_MODE_MOBILE
        }
    }

    private fun recreateGeckoSession(showToast: Boolean = true) {
        try {
            session.close()
        } catch (e: Exception) {
            // Oturum zaten kapalı olabilir
        }
        setupGeckoView()
        showHomepage()
        if (showToast) {
            Toast.makeText(this, "Tarayıcı motoru yenilendi", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupGeckoView() {
        val settings = GeckoSessionSettings.Builder()
            .usePrivateMode(isPrivateMode)
            .build()
        session = GeckoSession(settings)
        val runtime = GeckoRuntime.getDefault(this)
        
        applySettings()
        
        session.open(runtime)
        geckoView.setSession(session)

        session.navigationDelegate = object : GeckoSession.NavigationDelegate {
            override fun onLoadError(session: GeckoSession, uri: String?, error: WebRequestError): GeckoResult<String>? {
                Toast.makeText(this@MainActivity, "Load Error: ${error.code}", Toast.LENGTH_SHORT).show()
                return null
            }

            override fun onCanGoBack(session: GeckoSession, canGoBack: Boolean) {
                this@MainActivity.canGoBack = canGoBack
            }

            override fun onCanGoForward(session: GeckoSession, canGoForward: Boolean) {
                this@MainActivity.canGoForward = canGoForward
            }

            override fun onLocationChange(session: GeckoSession, url: String?, permissions: MutableList<GeckoSession.PermissionDelegate.ContentPermission>, hasUserGesture: Boolean) {
                url?.let {
                    if (it != "about:blank") {
                        toolbarSearchBar.setText(it)
                        if (!isPrivateMode) {
                            dbHelper.addHistory(currentTitle ?: it, it)
                        }
                    }
                }
            }
        }

        session.contentDelegate = object : GeckoSession.ContentDelegate {
            override fun onTitleChange(session: GeckoSession, title: String?) {
                currentTitle = title
            }

            override fun onCrash(session: GeckoSession) {
                recreateGeckoSession(true)
            }

            override fun onKill(session: GeckoSession) {
                recreateGeckoSession(true)
            }
        }

        session.scrollDelegate = object : GeckoSession.ScrollDelegate {
            override fun onScrollChanged(session: GeckoSession, scrollX: Int, scrollY: Int) {
                browserContainer.isEnabled = (scrollY <= 0)
            }
        }

        session.progressDelegate = object : GeckoSession.ProgressDelegate {
            override fun onProgressChange(session: GeckoSession, progress: Int) {
                progressBar.progress = progress
                progressBar.visibility = if (progress < 100) View.VISIBLE else View.GONE
            }
        }
    }

    private fun setupListeners() {
        browserContainer.setOnRefreshListener {
            session.reload()
            browserContainer.isRefreshing = false
        }

        homeSearchBar.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(homeSearchBar.text.toString())
                true
            } else false
        }

        toolbarSearchBar.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(toolbarSearchBar.text.toString())
                true
            } else false
        }

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_back -> if (canGoBack) session.goBack()
                R.id.nav_forward -> if (canGoForward) session.goForward()
                R.id.nav_home -> showHomepage()
                R.id.nav_refresh -> session.reload()
                R.id.nav_menu -> showOverflowMenu()
            }
            false
        }

        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            if (browserContainer.visibility == View.VISIBLE) {
                showHomepage()
            }
        }

        btnMenu.setOnClickListener {
            showOverflowMenu()
        }

        btnPrivateMode.setOnClickListener {
            togglePrivateMode()
        }
    }

    private fun togglePrivateMode() {
        // Eski moddan çıkarken verileri temizle
        if (isPrivateMode) {
            val runtime = GeckoRuntime.getDefault(this)
            runtime.storageController.clearData(StorageController.ClearFlags.ALL)
        }
        
        isPrivateMode = !isPrivateMode
        
        if (isPrivateMode) {
            Toast.makeText(this, "Gizli sekme açıldı — geçmiş kaydedilmeyecek", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Gizli sekme kapatıldı", Toast.LENGTH_SHORT).show()
        }
        
        applyPrivateModeUI()
        recreateGeckoSession(false)
    }

    private fun applyPrivateModeUI() {
        val isNightMode = (resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK) == android.content.res.Configuration.UI_MODE_NIGHT_YES

        val accentColor = if (isPrivateMode) {
            androidx.core.content.ContextCompat.getColor(this, R.color.private_mode_accent)
        } else {
            androidx.core.content.ContextCompat.getColor(this, R.color.accent_blue)
        }
        
        val bgColor = if (isPrivateMode) {
            androidx.core.content.ContextCompat.getColor(this, R.color.private_mode_bg)
        } else {
            androidx.core.content.ContextCompat.getColor(this, R.color.app_background)
        }

        val surfaceColor = if (isPrivateMode) {
            androidx.core.content.ContextCompat.getColor(this, R.color.private_mode_surface)
        } else {
            androidx.core.content.ContextCompat.getColor(this, R.color.surface_color)
        }

        val iconGrayColor = androidx.core.content.ContextCompat.getColor(this, R.color.icon_gray)

        // Status Bar ve Nav Bar İkon Rengi Yönetimi
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        if (isPrivateMode) {
            windowInsetsController.isAppearanceLightStatusBars = false
            windowInsetsController.isAppearanceLightNavigationBars = false
        } else {
            windowInsetsController.isAppearanceLightStatusBars = !isNightMode
            windowInsetsController.isAppearanceLightNavigationBars = !isNightMode
        }

        // Renkleri Belirle
        val finalIconColor = if (isPrivateMode) {
            androidx.core.content.ContextCompat.getColor(this, R.color.white)
        } else {
            iconGrayColor
        }
        
        val finalTextColor = if (isPrivateMode) {
            androidx.core.content.ContextCompat.getColor(this, R.color.white)
        } else {
            androidx.core.content.ContextCompat.getColor(this, R.color.text_primary)
        }

        // Arka Planları Uygula
        appBarLayout.setBackgroundColor(bgColor)
        homepageContainer.setBackgroundColor(bgColor)
        
        // Arama çubuğu arka planlarını optimize et
        addressBarContainer.backgroundTintList = android.content.res.ColorStateList.valueOf(surfaceColor)
        homeSearchBarContainer.backgroundTintList = android.content.res.ColorStateList.valueOf(surfaceColor)
        
        // Glow efekti
        glowView.setBackgroundResource(if (isPrivateMode) R.drawable.bg_glow_purple else R.drawable.bg_glow_yellow)

        // Bottom Navigation renklerini ayarla
        val navTextColorStateList = android.content.res.ColorStateList.valueOf(accentColor)
        bottomNavigation.itemIconTintList = navTextColorStateList
        bottomNavigation.itemTextColor = navTextColorStateList
        bottomNavigation.setBackgroundColor(bgColor)

        // İkon ve Metin Renklerini Uygula
        val colorStateList = android.content.res.ColorStateList.valueOf(finalIconColor)
        btnMenu.imageTintList = colorStateList
        findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar).setNavigationIconTint(finalIconColor)
        
        toolbarSearchBar.setTextColor(finalTextColor)
        toolbarSearchBar.setHintTextColor(if (isPrivateMode) 0x80FFFFFF.toInt() else androidx.core.content.ContextCompat.getColor(this, R.color.text_secondary))
        homeSearchBar.setTextColor(finalTextColor)
        homeSearchBar.setHintTextColor(if (isPrivateMode) 0x80FFFFFF.toInt() else androidx.core.content.ContextCompat.getColor(this, R.color.text_secondary))
        
        // Private Mode butonu görseli
        btnPrivateMode.imageTintList = android.content.res.ColorStateList.valueOf(
            if (isPrivateMode) accentColor else finalIconColor
        )
        privateModeIndicator.visibility = if (isPrivateMode) View.VISIBLE else View.GONE
        
        // Logo/Marka renkleri
        findViewById<android.widget.TextView>(R.id.brandPart1).setTextColor(accentColor)
        findViewById<android.widget.TextView>(R.id.brandPart2).setTextColor(accentColor)
        findViewById<android.widget.TextView>(R.id.betaBadge).setTextColor(accentColor)
        
        // Progress bar rengi
        progressBar.progressTintList = android.content.res.ColorStateList.valueOf(accentColor)

        // Speed Dial Adapter'ı güncelle
        if (speedDialRecyclerView.adapter is SpeedDialAdapter) {
            (speedDialRecyclerView.adapter as SpeedDialAdapter).updatePrivateMode(isPrivateMode)
        }
    }

    private fun performSearch(query: String) {
        val trimmedQuery = query.trim()
        if (trimmedQuery.isNotEmpty()) {
            val prefs = PreferenceManager.getDefaultSharedPreferences(this)
            val engine = prefs.getString("search_engine", "google")
            val searchUrl = when (engine) {
                "duckduckgo" -> "https://duckduckgo.com/?q="
                "brave" -> "https://search.brave.com/search?q="
                "bing" -> "https://www.bing.com/search?q="
                else -> "https://www.google.com/search?q="
            }

            val url = if (trimmedQuery.contains(".") && !trimmedQuery.contains(" ")) {
                if (trimmedQuery.startsWith("http")) trimmedQuery else "https://$trimmedQuery"
            } else {
                "$searchUrl$trimmedQuery&pws=0&gl=tr&gws_rd=cr"
            }
            
            homepageContainer.visibility = View.GONE
            browserContainer.visibility = View.VISIBLE
            
            findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar).setNavigationIcon(R.drawable.ic_back)
            
            session.loadUri(url)
            currentFocus?.let { 
                WindowCompat.getInsetsController(window, it).hide(WindowInsetsCompat.Type.ime())
            }
            homeSearchBar.clearFocus()
            toolbarSearchBar.clearFocus()
        }
    }

    private fun showHomepage() {
        browserContainer.visibility = View.GONE
        homepageContainer.visibility = View.VISIBLE
        toolbarSearchBar.text.clear()
        
        // Geri ikonunu gizle
        findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar).navigationIcon = null
        
        session.loadUri("about:blank")
    }

    private fun showOverflowMenu() {
        val view = findViewById<View>(R.id.nav_menu)
        val popup = androidx.appcompat.widget.PopupMenu(
            ContextThemeWrapper(this, R.style.PopupMenuOverlay),
            view
        )
        popup.menuInflater.inflate(R.menu.overflow_menu, popup.menu)
        
        // Force icons to show
        try {
            val fields = popup.javaClass.getDeclaredField("mPopup")
            fields.isAccessible = true
            val menuPopupHelper = fields.get(popup)
            menuPopupHelper?.javaClass
                ?.getMethod("setForceShowIcon", Boolean::class.javaPrimitiveType)
                ?.invoke(menuPopupHelper, true)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_bookmarks -> startActivity(Intent(this, BookmarksActivity::class.java))
                R.id.menu_history -> startActivity(Intent(this, HistoryActivity::class.java))
                R.id.menu_theme -> showThemeSelector()
                R.id.menu_settings -> startActivity(Intent(this, SettingsActivity::class.java))
                R.id.menu_about -> startActivity(Intent(this, AboutActivity::class.java))
                R.id.menu_clear_exit -> {
                    dbHelper.clearHistory()
                    Toast.makeText(this, "Geçmiş temizlendi ve çıkılıyor...", Toast.LENGTH_SHORT).show()
                    finishAffinity()
                }
            }
            true
        }
        popup.show()
    }

    private fun showThemeSelector() {
        val themes = arrayOf("Sistem", "Açık", "Koyu")
        val themeValues = arrayOf("system", "light", "dark")
        
        MaterialAlertDialogBuilder(this)
            .setTitle("Tema Seçin")
            .setItems(themes) { _, which ->
                val selectedTheme = themeValues[which]
                PreferenceManager.getDefaultSharedPreferences(this).edit()
                    .putString("theme", selectedTheme)
                    .apply()
                applyTheme(selectedTheme)
            }
            .show()
    }

    private fun applyTheme(theme: String?) {
        when (theme) {
            "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    private fun setupSpeedDial() {
        val items = listOf(
            SpeedDialItem("Google", "https://www.google.com"),
            SpeedDialItem("YouTube", "https://www.youtube.com"),
            SpeedDialItem("GitHub", "https://www.github.com"),
            SpeedDialItem("Wikipedia", "https://www.wikipedia.org"),
            SpeedDialItem("Reddit", "https://www.reddit.com")
        )
        
        speedDialRecyclerView.layoutManager = GridLayoutManager(this, 4)
        speedDialRecyclerView.adapter = SpeedDialAdapter(items, isPrivateMode) { item ->
            performSearch(item.url)
        }
    }

    private fun setupKeyboardAnimation() {
        val centralLayout = findViewById<View>(R.id.centralLayout)
        ViewCompat.setWindowInsetsAnimationCallback(
            window.decorView,
            object : WindowInsetsAnimationCompat.Callback(DISPATCH_MODE_STOP) {
                override fun onProgress(
                    insets: WindowInsetsCompat,
                    runningAnimations: MutableList<WindowInsetsAnimationCompat>
                ): WindowInsetsCompat {
                    val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
                    val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                    val diff = (imeInsets.bottom - systemBarsInsets.bottom).coerceAtLeast(0)
                    
                    if (homepageContainer.visibility == View.VISIBLE) {
                        centralLayout.translationY = -diff.toFloat() / 2f
                    }
                    return insets
                }
            }
        )
    }

    private fun setupBackNavigation() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (browserContainer.visibility == View.VISIBLE) {
                    if (canGoBack) {
                        session.goBack()
                    } else {
                        showHomepage()
                    }
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    private fun setupSystemBars() {
        ViewCompat.setOnApplyWindowInsetsListener(appBarLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, systemBars.top, 0, 0)
            insets
        }
        ViewCompat.setOnApplyWindowInsetsListener(bottomNavigation) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, 0, 0, systemBars.bottom)
            insets
        }
    }
}