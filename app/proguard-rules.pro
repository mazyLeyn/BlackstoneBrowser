# Test Checklist (Manuel Test Listesi)
# Bu değişiklikten sonra bir release APK/AAB derleyip GERÇEK CİHAZDA şunları test et:
# - Uygulama açılışı (crash var mı)
# - 3 çizgi (overflow) menüsünü açma — reflection kısmı en riskli nokta
# - Arama yapma (GeckoView session, tracker blocking)
# - Gizli sekme aç/kapa
# - Ayarlar ekranına girip tema değiştirme
# - History ekranını açma ve temizleme

# GeckoView - native/JNI binding'leri obfuscation'dan etkilenmesin
-keep class org.mozilla.geckoview.** { *; }
-keep class org.mozilla.gecko.** { *; }
-dontwarn org.mozilla.geckoview.**
-dontwarn org.mozilla.gecko.**

# MainActivity.kt içindeki showOverflowMenu() fonksiyonu reflection ile
# androidx.appcompat.widget.PopupMenu'nun iç sınıflarına (mPopup, MenuPopupHelper,
# setForceShowIcon) erişiyor. R8 bu isimleri değiştirirse reflection çağrısı
# NoSuchFieldException/NoSuchMethodException ile çöker. Bu sınıfları koru:
-keep class androidx.appcompat.widget.PopupMenu { *; }
-keep class androidx.appcompat.view.menu.MenuPopupHelper { *; }
-keepclassmembers class androidx.appcompat.view.menu.MenuPopupHelper {
    *;
}

# Kotlin data class / serialization kullanılan modeller varsa (SpeedDialItem gibi)
# alan isimlerini koru
-keep class com.example.mybrowser.SpeedDialItem { *; }

# SQLite/HistoryDbHelper içindeki reflection kullanımı yoksa bu kural gerekmez,
# ama HistoryDbHelper ve ilişkili data class'ları için güvenlik amaçlı koru
-keep class com.example.mybrowser.HistoryDbHelper { *; }

# Crash raporlarında stack trace okunabilir kalsın diye satır numaralarını koru
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
