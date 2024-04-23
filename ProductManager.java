import java.util.ArrayList;
import java.util.List;

public class ProductManager {
    public static List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        products.add(new Product("Apple", 0.99));
        products.add(new Product("Banana", 0.59));
        // Add more products as needed
        return products;
    }
}
