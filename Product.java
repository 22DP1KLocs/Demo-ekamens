import java.util.ResourceBundle;

public class Product {
	private String nameKey;
	private double price;
	private String categoryKey;
	private String localizedName;
	private String localizedCategory;

	public Product(String nameKey, double price, String categoryKey) {
			this.nameKey = nameKey;
			this.price = price;
			this.categoryKey = categoryKey;
	}

	public void updateLocalization(ResourceBundle bundle) {
			this.localizedName = bundle.getString(nameKey);
			this.localizedCategory = bundle.getString(categoryKey);
	}

	public String getName() {
			return localizedName;
	}

	public double getPrice() {
			return price;
	}

	public String getCategory() {
			return localizedCategory;
	}
}
