import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;

import java.util.Optional;

public class ShopGUI extends Application {
    private ListView<Product> productListView;
    private ListView<String> cartListView;
    private Label totalCostLabel;
    private Button checkoutButton;
    private ShoppingCart shoppingCart = new ShoppingCart();

    @Override
    public void start(Stage primaryStage) {
        // Initialize UI components
        productListView = new ListView<>(FXCollections.observableArrayList(ProductManager.getAllProducts()));
        productListView.setCellFactory(param -> new ListCell<Product>() {
            @Override
            protected void updateItem(Product item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " - $" + String.format("%.2f", item.getPrice()));
                }
            }
        });

        // Handle double-click events on products to add them to the cart
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
        totalCostLabel = new Label("Total: $0.00");
        checkoutButton = new Button("Checkout");

        // Set up VBox layout
        VBox root = new VBox(10);
        root.setPadding(new Insets(15));
        root.getChildren().addAll(new Label("Available Products:"), productListView, new Label("Your Cart:"), cartListView, totalCostLabel, checkoutButton);

        // Configure checkout button action
        checkoutButton.setOnAction(e -> checkout());

        // Set the scene and show the stage
        Scene scene = new Scene(root, 400, 600);
        primaryStage.setTitle("Shop GUI");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Initialize cart display after all components are in place
        updateCartDisplay();
    }

    private int promptForQuantity(String promptMessage) {
        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setTitle("Quantity Input");
        dialog.setHeaderText(null);
        dialog.setContentText(promptMessage);
        Optional<String> result = dialog.showAndWait();
        try {
            return result.map(Integer::parseInt).orElse(0);
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid number.");
            return 0;
        }
    }

    private void updateCartDisplay() {
        cartListView.setItems(FXCollections.observableArrayList(shoppingCart.getCartContents()));
        totalCostLabel.setText("Total: $" + String.format("%.2f", shoppingCart.calculateTotal()));
    }

    private void checkout() {
        double total = shoppingCart.checkout();
        totalCostLabel.setText("Total: $0.00");
        shoppingCart.saveCartToFile("/Users/kaspars/Desktop/Demo/purchases");
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

    public static void main(String[] args) {
        launch(args);
    }
}
