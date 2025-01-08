import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import org.bson.Document;
import org.bson.types.ObjectId;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import java.util.ArrayList;

public class HistoryView extends JFrame {
    private DataAdapter dataAdapter;
    private JPanel mainPanel;
    private JPanel summaryPanel;
    private CardLayout cardLayout;

    public HistoryView(DataAdapter dataAdapter) {
        this.dataAdapter = dataAdapter;
        this.setTitle("Purchase History and Summary");
        this.setSize(800, 800);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        cardLayout = new CardLayout();
        this.setLayout(cardLayout);
        
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(240, 240, 240)); 
        
        summaryPanel = new JPanel();
        summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.Y_AXIS));
        summaryPanel.setBackground(new Color(240, 240, 240));
        
        JButton switchToSummary = new JButton("View Monthly Summary");
        switchToSummary.setBackground(new Color(34, 139, 34));
        switchToSummary.setForeground(Color.WHITE);
        switchToSummary.addActionListener(e -> cardLayout.show(this.getContentPane(), "Summary"));
        
        JButton switchToHistory = new JButton("View Order History");
        switchToHistory.setBackground(new Color(0, 123, 255));
        switchToHistory.setForeground(Color.WHITE);
        switchToHistory.addActionListener(e -> cardLayout.show(this.getContentPane(), "History"));
        
        mainPanel.add(switchToSummary);
        displayOrderHistory();
        
        summaryPanel.add(switchToHistory);
        displayMonthlySummary();
        
        this.add(mainPanel, "History");
        this.add(summaryPanel, "Summary");
        
        cardLayout.show(this.getContentPane(), "History");
    }

    private void displayOrderHistory() {
        try {
            MongoCollection<Document> ordersCollection = dataAdapter.getMongoDatabase().getCollection("Orders");
            MongoCursor<Document> cursor = ordersCollection.find().iterator();
            while (cursor.hasNext()) {
                Document order = cursor.next();
                String orderID = extractOrderID(order);
                int userID = order.getInteger("UserID");
                double totalPrice = order.getDouble("TotalPrice");
                String date = extractDate(order.get("Date"));
                String userName = getUserName(userID);
    
                JLabel orderHeader = new JLabel(
                    "<html>Order ID: <b>" + orderID + "</b>, made by: <b>" + userName +
                    "</b>, total value is: <b>$" + totalPrice + "</b>, made on <b>" + date + "</b></html>"
                );
                orderHeader.setFont(new Font("Arial", Font.BOLD, 14));
                orderHeader.setForeground(Color.WHITE); 
    
                JPanel orderHeaderPanel = new JPanel();
                orderHeaderPanel.setBackground(new Color(0, 123, 255)); 
                orderHeaderPanel.setLayout(new FlowLayout(FlowLayout.LEFT)); 
                orderHeaderPanel.add(orderHeader);
    
                mainPanel.add(orderHeaderPanel);
    
                displayOrderItems(order.getList("OrderItems", Document.class), mainPanel);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayMonthlySummary() {
        try {
            MongoCollection<Document> ordersCollection = dataAdapter.getMongoDatabase().getCollection("Orders");
            LocalDate now = LocalDate.now();
            LocalDate firstDayOfMonth = now.withDayOfMonth(1);
            Date firstDay = Date.from(firstDayOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date lastDay = Date.from(now.withDayOfMonth(now.lengthOfMonth()).atStartOfDay(ZoneId.systemDefault()).toInstant());
            
            Document salesMatch = new Document("$match", new Document("Date", new Document("$gte", firstDay).append("$lte", lastDay)));
            Document totalSalesGroup = new Document("$group", new Document("_id", null).append("TotalSales", new Document("$sum", "$TotalPrice")));
            
            double totalSales = ordersCollection.aggregate(List.of(salesMatch, totalSalesGroup)).first().getDouble("TotalSales");
            JLabel totalSalesLabel = new JLabel("Total Sales for the Month:$ " + totalSales);
            totalSalesLabel.setFont(new Font("Arial", Font.BOLD, 16));
            totalSalesLabel.setForeground(new Color(0, 123, 255)); 
            summaryPanel.add(totalSalesLabel);
            
            displayProductsSold(ordersCollection, salesMatch);
            
            displayCustomerSpending(ordersCollection, salesMatch);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayProductsSold(MongoCollection<Document> ordersCollection, Document salesMatch) {
        Document unwindItems = new Document("$unwind", "$OrderItems");
        Document groupProducts = new Document("$group", new Document("_id", "$OrderItems.ProductID").append("TotalQuantity", new Document("$sum", "$OrderItems.PurchaseQuantity")));
        
        List<Document> productResults = ordersCollection.aggregate(List.of(salesMatch, unwindItems, groupProducts)).into(new ArrayList<>());
        
        DefaultTableModel productTableModel = new DefaultTableModel(new Object[]{"Product Name", "Quantity Sold"}, 0);
        
        for (Document productResult : productResults) {
            int productID = productResult.getInteger("_id");
            double quantity = productResult.getDouble("TotalQuantity");
            String productName = getProductName(productID);
            productTableModel.addRow(new Object[]{productName, quantity});
        }
        
        JTable productTable = new JTable(productTableModel);
        styleTable(productTable);
        JScrollPane productScrollPane = new JScrollPane(productTable);
        
        summaryPanel.add(new JLabel("<html><h3>Products Sold:</h3></html>"));
        summaryPanel.add(productScrollPane);
    }

    private void displayCustomerSpending(MongoCollection<Document> ordersCollection, Document salesMatch) {
        Document groupCustomers = new Document("$group", new Document("_id", "$UserID").append("TotalSpent", new Document("$sum", "$TotalPrice")));
        
        List<Document> customerResults = ordersCollection.aggregate(List.of(salesMatch, groupCustomers)).into(new ArrayList<>());
        
        DefaultTableModel customerTableModel = new DefaultTableModel(new Object[]{"Customer Name", "Total Spent"}, 0);
        
        for (Document customerResult : customerResults) {
            int userID = customerResult.getInteger("_id");
            double totalSpent = customerResult.getDouble("TotalSpent");
            String userName = getUserName(userID);
            customerTableModel.addRow(new Object[]{userName, totalSpent});
        }
        
        JTable customerTable = new JTable(customerTableModel);
        styleTable(customerTable);
        JScrollPane customerScrollPane = new JScrollPane(customerTable);
        
        summaryPanel.add(new JLabel("<html><h3>Customer Spending:</h3></html>"));
        summaryPanel.add(customerScrollPane);
    }

    private void styleTable(JTable table) {
         table.setFont(new Font("Arial", Font.PLAIN, 14));
         table.setRowHeight(30);
         table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
         table.getTableHeader().setBackground(new Color(34, 139, 34));
         table.getTableHeader().setForeground(Color.WHITE); 
         table.setFillsViewportHeight(true);
     }

    private void displayOrderItems(List<Document> orderItems, JPanel panel) {
         try {
             DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"Product Name", "Quantity"}, 0);
             for (Document item : orderItems) {
                 int productID = item.getInteger("ProductID");
                 double purchaseQuantity = extractQuantity(item.get("PurchaseQuantity"));
                 String productName = getProductName(productID);
                 tableModel.addRow(new Object[]{productName, purchaseQuantity});
             }
             JTable itemsTable = new JTable(tableModel);
             styleTable(itemsTable);
             itemsTable.setFillsViewportHeight(true); 
             JScrollPane scrollPane = new JScrollPane(itemsTable);
             panel.add(scrollPane);
         } catch (Exception e) {
             e.printStackTrace();
         }
     }

     private String extractOrderID(Document order) {
         Object id = order.get("_id");
         if (id instanceof Integer) {
             return String.valueOf(id);
         } else if (id instanceof ObjectId) {
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

     private String getUserName(int userID) {
         User user = dataAdapter.loadUserById(userID);
         return user != null ? user.getFullName() : "Unknown User";
     }

     private String getProductName(int productID) {
         Product product = dataAdapter.loadProduct(productID);
         return product != null ? product.getName() : "Unknown Product";
     }
}