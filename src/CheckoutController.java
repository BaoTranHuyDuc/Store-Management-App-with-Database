import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import java.util.ArrayList;
import java.util.List;

public class CheckoutController implements ActionListener {
    private BuyerView view;
    private DataAdapter dataAdapter; // to save and load product
    private Order order = null;

    public CheckoutController(BuyerView view, DataAdapter dataAdapter) {
        this.dataAdapter = dataAdapter;
        this.view = view;

        view.getBtnAdd().addActionListener(this);
        view.getBtnPay().addActionListener(this);

        order = new Order();

    }


    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == view.getBtnAdd())
            addProduct();
        else
        if (e.getSource() == view.getBtnPay())
            makeOrder();
    }


//This is where we should add update the quantity of products after purchase
    private void makeOrder() {
        JOptionPane.showMessageDialog(null, "Your products are ordered. Send the money to this account to complete: 0201000741267 Vietcombank ");
        
        int userId = Application.getInstance().getCurrentUser().getUserID();
        double totalCost = order.getTotalCost();

        List<Document> mongoOrderItems = new ArrayList<>();

        // Iterate through each OrderLine in the order
        for (OrderLine line : order.getLines()) {
            Product product = dataAdapter.loadProduct(line.getProductID());
            if (product != null) {
                // Update the product quantity in Products table
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
        .getCollection("Orders"); Document mongoOrder = new Document("UserID", userId)
        .append("TotalPrice", totalCost)
        .append("Date", new java.util.Date()) // Current date and time
        .append("OrderItems", mongoOrderItems);

        ordersCollection.insertOne(mongoOrder);
        System.out.println("mongodb success");

        // Clear the order and update the view
        order.getLines().clear();
        order.setTotalCost(0);
        view.clearCart();
        view.getLabTotal().setText("Total: $0.00");
    }

    private void addProduct() {
        String id = JOptionPane.showInputDialog("Enter ProductID: ");
        Product product = dataAdapter.loadProduct(Integer.parseInt(id));
        if (product == null) {
            JOptionPane.showMessageDialog(null, "This product does not exist!");
            return;
        }

        double quantity = Double.parseDouble(JOptionPane.showInputDialog(null,"Enter quantity: "));

        if (quantity < 0 || quantity > product.getQuantity()) {
            JOptionPane.showMessageDialog(null, "This quantity is not valid!");
            return;
        }


        OrderLine line = new OrderLine();
        line.setOrderID(this.order.getOrderID());
        line.setProductID(product.getProductID());
        line.setQuantity(quantity);
        line.setCost(quantity * product.getPrice());
        order.getLines().add(line);
        order.setTotalCost(order.getTotalCost() + line.getCost());



        Object[] row = new Object[5];
        row[0] = line.getProductID();
        row[1] = product.getName();
        row[2] = product.getPrice();
        row[3] = line.getQuantity();
        row[4] = line.getCost();

        this.view.addRow(row);
        this.view.getLabTotal().setText("Total: $" + order.getTotalCost());
        this.view.invalidate();
    }

}