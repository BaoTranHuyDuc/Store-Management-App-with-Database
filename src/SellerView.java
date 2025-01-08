import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SellerView extends JFrame {
    private JTextField txtProductID = new JTextField(10);
    private JTextField txtProductName = new JTextField(30);
    private JTextField txtProductPrice = new JTextField(10);
    private JTextField txtProductQuantity = new JTextField(10);

    private JButton btnLoad = new JButton("Load Product");
    private JButton btnSave = new JButton("Save Product");

    public SellerView() {
        this.setTitle("Manage Products");
        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));
        this.setSize(500, 300);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.getContentPane().setBackground(new Color(242, 242, 242)); // Light gray background

        btnLoad.setBackground(new Color(50, 150, 255)); // Blue button for load
        btnLoad.setForeground(Color.WHITE);
        btnLoad.setFont(new Font("Arial", Font.BOLD, 14));
        btnLoad.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnLoad.setFocusPainted(false);
        btnLoad.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btnSave.setBackground(new Color(0, 204, 102)); // Green button for save
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Arial", Font.BOLD, 14));
        btnSave.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnSave.setFocusPainted(false);
        btnSave.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btnLoad.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                btnLoad.setBackground(new Color(30, 130, 230)); // Darker blue on hover
            }

            public void mouseExited(MouseEvent evt) {
                btnLoad.setBackground(new Color(50, 150, 255)); // Reset to original color
            }
        });

        btnSave.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                btnSave.setBackground(new Color(0, 180, 80)); // Darker green on hover
            }

            public void mouseExited(MouseEvent evt) {
                btnSave.setBackground(new Color(0, 204, 102)); // Reset to original color
            }
        });

        JPanel panelProductID = createInputPanel("Product ID:", txtProductID);
        JPanel panelProductName = createInputPanel("Product Name:", txtProductName);
        JPanel panelProductPrice = createInputPanel("Price:", txtProductPrice);
        JPanel panelProductQuantity = createInputPanel("Quantity:", txtProductQuantity);

        styleTextField(txtProductID);
        styleTextField(txtProductName);
        styleTextField(txtProductPrice);
        styleTextField(txtProductQuantity);

        this.getContentPane().add(panelProductID);
        this.getContentPane().add(panelProductName);
        this.getContentPane().add(panelProductPrice);
        this.getContentPane().add(panelProductQuantity);

        JPanel panelButton = new JPanel();
        panelButton.setBackground(new Color(245, 245, 245)); // Slightly darker background
        panelButton.add(btnLoad);
        panelButton.add(btnSave);
        this.getContentPane().add(panelButton);
    }

    private JPanel createInputPanel(String labelText, JTextField textField) {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(245, 245, 245));
        panel.add(new JLabel(labelText));
        panel.add(textField);
        return panel;
    }

    private void styleTextField(JTextField textField) {
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150)));
        textField.setPreferredSize(new Dimension(150, 30));
    }

    public JButton getBtnLoad() {
        return btnLoad;
    }

    public JButton getBtnSave() {
        return btnSave;
    }

    public JTextField getTxtProductID() {
        return txtProductID;
    }

    public JTextField getTxtProductName() {
        return txtProductName;
    }

    public JTextField getTxtProductPrice() {
        return txtProductPrice;
    }

    public JTextField getTxtProductQuantity() {
        return txtProductQuantity;
    }
}
