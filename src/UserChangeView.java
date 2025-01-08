import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UserChangeView extends JFrame {
    private DataAdapter dataAdapter;
    private User currentUser;

    private JTextField txtUserName = new JTextField(20);
    private JPasswordField txtPassword = new JPasswordField(20);
    private JTextField txtFullName = new JTextField(20);
    private JTextField txtAccountNumber = new JTextField(20);
    private JTextField txtBank = new JTextField(20);
    private JButton btnSave = new JButton("Save Changes");
    private JButton btnCancel = new JButton("Cancel");

    public UserChangeView(DataAdapter dataAdapter, User currentUser) {
        this.dataAdapter = dataAdapter;
        this.currentUser = currentUser;

        this.setTitle("Edit Profile");
        this.setSize(500, 400);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLayout(new BorderLayout());

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(0, 153, 204));
        JLabel title = new JLabel("Edit Your Information");
        title.setFont(new Font("Sans Serif", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        titlePanel.add(title);
        this.add(titlePanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addFormField(formPanel, gbc, "Username:", txtUserName, 0);
        addFormField(formPanel, gbc, "Password:", txtPassword, 1);
        addFormField(formPanel, gbc, "Full Name:", txtFullName, 2);
        addFormField(formPanel, gbc, "Account Number:", txtAccountNumber, 3);
        addFormField(formPanel, gbc, "Bank:", txtBank, 4);

        this.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        btnSave.setBackground(new Color(0, 204, 102));
        btnSave.setForeground(Color.WHITE);
        btnCancel.setBackground(new Color(204, 0, 0));
        btnCancel.setForeground(Color.WHITE);

        btnSave.setFont(new Font("Sans Serif", Font.BOLD, 14));
        btnCancel.setFont(new Font("Sans Serif", Font.BOLD, 14));
        btnSave.setPreferredSize(new Dimension(150, 40));
        btnCancel.setPreferredSize(new Dimension(150, 40));

        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);
        this.add(buttonPanel, BorderLayout.SOUTH);

        loadUserInfo();

        btnSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveChanges();
            }
        });

        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, String labelText, JComponent field, int row) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Sans Serif", Font.BOLD, 14));
        label.setHorizontalAlignment(SwingConstants.RIGHT);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(field, gbc);
    }

    private void loadUserInfo() {
        txtUserName.setText(currentUser.getUsername());
        txtPassword.setText(currentUser.getPassword());
        txtFullName.setText(currentUser.getFullName());
        txtAccountNumber.setText(currentUser.getAccountNumber());
        txtBank.setText(currentUser.getBank());
    }

    private void saveChanges() {
        currentUser.setUsername(txtUserName.getText());
        currentUser.setPassword(new String(txtPassword.getPassword()));
        currentUser.setFullName(txtFullName.getText());
        currentUser.setAccountNumber(txtAccountNumber.getText());
        currentUser.setBank(txtBank.getText());

        boolean success = dataAdapter.saveUserChanges(currentUser);
        if (success) {
            JOptionPane.showMessageDialog(this, "Changes saved successfully!");
        } else {
            JOptionPane.showMessageDialog(this, "Failed to save changes. Please try again.");
        }
    }
}
