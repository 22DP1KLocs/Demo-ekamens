import java.util.stream.Collectors;
import java.util.List;
import java.util.ArrayList;
// ! all products in list and there names, price and catagory 
public class ProductManager {
    private static List<Product> products = new ArrayList<>();
    static {
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
// ! returns all the the products list 
    public static List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }
// ! this methode is to search products through name 
    public static List<Product> searchProducts(String query) {
        return products.stream()
                       .filter(p -> p.getName().toLowerCase().contains(query.toLowerCase()))
                       .collect(Collectors.toList());
    }
}
