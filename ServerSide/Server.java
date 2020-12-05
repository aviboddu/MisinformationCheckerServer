//This is the server program which will handle sending and receiving data from the client.

import java.io.*;
import java.net.*;
import java.nio.file.*;

import com.sun.net.httpserver.*;

public class Server {
    private static final String QUERY_TEMPLATE = "{\"items\":[%s]}";

    public static void main(String[] args) throws FileNotFoundException, IOException {
        // Initialize data for the algorithm
        MisinformationClassifier classifier = new MisinformationClassifier("pythonWebScraper/Database.csv");
        // Create an HttpServer instance on port 8000 accepting up to 100 concurrent connections
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 100);
        // Return the index.html file when the browser asks (only for the web app, wouldn't be used for Chrome Extension)
        server.createContext("/", (HttpExchange t) -> {
            String html = Files.readString(Paths.get("index.html"));
            send(t, "text/html; charset=utf-8", html);
        });
        // Return a classification when given the phrase to be classified
        server.createContext("/query", (HttpExchange t) -> {
            String s = parse("s", t.getRequestURI().getQuery());
            URLAndType URLandType = classifier.getURLandType(s);
            if (s.equals("") || URLandType == null) {
                send(t, "application/json", String.format(QUERY_TEMPLATE, ""));
                return;
            }
            // Sends the URLAndType as a JSON object
            send(t, "application/json", String.format(QUERY_TEMPLATE, json(URLandType)));
        });
        server.setExecutor(java.util.concurrent.Executors.newCachedThreadPool());
        server.start();
    }
	
	//Parses the query from the client
    private static String parse(String key, String... params) {
        for (String param : params) {
            String[] pair = param.split("=");
            if (pair.length == 2 && pair[0].equals(key)) {
                return pair[1];
            }
        }
        return "";
    }

	//Sends the classification to the client
    private static void send(HttpExchange t, String contentType, String data)
            throws IOException, UnsupportedEncodingException {
        t.getResponseHeaders().set("Content-Type", contentType);
        byte[] response = data.getBytes("UTF-8");
        t.sendResponseHeaders(200, response.length);
        try (OutputStream os = t.getResponseBody()) {
            os.write(response);
        }
    }

	//Converts the URLAndType object to a string in json
    private static String json(URLAndType URLandType) {
        StringBuilder results = new StringBuilder();
        results.append("{\"url\": \"").append(URLandType.getURL()).append("\",\"type\": ").append(URLandType.getCategory()).append("}");
        return results.toString();
    }
}
