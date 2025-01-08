import java.sql.Date;
import java.util.List;
import java.util.ArrayList;


public class Order {
    private int orderID;
    private int buyerID;
    private double totalCost;
    private double totalTax;
    private String date;
    private String userName; 

    private List<OrderLine> lines;

    public Order() {
        lines = new ArrayList<>();
    }

    public Order(int orderID, String userName, double totalCost, String date) {
        this.orderID = orderID;
        this.userName = userName;
        this.totalCost = totalCost;
        this.date = date;
        lines = new ArrayList<>();
    }

    public String getDate() {
        return date;
    }

    public String getUserName() {
        return userName;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public double getTotalTax() {
        return totalTax;
    }

    public void setTotalTax(double totalTax) {
        this.totalTax = totalTax;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public int getBuyerID() {
        return buyerID;
    }

    public void setBuyerID(int buyerID) {
        this.buyerID = buyerID;
    }

    public void addLine(OrderLine line) {
        lines.add(line);
    }

    public void removeLine(OrderLine line) {
        lines.remove(line);
    }

    public List<OrderLine> getLines() {
        return lines;
    }
}
