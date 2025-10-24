package com.httpbrowser;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.net.ssl.*;
import java.security.cert.X509Certificate;

/**
 * Web Client - HTTP/HTTPS Client với GUI
 */
public class WebClient {
    private JPanel mainPanel;
    private JTextField urlField;
    private JComboBox<String> methodComboBox;
    private JButton sendButton;
    private JTextArea responseArea;
    private JTextArea headerArea;
    private JEditorPane htmlPane;
    private JLabel statusLabel;
    private JCheckBox renderHtmlCheckBox;
    private JCheckBox httpsCheckBox;
    private JTabbedPane tabbedPane;
    private JTextArea postDataArea;
    
    public WebClient() {
        initializeUI();
        trustAllCertificates(); // For HTTPS testing
    }
    
    public JPanel getPanel() {
        return mainPanel;
    }
    
    private void initializeUI() {
        mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Top panel - URL input
        JPanel topPanel = createTopPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);
        
        // Center panel - Tabbed pane for results
        tabbedPane = new JTabbedPane();
        
        // Response Info Tab
        JPanel responseInfoPanel = createResponseInfoPanel();
        tabbedPane.addTab("📊 Thông tin phản hồi", responseInfoPanel);
        
        // Headers Tab
        JPanel headersPanel = createHeadersPanel();
        tabbedPane.addTab("📋 Headers", headersPanel);
        
        // HTML View Tab
        JPanel htmlViewPanel = createHtmlViewPanel();
        tabbedPane.addTab("🌐 HTML View", htmlViewPanel);
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        // Bottom panel - Status bar
        JPanel bottomPanel = createBottomPanel();
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        // Event listeners
        sendButton.addActionListener(e -> sendRequest());
        urlField.addActionListener(e -> sendRequest());
    }
    
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        
        // URL input
        JPanel urlPanel = new JPanel(new BorderLayout(5, 0));
        urlPanel.add(new JLabel("🌐 URL:"), BorderLayout.WEST);
        urlField = new JTextField("http://localhost:8080/");
        urlField.setFont(new Font("Monospaced", Font.PLAIN, 12));
        urlPanel.add(urlField, BorderLayout.CENTER);
        
        // Method selection and button
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        String[] methods = {"GET", "POST", "HEAD"};
        methodComboBox = new JComboBox<>(methods);
        controlPanel.add(new JLabel("Method:"));
        controlPanel.add(methodComboBox);
        
        sendButton = new JButton("🚀 Gửi yêu cầu");
        sendButton.setFont(new Font("Arial", Font.BOLD, 12));
        controlPanel.add(sendButton);
        
        renderHtmlCheckBox = new JCheckBox("Render HTML", true);
        controlPanel.add(renderHtmlCheckBox);
        
        httpsCheckBox = new JCheckBox("🔒 HTTPS", false);
        controlPanel.add(httpsCheckBox);
        
        urlPanel.add(controlPanel, BorderLayout.EAST);
        panel.add(urlPanel, BorderLayout.NORTH);
        
        // POST data panel
        JPanel postPanel = new JPanel(new BorderLayout(5, 0));
        postPanel.add(new JLabel("📝 POST Data (JSON/Form):"), BorderLayout.NORTH);
        postDataArea = new JTextArea(3, 50);
        postDataArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        postDataArea.setText("{\"message\": \"Hello from SimpleHttpBrowser\"}");
        JScrollPane postScroll = new JScrollPane(postDataArea);
        postPanel.add(postScroll, BorderLayout.CENTER);
        postPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        
        panel.add(postPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createResponseInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        
        responseArea = new JTextArea();
        responseArea.setEditable(false);
        responseArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        responseArea.setLineWrap(true);
        responseArea.setWrapStyleWord(true);
        
        JScrollPane scrollPane = new JScrollPane(responseArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createHeadersPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        
        headerArea = new JTextArea();
        headerArea.setEditable(false);
        headerArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(headerArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createHtmlViewPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        
        htmlPane = new JEditorPane();
        htmlPane.setEditable(false);
        htmlPane.setContentType("text/html");
        
        // Add hyperlink listener
        htmlPane.addHyperlinkListener(e -> {
            if (e.getEventType() == javax.swing.event.HyperlinkEvent.EventType.ACTIVATED) {
                if (e.getURL() != null) {
                    urlField.setText(e.getURL().toString());
                    sendRequest();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(htmlPane);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        statusLabel = new JLabel("✅ Sẵn sàng...");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel.add(statusLabel, BorderLayout.WEST);
        
        return panel;
    }
    
    public void loadUrl(String url) {
        urlField.setText(url);
        sendRequest();
    }
    
    private void sendRequest() {
        String urlString = urlField.getText().trim();
        String method = (String) methodComboBox.getSelectedItem();
        
        if (urlString.isEmpty()) {
            JOptionPane.showMessageDialog(mainPanel, "Vui lòng nhập URL!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Disable button during request
        sendButton.setEnabled(false);
        statusLabel.setText("⏳ Đang gửi yêu cầu " + method + "...");
        
        // Execute in background thread
        SwingWorker<HttpResponse, Void> worker = new SwingWorker<>() {
            @Override
            protected HttpResponse doInBackground() {
                return executeRequest(urlString, method);
            }
            
            @Override
            protected void done() {
                try {
                    HttpResponse response = get();
                    displayResponse(response);
                    statusLabel.setText("✅ Hoàn thành - " + response.statusCode + " " + response.statusMessage);
                } catch (Exception e) {
                    String errorMsg = "❌ Lỗi: " + e.getMessage();
                    statusLabel.setText(errorMsg);
                    responseArea.setText(errorMsg + "\n\n" + getStackTraceAsString(e));
                } finally {
                    sendButton.setEnabled(true);
                }
            }
        };
        
        worker.execute();
    }
    
    private HttpResponse executeRequest(String urlString, String method) {
        HttpResponse response = new HttpResponse();
        HttpURLConnection connection = null;
        
        try {
            URI uri = new URI(urlString);
            URL url = uri.toURL();
            connection = (HttpURLConnection) url.openConnection();
            
            // Set request method
            connection.setRequestMethod(method);
            
            // Set headers
            connection.setRequestProperty("User-Agent", "SimpleHttpBrowser/1.0");
            connection.setRequestProperty("Accept", "*/*");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            
            // For POST requests
            if ("POST".equals(method)) {
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                
                String postData = postDataArea.getText().trim();
                if (!postData.isEmpty()) {
                    try (OutputStream os = connection.getOutputStream()) {
                        byte[] input = postData.getBytes("UTF-8");
                        os.write(input, 0, input.length);
                    }
                }
            }
            
            // Get response code and message
            response.statusCode = connection.getResponseCode();
            response.statusMessage = connection.getResponseMessage();
            response.protocol = urlString.toLowerCase().startsWith("https") ? "HTTPS" : "HTTP";
            
            // Get headers
            response.headers = connection.getHeaderFields();
            
            // Get content length
            response.contentLength = connection.getContentLength();
            
            // Get content type
            response.contentType = connection.getContentType();
            
            // Read response body (for GET and POST)
            if ("GET".equals(method) || "POST".equals(method)) {
                InputStream inputStream;
                if (response.statusCode >= 200 && response.statusCode < 400) {
                    inputStream = connection.getInputStream();
                } else {
                    inputStream = connection.getErrorStream();
                }
                
                if (inputStream != null) {
                    response.body = readInputStream(inputStream);
                    
                    // Analyze HTML content
                    if (response.body != null) {
                        response.htmlStats = analyzeHtml(response.body);
                    }
                }
            }
            
        } catch (Exception e) {
            response.error = e.getMessage();
            response.exception = e;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        
        return response;
    }
    
    private String readInputStream(InputStream inputStream) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }
    
    private HtmlStats analyzeHtml(String html) {
        HtmlStats stats = new HtmlStats();
        
        // Count tags
        stats.pTags = countTags(html, "<p");
        stats.divTags = countTags(html, "<div");
        stats.spanTags = countTags(html, "<span");
        stats.imgTags = countTags(html, "<img");
        
        return stats;
    }
    
    private int countTags(String html, String tag) {
        int count = 0;
        int index = 0;
        String lowerHtml = html.toLowerCase();
        String lowerTag = tag.toLowerCase();
        
        while ((index = lowerHtml.indexOf(lowerTag, index)) != -1) {
            count++;
            index += lowerTag.length();
        }
        
        return count;
    }
    
    private void displayResponse(HttpResponse response) {
        StringBuilder info = new StringBuilder();
        
        // Status information
        info.append("═══════════════════════════════════════════════════════\n");
        info.append("📊 THÔNG TIN PHẢN HỒI HTTP\n");
        info.append("═══════════════════════════════════════════════════════\n\n");
        
        info.append("🔐 Protocol: ").append(response.protocol).append("\n");
        info.append("🔴 Mã trạng thái: ").append(response.statusCode).append(" ").append(response.statusMessage).append("\n");
        info.append("📄 Content-Type: ").append(response.contentType != null ? response.contentType : "N/A").append("\n");
        info.append("📏 Chiều dài nội dung: ");
        if (response.contentLength >= 0) {
            info.append(formatBytes(response.contentLength));
        } else if (response.body != null) {
            info.append(formatBytes(response.body.length()));
        } else {
            info.append("Không xác định");
        }
        info.append("\n\n");
        
        // HTML statistics
        if (response.htmlStats != null) {
            info.append("───────────────────────────────────────────────────────\n");
            info.append("📈 THỐNG KÊ HTML TAGS\n");
            info.append("───────────────────────────────────────────────────────\n\n");
            info.append("  🔹 Thẻ <p>:    ").append(response.htmlStats.pTags).append("\n");
            info.append("  🔹 Thẻ <div>:  ").append(response.htmlStats.divTags).append("\n");
            info.append("  🔹 Thẻ <span>: ").append(response.htmlStats.spanTags).append("\n");
            info.append("  🔹 Thẻ <img>:  ").append(response.htmlStats.imgTags).append("\n\n");
        }
        
        // Body content
        if (response.body != null && !response.body.isEmpty()) {
            info.append("───────────────────────────────────────────────────────\n");
            info.append("📄 NỘI DUNG PHẢN HỒI\n");
            info.append("───────────────────────────────────────────────────────\n\n");
            
            // Limit display length
            if (response.body.length() > 20000) {
                info.append(response.body.substring(0, 20000));
                info.append("\n\n... (Nội dung quá dài, chỉ hiển thị 20000 ký tự đầu) ...\n");
            } else {
                info.append(response.body);
            }
        }
        
        responseArea.setText(info.toString());
        responseArea.setCaretPosition(0);
        
        // Display headers
        displayHeaders(response);
        
        // Render HTML if option is enabled
        if (renderHtmlCheckBox.isSelected() && response.body != null && !response.body.isEmpty()) {
            renderHtml(response.body);
        } else {
            htmlPane.setText("<html><body style='padding:20px;font-family:Arial;'><h2>HTML Rendering</h2><p>Chọn checkbox 'Render HTML' và gửi lại yêu cầu để xem kết quả render.</p></body></html>");
        }
    }
    
    private void displayHeaders(HttpResponse response) {
        StringBuilder headers = new StringBuilder();
        
        headers.append("═══════════════════════════════════════════════════════\n");
        headers.append("📋 HTTP HEADERS\n");
        headers.append("═══════════════════════════════════════════════════════\n\n");
        
        if (response.headers != null) {
            for (Map.Entry<String, java.util.List<String>> entry : response.headers.entrySet()) {
                String key = entry.getKey();
                if (key == null) key = "Status";
                
                headers.append(String.format("%-30s : ", key));
                
                java.util.List<String> values = entry.getValue();
                if (values != null && !values.isEmpty()) {
                    headers.append(String.join(", ", values));
                }
                headers.append("\n");
            }
        }
        
        headerArea.setText(headers.toString());
        headerArea.setCaretPosition(0);
    }
    
    private void renderHtml(String html) {
        try {
            htmlPane.setText(html);
            htmlPane.setCaretPosition(0);
        } catch (Exception e) {
            htmlPane.setText("<html><body><h2>Lỗi khi render HTML</h2><pre>" + e.getMessage() + "</pre></body></html>");
        }
    }
    
    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " bytes";
        if (bytes < 1024 * 1024) return String.format("%.2f KB", bytes / 1024.0);
        return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
    }
    
    private String getStackTraceAsString(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
    
    private void trustAllCertificates() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return null; }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                }
            };
            
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            
            HostnameVerifier allHostsValid = (hostname, session) -> true;
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Inner classes
    static class HttpResponse {
        int statusCode;
        String statusMessage;
        String protocol;
        Map<String, java.util.List<String>> headers;
        String body;
        int contentLength;
        String contentType;
        HtmlStats htmlStats;
        String error;
        Exception exception;
    }
    
    static class HtmlStats {
        int pTags;
        int divTags;
        int spanTags;
        int imgTags;
    }
}
