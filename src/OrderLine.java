import java.util.ArrayList;

public class OrderLine {

    private int productID;
    private int orderID;
    private double quantity;
    private double cost;
    private String productName; 

    public OrderLine() {
    }

    public OrderLine(String productName, double quantity) {
        this.productName = productName;
        this.quantity = quantity;
    }

    public double getQuantity() {
        return quantity;
    }

    public String getProductName() {
        return productName;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }
}
