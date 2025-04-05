# 📱 Tarif Yönetim Sistemi

Bu proje, **tariflerin ve malzemelerin düzenlenmesini kolaylaştırmak için tasarlanmış** bir yönetim sistemidir. Kocaeli Üniversitesi Bilgisayar Mühendisliği Bölümü'nün belirlediği gereksinimler doğrultusunda geliştirilmiştir.

## 👥 Proje Ekibi
- **Anıl Engin Keretli** - 230201128
- **Ahmet Burak Karkaç** - 220201173

## 🎯 Sistem Gereksinimleri

### Fonksiyonel Gereksinimler
- **Tarif Yönetimi:** Tarif ekleme, güncelleme ve silme işlemleri
- **Malzeme Yönetimi:** Malzeme miktarlarını ve maliyetlerini takip etme
- **Tarif-Malzeme İlişkisi:** Her tarif için gerekli malzeme miktarlarının belirtilmesi

### Fonksiyonel Olmayan Gereksinimler
- **Kullanılabilirlik:** Sistem kullanıcı dostu olmalıdır
- **Performans:** Çok sayıda tarif ve malzemeyi verimli bir şekilde yönetebilmelidir
- **Genişletilebilirlik:** Gelecekte daha fazla tarif ve malzeme eklemek kolay olmalıdır

## 🛠️ Teknoloji ve Mimari

### Kullanılan Teknolojiler
- **Java** - Ana geliştirme dili
- **Java Swing** - Kullanıcı arayüzü
- **JDBC** - Veritabanı bağlantısı

### Sınıf Yapısı

1. **Recipe (Tarif) Sınıfı:**
   - recipeID (int) - Primary Key
   - name (String)
   - category (String)
   - cost (double)
   - prepTime (int)
   - instructions (String)

2. **Ingredient (Malzeme) Sınıfı:**
   - ingredientID (int) - Primary Key
   - name (String)
   - quantity (double)
   - unit (String)
   - pricePerUnit (double)

3. **RecipeIngredient (TarifMalzeme) Sınıfı:**
   - recipeID (int) - Foreign Key
   - ingredientID (int) - Foreign Key
   - quantity (double)

## 📌 Temel Özellikler

### Arayüz Özellikleri
- **Ana Menü:**
  - Tarif Ekleme
  - Tarif Güncelleme
  - Tarif Silme

- **Arama ve Filtreleme:**
  - Tarif ismine göre arama
  - Hazırlama süresine göre arama
  - Maliyet bazlı filtreleme
  - Kategori bazlı filtreleme

### Güvenlik Özellikleri
- **Duplicate Kontrol Sistemi:** Aynı isimde tarif eklenmesini önler

## 🔄 İlişkiler ve İşlevsellik

### Recipe ve RecipeIngredient İlişkisi
- 1'e-çok ilişki
- Bir tarif birden fazla malzeme içerebilir

### Ingredient ve RecipeIngredient İlişkisi
- 1'e-çok ilişki
- Bir malzeme birden fazla tarifte kullanılabilir

## 🎓 Kocaeli Üniversitesi
Bilgisayar Mühendisliği Bölümü  
Yazılım Laboratuvarı I  
2023-2024 Güz Dönemi 