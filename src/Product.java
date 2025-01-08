import com.google.gson.annotations.SerializedName;

public class Product {
    @SerializedName("ID")
    private int productID;

    @SerializedName("Name")
    private String name;

    @SerializedName("Price")
    private double price;

    @SerializedName("RemainingStock")
    private double quantity;

    // Getters and Setters
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }
}
