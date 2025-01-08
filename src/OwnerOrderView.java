import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class OwnerOrderView extends JFrame {

    private JTextField txtProductId = new JTextField(10);
    private JTextField txtAdditionalQuantity = new JTextField(10);
    private JButton btnSubmit = new JButton("Submit Order");
    private JLabel lblStatus = new JLabel("");

    private OwnerController ownerController;

    public OwnerOrderView(OwnerController ownerController) {
        this.ownerController = ownerController;

        this.setTitle("Order More Products");
        this.setSize(450, 300);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
        this.getContentPane().setBackground(new Color(242, 242, 242)); // Light gray background

        JPanel panelInput = new JPanel(new GridLayout(2, 2, 10, 10));
        panelInput.setBackground(new Color(242, 242, 242));
        panelInput.add(new JLabel("Product ID:"));
        panelInput.add(txtProductId);
        panelInput.add(new JLabel("Additional Quantity:"));
        panelInput.add(txtAdditionalQuantity);

        styleTextField(txtProductId);
        styleTextField(txtAdditionalQuantity);

        JPanel panelStatus = new JPanel();
        panelStatus.setBackground(new Color(242, 242, 242));
        panelStatus.add(lblStatus);

        lblStatus.setFont(new Font("Arial", Font.PLAIN, 14));
        lblStatus.setForeground(Color.RED); 

        JPanel panelButton = new JPanel();
        panelButton.setBackground(new Color(242, 242, 242));
        panelButton.add(btnSubmit);

        styleButton(btnSubmit);

        this.add(panelInput);
        this.add(panelButton);
        this.add(panelStatus);

        btnSubmit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                orderMoreProducts();
            }
        });
    }

    private void styleTextField(JTextField textField) {
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.setPreferredSize(new Dimension(150, 30));
        textField.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150)));
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(50, 150, 255)); // Blue button
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(30, 130, 230)); // Darker blue on hover
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(50, 150, 255)); // Reset to original color
            }
        });
    }

    private void orderMoreProducts() {
        try {
            int productId = Integer.parseInt(txtProductId.getText().trim());
            int additionalQuantity = Integer.parseInt(txtAdditionalQuantity.getText().trim());

            if (additionalQuantity <= 0) {
                lblStatus.setText("Invalid quantity! Must be greater than 0.");
                lblStatus.setForeground(Color.RED);
                return;
            }

            boolean success = ownerController.orderMoreQuantity(productId, additionalQuantity);
            if (success) {
                lblStatus.setText("Order placed successfully!");
                lblStatus.setForeground(new Color(0, 204, 102)); 
            } else {
                lblStatus.setText("Product not found!");
                lblStatus.setForeground(Color.RED); 
            }
        } catch (NumberFormatException ex) {
            lblStatus.setText("Invalid input. Please enter numeric values.");
            lblStatus.setForeground(Color.RED); 
        }
    }
}
