import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;

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
                    setText(item.getName() + " - $" + item.getPrice());
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
