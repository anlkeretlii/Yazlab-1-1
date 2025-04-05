package core;

import obj.Ingredient;
import obj.Recipe;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;

public class DataManager {
    private DBManager dbManager;

    public DataManager(DBManager dbManager) {
        this.dbManager = dbManager;
        this.dbManager.connect();
    }

    public DataManager getInstance() {
        return this;
    }

    // Veritabanına tarif eklemeye yarar.

    public void addRecipe(String recipeName, String category, int prepTime, String instructions, ArrayList<Ingredient> ingredientsList) {
        try {
            if (isDuplicateRecipe(recipeName)) {
                System.out.println("[HATA] Tarif zaten mevcut.");
                return;
            }
            // Şimdi bi transaction başlatalım
            dbManager.getConnection().setAutoCommit(false);

            // Tarif veritabanına ekleniyor
            String insertRecipeSQL = "INSERT INTO Tarifler (TarifAdi, Kategori, HazirlamaSuresi, Talimatlar) VALUES (?, ?, ?, ?)";
            PreparedStatement recipeStmt = dbManager.getConnection().prepareStatement(insertRecipeSQL, Statement.RETURN_GENERATED_KEYS);
            recipeStmt.setString(1, recipeName);
            recipeStmt.setString(2, category);
            recipeStmt.setInt(3, prepTime);
            recipeStmt.setString(4, instructions);
            recipeStmt.executeUpdate();

            // Oluşan tarif ID'sini alalım
            ResultSet generatedKeys = recipeStmt.getGeneratedKeys();
            int recipeID = -1;
            if (generatedKeys.next()) {
                recipeID = generatedKeys.getInt(1);
            }

            // Malzemeleri veritabanına ekleyelim
            String insertIngredientSQL = "INSERT INTO Tarif_Malzeme (TarifID, MalzemeID, MalzemeMiktar) VALUES (?, ?, ?)";
            PreparedStatement ingredientStmt = dbManager.getConnection().prepareStatement(insertIngredientSQL);
            for (Ingredient ingredient : ingredientsList) {
                int ingredientID = getIngredientID(ingredient.getName());
                ingredientStmt.setInt(1, recipeID);
                ingredientStmt.setInt(2, ingredientID);
                ingredientStmt.setDouble(3, ingredient.getQuantity());
                ingredientStmt.addBatch();
            }
            ingredientStmt.executeBatch();

            // Transaction'ı commit edelim
            dbManager.getConnection().commit();
        } catch (SQLException e) {
            try {
                // Hata oldu, rollback yapalım
                dbManager.getConnection().rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                // Auto-commit'i geri açalım
                dbManager.getConnection().setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    // Tarifin ID'sini kullanarak veritabanından tarifi bulur.
    public Recipe getRecipeByID(int recipeID) {
        Recipe recipe = null;
        try {
            // Bağlantı yoksa bağlan
            if (dbManager.getConnection() == null) {
                dbManager.connect();
            }

            // SQL sorgusu
            String query = "SELECT * FROM Tarifler WHERE TarifID = ?";
            PreparedStatement pstmt = dbManager.getConnection().prepareStatement(query);

            // Parametreyi ayarla
            pstmt.setInt(1, recipeID);
            ResultSet rs = pstmt.executeQuery();

            // Sonuçları al
            if (rs.next()) {
                recipe = new Recipe();
                recipe.setRecipeID(rs.getInt("TarifID")); // Tarif ID'si
                recipe.setName(rs.getString("TarifAdi")); // Tarif adı
                recipe.setCategory(rs.getString("Kategori")); // Kategori
                recipe.setPrepTime(rs.getInt("HazirlamaSuresi")); // Hazırlama süresi
                recipe.setInstructions(rs.getString("Talimatlar")); // Talimatlar
            }
        } catch (SQLException e) {
            // Hata mesajı
            System.out.println("[HATA] Tarif bilgisi alınamadı: " + e.getMessage());
        }
        return recipe; // Tarifi döndür
    }
// Bu metod tarif adını kullanarak tarif ID'sini veritabanından alır
public int getRecipeID(String recipeName) {
    try {
        // Bağlantı yoksa bağlan
        if (dbManager.getConnection() == null) {
            dbManager.connect(); // Bağlan
        }

        // Sorgu hazırlıyoruz
        String query = "SELECT TarifID FROM Tarifler WHERE TarifAdi = ?";
        PreparedStatement pstmt = dbManager.getConnection().prepareStatement(query);

        // Tarif adını sorguya ekliyoruz
        pstmt.setString(1, recipeName);
        ResultSet rs = pstmt.executeQuery();

        // Sonuç varsa ID'yi döndür
        if (rs.next()) {
            return rs.getInt("TarifID");
        }
    } catch (SQLException e) {
        // Hata mesajı yazdır
        System.out.println("[HATA] Tarif ID alınamadı: " + e.getMessage());
    }
    // Bulunamazsa -1 döndür
    return -1;
}
// Bu metod ingredientID'ye göre malzeme bilgilerini getirir
public Ingredient getIngredientByID(int ingredientID) {
    Ingredient ingredient = null;
    try {
        // Bağlantı yoksa bağlan
        if (dbManager.getConnection() == null) {
            dbManager.connect();
        }

        // SQL sorgusu
        String query = "SELECT * FROM Malzemeler WHERE MalzemeID = ?";
        PreparedStatement pstmt = dbManager.getConnection().prepareStatement(query);

        // Parametreyi ayarla
        pstmt.setInt(1, ingredientID);
        ResultSet rs = pstmt.executeQuery();

        // Sonuçları al
        if (rs.next()) {
            String name = rs.getString("MalzemeAdi");
            double quantity = rs.getDouble("ToplamMiktar");
            String unit = rs.getString("MalzemeBirim");
            double pricePerUnit = rs.getDouble("BirimFiyat");

            // Malzeme nesnesi oluştur
            ingredient = new Ingredient(ingredientID, name, quantity, unit, pricePerUnit);
        }
    } catch (SQLException e) {
        // Hata mesajı
        System.out.println("[HATA] Malzeme bilgisi alınamadı: " + e.getMessage());
    }
    return ingredient;
}

// Bu metod ingredientName'e göre malzeme ID'sini getirir
public int getIngredientID(String ingredientName) {
    try {
        // Bağlantı yoksa bağlan
        if (dbManager.getConnection() == null) {
            dbManager.connect();
        }

        // SQL sorgusu
        String query = "SELECT MalzemeID FROM Malzemeler WHERE MalzemeAdi = ?";
        PreparedStatement pstmt = dbManager.getConnection().prepareStatement(query);

        // Parametreyi ayarla
        pstmt.setString(1, ingredientName);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            return rs.getInt("MalzemeID");
        }
        return -1; // Bulunamazsa -1 döner
    } catch (SQLException e) {
        // Hata mesajı
        System.out.println("[HATA] Malzeme ID alınamadı: " + e.getMessage());
        return -1;
    }
}

// Bu metod bir tarifi günceller
public void updateRecipe(int recipeID, String recipeName, String category, int prepTime, String instructions, ArrayList<Ingredient> ingredientsList) {
    try {
        // Otomatik commit'i kapat
        dbManager.getConnection().setAutoCommit(false);

        // Tarif güncelleme SQL sorgusu
        String updateRecipeSQL = "UPDATE Tarifler SET TarifAdi = ?, Kategori = ?, HazirlamaSuresi = ?, Talimatlar = ? WHERE TarifID = ?";
        PreparedStatement recipeStmt = dbManager.getConnection().prepareStatement(updateRecipeSQL);
        recipeStmt.setString(1, recipeName);
        recipeStmt.setString(2, category);
        recipeStmt.setInt(3, prepTime);
        recipeStmt.setString(4, instructions);
        recipeStmt.setInt(5, recipeID);
        recipeStmt.executeUpdate();

        // Eski malzemeleri sil
        String deleteIngredientsSQL = "DELETE FROM Tarif_Malzeme WHERE TarifID = ?";
        PreparedStatement deleteIngredientsStmt = dbManager.getConnection().prepareStatement(deleteIngredientsSQL);
        deleteIngredientsStmt.setInt(1, recipeID);
        deleteIngredientsStmt.executeUpdate();

        // Yeni malzemeleri ekle
        String insertIngredientSQL = "INSERT INTO Tarif_Malzeme (TarifID, MalzemeID, MalzemeMiktar) VALUES (?, ?, ?)";
        PreparedStatement ingredientStmt = dbManager.getConnection().prepareStatement(insertIngredientSQL);
        for (Ingredient ingredient : ingredientsList) {
            int ingredientID = getIngredientID(ingredient.getName());
            ingredientStmt.setInt(1, recipeID);
            ingredientStmt.setInt(2, ingredientID);
            ingredientStmt.setDouble(3, ingredient.getQuantity());
            ingredientStmt.addBatch();
        }
        ingredientStmt.executeBatch();

        // Değişiklikleri kaydet
        dbManager.getConnection().commit();
    } catch (SQLException e) {
        try {
            // Hata olursa geri al
            dbManager.getConnection().rollback();
        } catch (SQLException rollbackEx) {
            rollbackEx.printStackTrace();
        }
        e.printStackTrace();
    }
}
// tarif silme metodu
public void deleteRecipe(int recipeID) {
    try {
        // bağlantı kurulu mu kontrol et
        if (dbManager.getConnection() == null) {
            dbManager.connect();
        }

        // önce tarif malzemelerini sil (foreign key yüzünden)
        String deleteIngredients = "DELETE FROM Tarif_Malzeme WHERE TarifID = ?";
        PreparedStatement pstmtDeleteIngredients = dbManager.getConnection().prepareStatement(deleteIngredients);
        pstmtDeleteIngredients.setInt(1, recipeID);
        pstmtDeleteIngredients.executeUpdate();

        // tarif tablosundan tarifi sil
        String deleteRecipe = "DELETE FROM Tarifler WHERE TarifID = ?";
        PreparedStatement pstmtDeleteRecipe = dbManager.getConnection().prepareStatement(deleteRecipe);
        pstmtDeleteRecipe.setInt(1, recipeID);
        pstmtDeleteRecipe.executeUpdate();

        System.out.println("[OK] tarif başarıyla silindi.");
    } catch (SQLException e) {
        System.out.println("[HATA] tarif silinemedi: " + e.getMessage());
    }
}

// tüm tarifleri al
public ArrayList<Recipe> getAllRecipes() {
    ArrayList<Recipe> recipes = new ArrayList<>();

    try {
        // bağlantı kurulu mu kontrol et
        if (dbManager.getConnection() == null) {
            dbManager.connect();
        }

        // tarifleri seç
        String query = "SELECT * FROM Tarifler";
        Statement stmt = dbManager.getConnection().createStatement();
        ResultSet rs = stmt.executeQuery(query);

        // sonuçları işle
        while (rs.next()) {
            Recipe recipe = new Recipe();
            recipe.setRecipeID(rs.getInt("TarifID"));
            recipe.setName(rs.getString("TarifAdi"));
            recipe.setCategory(rs.getString("Kategori"));
            recipe.setPrepTime(rs.getInt("HazirlamaSuresi"));
            recipe.setInstructions(rs.getString("Talimatlar"));
            recipes.add(recipe);
        }
    } catch (SQLException e) {
        System.out.println("sorgu başarısız: " + e.getMessage());
    }

    return recipes;
}

// tüm malzemeleri al
public ArrayList<Ingredient> getAllIngredients() {
    ArrayList<Ingredient> ingredients = new ArrayList<>();

    try {
        // bağlantı kurulu mu kontrol et
        if (dbManager.getConnection() == null) {
            dbManager.connect();
        }

        // malzemeleri seç
        String query = "SELECT * FROM Malzemeler";
        Statement stmt = dbManager.getConnection().createStatement();
        ResultSet rs = stmt.executeQuery(query);

        // sonuçları işle
        while (rs.next()) {
            Ingredient ingredient = new Ingredient(
                    rs.getInt("MalzemeID"),
                    rs.getString("MalzemeAdi"),
                    rs.getDouble("ToplamMiktar"),
                    rs.getString("MalzemeBirim"),
                    rs.getDouble("BirimFiyat")
            );
            ingredients.add(ingredient);
        }
    } catch (SQLException e) {
        System.out.println("sorgu başarısız: " + e.getMessage());
    }

    return ingredients;
}
// tarifleri seçilen malzemelere göre getirir
public ArrayList<Recipe> getRecipesByIngredients(ArrayList<String> selectedIngredients) {
    ArrayList<Recipe> recipes = new ArrayList<>();

    try {
        if (dbManager.getConnection() == null) {
            dbManager.connect();
        }

        // sorguyu başlat
        StringBuilder query = new StringBuilder("SELECT * FROM Tarifler WHERE TarifID IN (SELECT TarifID FROM Tarif_Malzeme WHERE MalzemeID IN (SELECT MalzemeID FROM Malzemeler WHERE MalzemeAdi IN (");

        // seçilen malzemeleri sorguya ekle
        for (int i = 0; i < selectedIngredients.size(); i++) {
            query.append("?");
            if (i != selectedIngredients.size() - 1) {
                query.append(", ");
            }
        }

        // sorguyu kapat
        query.append(")))");

        PreparedStatement pstmt = dbManager.getConnection().prepareStatement(query.toString());

        // malzeme isimlerini prepared statement'a ekle
        for (int i = 0; i < selectedIngredients.size(); i++) {
            pstmt.setString(i + 1, selectedIngredients.get(i));
        }

        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            Recipe recipe = new Recipe();
            recipe.setRecipeID(rs.getInt("TarifID"));
            recipe.setName(rs.getString("TarifAdi"));
            recipe.setCategory(rs.getString("Kategori"));
            recipe.setPrepTime(rs.getInt("HazirlamaSuresi"));
            recipe.setInstructions(rs.getString("Talimatlar"));
            recipes.add(recipe);
        }
    } catch (SQLException e) {
        System.out.println("sorgu başarısız: " + e.getMessage());
    }

    return recipes;
}

// yeni malzeme ekler
public void addIngredient(String name, double pricePerUnit, String unit) {
    try {
        if (dbManager.getConnection() == null) {
            dbManager.connect();
        }

        String insertIngredient = "INSERT INTO Malzemeler (MalzemeAdi, ToplamMiktar, MalzemeBirim, BirimFiyat) VALUES (?, ?, ?, ?)";
        PreparedStatement pstmt = dbManager.getConnection().prepareStatement(insertIngredient);
        pstmt.setString(1, name);
        pstmt.setDouble(2, 0);
        pstmt.setString(3, unit);
        pstmt.setDouble(4, pricePerUnit);
        pstmt.executeUpdate();

        System.out.println("[OK] malzeme başarıyla eklendi.");
    } catch (SQLException e) {
        System.out.println("[HATA] malzeme eklenemedi: " + e.getMessage());
    }
}

// malzeme siler
public void deleteIngredient(int ingredientID) {
    try {
        if (dbManager.getConnection() == null) {
            dbManager.connect();
        }
        // işlemi başlat
        dbManager.getConnection().setAutoCommit(false);

        // önce Tarif_Malzeme tablosundan sil
        String deleteFromTarifMalzemeSQL = "DELETE FROM Tarif_Malzeme WHERE MalzemeID = ?";
        PreparedStatement deleteFromTarifMalzemeStmt = dbManager.getConnection().prepareStatement(deleteFromTarifMalzemeSQL);
        deleteFromTarifMalzemeStmt.setInt(1, ingredientID);
        deleteFromTarifMalzemeStmt.executeUpdate();

        // sonra Malzemeler tablosundan sil
        String deleteIngredientSQL = "DELETE FROM Malzemeler WHERE MalzemeID = ?";
        PreparedStatement deleteIngredientStmt = dbManager.getConnection().prepareStatement(deleteIngredientSQL);
        deleteIngredientStmt.setInt(1, ingredientID);
        deleteIngredientStmt.executeUpdate();

        // işlemi tamamla
        dbManager.getConnection().commit();
        System.out.println("[OK] malzeme başarıyla silindi.");
    } catch (SQLException e) {
        System.out.println("[HATA] malzeme silinemedi: " + e.getMessage());
    }
}

// malzeme günceller
public void updateIngredient(int ingredientID, String name, double pricePerUnit, String unit) {
    try {
        if (dbManager.getConnection() == null) {
            dbManager.connect();
        }

        String updateIngredient = "UPDATE Malzemeler SET MalzemeAdi = ?, MalzemeBirim = ?, BirimFiyat = ? WHERE MalzemeID = ?";
        PreparedStatement pstmt = dbManager.getConnection().prepareStatement(updateIngredient);
        pstmt.setString(1, name);
        pstmt.setString(2, unit);
        pstmt.setDouble(3, pricePerUnit);
        pstmt.setInt(4, ingredientID);
        pstmt.executeUpdate();

        System.out.println("[OK] malzeme başarıyla güncellendi.");
    } catch (SQLException e) {
        System.out.println("[HATA] malzeme güncellenemedi: " + e.getMessage());
    }
}
// tarifin malzemelerini getirir
public ArrayList<Ingredient> getRecipeIngredients(int recipeID) {
    ArrayList<Ingredient> ingredients = new ArrayList<>();
    try {
        // veritabanı bağlantısı yoksa bağlan
        if (dbManager.getConnection() == null) {
            dbManager.connect();
        }

        // sql sorgusu
        String query = "SELECT Malzemeler.MalzemeID, Malzemeler.MalzemeAdi, Tarif_Malzeme.MalzemeMiktar, Malzemeler.MalzemeBirim, Malzemeler.BirimFiyat " +
                "FROM Tarif_Malzeme " +
                "JOIN Malzemeler ON Tarif_Malzeme.MalzemeID = Malzemeler.MalzemeID " +
                "WHERE Tarif_Malzeme.TarifID = ?";
        PreparedStatement pstmt = dbManager.getConnection().prepareStatement(query);

        // tarif id'sini sorguya ekle
        pstmt.setInt(1, recipeID);
        ResultSet rs = pstmt.executeQuery();

        // sonuçları işle
        while (rs.next()) {
            int id = rs.getInt("MalzemeID");
            String name = rs.getString("MalzemeAdi");
            double quantity = rs.getDouble("MalzemeMiktar");
            String unit = rs.getString("MalzemeBirim");
            double pricePerUnit = rs.getDouble("BirimFiyat");

            Ingredient ingredient = new Ingredient(id, name, quantity, unit, pricePerUnit);
            ingredients.add(ingredient);
        }
    } catch (SQLException e) {
        System.out.println("sorgu başarısız: " + e.getMessage());
    }
    return ingredients;
}

// envantere malzeme ekler
public void addToInventory(int ingredientID, double quantity) {
    try {
        // veritabanı bağlantısı yoksa bağlan
        if (dbManager.getConnection() == null) {
            dbManager.connect();
        }

        // sql sorgusu
        String updateInventory = "UPDATE Malzemeler SET ToplamMiktar = ToplamMiktar + ? WHERE MalzemeID = ?";

        PreparedStatement pstmt = dbManager.getConnection().prepareStatement(updateInventory);
        pstmt.setDouble(1, quantity);
        pstmt.setInt(2, ingredientID);
        pstmt.executeUpdate();

        System.out.println("[OK] malzeme envantere eklendi.");
    } catch (SQLException e) {
        System.out.println("[HATA] malzeme envantere eklenemedi: " + e.getMessage());
    }
}

// eksik malzeme maliyetini hesaplar
public double calculateMissingCost(int recipeID) {
    double missingCost = 0.0;

    ArrayList<Ingredient> recipeIngredients = getRecipeIngredients(recipeID);
    for (Ingredient ingredient : recipeIngredients) {
        Ingredient availableIngredient = getIngredientByID(ingredient.getId());
        if (availableIngredient == null || availableIngredient.getQuantity() < ingredient.getQuantity()) {
            if (availableIngredient != null) {
                missingCost += (ingredient.getQuantity() - availableIngredient.getQuantity()) * availableIngredient.getPricePerUnit();
            } else {
                missingCost += ingredient.getQuantity() * ingredient.getPricePerUnit();
            }
        }
    }

    return missingCost;
}

// tarifin toplam maliyetini hesaplar
public double calculateCost(int recipeID) {
    double totalCost = 0.0;
    try {
        // veritabanı bağlantısı yoksa bağlan
        if (dbManager.getConnection() == null) {
            dbManager.connect();
        }

        // sql sorgusu
        String query = "SELECT Malzemeler.BirimFiyat, Tarif_Malzeme.MalzemeMiktar " +
                "FROM Tarif_Malzeme " +
                "JOIN Malzemeler ON Tarif_Malzeme.MalzemeID = Malzemeler.MalzemeID " +
                "WHERE Tarif_Malzeme.TarifID = ?";
        PreparedStatement pstmt = dbManager.getConnection().prepareStatement(query);

        // tarif id'sini sorguya ekle
        pstmt.setInt(1, recipeID);
        ResultSet rs = pstmt.executeQuery();

        // sonuçları işle
        while (rs.next()) {
            double unitPrice = rs.getDouble("BirimFiyat");
            double quantity = rs.getDouble("MalzemeMiktar");
            totalCost += unitPrice * quantity;
        }
    } catch (SQLException e) {
        System.out.println("maliyet hesaplama başarısız: " + e.getMessage());
    }
    return totalCost;
}

// envanterden malzeme siler
public void deleteFromInventory(int ingredientID) {
    try {
        // veritabanı bağlantısı yoksa bağlan
        if (dbManager.getConnection() == null) {
            dbManager.connect();
        }

        // sql sorgusu
        String updateInventory = "UPDATE Malzemeler SET ToplamMiktar = 0 WHERE MalzemeID = ?";
        PreparedStatement pstmt = dbManager.getConnection().prepareStatement(updateInventory);
        pstmt.setInt(1, ingredientID);
        pstmt.executeUpdate();

        System.out.println("[OK] malzeme envanterden silindi.");
    } catch (SQLException e) {
        System.out.println("[HATA] malzeme envanterden silinemedi: " + e.getMessage());
    }
}

// tarifleri arar
public ArrayList<Recipe> searchRecipes(String searchTerm) {
    ArrayList<Recipe> recipes = new ArrayList<>();
    try {
        // sql sorgusu
        String query = "SELECT * FROM Tarifler WHERE TarifAdi LIKE ?";
        PreparedStatement pstmt = dbManager.getConnection().prepareStatement(query);

        // arama terimini sorguya ekle
        pstmt.setString(1, "%" + searchTerm + "%");
        ResultSet rs = pstmt.executeQuery();

        // sonuçları işle
        while (rs.next()) {
            Recipe recipe = new Recipe();
            recipe.setRecipeID(rs.getInt("TarifID"));
            recipe.setName(rs.getString("TarifAdi"));
            recipe.setCategory(rs.getString("Kategori"));
            recipe.setPrepTime(rs.getInt("HazirlamaSuresi"));
            recipes.add(recipe);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return recipes;
}// tarifleri filtrelemek için kullanılır
public ArrayList<Recipe> getFilteredRecipes(String category, String sortingOption, String costMin, String costMax, String typeMinCount, String typeMaxCount, ArrayList<String> selectedIngredients) {
    ArrayList<Recipe> recipes = new ArrayList<>();
    try {
        if (dbManager.getConnection() == null) {
            dbManager.connect();
        }

        StringBuilder query = new StringBuilder("SELECT * FROM Tarifler WHERE 1=1");

        // kategori filtresi
        if (!category.equals("Hepsi")) {
            query.append(" AND Kategori = ?");
        }



        // minimum malzeme sayısı filtresi
        if (!typeMinCount.isEmpty()) {
            query.append(" AND TarifID IN (SELECT TarifID FROM Tarif_Malzeme GROUP BY TarifID HAVING COUNT(TarifID) >= ?)");
        }

        // maksimum malzeme sayısı filtresi
        if (!typeMaxCount.isEmpty()) {
            query.append(" AND TarifID IN (SELECT TarifID FROM Tarif_Malzeme GROUP BY TarifID HAVING COUNT(TarifID) <= ?)");
        }

        // seçilen malzemeler filtresi
        if (!selectedIngredients.isEmpty()) {
            query.append(" AND TarifID IN (SELECT TarifID FROM Tarif_Malzeme WHERE MalzemeID IN (SELECT MalzemeID FROM Malzemeler WHERE MalzemeAdi IN (");
            for (int i = 0; i < selectedIngredients.size(); i++) {
                query.append("?");
                if (i != selectedIngredients.size() - 1) {
                    query.append(", ");
                }
            }
            query.append(")) GROUP BY TarifID HAVING COUNT(DISTINCT MalzemeID) = ?)");
        }

        // sıralama seçeneği
        String orderByColumn = "";
        switch (sortingOption) {
            case "Alfabetik":
                orderByColumn = "TarifAdi";
                break;
            case "Hazırlama Süresi":
                orderByColumn = "HazirlamaSuresi";
                break;
            case "Maliyet":
                orderByColumn = "Maliyet";
                break;
            default:
                orderByColumn = "TarifAdi";
        }

        if (!orderByColumn.isEmpty() && !orderByColumn.equals("Maliyet")) {
            query.append(" ORDER BY ").append(orderByColumn);
        }

        PreparedStatement pstmt = dbManager.getConnection().prepareStatement(query.toString());
        int index = 1;

        if (!category.equals("Hepsi")) {
            pstmt.setString(index, category);
            index++;
        }



        if (!typeMinCount.isEmpty()) {
            pstmt.setInt(index, Integer.parseInt(typeMinCount));
            index++;
        }

        if (!typeMaxCount.isEmpty()) {
            pstmt.setInt(index, Integer.parseInt(typeMaxCount));
            index++;
        }

        if (!selectedIngredients.isEmpty()) {
            for (String ingredient : selectedIngredients) {
                pstmt.setString(index, ingredient);
                index++;
            }
            pstmt.setInt(index, selectedIngredients.size());
        }

        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            Recipe recipe = new Recipe();
            recipe.setRecipeID(rs.getInt("TarifID"));
            recipe.setName(rs.getString("TarifAdi"));
            recipe.setCategory(rs.getString("Kategori"));
            recipe.setPrepTime(rs.getInt("HazirlamaSuresi"));
            recipe.setInstructions(rs.getString("Talimatlar"));
            recipes.add(recipe);
        }
    } catch (SQLException e) {
        System.out.println("Query failed: " + e.getMessage());
    }
if (!costMin.isEmpty()) {
    double minCost = Double.parseDouble(costMin);
    recipes.removeIf(recipe -> calculateCost(recipe.getRecipeID()) < minCost);
}

    if (!costMax.isEmpty()) {
    double maxCost = Double.parseDouble(costMax);
    recipes.removeIf(recipe -> calculateCost(recipe.getRecipeID()) > maxCost);
}
    if(sortingOption.equals("Maliyet")) {
        for (Recipe recipe : recipes) {
            recipe.setCost(calculateCost(recipe.getRecipeID()));
        }
        recipes.sort(Comparator.comparingDouble(Recipe::getCost));
    }
    return recipes;
}

// tarifleri aramak ve filtrelemek için kullanılır
public ArrayList<Recipe> searchAndFilterRecipes(String searchTerm, String category, String sortingOption, String costMin, String costMax, String typeMinCount, String typeMaxCount, ArrayList<String> selectedIngredients) {
    ArrayList<Recipe> recipes = new ArrayList<>();
    try {
        if (dbManager.getConnection() == null) {
            dbManager.connect();
        }

        // Construct the main query
        StringBuilder query = new StringBuilder("SELECT * FROM Tarifler WHERE TarifAdi LIKE ?");

        // Add filtering conditions
        if (!category.equals("Hepsi")) {
            query.append(" AND Kategori = ?");
        }

        if (!typeMinCount.isEmpty()) {
            query.append(" AND TarifID IN (SELECT TarifID FROM Tarif_Malzeme GROUP BY TarifID HAVING COUNT(TarifID) >= ?)");
        }

        if (!typeMaxCount.isEmpty()) {
            query.append(" AND TarifID IN (SELECT TarifID FROM Tarif_Malzeme GROUP BY TarifID HAVING COUNT(TarifID) <= ?)");
        }

        if (!selectedIngredients.isEmpty()) {
            query.append(" AND TarifID IN (SELECT TarifID FROM Tarif_Malzeme WHERE MalzemeID IN (SELECT MalzemeID FROM Malzemeler WHERE MalzemeAdi IN (");
            for (int i = 0; i < selectedIngredients.size(); i++) {
                query.append("?");
                if (i != selectedIngredients.size() - 1) {
                    query.append(", ");
                }
            }
            query.append(")) GROUP BY TarifID HAVING COUNT(DISTINCT MalzemeID) = ?)");
        }

        // Add sorting option
        String orderByColumn = "";
        switch (sortingOption) {
            case "Alfabetik":
                orderByColumn = "TarifAdi";
                break;
            case "Hazırlama Süresi":
                orderByColumn = "HazirlamaSuresi";
                break;
            case "Maliyet":
                orderByColumn = "Maliyet"; // Ensure this column exists in your table
                break;
            default:
                orderByColumn = "TarifAdi"; // Default sorting
        }

        if (!orderByColumn.isEmpty() && !orderByColumn.equals("Maliyet")) {
            query.append(" ORDER BY ").append(orderByColumn);
        }

        // Prepare the SQL statement
        PreparedStatement pstmt = dbManager.getConnection().prepareStatement(query.toString());
        int index = 1;

        // Bind the search term
        System.out.println("Search term: " + searchTerm);
        pstmt.setString(index, "%" + searchTerm + "%");
        index++;

        // Bind category and other filter parameters
        if (!category.isEmpty() && !category.equals("Hepsi")) {
            pstmt.setString(index, category);
            index++;
        }

        if (!typeMinCount.isEmpty()) {
            pstmt.setInt(index, Integer.parseInt(typeMinCount));
            index++;
        }

        if (!typeMaxCount.isEmpty()) {
            pstmt.setInt(index, Integer.parseInt(typeMaxCount));
            index++;
        }

        if (!selectedIngredients.isEmpty()) {
            for (String ingredient : selectedIngredients) {
                pstmt.setString(index, ingredient);
                index++;
            }
            pstmt.setInt(index, selectedIngredients.size());
        }

        // Execute the query and process the results
        ResultSet rs = pstmt.executeQuery();
        System.out.println("Query: " + pstmt.toString());
        while (rs.next()) {
            Recipe recipe = new Recipe();
            recipe.setRecipeID(rs.getInt("TarifID"));
            recipe.setName(rs.getString("TarifAdi"));
            recipe.setCategory(rs.getString("Kategori"));
            recipe.setPrepTime(rs.getInt("HazirlamaSuresi"));
            recipe.setInstructions(rs.getString("Talimatlar"));
            recipes.add(recipe);
        }
        for (Recipe recipe : recipes) {
            System.out.println(recipe.getName());
        }
    } catch (SQLException e) {
        System.out.println("Query failed: " + e.getMessage());
    }

    // Filter by cost
    if (!costMin.isEmpty()) {
        double minCost = Double.parseDouble(costMin);
        recipes.removeIf(recipe -> calculateCost(recipe.getRecipeID()) < minCost);
    }

    if (!costMax.isEmpty()) {
        double maxCost = Double.parseDouble(costMax);
        recipes.removeIf(recipe -> calculateCost(recipe.getRecipeID()) > maxCost);
    }

    // Sort by cost if selected
    if (sortingOption.equals("Maliyet")) {
        for (Recipe recipe : recipes) {
            recipe.setCost(calculateCost(recipe.getRecipeID()));
        }
        recipes.sort(Comparator.comparingDouble(Recipe::getCost));
    }

    return recipes;
}

// tarifin malzemelerle eşleşme yüzdesini hesaplar
public float calculateMatchingPercentage(int recipeID, ArrayList<String> selectedIngredients) {
    try {
        DataManager dataManager = new DataManager(dbManager);
        ArrayList<Ingredient> recipeIngredients = dataManager.getRecipeIngredients(recipeID);
        int totalIngredients = recipeIngredients.size();
        int matchingIngredients = 0;
        for (Ingredient ingredient : recipeIngredients) {
            if (selectedIngredients.contains(ingredient.getName())) {
                matchingIngredients++;
            }
        }
        return (float) matchingIngredients / totalIngredients * 100;
    } catch (Exception e) {
        System.out.println("Hata: " + e.getMessage());
    }
    return 0;
}

// envanterdeki malzemeleri getirir
public ArrayList<Ingredient> getInventoryIngredients() {
    ArrayList<Ingredient> ingredients = new ArrayList<>();
    try {
        if (dbManager.getConnection() == null) {
            dbManager.connect();
        }

        String query = "SELECT * FROM Malzemeler WHERE ToplamMiktar > 0";
        Statement stmt = dbManager.getConnection().createStatement();
        ResultSet rs = stmt.executeQuery(query);

        while (rs.next()) {
            Ingredient ingredient = new Ingredient(
                    rs.getInt("MalzemeID"),
                    rs.getString("MalzemeAdi"),
                    rs.getDouble("ToplamMiktar"),
                    rs.getString("MalzemeBirim"),
                    rs.getDouble("BirimFiyat")
            );
            ingredients.add(ingredient);
        }
    } catch (SQLException e) {
        System.out.println("Query failed: " + e.getMessage());
    }
    return ingredients;
}
    public boolean isDuplicateRecipe(String recipeName) {
        try {
            if (dbManager.getConnection() == null) {
                dbManager.connect();
            }

            String query = "SELECT COUNT(*) FROM Tarifler WHERE TarifAdi = ?";
            PreparedStatement pstmt = dbManager.getConnection().prepareStatement(query);
            pstmt.setString(1, recipeName);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println("Duplicate check failed: " + e.getMessage());
        }
        return false;
    }
}