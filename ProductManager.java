import java.util.ArrayList;
import java.util.List;

public class ProductManager {
    private static List<Product> products = new ArrayList<>();

    static {
        products.add(new Product("Apple", 0.99));
        products.add(new Product("Banana", 0.59));
        products.add(new Product("Orange", 1.29));
    }

    public static List<Product> getAllProducts() {
        return products;
    }
}
