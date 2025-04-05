package ui;

import com.sun.tools.javac.Main;
import core.DBManager;
import core.DataManager;

import javax.swing.*;

public class AddIngredientGUI extends JFrame {
    private JTextField ingrNameTField; // malzeme adı için text field
    private JComboBox unitCBox; // birim seçimi için combo box
    private JTextField unitCostTField; // birim maliyeti için text field
    private JButton okBtn; // tamam butonu
    private JButton cancelBtn; // iptal butonu
    private JPanel mainPanel; // ana panel
    private DataManager dataManager; // veri yöneticisi
    private DBManager dbManager; // veritabanı yöneticisi
    private MainGUI mainGUI; // ana arayüz referansı

    public AddIngredientGUI(JFrame parent, MainGUI mainGUI) {
        super("Add Ingredient"); // pencere başlığı
        this.mainGUI = mainGUI;  // referansı başlat

        setContentPane(mainPanel); // ana paneli ayarla
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // pencere kapatılınca sadece bu pencereyi kapat
        pack(); // pencereyi boyutlandır
        setLocationRelativeTo(parent); // pencereyi ortala
        okBtn.addActionListener(e -> { // tamam butonuna tıklanınca
            DBManager dbManager = new DBManager(); // yeni db yöneticisi oluştur
            DataManager dataManager = new DataManager(dbManager); // yeni veri yöneticisi oluştur
            System.out.println(unitCBox.getSelectedItem().toString()); // seçilen birimi yazdır
            dataManager.addIngredient(ingrNameTField.getText(), Double.parseDouble(unitCostTField.getText()), unitCBox.getSelectedItem().toString()); // malzeme ekle
            mainGUI.updateIngredientsTable(); // malzeme tablosunu güncelle
            mainGUI.updateIngredientCheckBoxFilter(); // malzeme checkbox'larını güncelle
            dispose(); // pencereyi kapat
        });
        cancelBtn.addActionListener(e -> dispose()); // iptal butonuna tıklanınca pencereyi kapat
    }

    public void createUIComponents() {
        String[] units = {"kg", "g", "lt", "ml", "adet"}; // birim seçenekleri
        unitCBox = new JComboBox(); // combo box oluştur

        unitCBox.setModel(new DefaultComboBoxModel(units)); // combo box'a birimleri ekle
    }
}