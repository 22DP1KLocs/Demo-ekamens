import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import java.util.*;
import java.util.stream.Collectors;

public class ShopGUI extends Application {
    private ListView<Product> productListView;
    private ListView<String> cartListView;
    private Label totalCostLabel;
    private TextField searchField;
    private Button checkoutButton;
    private ShoppingCart shoppingCart = new ShoppingCart();
    private HBox categoryButtons;
    private String currentCategory = null; // Track the currently selected category

    @Override
    public void start(Stage primaryStage) {
        shoppingCart = new ShoppingCart();
        productListView = new ListView<>(FXCollections.observableArrayList(ProductManager.getAllProducts()));

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
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                Product selected = productListView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    int quantity = promptForQuantity("How many of " + selected.getName() + " would you like to add?");
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
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                String selected = cartListView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    String productName = selected.split(" - ")[0];
                    Product product = findProductByName(productName);
                    if (product != null) {
                        int quantityToRemove = promptForQuantity("How many of " + product.getName() + " would you like to remove?");
                        if (quantityToRemove > 0) {
                            shoppingCart.removeProduct(product, quantityToRemove);
                            updateCartDisplay();
                        }
                    }
                }
            }
        });

        totalCostLabel = new Label("Total: $0.00");
        checkoutButton = new Button("Checkout");
        checkoutButton.setOnAction(e -> checkout());

        searchField = new TextField();
        searchField.setPromptText("Search for products...");
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            productListView.setItems(FXCollections.observableArrayList(ProductManager.searchProducts(newValue)));
        });

        setupCategoryButtons();

        VBox root = new VBox(10);
        root.setPadding(new Insets(15));
        root.getChildren().addAll(new Label("Search:"), searchField, categoryButtons, new Label("Available Products:"), productListView, new Label("Your Cart:"), cartListView, totalCostLabel, checkoutButton);

        Scene scene = new Scene(root, 400, 600);
        primaryStage.setTitle("Shop GUI");
        primaryStage.setScene(scene);
        primaryStage.show();

        updateCartDisplay();
    }

    private void setupCategoryButtons() {
        Set<String> categories = ProductManager.getAllProducts().stream()
                                              .map(Product::getCategory)
                                              .collect(Collectors.toSet());
        categoryButtons = new HBox(10);
        for (String category : categories) {
            ToggleButton button = new ToggleButton(category);
            button.setOnAction(e -> toggleCategoryFilter(button, category));
            categoryButtons.getChildren().add(button);
        }
    }

    private void toggleCategoryFilter(ToggleButton button, String category) {
        if (currentCategory != null && currentCategory.equals(category) && button.isSelected()) {
            productListView.setItems(FXCollections.observableArrayList(ProductManager.getAllProducts()));
            currentCategory = null;
            button.setSelected(false);
        } else {
            productListView.setItems(FXCollections.observableArrayList(
                ProductManager.getAllProducts().stream()
                              .filter(p -> p.getCategory().equals(category))
                              .collect(Collectors.toList())
            ));
            currentCategory = category;
            categoryButtons.getChildren().stream()
                .filter(b -> b instanceof ToggleButton && b != button)
                .forEach(b -> ((ToggleButton)b).setSelected(false));
        }
    }

    private int promptForQuantity(String promptMessage) {
        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setTitle("Quantity Input");
        dialog.setHeaderText(null);
        dialog.setContentText(promptMessage);
        Optional<String> result = dialog.showAndWait();
        return result.map(Integer::parseInt).orElse(0);
    }

    private void updateCartDisplay() {
        cartListView.setItems(FXCollections.observableArrayList(shoppingCart.getCartContents()));
        totalCostLabel.setText("Total: $" + String.format("%.2f", shoppingCart.calculateTotal()));
    }

    private void checkout() {
        double total = shoppingCart.calculateTotal();
        shoppingCart.saveCartToFile("/Users/kaspars/Desktop/Demo/purchases");
        shoppingCart.checkout();
        totalCostLabel.setText("Total: $0.00");
        updateCartDisplay();
        showAlert("Checkout Complete", "The total cost was $" + String.format("%.2f", total));
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private Product findProductByName(String name) {
        return ProductManager.getAllProducts().stream()
                             .filter(p -> p.getName().equals(name))
                             .findFirst()
                             .orElse(null);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
