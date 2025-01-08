import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import org.bson.Document;
import org.bson.types.ObjectId;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

public class UserHistoryView extends JFrame {

    private DataAdapter dataAdapter;
    private User currentUser;

    public UserHistoryView(DataAdapter dataAdapter, User currentUser) {
        this.dataAdapter = dataAdapter;
        this.currentUser = currentUser;

        this.setTitle("Your Purchase History");
        this.setSize(800, 600);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLayout(new BorderLayout());
        this.getContentPane().setBackground(Color.WHITE);

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(0, 153, 204));
        JLabel title = new JLabel("Purchase History");
        title.setFont(new Font("Sans Serif", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        titlePanel.add(title);
        this.add(titlePanel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.add(scrollPane, BorderLayout.CENTER);

        displayUserOrderHistory(contentPanel);
    }

    private void displayUserOrderHistory(JPanel contentPanel) {
        try {
            MongoCollection<Document> ordersCollection = dataAdapter.getMongoDatabase().getCollection("Orders");
            MongoCursor<Document> cursor = ordersCollection.find(new Document("UserID", currentUser.getUserID())).iterator();

            while (cursor.hasNext()) {
                Document order = cursor.next();

                String orderID = extractOrderID(order);
                double totalPrice = order.getDouble("TotalPrice");
                String date = extractDate(order.get("Date"));

                JPanel orderPanel = new JPanel(new BorderLayout());
                orderPanel.setBackground(new Color(245, 245, 245));
                orderPanel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(0, 153, 204), 2),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));

                JLabel orderHeader = new JLabel(
                    "<html><b>Order ID:</b> " + orderID + " &nbsp;&nbsp; <b>Total Value:</b> $" 
                    + totalPrice + " &nbsp;&nbsp; <b>Date:</b> " + date + "</html>");
                orderHeader.setFont(new Font("Sans Serif", Font.PLAIN, 16));
                orderPanel.add(orderHeader, BorderLayout.NORTH);

                JTable itemsTable = createOrderItemsTable(order);
                if (itemsTable != null) {
                    JScrollPane tableScrollPane = new JScrollPane(itemsTable);
                    tableScrollPane.setPreferredSize(new Dimension(600, 100));
                    orderPanel.add(tableScrollPane, BorderLayout.CENTER);
                }

                contentPanel.add(orderPanel);
                contentPanel.add(Box.createVerticalStrut(10));
            }

            contentPanel.revalidate();
            contentPanel.repaint();

            cursor.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JTable createOrderItemsTable(Document order) {
        try {
            List<Document> orderItems = order.getList("OrderItems", Document.class);

            DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"Product ID", "Quantity"}, 0);

            for (Document item : orderItems) {
                int productID = item.getInteger("ProductID");
                double purchaseQuantity = extractQuantity(item.get("PurchaseQuantity"));
                tableModel.addRow(new Object[]{productID, purchaseQuantity});
            }

            JTable itemsTable = new JTable(tableModel);
            itemsTable.setFillsViewportHeight(true);
            itemsTable.setBackground(Color.WHITE);
            itemsTable.setGridColor(new Color(220, 220, 220));
            itemsTable.setFont(new Font("Sans Serif", Font.PLAIN, 14));
            itemsTable.setRowHeight(25);

            return itemsTable;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String extractOrderID(Document order) {
        Object id = order.get("_id");
        if (id instanceof ObjectId) {
            return ((ObjectId) id).toHexString();
        } else {
            return "Unknown ID";
        }
    }

    private String extractDate(Object dateField) {
        if (dateField instanceof String) {
            return (String) dateField;
        } else if (dateField instanceof java.util.Date) {
            return dateField.toString();
        } else {
            return "Unknown Date";
        }
    }

    private double extractQuantity(Object quantityField) {
        if (quantityField instanceof Integer) {
            return ((Integer) quantityField).doubleValue();
        } else if (quantityField instanceof Double) {
            return (Double) quantityField;
        } else {
            return -1;
        }
    }
}
