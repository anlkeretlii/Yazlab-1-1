package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import obj.Ingredient;
import obj.Recipe;
import core.DBManager;
import core.DataManager;

public class MainGUI {
    private JPanel MainPanel;
    private JPanel rightPanel;
    private JPanel inventoryPanel;
    private JPanel ingredientsPanel;
    private JPanel centerPanel;
    private JPanel searchBar;
    private JButton invAddBtn;
    private JButton invDelBtn;
    private JButton invUpdtBtn;
    private JButton ingrAddBtn;
    private JButton ingrDelBtn;
    private JButton ingrUpdtBtn;
    private JTable invTable;
    private JTable ingrTable;
    private JTextField tfield_searchBar;
    private JButton searchBtn;
    private JTable recipesTable;
    private JPanel recipesPanel;
    private JButton ekleButton;
    private JButton silButton;
    private JButton güncelleButton;
    private JPanel recipesBottomPanel;
    private JPanel invBotPanel;
    private JPanel ingrBotPanel;
    private JTextField costMinTField;
    private JTextField costMaxTField;
    private JComboBox categoryCBox;
    private JComboBox sortCBox;
    private JTextField typeMinCountTField;
    private JTextField typeMaxCountTField;
    private JButton reloadBtn;
    private JButton filterBtn;
    private JPanel filtersPanel;
    private JPanel sortingfPanel;
    private JPanel costfPanel;
    private JPanel ingrfPanel;
    private JPanel categoryfPanel;
    private JPanel ingrVarfPanel;
    private JPanel filterBtnsPanel;
    private JScrollPane ingrfScrollPane;
    private JScrollPane recipesScollPane;
    private JScrollPane ingrScrollPane;
    private JScrollPane invScrollPane;

    // bu sınıf, tarif yöneticisi uygulamasının ana arayüzünü oluşturur
public MainGUI() {
    initializeFrame();
}

// frame'i başlatır
private void initializeFrame() {
    JFrame frame = new JFrame("Recipe Manager");
    frame.setContentPane(MainPanel);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
}

// ui bileşenlerini başlatır
private void createUIComponents() {
    initializeButtons();
    initializeIngredientCheckBoxFilter();
    initializeRecipeTable();
    initializeInventoryTable();
    initializeIngredientsTable();

    initializeSortingComboBox();
    initializeCategoryComboBox();
    initializeCostFilter();
    initializeTypeCountFilter();
    initializeRecipeTableMouseListener();
}

// tarif tablosunu başlatır
private void initializeRecipeTable() {
    recipesTable = new JTable();
    updateRecipesTable(null);
    recipesScollPane = new JScrollPane(recipesTable);
}

// tarif tablosunu günceller
public void updateRecipesTable(ArrayList<Recipe> recipes) {
    DataManager dataManager = new DataManager(new DBManager());
    if (recipes == null) {
        recipes = dataManager.getAllRecipes();
    }

    String[] columnNames = {"Tarif Adı", "Kategori", "Hazırlama Süresi", "Maliyet", "Sana Maliyeti", "Malzeme Eşleşme"};
    Object[][] data = new Object[recipes.size()][6];
    ArrayList<String> selectedIngredients = new ArrayList<>();
    Component[] components = ((JPanel) ingrfScrollPane.getViewport().getView()).getComponents();
    for (Component component : components) {
        if (component instanceof JCheckBox) {
            JCheckBox checkBox = (JCheckBox) component;
            if (checkBox.isSelected()) {
                selectedIngredients.add(checkBox.getText());
            }
        }
    }
    for (int i = 0; i < recipes.size(); i++) {
        Recipe recipe = recipes.get(i);
        data[i][0] = recipe.getName();
        data[i][1] = recipe.getCategory();
        data[i][2] = recipe.getPrepTime();
        data[i][3] = dataManager.calculateCost(recipe.getRecipeID()) + " TL";
        double missingCost = dataManager.calculateMissingCost(recipe.getRecipeID());
        data[i][4] = String.format("%.2f TL", missingCost);
        data[i][5] = dataManager.calculateMatchingPercentage(recipe.getRecipeID(), selectedIngredients) + "%";
    }

    DefaultTableModel model = new DefaultTableModel(data, columnNames);
    recipesTable.setModel(model);

    // "sana maliyeti" (4. sütun) için özel render'ı anonim sınıf ile uyguluyoruz
    recipesTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (column == 4) {  // "sana maliyeti" sütunu
                String valueStr = value.toString().replace(" TL", "");  // "TL" ifadesini kaldırıyoruz
                valueStr = valueStr.replace(",", "."); // Replace comma with dot for decimal parsing
                double missingCost = Double.parseDouble(valueStr);

                // missingCost değerine göre arka plan rengini belirle
                if (missingCost > 0) {
                    c.setBackground(Color.RED);  // kırmızı
                    c.setForeground(Color.WHITE);  // beyaz yazı
                } else {
                    c.setBackground(Color.GREEN);  // yeşil
                    c.setForeground(Color.BLACK);  // siyah yazı
                }
            } else {
                // diğer sütunlar için varsayılan renkleri kullan
                c.setBackground(Color.WHITE);
                c.setForeground(Color.BLACK);
            }

            return c;
        }
    });
}

// arayüzdeki sıralama seçeneklerini başlatır
private void initializeSortingComboBox() {
    // sıralama seçenekleri
    String[] sortingOptions = {"Alfabetik", "Hazırlama Süresi", "Maliyet"};
    sortCBox = new JComboBox(sortingOptions);
}

// kategori seçeneklerini başlatır
private void initializeCategoryComboBox() {
    // kategori seçenekleri
    String[] categoryOptions = {"Hepsi","Kahvaltılık", "Çorbalar", "Ana Yemekler", "Tatlılar", "Salatalar", "Atıştırmalıklar","İçecekler"};
    categoryCBox = new JComboBox(categoryOptions);
}

// maliyet filtreleme alanlarını başlatır
private void initializeCostFilter() {
    costMinTField = new JTextField();
    costMaxTField = new JTextField();
}

// malzeme türü sayısı filtreleme alanlarını başlatır
private void initializeTypeCountFilter() {
    typeMinCountTField = new JTextField();
    typeMaxCountTField = new JTextField();
}

// envanter tablosunu başlatır
private void initializeInventoryTable(){
    invTable = new JTable();
    updateInventoryTable();
    invScrollPane = new JScrollPane(invTable);
}

// envanter tablosunu günceller
public void updateInventoryTable() {
    DataManager dataManager = new DataManager(new DBManager());
    ArrayList<Ingredient> ingredients = dataManager.getAllIngredients();

    // miktarı 0 olan malzemeleri filtrele
    ArrayList<Ingredient> filteredIngredients = new ArrayList<>();
    for (Ingredient ingredient : ingredients) {
        if (ingredient.getQuantity() > 0) {
            filteredIngredients.add(ingredient);
        }
    }

    // tablo sütun adları
    String[] columnNames = {"Malzeme Adı", "Miktar", "Birim"};
    Object[][] data = new Object[filteredIngredients.size()][3];

    // tablo verilerini doldur
    for (int i = 0; i < filteredIngredients.size(); i++) {
        Ingredient ingredient = filteredIngredients.get(i);
        data[i][0] = ingredient.getName();
        data[i][1] = ingredient.getQuantity();
        data[i][2] = ingredient.getUnit();
    }

    DefaultTableModel model = new DefaultTableModel(data, columnNames);
    invTable.setModel(model);
}

// malzemeler tablosunu başlatır
private void initializeIngredientsTable(){
    ingrTable = new JTable();
    updateIngredientsTable();
    ingrScrollPane = new JScrollPane(invTable);
}

// malzemeler tablosunu günceller
public void updateIngredientsTable() {
    DataManager dataManager = new DataManager(new DBManager());
    ArrayList<Ingredient> ingredients = dataManager.getAllIngredients();

    // tablo sütun adları
    String[] columnNames = {"Malzeme", "Birim Fiyatı", "Birim"};
    Object[][] data = new Object[ingredients.size()][3];

    // tablo verilerini doldur
    for (int i = 0; i < ingredients.size(); i++) {
        Ingredient ingredient = ingredients.get(i);
        data[i][0] = ingredient.getName();
        data[i][1] = ingredient.getPricePerUnit() + " TL";
        data[i][2] = ingredient.getUnit();
    }

    DefaultTableModel model = new DefaultTableModel(data, columnNames);
    ingrTable.setModel(model);
}

// seçilen malzemeyi siler
private void deleteIngredient() {
    int selectedRow = ingrTable.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(MainPanel, "Lütfen bir malzeme seçin.");
        return;
    }

    String ingredientName = (String) ingrTable.getValueAt(selectedRow, 0);
    DataManager dataManager = new DataManager(new DBManager());
    int ingredientID = dataManager.getIngredientID(ingredientName);
    dataManager.deleteIngredient(ingredientID);
    updateIngredientsTable();
    updateIngredientCheckBoxFilter();
    updateInventoryTable();
}

// butonları başlatır
private void initializeButtons() {
    filterBtn = new JButton("Filtrele");
    filterBtn.addActionListener(e -> {
        filterRecipes();
    });
    ekleButton = new JButton("Ekle");
    ekleButton.addActionListener(e -> {
        SwingUtilities.invokeLater(() -> {
            AddRecipeGUI addRecipeGUI = new AddRecipeGUI((JFrame) SwingUtilities.getWindowAncestor(MainPanel), this);
            addRecipeGUI.setVisible(true);
        });
    });
    reloadBtn = new JButton("Yenile");
    reloadBtn.addActionListener(e -> {
        updateRecipesTable(null);
    });
    silButton = new JButton("Sil");
    silButton.addActionListener(e -> {
        deleteRecipe();
    });
    ingrAddBtn = new JButton("Ekle");
    ingrAddBtn.addActionListener(e -> {
        SwingUtilities.invokeLater(() -> {
            AddIngredientGUI addIngredientGUI = new AddIngredientGUI((JFrame) SwingUtilities.getWindowAncestor(MainPanel), this);
            addIngredientGUI.setVisible(true);
        });
    });
    ingrDelBtn = new JButton("Sil");
    ingrDelBtn.addActionListener(e -> {
        deleteIngredient();
    });
    güncelleButton = new JButton("Güncelle");
    güncelleButton.addActionListener(e -> {
        updateRecipe();
    });
    ingrUpdtBtn = new JButton("Güncelle");
    ingrUpdtBtn.addActionListener(e -> {
        updateIngredient();
    });
    invAddBtn = new JButton("Ekle");
    invAddBtn.addActionListener(e -> {
        addToInventoryGUI((JFrame) SwingUtilities.getWindowAncestor(MainPanel));
    });
    invDelBtn = new JButton("Sil");
    invDelBtn.addActionListener(this::invDelBtnActionPerformed);
    invUpdtBtn = new JButton("Güncelle");
    invUpdtBtn.addActionListener(this::invUpdtBtnActionPerformed);
    searchBtn = new JButton("Ara");
    searchBtn.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            DataManager dataManager = new DataManager(new DBManager());
            String searchTerm = tfield_searchBar.getText();
            ArrayList<Recipe> searchResults = dataManager.searchRecipes(searchTerm);
            updateRecipesTable(searchResults);
        }
    });
}// tarif silme fonksiyonu
private void deleteRecipe() {
    int selectedRow = recipesTable.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(MainPanel, "Lütfen bir tarif seçin.");
        return;
    }

    String recipeName = (String) recipesTable.getValueAt(selectedRow, 0);
    DataManager dataManager = new DataManager(new DBManager());
    int recipeID = dataManager.getRecipeID(recipeName);
    dataManager.deleteRecipe(recipeID);
    updateRecipesTable(null);
}

// malzeme checkbox'larını başlatma fonksiyonu
private void initializeIngredientCheckBoxFilter() {
    DataManager dataManager = new DataManager(new DBManager());
    ArrayList<Ingredient> ingredients = dataManager.getAllIngredients();

    // checkbox'lar için panel oluşturuyoruz
    JPanel checkBoxPanel = new JPanel();
    checkBoxPanel.setLayout(new BoxLayout(checkBoxPanel, BoxLayout.Y_AXIS));

    // her malzeme için dinamik olarak bir JCheckBox oluşturuyoruz
    ArrayList<JCheckBox> checkBoxList = new ArrayList<>();
    for (Ingredient ingredient : ingredients) {
        JCheckBox checkBox = new JCheckBox(ingredient.getName());
        checkBoxList.add(checkBox);
        checkBoxPanel.add(checkBox);
    }

    // scrollable bir panel içerisine checkbox'ları ekliyoruz
    ingrfScrollPane = new JScrollPane(checkBoxPanel);
}

// malzeme checkbox'larını güncelleme fonksiyonu
public void updateIngredientCheckBoxFilter() {
    DataManager dataManager = new DataManager(new DBManager());
    ArrayList<Ingredient> ingredients = dataManager.getAllIngredients();

    // mevcut checkbox'ları temizliyoruz
    JPanel checkBoxPanel = new JPanel();
    checkBoxPanel.setLayout(new BoxLayout(checkBoxPanel, BoxLayout.Y_AXIS));

    // yeni checkbox'lar ekliyoruz
    for (Ingredient ingredient : ingredients) {
        JCheckBox checkBox = new JCheckBox(ingredient.getName());
        checkBoxPanel.add(checkBox);
    }

    // scroll pane'i yeni panel ile güncelliyoruz
    ingrfScrollPane.setViewportView(checkBoxPanel);

    // arayüzü yeniliyoruz
    ingrfScrollPane.revalidate();
    ingrfScrollPane.repaint();
}

// seçilen malzemelere göre tarifleri filtreleme fonksiyonu
private void filterRecipesBySelectedIngredients(ArrayList<JCheckBox> checkBoxList) {
    ArrayList<String> selectedIngredients = new ArrayList<>();
    for (JCheckBox checkBox : checkBoxList) {
        if (checkBox.isSelected()) {
            selectedIngredients.add(checkBox.getText());
        }
    }

    DataManager dataManager = new DataManager(new DBManager());
    ArrayList<Recipe> recipes = dataManager.getRecipesByIngredients(selectedIngredients);

    String[] columnNames = {"Tarif Adı", "Kategori", "Hazırlama Süresi", "Talimatlar"};
    Object[][] data = new Object[recipes.size()][4];

    for (int i = 0; i < recipes.size(); i++) {
        Recipe recipe = recipes.get(i);
        data[i][0] = recipe.getName();
        data[i][1] = recipe.getCategory();
        data[i][2] = recipe.getPrepTime();
        data[i][3] = recipe.getInstructions();
    }

    DefaultTableModel model = new DefaultTableModel(data, columnNames);
    recipesTable.setModel(model);
}

// tarifleri filtreleme fonksiyonu
private void filterRecipes() {
    String category = (String) categoryCBox.getSelectedItem();
    String sortingOption = (String) sortCBox.getSelectedItem();
    String costMin = costMinTField.getText();
    String costMax = costMaxTField.getText();
    String typeMinCount = typeMinCountTField.getText();
    String typeMaxCount = typeMaxCountTField.getText();

    // checkbox'lardan seçilen malzemeleri alıyoruz
    ArrayList<String> selectedIngredients = new ArrayList<>();
    Component[] components = ((JPanel) ingrfScrollPane.getViewport().getView()).getComponents();
    for (Component component : components) {
        if (component instanceof JCheckBox) {
            JCheckBox checkBox = (JCheckBox) component;
            if (checkBox.isSelected()) {
                selectedIngredients.add(checkBox.getText());
            }
        }
    }

    DataManager dataManager = new DataManager(new DBManager());
    if (tfield_searchBar.getText().length() > 0) {
        ArrayList<Recipe> recipes = dataManager.searchAndFilterRecipes(tfield_searchBar.getText(), category, sortingOption, costMin, costMax, typeMinCount, typeMaxCount, selectedIngredients);
        updateRecipesTable(recipes);
        return;
    }
    ArrayList<Recipe> recipes = dataManager.getFilteredRecipes(category, sortingOption, costMin, costMax, typeMinCount, typeMaxCount, selectedIngredients);
    // filtrelenmiş tariflerle tarifler tablosunu güncelliyoruz
    updateRecipesTable(recipes);
}

// tarif güncelleme fonksiyonu
private void updateRecipe() {
    int selectedRow = recipesTable.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(MainPanel, "Lütfen bir tarif seçin.");
        return;
    }

    String recipeName = (String) recipesTable.getValueAt(selectedRow, 0);
    DataManager dataManager = new DataManager(new DBManager());
    int recipeID = dataManager.getRecipeID(recipeName);
    Recipe recipe = dataManager.getRecipeByID(recipeID);

    SwingUtilities.invokeLater(() -> {
        UpdateRecipeGUI updateRecipeGUI = new UpdateRecipeGUI((JFrame) SwingUtilities.getWindowAncestor(MainPanel), this, recipe);
        updateRecipeGUI.setVisible(true);
    });
}

// malzeme güncelleme fonksiyonu
private void updateIngredient() {
    int selectedRow = ingrTable.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(MainPanel, "Lütfen bir malzeme seçin.");
        return;
    }

    String ingredientName = (String) ingrTable.getValueAt(selectedRow, 0);
    DataManager dataManager = new DataManager(new DBManager());
    int ingredientID = dataManager.getIngredientID(ingredientName);
    Ingredient ingredient = dataManager.getIngredientByID(ingredientID);

    SwingUtilities.invokeLater(() -> {
        UpdateIngredientGUI updateIngredientGUI = new UpdateIngredientGUI((JFrame) SwingUtilities.getWindowAncestor(MainPanel), this, ingredient);
        updateIngredientGUI.setVisible(true);
    });
}

// envanterden malzeme silme butonuna tıklama işlemi
private void invDelBtnActionPerformed(java.awt.event.ActionEvent evt) {
    int selectedRow = invTable.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(MainPanel, "Lütfen bir malzeme seçin.");
        return;
    }

    String ingredientName = (String) invTable.getValueAt(selectedRow, 0);
    DataManager dataManager = new DataManager(new DBManager());
    int ingredientID = dataManager.getIngredientID(ingredientName);
    dataManager.deleteFromInventory(ingredientID);
    updateInventoryTable();
    updateRecipesTable(null);
}

// envantere malzeme ekleme butonuna tıklama işlemi
private void invAddBtnActionPerformed(java.awt.event.ActionEvent evt) {
    SwingUtilities.invokeLater(() -> {
        AddIngredientGUI addIngredientGUI = new AddIngredientGUI((JFrame) SwingUtilities.getWindowAncestor(MainPanel), this);
        addIngredientGUI.setVisible(true);
    });
}

// envanterdeki malzemeyi güncelleme butonuna tıklama işlemi
private void invUpdtBtnActionPerformed(java.awt.event.ActionEvent evt) {
    int selectedRow = invTable.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(MainPanel, "Lütfen bir malzeme seçin.");
        return;
    }
    categoryfPanel.setLayout(null);
    String ingredientName = (String) invTable.getValueAt(selectedRow, 0);
    DataManager dataManager = new DataManager(new DBManager());
    int ingredientID = dataManager.getIngredientID(ingredientName);
    Ingredient ingredient = dataManager.getIngredientByID(ingredientID);

    SwingUtilities.invokeLater(() -> {
        UpdateIngredientGUI updateIngredientGUI = new UpdateIngredientGUI((JFrame) SwingUtilities.getWindowAncestor(MainPanel), this, ingredient);
        updateIngredientGUI.setVisible(true);
    });
}

// envantere malzeme ekleme arayüzü
private void addToInventoryGUI(JFrame parent) {
    JDialog dialog = new JDialog(parent, "Malzeme Ekle", true);  // modaliteyi true yapıyoruz
    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new GridLayout(3, 2));
    dialog.setContentPane(mainPanel);
    dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    dialog.setPreferredSize(new Dimension(300, 150));
    dialog.setLocationRelativeTo(parent);

    JLabel lbl1 = new JLabel("Malzeme Adı:");
    JComboBox comboBox1 = new JComboBox();
    DataManager dataManager = new DataManager(new DBManager());
    ArrayList<Ingredient> allIngredients = dataManager.getAllIngredients();
    ArrayList<Ingredient> inventoryIngredients = dataManager.getInventoryIngredients(); // bu metodun var olduğunu varsayıyoruz

    // envanterde zaten bulunan malzemeleri filtreliyoruz
    ArrayList<Ingredient> availableIngredients = new ArrayList<>();
    for (Ingredient ingredient : allIngredients) {
        boolean inInventory = false;
        for (Ingredient invIngredient : inventoryIngredients) {
            if (ingredient.getName().equals(invIngredient.getName())) {
                inInventory = true;
                break;
            }
        }
        if (!inInventory) {
            availableIngredients.add(ingredient);
        }
    }
    if (availableIngredients.size() == 0) {
        JOptionPane.showMessageDialog(MainPanel, "Tüm malzemeler envanterde.");
        return;
    }
    for (Ingredient ingredient : availableIngredients) {
        comboBox1.addItem(ingredient.getName());
    }

    JLabel lbl2 = new JLabel("Miktar:");
    JSpinner spinner1 = new JSpinner();
    spinner1.setModel(new SpinnerNumberModel(0, 0, 1000, 0.1));

    JButton btn1 = new JButton("Ekle");
    JButton btn2 = new JButton("İptal");
    mainPanel.add(lbl1);
    mainPanel.add(comboBox1);
    mainPanel.add(lbl2);
    mainPanel.add(spinner1);
    mainPanel.add(btn1);
    mainPanel.add(btn2);

    btn1.addActionListener(e -> {
        String ingredientName = comboBox1.getSelectedItem().toString();
        double quantity = (double) spinner1.getValue();

        dataManager.addToInventory(dataManager.getIngredientID(ingredientName), quantity);
        updateInventoryTable();
        updateRecipesTable(null);
        dialog.dispose();
    });

    btn2.addActionListener(e -> {
        dialog.dispose();
    });

    dialog.pack();
    dialog.setVisible(true);
}

// tarifler tablosu için mouse listener başlatma fonksiyonu
private void initializeRecipeTableMouseListener() {
    recipesTable.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            int selectedRow = recipesTable.getSelectedRow();
            if (selectedRow != -1) {
                String recipeName = (String) recipesTable.getValueAt(selectedRow, 0);
                DataManager dataManager = new DataManager(new DBManager());
                int recipeID = dataManager.getRecipeID(recipeName);
                Recipe recipe = dataManager.getRecipeByID(recipeID);
                double cost = dataManager.calculateCost(recipeID);
                ArrayList<Ingredient> ingredients = dataManager.getRecipeIngredients(recipeID);
                SwingUtilities.invokeLater(() -> {
                    RecipeDetailsGUI recipeDetailsGUI = new RecipeDetailsGUI((JFrame) SwingUtilities.getWindowAncestor(MainPanel), MainGUI.this, recipe, cost, ingredients);
                    recipeDetailsGUI.setVisible(true);
                });
            }
        }
    });
}
}