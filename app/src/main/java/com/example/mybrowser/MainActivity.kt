package com.example.mybrowser

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.core.view.WindowInsetsCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import org.mozilla.geckoview.GeckoResult
import org.mozilla.geckoview.GeckoRuntime
import org.mozilla.geckoview.GeckoSession
import org.mozilla.geckoview.GeckoView
import org.mozilla.geckoview.WebRequestError

class MainActivity : AppCompatActivity() {

    private lateinit var geckoView: GeckoView
    private lateinit var session: GeckoSession
    private lateinit var homepageContainer: View
    private lateinit var browserContainer: SwipeRefreshLayout
    private lateinit var progressBar: ProgressBar
    
    private var canGoBack: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Pencereyi sistem çubuklarının (status bar, keyboard) arkasına uzat
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        setContentView(R.layout.activity_main)

        geckoView = findViewById(R.id.geckoview)
        progressBar = findViewById(R.id.progressBar)
        homepageContainer = findViewById(R.id.homepageContainer)
        browserContainer = findViewById(R.id.browserContainer)
        val homeSearchBar = findViewById<EditText>(R.id.homeSearchBar)
        val centralLayout = findViewById<View>(R.id.centralLayout)

        // Klavye Animasyonu Yönetimi
        ViewCompat.setWindowInsetsAnimationCallback(
            window.decorView,
            object : WindowInsetsAnimationCompat.Callback(DISPATCH_MODE_STOP) {
                override fun onProgress(
                    insets: WindowInsetsCompat,
                    runningAnimations: MutableList<WindowInsetsAnimationCompat>
                ): WindowInsetsCompat {
                    val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
                    val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                    
                    // Klavyenin yüksekliğini hesapla (sistem çubuklarını çıkararak)
                    val diff = (imeInsets.bottom - systemBarsInsets.bottom).coerceAtLeast(0)
                    
                    // Merkezi tasarımı klavye kadar yukarı kaydır (biraz da boşluk bırak: -diff)
                    // HomepageContainer görünürse animasyonu uygula
                    if (homepageContainer.visibility == View.VISIBLE) {
                        centralLayout.translationY = -diff.toFloat() / 2f
                    }
                    
                    return insets
                }
            }
        )

        session = GeckoSession()
        val runtime = GeckoRuntime.getDefault(this)
        session.open(runtime)
        geckoView.setSession(session)

        // Hata ve Gezinti Yönetimi
        session.navigationDelegate = object : GeckoSession.NavigationDelegate {
            override fun onLoadError(session: GeckoSession, uri: String?, error: WebRequestError): GeckoResult<String>? {
                Toast.makeText(this@MainActivity, "Sayfa yüklenemedi: ${error.code}", Toast.LENGTH_SHORT).show()
                return null
            }

            override fun onCanGoBack(session: GeckoSession, canGoBack: Boolean) {
                this@MainActivity.canGoBack = canGoBack
            }
        }

        // Kaydırma Yönetimi: Sayfa en üstte değilse SwipeRefreshLayout'u kapat
        session.scrollDelegate = object : GeckoSession.ScrollDelegate {
            override fun onScrollChanged(session: GeckoSession, scrollX: Int, scrollY: Int) {
                browserContainer.isEnabled = (scrollY <= 0)
            }
        }

        // İlerleme Yönetimi
        session.progressDelegate = object : GeckoSession.ProgressDelegate {
            override fun onProgressChange(session: GeckoSession, progress: Int) {
                if (progress < 100) {
                    progressBar.visibility = View.VISIBLE
                } else {
                    progressBar.visibility = View.GONE
                }
                progressBar.progress = progress
            }
        }

        // Çek-Yenile Mantığı
        browserContainer.setOnRefreshListener {
            session.reload()
            browserContainer.isRefreshing = false
        }

        // Ana Sayfa Arama Mantığı
        homeSearchBar.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = homeSearchBar.text.toString().trim()
                if (query.isNotEmpty()) {
                    val url = if (query.contains(".") && !query.contains(" ")) {
                        if (query.startsWith("http")) query else "https://$query"
                    } else {
                        "https://www.google.com/search?q=$query&pws=0&gl=tr&gws_rd=cr"
                    }
                    
                    homepageContainer.visibility = View.GONE
                    browserContainer.visibility = View.VISIBLE
                    
                    session.loadUri(url)
                    homeSearchBar.clearFocus()
                }
                true
            } else {
                false
            }
        }

        // Geri Tuşu Mantığı
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (browserContainer.visibility == View.VISIBLE && canGoBack) {
                    session.goBack()
                } else {
                    // Eğer ana sayfadaysak veya geri gidilecek sayfa yoksa uygulamayı kapat
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }
}