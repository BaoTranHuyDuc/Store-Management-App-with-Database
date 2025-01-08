import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class BuyerView extends JFrame {

    private JButton btnAdd = new JButton("Add Item");
    private JButton btnPay = new JButton("Finish & Pay");

    private DefaultTableModel items = new DefaultTableModel(); // Table model for cart items
    private JTable tblItems = new JTable(items);
    private JLabel labTotal = new JLabel("Total: $0.00", SwingConstants.RIGHT);

    public BuyerView() {
        this.setTitle("Shopping Cart");
        this.setSize(500, 600);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLayout(new BorderLayout());

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(34, 139, 34));
        JLabel title = new JLabel("Your Cart");
        title.setFont(new Font("Sans Serif", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        titlePanel.add(title);
        this.add(titlePanel, BorderLayout.NORTH);

        items.addColumn("Product ID");
        items.addColumn("Name");
        items.addColumn("Price (USD)");
        items.addColumn("Quantity");
        items.addColumn("Cost (USD)");

        tblItems.setFont(new Font("Sans Serif", Font.PLAIN, 14));
        tblItems.setRowHeight(25);
        tblItems.getTableHeader().setFont(new Font("Sans Serif", Font.BOLD, 14));
        tblItems.getTableHeader().setBackground(new Color(34, 139, 34));
        tblItems.getTableHeader().setForeground(Color.WHITE);
        tblItems.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(tblItems);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());

        labTotal.setFont(new Font("Sans Serif", Font.BOLD, 16));
        labTotal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPanel.add(labTotal);
        
        bottomPanel.add(totalPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        
        btnAdd.setFont(new Font("Sans Serif", Font.BOLD, 14));
        btnAdd.setPreferredSize(new Dimension(150, 40));
        btnAdd.setBackground(new Color(0, 123, 255));
        btnAdd.setForeground(Color.WHITE);

        btnPay.setFont(new Font("Sans Serif", Font.BOLD, 14));
        btnPay.setPreferredSize(new Dimension(150, 40));
        btnPay.setBackground(new Color(34, 139, 34));
        btnPay.setForeground(Color.WHITE);

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnPay);
        
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        this.add(bottomPanel, BorderLayout.SOUTH);
    }

    public JButton getBtnAdd() {
        return btnAdd;
    }

    public JButton getBtnPay() {
        return btnPay;
    }

    public JLabel getLabTotal() {
        return labTotal;
    }

    public void clearCart() {
        items.setRowCount(0);

        labTotal.setText("Total: $0.00");
    }

    public void addRow(Object[] row) {
         items.addRow(row);
         updateTotal(); 
    }

    private void updateTotal() {
         double total = 0.0;
         for (int i = 0; i < items.getRowCount(); i++) {
             total += (double) items.getValueAt(i, 4); 
         }
         labTotal.setText(String.format("Total: $%.2f", total)); 
     }
}