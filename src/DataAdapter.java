import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.net.URL;
import java.lang.reflect.Type;

public class DataAdapter {
    private Connection connection;
    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;

    public DataAdapter(Connection connection) {
        this.connection = connection;
        MongoClientURI uri = new MongoClientURI("mongodb+srv://btranhuyduc:matkhau456@cluster0.4xq8n.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0");
        mongoClient = new MongoClient(uri);
        mongoDatabase = mongoClient.getDatabase("project");
    }

    public Connection getConnection() {
        return connection;
    }

    public MongoDatabase getMongoDatabase() {
        return mongoDatabase;
    }

    public void closeMongoClient() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }


    public Product loadProduct(int id) {
        try {
            URL url = new URL("http://localhost:8080/products/" + id);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
    
            if (connection.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
    
                JSONObject productJson = new JSONObject(response.toString());
    
                Product product = new Product();
                product.setProductID(productJson.getInt("ID"));
                product.setName(productJson.getString("Name"));
                product.setPrice(productJson.getDouble("Price"));
                product.setQuantity(productJson.getDouble("RemainingStock"));
                return product;
            } else {
                System.out.println("Failed to fetch product. HTTP response code: " + connection.getResponseCode());
            }
        } catch (Exception e) {
            System.out.println("Error loading product!");
            e.printStackTrace();
        }
        return null;
    }

    public boolean saveProduct(Product product) {
        try {
            URL url = new URL("http://localhost:8080/products");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            JSONObject productJson = new JSONObject();
            productJson.put("ID", product.getProductID());
            productJson.put("Name", product.getName());
            productJson.put("Price", product.getPrice());
            productJson.put("RemainingStock", product.getQuantity());

            OutputStream os = connection.getOutputStream();
            os.write(productJson.toString().getBytes());
            os.flush();
            os.close();

            int responseCode = connection.getResponseCode();
            connection.disconnect();
            return responseCode == 200;
        } catch (Exception e) {
            System.out.println("Error saving product!");
            e.printStackTrace();
            return false;
        }
    }

    public User loadUserById(int userId) {
        try {
            URL url = new URL("http://localhost:8080/users/" + userId);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
    
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
    
                    JSONObject userJson = new JSONObject(response.toString());
                    User user = new User();
                    user.setUserID(userJson.getInt("ID"));
                    user.setUsername(userJson.getString("UserName"));
                    user.setPassword(userJson.getString("Password"));
                    user.setFullName(userJson.getString("DisplayName"));
                    user.setRole(userJson.getString("Role"));
                    user.setAccountNumber(userJson.optString("AccountNumber", null)); // Handle optional fields
                    user.setBank(userJson.optString("Bank", null));
    
                    return user;
                }
            } else {
                System.out.println("Failed to fetch user data. Response code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public User loadUser(String username, String password) {
        try {
            URL url = new URL("http://localhost:8080/users");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
    
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
    
                JSONArray usersArray = new JSONArray(response.toString());
                for (int i = 0; i < usersArray.length(); i++) {
                    JSONObject userJson = usersArray.getJSONObject(i);
    
                    if (username.equals(userJson.getString("UserName")) &&
                        password.equals(userJson.getString("Password"))) {
                        User user = new User();
                        user.setUserID(userJson.getInt("ID"));
                        user.setUsername(userJson.getString("UserName"));
                        user.setPassword(userJson.getString("Password"));
                        user.setFullName(userJson.getString("DisplayName"));
                        user.setRole(userJson.getString("Role"));
                        user.setAccountNumber(userJson.optString("AccountNumber", null));
                        user.setBank(userJson.optString("Bank", null));
    
                        return user;
                    }
                }
            } else {
                System.out.println("Failed to fetch users from server. HTTP response code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    
    public List<Product> loadAllProducts() {
        List<Product> products = new ArrayList<>();

        try {
            URL url = new URL("http://localhost:8080/products");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                String json = response.toString();
                Gson gson = new Gson();
                Type productListType = new TypeToken<List<Product>>() {}.getType();
                products = gson.fromJson(json, productListType);
            } else {
                System.out.println("Failed to fetch products. HTTP response code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error occurred while fetching products.");
        }

        return products;
    }


    public List<Document> getOrdersByUserID(int userID) {
        MongoCollection<Document> ordersCollection = mongoDatabase.getCollection("Orders");
        return ordersCollection.find(new Document("UserID", userID))
                .sort(new Document("_id", 1))
                .into(new java.util.ArrayList<>());
    }

    public List<Document> getOrderItems(Document order) {
        return (List<Document>) order.get("OrderItems");
    }

    public boolean saveUserChanges(User user) {
        try {
            URL url = new URL("http://localhost:8080/users/" + user.getUserID());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
    
            JSONObject userJson = new JSONObject();
            userJson.put("UserName", user.getUsername());
            userJson.put("Password", user.getPassword());
            userJson.put("DisplayName", user.getFullName());
            userJson.put("AccountNumber", user.getAccountNumber());
            userJson.put("Bank", user.getBank());
    
            try (OutputStream os = connection.getOutputStream()) {
                os.write(userJson.toString().getBytes("UTF-8"));
            }
    
            int responseCode = connection.getResponseCode();
            connection.disconnect();
    
            return responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_NO_CONTENT;
    
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
