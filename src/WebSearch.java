import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class WebSearch {
    private HttpServer server;

    public WebSearch(int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
    }

    public void addContext(String path, HttpHandler handler) {
        server.createContext(path, handler);
    }

    public void start() {
        server.start();
        System.out.println("WebSearch is running on port " + server.getAddress().getPort());
    }

    public static void main(String[] args) throws IOException {
        WebSearch webSearch = new WebSearch(9090);

        webSearch.addContext("/search", exchange -> {
            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                String query = exchange.getRequestURI().getQuery();
        
                try {
                    String productsJson = fetchProductsFromWebServer();
        
                    String htmlResponse = filterProductsToHtml(productsJson, query);
        
                    sendHtmlResponse(exchange, htmlResponse);
                } catch (IOException e) {
                    e.printStackTrace();
                    exchange.sendResponseHeaders(500, -1);
                }
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        });

        webSearch.start();
    }

    private static String fetchProductsFromWebServer() throws IOException {
        URL url = new URL("http://localhost:8080/products");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            return reader.lines().reduce("", (acc, line) -> acc + line);
        }
    }

    private static String filterProducts(String productsJson, String query) {
        if (query == null || query.isEmpty()) {
            return productsJson;
        }

        Map<String, String> queryParams = parseQuery(query);

        JSONArray productsArray = new JSONArray(productsJson);
        JSONArray filteredArray = new JSONArray();

        for (int i = 0; i < productsArray.length(); i++) {
            JSONObject product = productsArray.getJSONObject(i);
            boolean matches = true;

            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                String key = entry.getKey();
                String rawValue = entry.getValue();
                String operant = extractOperant(rawValue);
                String value = rawValue.replaceAll("[<>=]", ""); // Remove operant from the value


                if (key.equalsIgnoreCase("ID")) {
                    if (operant.equalsIgnoreCase("<")) {
                        matches = matches && product.optDouble("ID") < Double.parseDouble(value);
                    } else if (operant.equalsIgnoreCase(">")) {
                        matches = matches && product.optDouble("ID") > Double.parseDouble(value);
                    } else {
                        matches = matches && product.optDouble("ID") == Double.parseDouble(value);
                    }
                } else if (key.equalsIgnoreCase("Price")) {
                    if (operant.equalsIgnoreCase("<")) {
                        matches = matches && product.optDouble("Price") < Double.parseDouble(value);
                    } else if (operant.equalsIgnoreCase(">")) {
                        matches = matches && product.optDouble("Price") > Double.parseDouble(value);
                    } else {
                        matches = matches && product.optDouble("Price") == Double.parseDouble(value);
                    }
                } else if (key.equalsIgnoreCase("RemainingStock")) {
                    if (operant.equalsIgnoreCase("<")) {
                        matches = matches && product.optDouble("RemainingStock") < Double.parseDouble(value);
                    } else if (operant.equalsIgnoreCase(">")) {
                        matches = matches && product.optDouble("RemainingStock") > Double.parseDouble(value);
                    } else {
                        matches = matches && product.optDouble("RemainingStock") == Double.parseDouble(value);
                    }
                } else if (key.equalsIgnoreCase("Name")) {
                    matches = matches && product.optString("Name").equalsIgnoreCase(value);
                }

                if (!matches) break;
            }

            if (matches) {
            JSONObject reorderedProduct = new JSONObject();
            reorderedProduct.put("ID", product.optInt("ID"));
            reorderedProduct.put("Name", product.optString("Name"));
            reorderedProduct.put("Price", product.optDouble("Price"));
            reorderedProduct.put("RemainingStock", product.optInt("RemainingStock"));

            filteredArray.put(reorderedProduct);
            }
        }

        return filteredArray.toString();
    }

    private static String extractOperant(String value) {
        if (value.startsWith("<")) {
            return "<";
        } else if (value.startsWith(">")) {
            return ">";
        } else {
            return "=";
        }
    }

    private static Map<String, String> parseQuery(String query) {
        Map<String, String> queryParams = new HashMap<>();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            // Identify the operator
            String operator;
            if (pair.contains("<")) {
                operator = "<";
            } else if (pair.contains(">")) {
                operator = ">";
            } else {
                operator = "=";
            }
    
            // Split by the identified operator
            String[] keyValue = pair.split("\\" + operator, 2);
            if (keyValue.length == 2) {
                String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                String value = URLDecoder.decode(operator + keyValue[1], StandardCharsets.UTF_8); // Preserve the operator in the value
                queryParams.put(key, value);
            }
        }
        return queryParams;
    }

    private static void sendHtmlResponse(HttpExchange exchange, String htmlResponse) throws IOException {
        byte[] responseBytes = htmlResponse.getBytes();
        exchange.getResponseHeaders().set("Content-Type", "text/html");
        exchange.sendResponseHeaders(200, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }
    
    private static String filterProductsToHtml(String productsJson, String query) {
        if (query == null || query.isEmpty()) {
            return generateHtmlPage(new JSONArray(productsJson));
        }
    
        Map<String, String> queryParams = parseQuery(query);
    
        JSONArray productsArray = new JSONArray(productsJson);
        JSONArray filteredArray = new JSONArray();
    
        for (int i = 0; i < productsArray.length(); i++) {
            JSONObject product = productsArray.getJSONObject(i);
            boolean matches = true;
    
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                String key = entry.getKey();
                String rawValue = entry.getValue();
                String operant = extractOperant(rawValue);
                String value = rawValue.replaceAll("[<>=]", ""); // Remove operant from the value
    
                if (key.equalsIgnoreCase("ID")) {
                    if (operant.equalsIgnoreCase("<")) {
                        matches = matches && product.optDouble("ID") < Double.parseDouble(value);
                    } else if (operant.equalsIgnoreCase(">")) {
                        matches = matches && product.optDouble("ID") > Double.parseDouble(value);
                    } else {
                        matches = matches && product.optDouble("ID") == Double.parseDouble(value);
                    }
                } else if (key.equalsIgnoreCase("Price")) {
                    if (operant.equalsIgnoreCase("<")) {
                        matches = matches && product.optDouble("Price") < Double.parseDouble(value);
                    } else if (operant.equalsIgnoreCase(">")) {
                        matches = matches && product.optDouble("Price") > Double.parseDouble(value);
                    } else {
                        matches = matches && product.optDouble("Price") == Double.parseDouble(value);
                    }
                } else if (key.equalsIgnoreCase("RemainingStock")) {
                    if (operant.equalsIgnoreCase("<")) {
                        matches = matches && product.optDouble("RemainingStock") < Double.parseDouble(value);
                    } else if (operant.equalsIgnoreCase(">")) {
                        matches = matches && product.optDouble("RemainingStock") > Double.parseDouble(value);
                    } else {
                        matches = matches && product.optDouble("RemainingStock") == Double.parseDouble(value);
                    }
                } else if (key.equalsIgnoreCase("Name")) {
                    matches = matches && product.optString("Name").equalsIgnoreCase(value);
                }
    
                if (!matches) break;
            }
    
            if (matches) {
                filteredArray.put(product);
            }
        }
    
        return generateHtmlPage(filteredArray);
    }
    
    private static String generateHtmlPage(JSONArray products) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html>");
        html.append("<head>");
        html.append("<title>Product Search Results</title>");
        html.append("<style>");
        html.append("table {width: 100%; border-collapse: collapse;}");
        html.append("th, td {border: 1px solid black; padding: 8px; text-align: left;}");
        html.append("th {background-color: #f2f2f2;}");
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");
        html.append("<h1>Search Results</h1>");
        html.append("<table>");
        html.append("<tr><th>ID</th><th>Name</th><th>Price</th><th>Remaining Stock</th></tr>");
    
        for (int i = 0; i < products.length(); i++) {
            JSONObject product = products.getJSONObject(i);
            html.append("<tr>");
            html.append("<td>").append(product.optInt("ID")).append("</td>");
            html.append("<td>").append(product.optString("Name")).append("</td>");
            html.append("<td>").append(product.optDouble("Price")).append("</td>");
            html.append("<td>").append(product.optInt("RemainingStock")).append("</td>");
            html.append("</tr>");
        }
    
        html.append("</table>");
        html.append("</body>");
        html.append("</html>");
    
        return html.toString();
    }
}
