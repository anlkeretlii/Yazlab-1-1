// UpdateIngredientGUI.java
package ui;

import core.DBManager;
import core.DataManager;
import obj.Ingredient;

import javax.swing.*;

// bu sınıf, malzeme güncelleme arayüzünü oluşturur
public class UpdateIngredientGUI extends JFrame {
    private JTextField ingrNameTField; // malzeme adı için text field
    private JComboBox unitCBox; // birim seçimi için combo box
    private JTextField unitCostTField; // birim maliyeti için text field
    private JButton okBtn; // tamam butonu
    private JButton cancelBtn; // iptal butonu
    private JPanel mainPanel; // ana panel
    private DataManager dataManager; // veri yöneticisi
    private DBManager dbManager; // veritabanı yöneticisi
    private MainGUI mainGUI; // ana arayüz referansı
    private Ingredient ingredient; // güncellenecek malzeme

    // constructor, arayüzü başlatır
    public UpdateIngredientGUI(JFrame parent, MainGUI mainGUI, Ingredient ingredient) {
        super("Update Ingredient"); // pencere başlığı
        this.mainGUI = mainGUI; // referansı başlat
        this.ingredient = ingredient; // malzemeyi ata

        // arayüz bileşenlerini ayarla
        setContentPane(mainPanel); // ana paneli ayarla
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // pencere kapatılınca sadece bu pencereyi kapat
        pack(); // pencereyi boyutlandır
        setLocationRelativeTo(parent); // pencereyi ortala

        // başlangıç değerlerini ayarla
        ingrNameTField.setText(ingredient.getName()); // malzeme adını text field'a yaz
        unitCostTField.setText(String.valueOf(ingredient.getPricePerUnit())); // birim maliyetini text field'a yaz
        unitCBox.setSelectedItem(ingredient.getUnit()); // birimi combo box'a yaz

        // tamam butonuna tıklanınca
        okBtn.addActionListener(e -> {
            DBManager dbManager = new DBManager(); // yeni db yöneticisi oluştur
            DataManager dataManager = new DataManager(dbManager); // yeni veri yöneticisi oluştur
            System.out.println("Updated Name: " + ingrNameTField.getText()); // güncellenen adı yazdır

            // malzemeyi güncelle
            dataManager.updateIngredient(
                    ingredient.getId(), // malzeme id'si
                    ingrNameTField.getText(), // malzeme adı
                    Double.parseDouble(unitCostTField.getText()), // birim maliyeti
                    unitCBox.getSelectedItem().toString() // birim
            );

            mainGUI.updateIngredientsTable(); // malzeme tablosunu güncelle
            mainGUI.updateIngredientCheckBoxFilter(); // malzeme checkbox'larını güncelle
            dispose(); // pencereyi kapat
        });

        // iptal butonuna tıklanınca pencereyi kapat
        cancelBtn.addActionListener(e -> dispose());
    }

    // createUIComponents method to initialize components created via GUI Designer
    private void createUIComponents() {
        // Initialize the JComboBox with units
        unitCBox = new JComboBox(); // combo box oluştur
        unitCBox.addItem("kg"); // kg birimi ekle
        unitCBox.addItem("g"); // g birimi ekle
        unitCBox.addItem("lt"); // lt birimi ekle
        unitCBox.addItem("ml"); // ml birimi ekle
        unitCBox.addItem("adet"); // adet birimi ekle
    }
}