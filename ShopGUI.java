import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.collections.FXCollections;
import java.util.*;
import java.util.stream.Collectors;

public class ShopGUI extends Application {
    private ListView<Product> productListView;
    private ListView<String> cartListView;
    private Label totalCostLabel;
    private TextField searchField;
    private Button checkoutButton;
    private ShoppingCart shoppingCart;
    private HBox categoryButtons;
    private ResourceBundle resourceBundle;
    private HBox languageButtons; // Buttons for language switching
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        shoppingCart = new ShoppingCart();
        productListView = new ListView<>(FXCollections.observableArrayList(ProductManager.getAllProducts()));

        // Load the default resource bundle
        resourceBundle = ResourceBundle.getBundle("MessagesBundle", Locale.getDefault());
        setupUI();
    }

    private void setupUI() {
        productListView.setCellFactory(param -> new ListCell<Product>() {
            @Override
            protected void updateItem(Product item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " - $" + String.format("%.2f", item.getPrice()) + " (" + item.getCategory() + ")");
                }
            }
        });

        productListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Product selected = productListView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    int quantity = promptForQuantity(resourceBundle.getString("addQuantity") + selected.getName());
                    if (quantity > 0) {
                        shoppingCart.addProduct(selected, quantity);
                        updateCartDisplay();
                    }
                }
            }
        });

        cartListView = new ListView<>();
        cartListView.setItems(FXCollections.observableArrayList(shoppingCart.getCartContents()));

        cartListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String selected = cartListView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    Product product = ProductManager.findProductByName(selected.split(" - ")[0]);
                    if (product != null) {
                        int quantityToRemove = promptForQuantity(resourceBundle.getString("removeQuantity") + product.getName());
                        if (quantityToRemove > 0) {
                            shoppingCart.removeProduct(product, quantityToRemove);
                            updateCartDisplay();
                        }
                    }
                }
            }
        });

        totalCostLabel = new Label(resourceBundle.getString("totalLabel") + " $0.00");
        checkoutButton = new Button(resourceBundle.getString("checkoutButton"));
        checkoutButton.setOnAction(e -> checkout());

        searchField = new TextField();
        searchField.setPromptText(resourceBundle.getString("searchPrompt"));
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            productListView.setItems(FXCollections.observableArrayList(ProductManager.searchProducts(newValue)));
        });

        setupCategoryButtons();
        setupLanguageButtons(); // Setup language selection buttons

        VBox root = new VBox(10);
        root.setPadding(new Insets(15));
        root.getChildren().addAll(languageButtons, new Label(resourceBundle.getString("searchLabel")), searchField, categoryButtons, new Label(resourceBundle.getString("productsLabel")), productListView, new Label(resourceBundle.getString("cartLabel")), cartListView, totalCostLabel, checkoutButton);

        Scene scene = new Scene(root, 400, 600);
        primaryStage.setTitle(resourceBundle.getString("windowTitle"));
        primaryStage.setScene(scene);
        primaryStage.show();

        updateCartDisplay();
    }

    private void setupCategoryButtons() {
        Set<String> categories = ProductManager.getAllProducts().stream()
            .map(Product::getCategory)
            .collect(Collectors.toSet());
        categoryButtons = new HBox(10);
        categories.forEach(category -> {
            ToggleButton button = new ToggleButton(category);
            button.setOnAction(e -> {
                if (button.isSelected()) {
                    productListView.setItems(FXCollections.observableArrayList(ProductManager.filterProductsByCategory(category)));
                } else {
                    productListView.setItems(FXCollections.observableArrayList(ProductManager.getAllProducts()));
                }
            });
            categoryButtons.getChildren().add(button);
        });
    }

    private void setupLanguageButtons() {
        languageButtons = new HBox(10);
        String[] languages = {"English", "Spanish", "Latvian"};
        @SuppressWarnings("deprecation")
        Locale[] locales = {Locale.ENGLISH, new Locale("es"), new Locale("lv")};

        for (int i = 0; i < languages.length; i++) {
            Button langButton = new Button(languages[i]);
            Locale locale = locales[i];
            langButton.setOnAction(e -> {
                resourceBundle = ResourceBundle.getBundle("MessagesBundle", locale);
                setupUI();
            });
            languageButtons.getChildren().add(langButton);
        }
    }

    private int promptForQuantity(String promptMessage) {
        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setTitle(resourceBundle.getString("quantityTitle"));
        dialog.setHeaderText(null);
        dialog.setContentText(promptMessage);
        Optional<String> result = dialog.showAndWait();
        try {
            return result.map(Integer::parseInt).orElse(0);
        } catch (NumberFormatException e) {
            showAlert(resourceBundle.getString("errorTitle"), resourceBundle.getString("invalidNumber"));
            return 0;
        }
    }

    private void updateCartDisplay() {
        cartListView.setItems(FXCollections.observableArrayList(shoppingCart.getCartContents()));
        totalCostLabel.setText(resourceBundle.getString("totalLabel") + " $" + String.format("%.2f", shoppingCart.calculateTotal()));
    }

    private void checkout() {
        double total = shoppingCart.calculateTotal();
        shoppingCart.checkout();
        totalCostLabel.setText(resourceBundle.getString("totalLabel") + " $0.00");
        showAlert(resourceBundle.getString("checkoutComplete"), resourceBundle.getString("totalCost") + " $" + String.format("%.2f", total));
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
