import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ProductBrowseView extends JFrame {
    private JTable tblProducts;
    private DefaultTableModel productTableModel;

    public ProductBrowseView(DataAdapter dataAdapter) {
        setTitle("Browse Products");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(0, 153, 204));
        JLabel title = new JLabel("Available Products");
        title.setFont(new Font("Sans Serif", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        titlePanel.add(title);
        add(titlePanel, BorderLayout.NORTH);

        productTableModel = new DefaultTableModel(
            new String[]{"Product ID", "Name", "Price (USD)", "Stock Remaining"}, 0
        );
        tblProducts = new JTable(productTableModel);
        tblProducts.setFillsViewportHeight(true);
        tblProducts.setRowHeight(25);
        tblProducts.setFont(new Font("Sans Serif", Font.PLAIN, 14));
        tblProducts.getTableHeader().setFont(new Font("Sans Serif", Font.BOLD, 14));
        tblProducts.getTableHeader().setBackground(new Color(0, 153, 204));
        tblProducts.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(tblProducts);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(Color.WHITE);
        JButton btnClose = new JButton("Close");
        btnClose.setFont(new Font("Sans Serif", Font.BOLD, 14));
        btnClose.setBackground(new Color(204, 0, 0));
        btnClose.setForeground(Color.WHITE);
        btnClose.setPreferredSize(new Dimension(120, 40));
        footerPanel.add(btnClose);
        add(footerPanel, BorderLayout.SOUTH);

        loadProductData(dataAdapter);

        btnClose.addActionListener(e -> dispose());
    }

    private void loadProductData(DataAdapter dataAdapter) {
        List<Product> products = dataAdapter.loadAllProducts();

        for (Product product : products) {
            Object[] rowData = {
                product.getProductID(),
                product.getName(),
                String.format("%.2f", product.getPrice()),
                product.getQuantity()
            };
            productTableModel.addRow(rowData);
        }
    }
}
