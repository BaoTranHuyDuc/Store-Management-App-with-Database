import java.io.IOException;
import java.sql.*;

public class Application {

    private static Application instance;   // Singleton pattern

    public static Application getInstance() {
        if (instance == null) {
            instance = new Application();
        }
        return instance;
    }

    private Connection connection;

    public Connection getConnection() {
        return connection;
    }

    private DataAdapter dataAdapter;

    private User currentUser = null;

    public User getCurrentUser() { return currentUser; }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    private SellerView productView = new SellerView();

    private BuyerView checkoutScreen = new BuyerView();


    public SellerView getProductView() {
        return productView;
    }

    public BuyerView getCheckoutScreen() {
        return checkoutScreen;
    }

    public LoginScreen loginScreen = new LoginScreen();

    public LoginScreen getLoginScreen() {
        return loginScreen;
    }

    public LoginController loginController;
    private ProductController productController;

    public ProductController getProductController() {
        return productController;
    }

    private CheckoutController checkoutController;

    public CheckoutController getCheckoutController() {
        return checkoutController;
    }

    public DataAdapter getDataAdapter() {
        return dataAdapter;
    }


    private Application() {

        dataAdapter = new DataAdapter(connection);
       
        productController = new ProductController(productView, dataAdapter);

        checkoutController = new CheckoutController(checkoutScreen, dataAdapter);

        loginController = new LoginController(loginScreen, dataAdapter);
    }

    public MainScreen getMainScreen() {
        return new MainScreen(currentUser);
    }

    public static void main(String[] args) {
        Application.getInstance().getLoginScreen().setVisible(true);
    }
}
