import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.io.File;

public class PurchaseHistory {
    private static final String PURCHASES_DIR = "purchases";

    public static void recordPurchase(Map<Product, Integer> products, double total) {
        File directory = new File(PURCHASES_DIR);
        if (!directory.exists()) {
            directory.mkdir();
        }

        String filename = PURCHASES_DIR + File.separator + "purchase_" + System.currentTimeMillis() + ".txt";

        try (PrintWriter out = new PrintWriter(new FileWriter(filename))) {
            out.println("Purchase Date: " + ZonedDateTime.now().format(DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy")));
            out.println("Items:");
            for (Map.Entry<Product, Integer> entry : products.entrySet()) {
                Product product = entry.getKey();
                int quantity = entry.getValue();
                double cost = product.getPrice() * quantity;
                out.printf("%s - %d x $%.2f = $%.2f%n", product.getName(), quantity, product.getPrice(), cost);
            }
            out.printf("Total: $%.2f%n", total);
        } catch (IOException e) {
            System.err.println("Error writing to purchase history file: " + e.getMessage());
        }
    }
}
