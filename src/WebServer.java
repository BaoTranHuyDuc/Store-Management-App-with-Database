import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

public class WebServer {
    private HttpServer server;
    private static final String DB_URL = "jdbc:sqlite:project.db";

    public WebServer(int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        
    }

    public void addContext(String path, HttpHandler handler) {
        server.createContext(path, handler);
    }

    public void start() {
        server.start();
        System.out.println("Web server is running on the port " + server.getAddress().getPort());
    }

    public static void main(String[] args) throws IOException {
        WebServer webServer = new WebServer(8080);

        // Add context for retrieving products
        webServer.addContext("/products", exchange -> {
            String path = exchange.getRequestURI().getPath();
            String[] pathParts = path.split("/");
            if (pathParts.length == 3 && !pathParts[2].isEmpty()) {
                try {
                    int productId = Integer.parseInt(pathParts[2]);
                    String jsonResponse = fetchProductByIdAsJson(productId);
                    sendJsonResponse(exchange, jsonResponse);
                } catch (NumberFormatException e) {
                    sendJsonResponse(exchange, "{\"message\":\"Invalid product ID.\"}", 400);
                }
            } else if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                    handlePostProduct(exchange);
            } else if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                String jsonResponse = fetchProductsAsJson();
                sendJsonResponse(exchange, jsonResponse);
            } else {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            }
        });

        // Add context for retrieving orders
        webServer.addContext("/orders", exchange -> {
            String path = exchange.getRequestURI().getPath();
            String[] pathParts = path.split("/");
            if (pathParts.length == 3 && !pathParts[2].isEmpty()) {
                try {
                    int orderId = Integer.parseInt(pathParts[2]);
                    String jsonResponse = fetchOrderByIdAsJson(orderId);
                    sendJsonResponse(exchange, jsonResponse);
                } catch (NumberFormatException e) {
                    sendJsonResponse(exchange, "{\"message\":\"Invalid order ID.\"}", 400);
                }
            } else if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                String jsonResponse = fetchOrdersAsJson();
                sendJsonResponse(exchange, jsonResponse);
            } else {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            }
        });

        webServer.addContext("/users", exchange -> {
            String path = exchange.getRequestURI().getPath();
            String[] pathParts = path.split("/");
            
            if (pathParts.length == 3 && !pathParts[2].isEmpty()) {
                try {
                    int userId = Integer.parseInt(pathParts[2]);        
                    if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                        String jsonResponse = fetchUserByIdAsJson(userId);
                        sendJsonResponse(exchange, jsonResponse);
                    } else if ("PUT".equalsIgnoreCase(exchange.getRequestMethod())) {
                        handleUpdateUser(exchange, userId);
                    } else {
                        exchange.sendResponseHeaders(405, -1); // Method Not Allowed
                    }
                } catch (NumberFormatException e) {
                    sendJsonResponse(exchange, "{\"message\":\"Invalid user ID.\"}", 400);
                }
            } else if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                String jsonResponse = fetchUsersAsJson();
                sendJsonResponse(exchange, jsonResponse);
            } else {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            }
        });

        webServer.start();
    }

    private static void handlePostProduct(HttpExchange exchange) {
        try {
            // Read the request body
            String requestBody = new String(exchange.getRequestBody().readAllBytes());
            System.out.println("Received JSON: " + requestBody);
    
            // Parse the JSON input
            JSONObject productJson = new JSONObject(requestBody);
            int id = productJson.getInt("ID");
            String name = productJson.getString("Name");
            double price = productJson.getDouble("Price");
            int remainingStock = productJson.getInt("RemainingStock");
    
            boolean success = saveProductToDatabase(id, name, price, remainingStock);
    
            if (success) {
                String response = "{\"message\":\"Product saved successfully.\"}";
                sendJsonResponse(exchange, response, 200);
            } else {
                String response = "{\"message\":\"Failed to save the product.\"}";
                sendJsonResponse(exchange, response, 500);
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                String response = "{\"message\":\"Invalid request.\"}";
                sendJsonResponse(exchange, response, 400); // Bad Request
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
    
    // Utility method to save the product in the database
    private static boolean saveProductToDatabase(int id, String name, double price, int remainingStock) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            // Check if the product already exists
            PreparedStatement checkStmt = conn.prepareStatement("SELECT COUNT(*) FROM Products WHERE ID = ?");
            checkStmt.setInt(1, id);
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            boolean exists = rs.getInt(1) > 0;
    
            // Update or Insert based on existence, that's why we use POST instead of PUT
            PreparedStatement stmt;
            if (exists) {
                stmt = conn.prepareStatement("UPDATE Products SET Name = ?, Price = ?, RemainingStock = ? WHERE ID = ?");
                stmt.setString(1, name);
                stmt.setDouble(2, price);
                stmt.setInt(3, remainingStock);
                stmt.setInt(4, id);
            } else {
                stmt = conn.prepareStatement("INSERT INTO Products (ID, Name, Price, RemainingStock) VALUES (?, ?, ?, ?)");
                stmt.setInt(1, id);
                stmt.setString(2, name);
                stmt.setDouble(3, price);
                stmt.setInt(4, remainingStock);
            }
            stmt.executeUpdate();
            stmt.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void handleUpdateUser(HttpExchange exchange, int userId) {
        try {
            // Read the request body
            String requestBody = new String(exchange.getRequestBody().readAllBytes());
            System.out.println("Received JSON for user update: " + requestBody);
    
            // Parse the JSON input
            JSONObject userJson = new JSONObject(requestBody);
            String userName = userJson.optString("UserName");
            String password = userJson.optString("Password");
            String displayName = userJson.optString("DisplayName");
            String accountNumber = userJson.optString("AccountNumber");
            String bank = userJson.optString("Bank");
    
            boolean success = updateUserInDatabase(userId, userName, password, displayName, accountNumber, bank);
    
            if (success) {
                String response = "{\"message\":\"User updated successfully.\"}";
                sendJsonResponse(exchange, response, 200);
            } else {
                String response = "{\"message\":\"Failed to update user.\"}";
                sendJsonResponse(exchange, response, 500);
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                String response = "{\"message\":\"Invalid request.\"}";
                sendJsonResponse(exchange, response, 400); // Bad Request
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    private static boolean updateUserInDatabase(int userId, String userName, String password, String displayName, String accountNumber, String bank) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = "UPDATE Users SET UserName = ?, Password = ?, DisplayName = ?, AccountNumber = ?, Bank = ? WHERE ID = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, userName);
            stmt.setString(2, password);
            stmt.setString(3, displayName);
            stmt.setString(4, accountNumber);
            stmt.setString(5, bank);
            stmt.setInt(6, userId);
    
            int rowsAffected = stmt.executeUpdate();
            stmt.close();
            return rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // new version with statusCode param
    private static void sendJsonResponse(HttpExchange exchange, String jsonResponse, int statusCode) throws IOException {
        byte[] responseBytes = jsonResponse.getBytes();
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }

    //old version without statusCode param
    private static void sendJsonResponse(HttpExchange exchange, String jsonResponse) throws IOException {
        byte[] responseBytes = jsonResponse.getBytes();
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }

    private static String fetchProductsAsJson() {
        StringBuilder json = new StringBuilder("[");
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Products");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                if (json.length() > 1) json.append(",");
                json.append("{")
                        .append("\"ID\":").append(rs.getInt("ID")).append(",")
                        .append("\"Name\":\"").append(rs.getString("Name")).append("\",")
                        .append("\"Price\":").append(rs.getDouble("Price")).append(",")
                        .append("\"RemainingStock\":").append(rs.getInt("RemainingStock"))
                        .append("}");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        json.append("]");
        return json.toString();
    }

    private static String fetchOrdersAsJson() {
        StringBuilder json = new StringBuilder("[");
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement("SELECT ID, UserID, TotalPrice, Date FROM Orders");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                if (json.length() > 1) json.append(",");
                json.append("{")
                        .append("\"OrderID\":").append(rs.getInt("ID")).append(",")
                        .append("\"UserID\":\"").append(rs.getString("UserID")).append("\",")
                        .append("\"TotalPrice\":").append(rs.getDouble("TotalPrice")).append(",")
                        .append("\"Date\":\"").append(rs.getString("Date")).append("\"")
                        .append("}");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        json.append("]");
        return json.toString();
    }

    private static String fetchUsersAsJson() {
        StringBuilder json = new StringBuilder("[");
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement("SELECT ID, UserName, Password, DisplayName, Role, AccountNumber, Bank FROM Users");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                if (json.length() > 1) json.append(",");
                json.append("{")
                        .append("\"ID\":\"").append(rs.getString("ID")).append("\",")
                        .append("\"UserName\":\"").append(rs.getString("UserName")).append("\",")
                        .append("\"Password\":\"").append(rs.getString("Password")).append("\",")
                        .append("\"DisplayName\":\"").append(rs.getString("DisplayName")).append("\",")
                        .append("\"Role\":\"").append(rs.getString("Role")).append("\",")
                        .append("\"AccountNumber\":\"").append(rs.getString("AccountNumber")).append("\",")
                        .append("\"Bank\":\"").append(rs.getString("Bank")).append("\"")
                        .append("}");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        json.append("]");
        return json.toString();
    }


    private static String fetchProductByIdAsJson(int id) {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Products WHERE ID = ?")) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new JSONObject()
                        .put("ID", rs.getInt("ID"))
                        .put("Name", rs.getString("Name"))
                        .put("Price", rs.getDouble("Price"))
                        .put("RemainingStock", rs.getInt("RemainingStock"))
                        .toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "{}";
    }

    private static String fetchOrderByIdAsJson(int id) {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Orders WHERE ID = ?")) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new JSONObject()
                        .put("OrderID", rs.getInt("ID"))
                        .put("UserID", rs.getString("UserID"))
                        .put("TotalPrice", rs.getDouble("TotalPrice"))
                        .put("Date", rs.getString("Date"))
                        .toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "{}";
    }

    private static String fetchUserByIdAsJson(int id) {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Users WHERE ID = ?")) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new JSONObject()
                        .put("ID", rs.getInt("ID"))
                        .put("UserName", rs.getString("UserName"))
                        .put("Password", rs.getString("Password"))
                        .put("DisplayName", rs.getString("DisplayName"))
                        .put("Role", rs.getString("Role"))
                        .put("AccountNumber", rs.getString("AccountNumber"))
                        .put("Bank", rs.getString("Bank"))
                        .toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "{}";
    }
}
