import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.collections.FXCollections;
import java.util.Locale;
import java.util.ResourceBundle;

public class ShopGUI extends Application {
    private ListView<Product> productListView;
    private ListView<String> cartListView;
    private Label totalCostLabel;
    private TextField searchField;
    private Button checkoutButton;
    private ShoppingCart shoppingCart = new ShoppingCart();
    private HBox categoryButtons;
    private ResourceBundle resourceBundle;
    private HBox languageButtons; // Buttons for language switching

    @Override
    public void start(Stage primaryStage) {
        resourceBundle = ResourceBundle.getBundle("MessagesBundle", Locale.getDefault());
        ProductManager.getAllProducts().forEach(p -> p.updateLocalization(resourceBundle));
        
        productListView = new ListView<>(FXCollections.observableArrayList(ProductManager.getAllProducts()));
        cartListView = new ListView<>();
        cartListView.setItems(FXCollections.observableArrayList(shoppingCart.getCartContents()));
        
        setupUI(primaryStage);
    }

    private void setupUI(Stage primaryStage) {
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

        totalCostLabel = new Label(resourceBundle.getString("totalLabel") + " $0.00");
        checkoutButton = new Button(resourceBundle.getString("checkoutButton"));
        checkoutButton.setOnAction(e -> checkout());

        searchField = new TextField();
        searchField.setPromptText(resourceBundle.getString("searchPrompt"));

        setupLanguageButtons();

        VBox root = new VBox(10);
        root.setPadding(new Insets(15));
        root.getChildren().addAll(
            new Label(resourceBundle.getString("searchLabel")),
            searchField,
            categoryButtons,
            new Label(resourceBundle.getString("availableProductsLabel")),
            productListView,
            new Label(resourceBundle.getString("yourCartLabel")),
            cartListView,
            totalCostLabel,
            checkoutButton,
            languageButtons
        );

        Scene scene = new Scene(root, 400, 600);
        primaryStage.setTitle(resourceBundle.getString("windowTitle"));
        primaryStage.setScene(scene);
        primaryStage.show();
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
                ProductManager.getAllProducts().forEach(p -> p.updateLocalization(resourceBundle));
                totalCostLabel.setText(resourceBundle.getString("totalLabel") + " $0.00");
                checkoutButton.setText(resourceBundle.getString("checkoutButton"));
                searchField.setPromptText(resourceBundle.getString("searchPrompt"));
                primaryStage.setTitle(resourceBundle.getString("windowTitle"));
            });
            languageButtons.getChildren().add(langButton);
        }
    }

    private void checkout() {
        double total = shoppingCart.calculateTotal();
        shoppingCart.checkout();
        totalCostLabel.setText(resourceBundle.getString("totalLabel") + " $0.00");
        showAlert(resourceBundle.getString("checkoutCompleteTitle"),
                resourceBundle.getString("checkoutCompleteMessage") + String.format("%.2f", total));
        cartListView.getItems().clear();
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
