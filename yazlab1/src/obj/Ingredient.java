// ingredient sınıfı, tariflerde kullanılan malzemeleri temsil eder
package obj;

public class Ingredient {
    private int ingredientID; // malzeme id'si
    private String name; // malzeme adı
    private double quantity; // malzeme miktarı

    // tam özellikli constructor
    public Ingredient(int ingredientID, String name, double quantity, String unit, double pricePerUnit) {
        this.ingredientID = ingredientID; // id'yi ata
        this.name = name; // adı ata
        this.quantity = quantity; // miktarı ata
        this.unit = unit; // birimi ata
        this.pricePerUnit = pricePerUnit; // birim fiyatı ata
    }

    // sadece ad ve miktar içeren constructor
    public Ingredient(String name, double quantity) {
        this.name = name; // adı ata
        this.quantity = quantity; // miktarı ata
    }

    // id, ad ve miktar içeren constructor
    public Ingredient(int id, String name, double quantity) {
        this.ingredientID = id; // id'yi ata
        this.name = name; // adı ata
        this.quantity = quantity; // miktarı ata
    }

    // id'yi döndürür
    public int getId() {
        return ingredientID; // id'yi döndür
    }

    // id'yi ayarlar
    public void setId(int ingredientID) {
        this.ingredientID = ingredientID; // id'yi ayarla
    }

    // adı döndürür
    public String getName() {
        return name; // adı döndür
    }

    // adı ayarlar
    public void setName(String name) {
        this.name = name; // adı ayarla
    }

    // miktarı döndürür
    public double getQuantity() {
        return quantity; // miktarı döndür
    }

    // miktarı ayarlar
    public void setQuantity(double quantity) {
        this.quantity = quantity; // miktarı ayarla
    }

    // birimi döndürür
    public String getUnit() {
        return unit; // birimi döndür
    }

    // birimi ayarlar
    public void setUnit(String unit) {
        this.unit = unit; // birimi ayarla
    }

    // birim fiyatı döndürür
    public double getPricePerUnit() {
        return pricePerUnit; // birim fiyatı döndür
    }

    // birim fiyatı ayarlar
    public void setPricePerUnit(double pricePerUnit) {
        this.pricePerUnit = pricePerUnit; // birim fiyatı ayarla
    }

    private String unit; // malzeme birimi
    private double pricePerUnit; // malzeme birim fiyatı
}