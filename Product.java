public class Product {
	private String name;
	private double price;
	private String category;
//! inatzilization of parametrs 
	public Product(String name, double price, String category) {
			this.name = name;
			this.price = price;
			this.category = category;
	}

	public String getName() {
			return name;
	}

	public double getPrice() {
			return price;
	}

	public String getCategory() {
			return category;
	}
}