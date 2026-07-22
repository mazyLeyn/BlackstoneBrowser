# Blackstone Browser

Mozilla GeckoView tabanlı, sade ve performans odaklı bir Android web tarayıcısı.

Blackstone Browser, gereksiz eklentilerden uzak durup temel tarayıcı deneyimini
hızlı ve akıcı şekilde sunmayı hedefler. Chromium WebView yerine GeckoView (Firefox
motoru) kullanır.

## Özellikler

- GeckoView (Firefox motoru) tabanlı hızlı sayfa render
- Adres çubuğundan URL veya arama sorgusu girme, seçilebilir arama motoru (Google, DuckDuckGo, Brave, Bing)
- Hızlı erişim (speed dial) ana ekranı
- Gezinme geçmişi kaydı ve tek tuşla temizleme
- Gizli sekme modu — geçmiş kaydedilmez, ayrı oturum
- JavaScript açma/kapama, masaüstü modu, tema seçimi (açık/koyu/sistem)
- Material Design arayüz, kenardan kenara (edge-to-edge) tasarım

## Kurulum

### Android Studio ile

```bash
git clone https://github.com/mazyLeyn/BlackstoneBrowser.git
```

Projeyi Android Studio'da açın, Gradle sync tamamlandıktan sonra çalıştırın.
Minimum SDK 26 (Android 8.0) gerektirir.

### APK

Derlenmiş APK dosyaları **Releases** bölümünden indirilebilir. Play Store veya
Galaxy Store dışından kurulum yaptığınız için cihazınız "bilinmeyen kaynak" uyarısı
gösterebilir — bu normaldir.

## İndir

En son sürüm: https://github.com/mazyLeyn/BlackstoneBrowser/releases/latest

## Kullanılan Teknolojiler

- Kotlin
- Android SDK
- Mozilla GeckoView
- Material Components
- SQLite (gezinme geçmişi için)

## Yol Haritası

- [x] Gezinme geçmişi
- [x] Gizli sekme modu
- [ ] Yer imleri
- [ ] İzleyici/reklam engelleme (geliştirme aşamasında, henüz kararlı değil)
- [ ] Çoklu sekme desteği
- [ ] İndirme yöneticisi

## Bilinen Kısıtlar

Proje aktif geliştirme aşamasında. Yer imleri henüz işlevsel değil, izleyici
engelleme özelliği test edilip stabilite sorunları nedeniyle geri alındı.

## Katkıda Bulunma

Katkılar, hata bildirimleri ve öneriler memnuniyetle karşılanır.

1. Fork oluşturun.
2. Yeni bir branch açın.
3. Değişikliklerinizi yapın.
4. Pull Request gönderin.

## Lisans

Bu proje MIT Lisansı altında lisanslanmıştır.