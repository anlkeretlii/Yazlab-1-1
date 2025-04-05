package ui;

import core.DBManager;
import core.DataManager;
import obj.Ingredient;
import obj.Recipe;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

// tarif güncelleme arayüzü
public class UpdateRecipeGUI extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField recipeNameTField;
    private JSpinner prepTimeSpinner;
    private JTextArea detailsTxtArea;
    private JButton malzemeEkleButton;
    private JComboBox categoryCBox;
    private JScrollPane ingrScrollPane;
    private JTable table1;
    private JButton malzemeSilButton;
    private ArrayList<Ingredient> selectedIngredients;
    private ArrayList<JCheckBox> ingrCheckBoxList;
    private MainGUI mainGUI;
    private Recipe recipe;

    // constructor
    public UpdateRecipeGUI(JFrame parent, MainGUI mainGUI, Recipe recipe) {
        super(parent, "Yeni Tarif Ekle", true);  // modaliteyi true yap
        this.mainGUI = mainGUI;  // referansı başlat
        this.recipe = recipe;
        setContentPane(contentPane);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);  // sadece bu pencereyi kapat
        pack();
        setLocationRelativeTo(parent);  // pencereyi ortala
        DataManager dataManager = new DataManager(new DBManager());
        selectedIngredients = dataManager.getRecipeIngredients(recipe.getRecipeID()); // listeyi başlat

        updateIngredientsTable();
        buttonOK.addActionListener(e -> onOK());
        buttonCancel.addActionListener(e -> onCancel());

        // pencere kapatılınca onCancel() çağır
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }

            // pencere kapatılınca updateRecipeTable çağır
            public void windowClosed(WindowEvent e) {
                mainGUI.updateRecipesTable(null);
            }
        });

        // ESCAPE tuşuna basılınca onCancel() çağır
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    // tamam butonuna tıklanınca
    private void onOK() {
        // kaydetme işlemleri burada yapılabilir
        System.out.println("Recipe added!");
        DataManager dataManager = new DataManager(new DBManager());

        dataManager.updateRecipe(recipe.getRecipeID(), recipeNameTField.getText(), categoryCBox.getSelectedItem().toString(), (int) prepTimeSpinner.getValue(), detailsTxtArea.getText(), selectedIngredients);
        dispose();
    }

    // iptal butonuna tıklanınca
    private void onCancel() {
        // gerekli işlemler
        dispose();
    }

    // arayüz bileşenlerini oluştur
    private void createUIComponents() {
        // TODO: özel bileşen oluşturma kodu buraya
        String[] categories = {"Kahvaltılık", "Çorbalar", "Ana Yemekler", "Tatlılar", "Salatalar", "Atıştırmalıklar", "İçecekler"};
        categoryCBox = new JComboBox(categories);
        categoryCBox.setSelectedItem(recipe.getCategory());
        recipeNameTField = new JTextField(recipe.getName());
        prepTimeSpinner = new JSpinner(new SpinnerNumberModel(recipe.getPrepTime(), 0, 1000, 1));
        detailsTxtArea = new JTextArea(recipe.getInstructions());

        malzemeEkleButton = new JButton("Malzeme Ekle");
        malzemeEkleButton.addActionListener(e -> addIngredientDialog((JDialog) SwingUtilities.getWindowAncestor(contentPane)));
        malzemeSilButton = new JButton("Malzeme Sil");
        malzemeSilButton.addActionListener(e -> deleteIngredient());
    }

    // malzeme ekleme diyalogu
    private void addIngredientDialog(JDialog parent) {
        JDialog dialog = new JDialog(parent, "Malzeme Ekle", true);
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(3, 2));
        DataManager dataManager = new DataManager(new DBManager());
        ArrayList<Ingredient> ingredients = dataManager.getAllIngredients();
        JComboBox ingredientCBox = new JComboBox();
        JTextField countTField = new JTextField();
        for (Ingredient ingredient : ingredients) {
            ingredientCBox.addItem(ingredient.getName());
        }
        JButton addIngredientButton = new JButton("Ekle");
        addIngredientButton.addActionListener(e -> {
            selectedIngredients.add(new Ingredient(ingredientCBox.getSelectedItem().toString(), Double.parseDouble(countTField.getText())));
            updateIngredientsTable();
            dialog.dispose();
        });
        JButton cancelButton = new JButton("İptal");
        cancelButton.addActionListener(e -> dialog.dispose());
        JLabel ingredientLabel = new JLabel("Malzeme:");
        mainPanel.add(ingredientLabel);
        mainPanel.add(ingredientCBox);
        JLabel countLabel = new JLabel("Miktar:");
        mainPanel.add(countLabel);
        mainPanel.add(countTField);

        mainPanel.add(addIngredientButton);
        mainPanel.add(cancelButton);

        dialog.setContentPane(mainPanel);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    // malzeme tablosunu güncelle
    private void updateIngredientsTable() {
        String[] columnNames = {"Malzeme", "Miktar"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (Ingredient ingredient : selectedIngredients) {
            model.addRow(new Object[]{ingredient.getName(), ingredient.getQuantity()});
        }

        table1.setModel(model);
    }

    // malzeme silme işlemi
    private void deleteIngredient() {
        int selectedRow = table1.getSelectedRow();
        if (selectedRow != -1) {
            selectedIngredients.remove(selectedRow);
            updateIngredientsTable();
        }
    }
}