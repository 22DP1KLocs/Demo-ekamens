import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class ProductManager {
    private static List<Product> products = new ArrayList<>();

    static {
        // ! Initialize with some products
        products.add(new Product("Apple", 0.99, "Fruits"));
        products.add(new Product("Banana", 0.59, "Fruits"));
        products.add(new Product("Orange", 0.79, "Fruits"));
        products.add(new Product("Milk", 1.50, "Dairy"));
        products.add(new Product("Bread", 1.25, "Bakery"));
        products.add(new Product("Eggs", 2.00, "Dairy"));
        products.add(new Product("Cheese", 2.50, "Dairy"));
        products.add(new Product("Chicken", 4.99, "Meat"));
        products.add(new Product("Beef", 5.49, "Meat"));
        products.add(new Product("Water Bottle", 0.99, "Beverages"));
    }

    // ! Returns all products
    public static List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }

    // ! Filters products by category
    public static List<Product> filterProductsByCategory(String category) {
        return products.stream()
                       .filter(p -> p.getCategory().equalsIgnoreCase(category))
                       .collect(Collectors.toList());
    }

    // ! Finds a product by name
    public static Product findProductByName(String name) {
        return products.stream()
                       .filter(p -> p.getName().equalsIgnoreCase(name))
                       .findFirst()
                       .orElse(null);
    }

    // ! Searches products based on a query (name contains query)
    public static List<Product> searchProducts(String query) {
        return products.stream()
                       .filter(p -> p.getName().toLowerCase().contains(query.toLowerCase()))
                       .collect(Collectors.toList());
    }
}
