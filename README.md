# 📱 Tarif Yönetim Sistemi

Bu proje, **tariflerin ve malzemelerin düzenlenmesini kolaylaştırmak için tasarlanmış** bir yönetim sistemidir. Kocaeli Üniversitesi Bilgisayar Mühendisliği Bölümü'nün belirlediği gereksinimler doğrultusunda geliştirilmiştir.

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
![WhatsApp Görsel 2025-04-05 saat 19 43 51_aa2c70e9](https://github.com/user-attachments/assets/d4c63acf-c6dc-47c9-b987-6836e91eb8a8)

## 🔄 Database:
![WhatsApp Görsel 2025-04-05 saat 19 43 56_f119938e](https://github.com/user-attachments/assets/f2b58114-4b1e-425f-bb93-e31d69728c03)




## 🎓 Kocaeli Üniversitesi
Bilgisayar Mühendisliği Bölümü  
Yazılım Laboratuvarı I  
2023-2024 Güz Dönemi 
