package obj;

public class RecipeIngredient {
    private int recipeID; // tarif id'si
    private int ingredientID; // malzeme id'si
    private double quantity; // malzeme miktarı

    // tarif id'sini döndürür
    public int getRecipeID() {
        return recipeID; // tarif id'si
    }

    // tarif id'sini ayarlar
    public void setRecipeID(int recipeID) {
        this.recipeID = recipeID; // tarif id'si
    }

    // malzeme id'sini döndürür
    public int getIngredientID() {
        return ingredientID; // malzeme id'si
    }

    // malzeme id'sini ayarlar
    public void setIngredientID(int ingredientID) {
        this.ingredientID = ingredientID; // malzeme id'si
    }

    // malzeme miktarını döndürür
    public double getQuantity() {
        return quantity; // malzeme miktarı
    }

    // malzeme miktarını ayarlar
    public void setQuantity(double quantity) {
        this.quantity = quantity; // malzeme miktarı
    }
}