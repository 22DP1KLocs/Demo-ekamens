import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ShoppingCart {
    private Map<Product, Integer> cart = new HashMap<>();

    public void addProduct(Product product, int quantity) {
        if (product != null && quantity > 0) {
            cart.put(product, cart.getOrDefault(product, 0) + quantity);
        }
    }

    public double calculateTotal() {
        return cart.entrySet().stream().mapToDouble(e -> e.getKey().getPrice() * e.getValue()).sum();
    }

    public double checkout() {
        double total = calculateTotal();
        cart.clear(); // Clears the cart after checkout
        return total;
    }

    public ObservableList<String> getCartContents() {
        ObservableList<String> contents = FXCollections.observableArrayList();
        for (Map.Entry<Product, Integer> entry : cart.entrySet()) {
            contents.add(entry.getKey().getName() + " - $" + entry.getKey().getPrice() + " x " + entry.getValue() + " = $" + (entry.getKey().getPrice() * entry.getValue()));
        }
        return contents;
    }

    public void saveCartToFile(String folderPath) {
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        String fileName = folderPath + File.separator + "purchase_" + System.currentTimeMillis() + ".txt";
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            writer.println("Purchase Date: " + new Date());
            writer.println("Items:");
            for (Map.Entry<Product, Integer> entry : cart.entrySet()) {
                Product product = entry.getKey();
                int quantity = entry.getValue();
                writer.println(product.getName() + " - $" + product.getPrice() + " x " + quantity + " = $" + (product.getPrice() * quantity));
            }
            writer.println("Total: $" + calculateTotal());
            System.out.println("Cart saved to " + fileName);
        } catch (IOException e) {
            System.err.println("Error saving cart to file: " + e.getMessage());
        }
    }
}
