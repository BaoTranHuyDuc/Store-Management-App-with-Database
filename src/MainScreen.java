import javax.swing.*;
import java.awt.*;

public class MainScreen extends JFrame {

    private JButton btnBuy = new JButton("Buy Product");
    private JButton btnSell = new JButton("Adjust Product");
    private JButton btnBrowseProducts = new JButton("Browse Products");
    private JButton btnViewHistory = new JButton("See Sales Report");
    private JButton btnUserHistory = new JButton("View My Purchase History");
    private JButton btnEditProfile = new JButton("Edit Profile");
    private JButton btnOrderForCustomer = new JButton("Order for Customer");
    private JButton btnOrderMoreProducts = new JButton("Order More Products");

    public MainScreen(User currentUser) {
        this.setTitle("Store Management System");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(600, 500);
        this.setLayout(new BorderLayout());
        this.getContentPane().setBackground(Color.WHITE);

        JPanel panelTitle = new JPanel();
        panelTitle.setBackground(new Color(0, 153, 204));
        JLabel title = new JLabel("Store Management System");
        title.setFont(new Font("Sans Serif", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        panelTitle.add(title);
        this.add(panelTitle, BorderLayout.NORTH);

        JPanel panelWelcome = new JPanel();
        panelWelcome.setBackground(Color.WHITE);
        JLabel welcomeMessage = new JLabel(
            "<html><div style='text-align:center;'>Hello " + currentUser.getFullName() + ",<br>" +
            "You are a " + currentUser.getRole() + ",<br>" +
            "Your ID is " + currentUser.getUserID() + "</div></html>");
        welcomeMessage.setFont(new Font("Sans Serif", Font.PLAIN, 16));
        panelWelcome.add(welcomeMessage);
        this.add(panelWelcome, BorderLayout.CENTER);

        JPanel panelButtons = new JPanel();
        panelButtons.setLayout(new GridLayout(4, 2, 10, 10));
        panelButtons.setBackground(new Color(245, 245, 245));
        panelButtons.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        if ("Customer".equals(currentUser.getRole())) {
            addStyledButton(panelButtons, btnBuy, new Color(102, 204, 0));
            addStyledButton(panelButtons, btnBrowseProducts, new Color(102, 204, 0));
            addStyledButton(panelButtons, btnUserHistory, new Color(102, 204, 0));
            addStyledButton(panelButtons, btnEditProfile, new Color(102, 204, 0));
        } else if ("Owner".equals(currentUser.getRole())) {
            addStyledButton(panelButtons, btnSell, new Color(255, 153, 51));
            addStyledButton(panelButtons, btnViewHistory, new Color(255, 153, 51));
            addStyledButton(panelButtons, btnOrderMoreProducts, new Color(255, 153, 51));
        } else if ("Cashier".equals(currentUser.getRole())) {
            addStyledButton(panelButtons, btnSell, new Color(0, 153, 204));
            addStyledButton(panelButtons, btnOrderForCustomer, new Color(0, 153, 204));
        }

        this.add(panelButtons, BorderLayout.SOUTH);

        btnBuy.addActionListener(e -> Application.getInstance().getCheckoutScreen().setVisible(true));
        btnSell.addActionListener(e -> Application.getInstance().getProductView().setVisible(true));
        btnBrowseProducts.addActionListener(e -> new ProductBrowseView(Application.getInstance().getDataAdapter()).setVisible(true));
        btnUserHistory.addActionListener(e -> new UserHistoryView(Application.getInstance().getDataAdapter(), currentUser).setVisible(true));
        btnEditProfile.addActionListener(e -> new UserChangeView(Application.getInstance().getDataAdapter(), currentUser).setVisible(true));
        btnViewHistory.addActionListener(e -> new HistoryView(Application.getInstance().getDataAdapter()).setVisible(true));
        btnOrderForCustomer.addActionListener(e -> {
            CashierController cashierController = new CashierController(Application.getInstance().getDataAdapter());
            Order currentOrder = new Order();
            CashierView cashierView = new CashierView(cashierController, currentOrder);
            cashierView.setVisible(true);
        });
        btnOrderMoreProducts.addActionListener(e -> {
            OwnerController ownerController = new OwnerController(Application.getInstance().getDataAdapter());
            OwnerOrderView ownerOrderView = new OwnerOrderView(ownerController);
            ownerOrderView.setVisible(true);
        });
    }

    private void addStyledButton(JPanel panel, JButton button, Color color) {
        button.setPreferredSize(new Dimension(200, 50));
        button.setFocusPainted(false);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Sans Serif", Font.BOLD, 14));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.darker(), 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.brighter());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });
        panel.add(button);
    }
}
