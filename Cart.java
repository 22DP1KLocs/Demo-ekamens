import java.util.HashMap;
import java.util.Map;

public class Cart {
    private Map<Product, Integer> items;

    public Cart() {
        items = new HashMap<>();
    }

    public void addToCart(Product product, int quantity) {
        items.put(product, items.getOrDefault(product, 0) + quantity);
    }

    public void removeFromCart(Product product, int quantity) {
        if (items.containsKey(product) && items.get(product) > quantity) {
            items.put(product, items.get(product) - quantity);
        } else {
            items.remove(product);
        }
    }

    public Map<Product, Integer> getItems() {
        return items;
    }
}
