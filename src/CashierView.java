import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CashierView extends JFrame {

    private JButton btnAdd = new JButton("Add a new item");
    private JButton btnPay = new JButton("Order for Customer");

    private JTextField txtUserId = new JTextField(10);
    private JLabel lblUserId = new JLabel("User ID:");

    private DefaultTableModel items = new DefaultTableModel(); // store information for the table!
    private JTable tblItems = new JTable(items); 
    private JLabel labTotal = new JLabel("Total: ");

    private CashierController cashierController;

    private Order currentOrder;

    public CashierView(CashierController cashierController, Order currentOrder) {
        this.cashierController = cashierController;
        this.currentOrder = currentOrder;

        this.setTitle("Cashier Checkout");
        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
        this.setSize(500, 700);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        this.getContentPane().setBackground(new Color(242, 242, 242)); // Light gray background

        items.addColumn("Product ID");
        items.addColumn("Name");
        items.addColumn("Price");
        items.addColumn("Quantity");
        items.addColumn("Cost");

        tblItems.setFillsViewportHeight(true);
        tblItems.setFont(new Font("Arial", Font.PLAIN, 14));
        tblItems.setRowHeight(30);
        tblItems.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        tblItems.getTableHeader().setBackground(new Color(50, 150, 255)); // Light blue header background
        tblItems.getTableHeader().setForeground(Color.WHITE);
        tblItems.setBackground(Color.WHITE);
        tblItems.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        JPanel panelOrder = new JPanel();
        panelOrder.setPreferredSize(new Dimension(500, 450));
        panelOrder.setLayout(new BoxLayout(panelOrder, BoxLayout.PAGE_AXIS));
        JScrollPane scrollPane = new JScrollPane(tblItems);
        panelOrder.add(scrollPane);
        panelOrder.add(labTotal);
        this.getContentPane().add(panelOrder);

        JPanel panelUserId = new JPanel();
        panelUserId.setPreferredSize(new Dimension(500, 50));
        panelUserId.setBackground(new Color(245, 245, 245)); // Slightly darker background for input panel
        panelUserId.add(lblUserId);
        panelUserId.add(txtUserId);
        txtUserId.setFont(new Font("Arial", Font.PLAIN, 14));
        txtUserId.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150)));
        this.getContentPane().add(panelUserId);

        JPanel panelButton = new JPanel();
        panelButton.setPreferredSize(new Dimension(500, 100));
        panelButton.setBackground(new Color(245, 245, 245)); // Consistent panel background
        panelButton.add(btnAdd);
        panelButton.add(btnPay);

        btnAdd.setBackground(new Color(50, 150, 255));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFont(new Font("Arial", Font.BOLD, 14));
        btnAdd.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnAdd.setFocusPainted(false);
        btnAdd.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btnPay.setBackground(new Color(0, 204, 102)); // Green color for pay button
        btnPay.setForeground(Color.WHITE);
        btnPay.setFont(new Font("Arial", Font.BOLD, 14));
        btnPay.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnPay.setFocusPainted(false);
        btnPay.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btnAdd.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnAdd.setBackground(new Color(30, 130, 230)); // Darker blue on hover
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnAdd.setBackground(new Color(50, 150, 255)); // Reset to original color
            }
        });

        btnPay.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnPay.setBackground(new Color(0, 180, 80)); // Darker green on hover
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnPay.setBackground(new Color(0, 204, 102)); // Reset to original color
            }
        });

        this.getContentPane().add(panelButton);

        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addItem();
            }
        });

        btnPay.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processPayment();
            }
        });
    }

    private void addItem() {
        try {
            String productIdInput = JOptionPane.showInputDialog("Enter Product ID:");
            int productId = Integer.parseInt(productIdInput);
    
            Product product = Application.getInstance().getDataAdapter().loadProduct(productId);
            if (product == null) {
                JOptionPane.showMessageDialog(this, "This product does not exist!");
                return;
            }
    
            String quantityInput = JOptionPane.showInputDialog("Enter Quantity:");
            int quantity = Integer.parseInt(quantityInput);
    
            if (quantity <= 0 || quantity > product.getQuantity()) {
                JOptionPane.showMessageDialog(this, "Invalid quantity! Available stock: " + product.getQuantity());
                return;
            }
    
            double cost = quantity * product.getPrice();
            OrderLine line = new OrderLine();
            line.setOrderID(currentOrder.getOrderID()); 
            line.setProductID(productId);
            line.setQuantity(quantity);
            line.setCost(cost);
    
            currentOrder.getLines().add(line);
            currentOrder.setTotalCost(currentOrder.getTotalCost() + cost);
    
            Object[] row = new Object[]{productId, product.getName(), product.getPrice(), quantity, cost};
            items.addRow(row);
            labTotal.setText("Total: $" + String.format("%.2f", currentOrder.getTotalCost()));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid input. Please enter numeric values.");
        }
    }

    public void processPayment() {
        try {
            int userId = Integer.parseInt(txtUserId.getText().trim());
            cashierController.makeOrderForUser(currentOrder, userId);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid User ID.");
        }
    }

    public void clearCart() {
        items.setRowCount(0);
        labTotal.setText("Total: $0.00");
    }

    public void addRow(Object[] row) {
        items.addRow(row);
    }

    public JLabel getLabTotal() {
        return labTotal;
    }

    public JTable getTblItems() {
        return tblItems;
    }
}
