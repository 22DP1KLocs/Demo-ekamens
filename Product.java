// ! this methode is for product main parametrs, this sets what is the values for each and this allows to catogorizes the products through these products 

public class Product {
	private String name;
	private double price;
	private String category;

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

	public String getCategory(){
		return category;
	}
}
