import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.bson.Document;
import com.mongodb.client.MongoCollection;

public class CashierController {
    private DataAdapter dataAdapter;

    public CashierController(DataAdapter dataAdapter) {
        this.dataAdapter = dataAdapter;
    }

    public void makeOrderForUser(Order order, int userId) {
        JOptionPane.showMessageDialog(null, "The products are ordered. Ask the customer to send the money to this account to complete: 0201000741267 Vietcombank ");

        double totalCost = order.getTotalCost();

        List<Document> mongoOrderItems = new ArrayList<>();

        for (OrderLine line : order.getLines()) {
            Product product = dataAdapter.loadProduct(line.getProductID());
            if (product != null) {
                product.setQuantity(product.getQuantity() - line.getQuantity());
                dataAdapter.saveProduct(product);
                Document mongoOrderItem = new Document("ProductID", line.getProductID())
                        .append("PurchaseQuantity", line.getQuantity());
                mongoOrderItems.add(mongoOrderItem);
            }
        }

        MongoCollection<Document> ordersCollection = Application.getInstance()
                .getDataAdapter()
                .getMongoDatabase()
                .getCollection("Orders");
        Document mongoOrder = new Document("UserID", userId)
                .append("TotalPrice", totalCost)
                .append("Date", new java.util.Date())
                .append("OrderItems", mongoOrderItems);

        ordersCollection.insertOne(mongoOrder);
        System.out.println("MongoDB order logging success");

        order.getLines().clear();
        order.setTotalCost(0);
    }
}
