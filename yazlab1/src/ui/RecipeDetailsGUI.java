// tarif detaylarını gösteren arayüz
package ui;

import obj.Ingredient;
import obj.Recipe;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;

public class RecipeDetailsGUI extends JDialog {
    private JTextArea txtarea_details; // tarif talimatları için metin alanı
    private JTable tbl_ingredients; // malzemeleri göstermek için tablo
    private JLabel lbl_recipeName; // tarif adı etiketi
    private JLabel lbl_prepTime; // hazırlama süresi etiketi
    private JLabel lbl_category; // kategori etiketi
    private JLabel lbl_cost; // maliyet etiketi
    private JPanel mainPanel; // ana panel
    private MainGUI mainGUI; // ana arayüz referansı
    private Recipe recipe; // tarif nesnesi
    private double cost; // tarif maliyeti
    private ArrayList<Ingredient> ingredients; // malzeme listesi

    public RecipeDetailsGUI(JFrame parent, MainGUI mainGUI, Recipe recipe, double cost, ArrayList<Ingredient> ingredients) {
        super(parent, "Tarif Detayları", true); // modal pencere oluştur
        this.mainGUI = mainGUI; // ana arayüzü ata
        this.recipe = recipe; // tarif nesnesini ata
        this.cost = cost; // maliyeti ata
        this.ingredients = ingredients; // malzemeleri ata
        setContentPane(mainPanel); // ana paneli ayarla
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); // pencere kapatılınca sadece bu pencereyi kapat
        pack(); // pencereyi boyutlandır
        setLocationRelativeTo(parent); // pencereyi ortala

        // tarif bilgilerini etiketlere ata
        lbl_recipeName.setText(recipe.getName()); // tarif adını etikete yaz
        lbl_prepTime.setText(recipe.getPrepTime() + " dk"); // hazırlama süresini etikete yaz
        lbl_category.setText(recipe.getCategory()); // kategoriyi etikete yaz
        lbl_cost.setText(cost + " TL"); // maliyeti etikete yaz

        // tarif talimatlarını metin alanına yaz
        txtarea_details.setText(recipe.getInstructions()); // talimatları metin alanına yaz
        txtarea_details.setEditable(false); // metin alanını düzenlenemez yap

        // tablo sütun adları
        String[] columnNames = {"Ingredient Name", "Quantity", "Unit", "Maliyet"};

        // tablo modeli oluştur
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        // tablo modelini tabloya ata
        tbl_ingredients.setModel(model);

        // malzemeleri tabloya ekle
        for (Ingredient ingredient : ingredients) {
            model.addRow(new Object[]{ingredient.getName(), ingredient.getQuantity(), ingredient.getUnit(), String.format("%.2f TL", ingredient.getPricePerUnit() * ingredient.getQuantity())});
        }
    }
}