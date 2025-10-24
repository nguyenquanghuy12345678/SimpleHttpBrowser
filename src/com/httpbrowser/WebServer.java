package com.httpbrowser;

import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Web Server - Simple HTTP Server để test
 */
public class WebServer {
    private HttpServer server;
    private int port;
    private boolean running = false;
    
    public WebServer(int port) {
        this.port = port;
    }
    
    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        
        // Register endpoints
        server.createContext("/", new HomeHandler());
        server.createContext("/test", new TestHandler());
        server.createContext("/info", new InfoHandler());
        server.createContext("/echo", new EchoHandler());
        server.createContext("/status", new StatusHandler());
        server.createContext("/api/users", new ApiUsersHandler());
        
        server.setExecutor(null); // Creates a default executor
        server.start();
        running = true;
        
        System.out.println("🖧 Web Server started on port " + port);
    }
    
    public void stop() {
        if (server != null) {
            server.stop(0);
            running = false;
            System.out.println("🛑 Web Server stopped");
        }
    }
    
    public boolean isRunning() {
        return running;
    }
    
    // Home Handler
    static class HomeHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            
            if ("GET".equals(method)) {
                handleGet(exchange);
            } else if ("HEAD".equals(method)) {
                handleHead(exchange);
            } else {
                sendResponse(exchange, 405, "Method Not Allowed");
            }
        }
        
        private void handleGet(HttpExchange exchange) throws IOException {
            String html = generateHomePage();
            byte[] response = html.getBytes(StandardCharsets.UTF_8);
            
            exchange.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, response.length);
            
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response);
            }
        }
        
        private void handleHead(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
            exchange.getResponseHeaders().add("Content-Length", String.valueOf(generateHomePage().length()));
            exchange.sendResponseHeaders(200, -1);
        }
        
        private String generateHomePage() {
            return "<!DOCTYPE html>\n" +
                "<html lang='vi'>\n" +
                "<head>\n" +
                "    <meta charset='UTF-8'>\n" +
                "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>\n" +
                "    <title>Simple HTTP Browser - Home</title>\n" +
                "    <style>\n" +
                "        body { font-family: Arial, sans-serif; margin: 40px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; }\n" +
                "        .container { max-width: 800px; margin: 0 auto; background: rgba(255,255,255,0.95); padding: 30px; border-radius: 10px; color: #333; box-shadow: 0 10px 30px rgba(0,0,0,0.3); }\n" +
                "        h1 { color: #667eea; text-align: center; margin-bottom: 10px; }\n" +
                "        .subtitle { text-align: center; color: #888; margin-bottom: 30px; }\n" +
                "        .info { background: #f0f0f0; padding: 15px; border-radius: 5px; margin: 20px 0; border-left: 4px solid #667eea; }\n" +
                "        .endpoints { background: white; padding: 20px; border-radius: 5px; margin: 20px 0; }\n" +
                "        .endpoint { padding: 10px; margin: 5px 0; background: #f8f8f8; border-radius: 3px; font-family: monospace; }\n" +
                "        .method { display: inline-block; padding: 3px 8px; border-radius: 3px; font-weight: bold; margin-right: 10px; }\n" +
                "        .get { background: #28a745; color: white; }\n" +
                "        .post { background: #ffc107; color: #333; }\n" +
                "        .head { background: #17a2b8; color: white; }\n" +
                "        a { color: #667eea; text-decoration: none; }\n" +
                "        a:hover { text-decoration: underline; }\n" +
                "        .stats { display: flex; justify-content: space-around; margin: 20px 0; }\n" +
                "        .stat { text-align: center; padding: 15px; background: #667eea; color: white; border-radius: 5px; flex: 1; margin: 0 5px; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class='container'>\n" +
                "        <h1>🌐 Simple HTTP Browser Server</h1>\n" +
                "        <p class='subtitle'>Web Server cho testing HTTP/HTTPS requests</p>\n" +
                "        \n" +
                "        <div class='info'>\n" +
                "            <p><strong>⏰ Server Time:</strong> " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "</p>\n" +
                "            <p><strong>🖧 Status:</strong> <span style='color: green;'>🟢 Running</span></p>\n" +
                "            <p><strong>📍 Port:</strong> 8080</p>\n" +
                "        </div>\n" +
                "        \n" +
                "        <div class='stats'>\n" +
                "            <div class='stat'><h2>5</h2><p>Endpoints</p></div>\n" +
                "            <div class='stat'><h2>3</h2><p>Methods</p></div>\n" +
                "            <div class='stat'><h2>✓</h2><p>Active</p></div>\n" +
                "        </div>\n" +
                "        \n" +
                "        <div class='endpoints'>\n" +
                "            <h3>📍 Available Endpoints:</h3>\n" +
                "            <div class='endpoint'>\n" +
                "                <span class='method get'>GET</span>\n" +
                "                <a href='/'>/</a> - Trang chủ này\n" +
                "            </div>\n" +
                "            <div class='endpoint'>\n" +
                "                <span class='method get'>GET</span>\n" +
                "                <a href='/test'>/test</a> - Trang test với nhiều HTML tags\n" +
                "            </div>\n" +
                "            <div class='endpoint'>\n" +
                "                <span class='method get'>GET</span>\n" +
                "                <a href='/info'>/info</a> - Thông tin server chi tiết\n" +
                "            </div>\n" +
                "            <div class='endpoint'>\n" +
                "                <span class='method post'>POST</span>\n" +
                "                /echo - Echo lại POST data\n" +
                "            </div>\n" +
                "            <div class='endpoint'>\n" +
                "                <span class='method head'>HEAD</span>\n" +
                "                /status - Status check (HEAD only)\n" +
                "            </div>\n" +
                "            <div class='endpoint'>\n" +
                "                <span class='method get'>GET</span>\n" +
                "                <a href='/api/users'>/api/users</a> - Demo API JSON\n" +
                "            </div>\n" +
                "        </div>\n" +
                "        \n" +
                "        <div class='info'>\n" +
                "            <h4>💡 Hướng dẫn sử dụng:</h4>\n" +
                "            <p>1. Mở tab <strong>Web Client</strong></p>\n" +
                "            <p>2. Nhập URL (ví dụ: http://localhost:8080/test)</p>\n" +
                "            <p>3. Chọn method (GET, POST, hoặc HEAD)</p>\n" +
                "            <p>4. Nhấn <strong>Gửi yêu cầu</strong></p>\n" +
                "            <p>5. Xem kết quả trong các tab</p>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
        }
    }
    
    // Test Handler with many HTML tags
    static class TestHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "Method Not Allowed");
                return;
            }
            
            String html = generateTestPage();
            byte[] response = html.getBytes(StandardCharsets.UTF_8);
            
            exchange.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, response.length);
            
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response);
            }
        }
        
        private String generateTestPage() {
            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'><title>Test Page</title></head><body>");
            html.append("<h1>Test Page - HTML Tags Demo</h1>");
            
            // Add multiple divs
            for (int i = 1; i <= 10; i++) {
                html.append("<div class='section'>This is div number ").append(i).append("</div>");
            }
            
            // Add multiple paragraphs
            for (int i = 1; i <= 15; i++) {
                html.append("<p>This is paragraph number ").append(i).append(". Lorem ipsum dolor sit amet.</p>");
            }
            
            // Add multiple spans
            for (int i = 1; i <= 8; i++) {
                html.append("<span>Span ").append(i).append("</span> ");
            }
            
            // Add multiple images
            for (int i = 1; i <= 5; i++) {
                html.append("<img src='image").append(i).append(".jpg' alt='Image ").append(i).append("'>");
            }
            
            html.append("</body></html>");
            return html.toString();
        }
    }
    
    // Info Handler
    static class InfoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "Method Not Allowed");
                return;
            }
            
            StringBuilder info = new StringBuilder();
            info.append("<!DOCTYPE html><html><head><meta charset='UTF-8'><title>Server Info</title>");
            info.append("<style>body{font-family:monospace;padding:20px;background:#f5f5f5;}");
            info.append("table{border-collapse:collapse;width:100%;background:white;}");
            info.append("th,td{border:1px solid #ddd;padding:12px;text-align:left;}");
            info.append("th{background:#667eea;color:white;}</style></head><body>");
            info.append("<h2>🖧 Server Information</h2>");
            info.append("<table>");
            info.append("<tr><th>Property</th><th>Value</th></tr>");
            info.append("<tr><td>Server Time</td><td>").append(LocalDateTime.now()).append("</td></tr>");
            info.append("<tr><td>Server Port</td><td>8080</td></tr>");
            info.append("<tr><td>Protocol</td><td>HTTP/1.1</td></tr>");
            info.append("<tr><td>Request Method</td><td>").append(exchange.getRequestMethod()).append("</td></tr>");
            info.append("<tr><td>Request URI</td><td>").append(exchange.getRequestURI()).append("</td></tr>");
            info.append("<tr><td>Remote Address</td><td>").append(exchange.getRemoteAddress()).append("</td></tr>");
            
            // Headers
            info.append("<tr><td colspan='2'><strong>Request Headers:</strong></td></tr>");
            for (Map.Entry<String, List<String>> entry : exchange.getRequestHeaders().entrySet()) {
                info.append("<tr><td>").append(entry.getKey()).append("</td><td>")
                    .append(String.join(", ", entry.getValue())).append("</td></tr>");
            }
            
            info.append("</table></body></html>");
            
            byte[] response = info.toString().getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, response.length);
            
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response);
            }
        }
    }
    
    // Echo Handler for POST
    static class EchoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "Method Not Allowed - Use POST");
                return;
            }
            
            // Read POST body
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            
            String response = "{\n" +
                "  \"status\": \"success\",\n" +
                "  \"message\": \"Echo successful\",\n" +
                "  \"timestamp\": \"" + LocalDateTime.now() + "\",\n" +
                "  \"receivedData\": " + body + "\n" +
                "}";
            
            byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(200, responseBytes.length);
            
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBytes);
            }
        }
    }
    
    // Status Handler for HEAD
    static class StatusHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().add("Content-Type", "text/plain");
            exchange.getResponseHeaders().add("Server-Status", "OK");
            exchange.getResponseHeaders().add("Server-Time", LocalDateTime.now().toString());
            exchange.getResponseHeaders().add("X-Custom-Header", "SimpleHttpBrowser-Server");
            
            if ("HEAD".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(200, -1);
            } else if ("GET".equals(exchange.getRequestMethod())) {
                String response = "Status: OK\nTime: " + LocalDateTime.now();
                byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(200, bytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                }
            } else {
                sendResponse(exchange, 405, "Method Not Allowed");
            }
        }
    }
    
    // API Users Handler
    static class ApiUsersHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "Method Not Allowed");
                return;
            }
            
            String json = "{\n" +
                "  \"users\": [\n" +
                "    {\"id\": 1, \"name\": \"Nguyen Van A\", \"email\": \"nguyenvana@example.com\"},\n" +
                "    {\"id\": 2, \"name\": \"Tran Thi B\", \"email\": \"tranthib@example.com\"},\n" +
                "    {\"id\": 3, \"name\": \"Le Van C\", \"email\": \"levanc@example.com\"}\n" +
                "  ],\n" +
                "  \"total\": 3,\n" +
                "  \"timestamp\": \"" + LocalDateTime.now() + "\"\n" +
                "}";
            
            byte[] response = json.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(200, response.length);
            
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response);
            }
        }
    }
    
    private static void sendResponse(HttpExchange exchange, int statusCode, String message) throws IOException {
        byte[] response = message.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, response.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response);
        }
    }
}
