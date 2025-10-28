package com.httpbrowser;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;
import javax.net.ssl.*;
import java.security.cert.X509Certificate;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.awt.datatransfer.*;
import java.awt.Toolkit;

/**
 * Web Client - HTTP/HTTPS Client với GUI - Full Featured Version
 * Version 3.2 - Complete Edition
 */
public class WebClient {
    private JPanel mainPanel;
    private JTextField urlField;
    private JComboBox<String> methodComboBox;
    private JButton sendButton;
    private JButton clearButton;
    private JButton saveButton;
    private JButton copyButton;
    private JTextArea responseArea;
    private JTextArea headerArea;
    private JEditorPane htmlPane;
    private JLabel statusLabel;
    private JLabel responseSizeLabel;
    private JLabel responseTimeLabel;
    private JCheckBox renderHtmlCheckBox;
    private JCheckBox followRedirectsCheckBox;
    private JCheckBox useCookiesCheckBox;
    private JTabbedPane tabbedPane;
    private JTextArea postDataArea;
    private JTextArea customHeadersArea;
    private JComboBox<String> urlHistoryCombo;
    private DefaultComboBoxModel<String> historyModel;
    private List<String> urlHistory;
    private JProgressBar progressBar;
    private JSpinner timeoutSpinner;
    
    // Cookie storage
    private Map<String, List<String>> cookieStore = new HashMap<>();
    private HttpResponse lastResponse;
    
    // Quick access URLs
    private static final String[] POPULAR_URLS = {
        "http://localhost:8080/",
        "https://www.google.com",
        "https://www.github.com",
        "https://api.github.com",
        "https://httpbin.org/get",
        "https://jsonplaceholder.typicode.com/posts",
        "https://www.wikipedia.org",
        "https://stackoverflow.com"
    };
    
    public WebClient() {
        urlHistory = new ArrayList<>();
        historyModel = new DefaultComboBoxModel<>();
        cookieStore = new HashMap<>();
        initializeUI();
        trustAllCertificates(); // For HTTPS testing
        loadPopularUrls();
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
        
        // URL input panel with history
        JPanel urlPanel = new JPanel(new BorderLayout(5, 5));
        
        // URL label and field
        JPanel urlInputPanel = new JPanel(new BorderLayout(5, 0));
        urlInputPanel.add(new JLabel("🌐 URL:"), BorderLayout.WEST);
        
        urlField = new JTextField("http://localhost:8080/");
        urlField.setFont(new Font("Monospaced", Font.PLAIN, 12));
        urlField.setToolTipText("Nhập URL đầy đủ (http:// hoặc https://)");
        urlInputPanel.add(urlField, BorderLayout.CENTER);
        
        // Quick URLs dropdown
        JPanel quickPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        quickPanel.add(new JLabel("⚡ Quick:"));
        urlHistoryCombo = new JComboBox<>(historyModel);
        urlHistoryCombo.setPreferredSize(new Dimension(200, 25));
        urlHistoryCombo.addActionListener(e -> {
            String selected = (String) urlHistoryCombo.getSelectedItem();
            if (selected != null && !selected.isEmpty()) {
                urlField.setText(selected);
            }
        });
        quickPanel.add(urlHistoryCombo);
        urlInputPanel.add(quickPanel, BorderLayout.EAST);
        
        urlPanel.add(urlInputPanel, BorderLayout.NORTH);
        
        // Control panel - Method, buttons, options
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        
        String[] methods = {"GET", "POST", "HEAD", "PUT", "DELETE"};
        methodComboBox = new JComboBox<>(methods);
        methodComboBox.setToolTipText("Chọn HTTP Method");
        controlPanel.add(new JLabel("Method:"));
        controlPanel.add(methodComboBox);
        
        sendButton = new JButton("🚀 Gửi yêu cầu");
        sendButton.setFont(new Font("Arial", Font.BOLD, 12));
        sendButton.setBackground(new Color(0, 120, 215));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);
        sendButton.setToolTipText("Gửi HTTP request (hoặc nhấn Enter)");
        controlPanel.add(sendButton);
        
        clearButton = new JButton("🗑️ Clear");
        clearButton.setToolTipText("Xóa tất cả dữ liệu");
        clearButton.addActionListener(e -> clearAll());
        controlPanel.add(clearButton);
        
        controlPanel.add(new JSeparator(SwingConstants.VERTICAL));
        
        renderHtmlCheckBox = new JCheckBox("🎨 Render HTML", true);
        renderHtmlCheckBox.setToolTipText("Hiển thị HTML được render");
        controlPanel.add(renderHtmlCheckBox);
        
        followRedirectsCheckBox = new JCheckBox("↪️ Follow Redirects", true);
        followRedirectsCheckBox.setToolTipText("Tự động follow HTTP redirects (3xx)");
        controlPanel.add(followRedirectsCheckBox);
        
        useCookiesCheckBox = new JCheckBox("🍪 Use Cookies", true);
        useCookiesCheckBox.setToolTipText("Tự động lưu và gửi cookies");
        controlPanel.add(useCookiesCheckBox);
        
        controlPanel.add(new JSeparator(SwingConstants.VERTICAL));
        
        // Timeout configuration
        JLabel timeoutLabel = new JLabel("⏱️ Timeout:");
        controlPanel.add(timeoutLabel);
        
        SpinnerNumberModel timeoutModel = new SpinnerNumberModel(15, 5, 60, 5);
        timeoutSpinner = new JSpinner(timeoutModel);
        timeoutSpinner.setPreferredSize(new Dimension(60, 25));
        timeoutSpinner.setToolTipText("Timeout trong giây");
        controlPanel.add(timeoutSpinner);
        controlPanel.add(new JLabel("s"));
        
        controlPanel.add(new JSeparator(SwingConstants.VERTICAL));
        
        saveButton = new JButton("💾 Save");
        saveButton.setToolTipText("Lưu response vào file");
        saveButton.addActionListener(e -> saveResponse());
        controlPanel.add(saveButton);
        
        copyButton = new JButton("📋 Copy");
        copyButton.setToolTipText("Copy response to clipboard");
        copyButton.addActionListener(e -> copyToClipboard());
        controlPanel.add(copyButton);
        
        urlPanel.add(controlPanel, BorderLayout.CENTER);
        panel.add(urlPanel, BorderLayout.NORTH);
        
        // Tabbed panel for POST data and Custom Headers
        JTabbedPane inputTabs = new JTabbedPane();
        
        // POST data panel
        JPanel postPanel = new JPanel(new BorderLayout(5, 5));
        postDataArea = new JTextArea(3, 50);
        postDataArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        postDataArea.setText("{\"message\": \"Hello from SimpleHttpBrowser\", \"timestamp\": \"" + 
                           LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "\"}");
        postDataArea.setLineWrap(true);
        JScrollPane postScroll = new JScrollPane(postDataArea);
        postPanel.add(postScroll, BorderLayout.CENTER);
        inputTabs.addTab("📝 Request Body", postPanel);
        
        // Custom headers panel
        JPanel headersPanel = new JPanel(new BorderLayout(5, 5));
        customHeadersArea = new JTextArea(3, 50);
        customHeadersArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        customHeadersArea.setText("# Custom Headers (format: Key: Value)\n# User-Agent: CustomBrowser/1.0\n# Authorization: Bearer token123");
        customHeadersArea.setLineWrap(true);
        JScrollPane headersScroll = new JScrollPane(customHeadersArea);
        headersPanel.add(headersScroll, BorderLayout.CENTER);
        inputTabs.addTab("🔧 Custom Headers", headersPanel);
        
        panel.add(inputTabs, BorderLayout.CENTER);
        
        // Progress bar
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(false);
        progressBar.setStringPainted(true);
        progressBar.setString("Ready");
        progressBar.setVisible(false);
        panel.add(progressBar, BorderLayout.SOUTH);
        
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
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        
        // Status label
        statusLabel = new JLabel("✅ Sẵn sàng...");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        panel.add(statusLabel, BorderLayout.CENTER);
        
        // Info panel - Response time and size
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        
        responseTimeLabel = new JLabel("⏱️ Time: 0ms");
        responseTimeLabel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        infoPanel.add(responseTimeLabel);
        
        responseSizeLabel = new JLabel("📦 Size: 0 bytes");
        responseSizeLabel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        infoPanel.add(responseSizeLabel);
        
        panel.add(infoPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private void clearAll() {
        responseArea.setText("");
        headerArea.setText("");
        htmlPane.setText("");
        statusLabel.setText("✅ Cleared - Ready");
        responseTimeLabel.setText("⏱️ Time: 0ms");
        responseSizeLabel.setText("📦 Size: 0 bytes");
        lastResponse = null;
    }
    
    private void saveResponse() {
        if (lastResponse == null || lastResponse.body == null) {
            JOptionPane.showMessageDialog(mainPanel, 
                "Không có response để lưu!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Lưu Response");
        
        // Suggest file extension based on content type
        String extension = ".txt";
        if (lastResponse.contentType != null) {
            if (lastResponse.contentType.contains("html")) extension = ".html";
            else if (lastResponse.contentType.contains("json")) extension = ".json";
            else if (lastResponse.contentType.contains("xml")) extension = ".xml";
        }
        
        fileChooser.setSelectedFile(new java.io.File("response" + extension));
        
        int result = fileChooser.showSaveDialog(mainPanel);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                java.io.File file = fileChooser.getSelectedFile();
                try (java.io.FileWriter writer = new java.io.FileWriter(file)) {
                    writer.write(lastResponse.body);
                }
                JOptionPane.showMessageDialog(mainPanel, 
                    "✅ Đã lưu response vào:\n" + file.getAbsolutePath(), 
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(mainPanel, 
                    "❌ Lỗi khi lưu file:\n" + ex.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void copyToClipboard() {
        if (lastResponse == null || lastResponse.body == null) {
            JOptionPane.showMessageDialog(mainPanel, 
                "Không có response để copy!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Show dialog to choose what to copy
        Object[] options = {"Response Body", "Headers", "All (Body + Headers)", "Cancel"};
        int choice = JOptionPane.showOptionDialog(mainPanel,
            "Chọn nội dung cần copy:",
            "Copy to Clipboard",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]);
        
        String content = "";
        switch (choice) {
            case 0: // Response Body
                content = lastResponse.body;
                break;
            case 1: // Headers
                content = headerArea.getText();
                break;
            case 2: // All
                content = "═══════════════════════════════════════════════════════\n";
                content += "HEADERS:\n";
                content += "═══════════════════════════════════════════════════════\n\n";
                content += headerArea.getText();
                content += "\n\n═══════════════════════════════════════════════════════\n";
                content += "RESPONSE BODY:\n";
                content += "═══════════════════════════════════════════════════════\n\n";
                content += lastResponse.body;
                break;
            default:
                return;
        }
        
        if (!content.isEmpty()) {
            StringSelection selection = new StringSelection(content);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, selection);
            
            statusLabel.setText("✅ Đã copy vào clipboard!");
            
            // Reset status after 3 seconds
            javax.swing.Timer timer = new javax.swing.Timer(3000, e -> statusLabel.setText("✅ Sẵn sàng..."));
            timer.setRepeats(false);
            timer.start();
        }
    }
    
    private void loadPopularUrls() {
        for (String url : POPULAR_URLS) {
            historyModel.addElement(url);
        }
    }
    
    private void addToHistory(String url) {
        if (!urlHistory.contains(url)) {
            urlHistory.add(0, url);
            historyModel.insertElementAt(url, 0);
            
            // Keep history limited
            if (urlHistory.size() > 20) {
                urlHistory.remove(urlHistory.size() - 1);
                historyModel.removeElementAt(historyModel.getSize() - 1);
            }
        }
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
        
        // Add to history
        addToHistory(urlString);
        
        // Disable button during request
        sendButton.setEnabled(false);
        progressBar.setVisible(true);
        progressBar.setIndeterminate(true);
        progressBar.setString("Sending " + method + " request...");
        statusLabel.setText("⏳ Đang gửi yêu cầu " + method + "...");
        
        // Execute in background thread
        SwingWorker<HttpResponse, Void> worker = new SwingWorker<>() {
            private long startTime;
            
            @Override
            protected HttpResponse doInBackground() {
                startTime = System.currentTimeMillis();
                return executeRequest(urlString, method);
            }
            
            @Override
            protected void done() {
                try {
                    HttpResponse response = get();
                    long responseTime = System.currentTimeMillis() - startTime;
                    response.responseTime = responseTime;
                    
                    displayResponse(response);
                    
                    String statusMsg = String.format("✅ Hoàn thành - %d %s", 
                                                    response.statusCode, response.statusMessage);
                    statusLabel.setText(statusMsg);
                    responseTimeLabel.setText(String.format("⏱️ Time: %dms", responseTime));
                    
                    if (response.body != null) {
                        responseSizeLabel.setText("📦 Size: " + formatBytes(response.body.length()));
                    }
                    
                } catch (Exception e) {
                    String errorMsg = "❌ Lỗi: " + e.getMessage();
                    statusLabel.setText(errorMsg);
                    responseArea.setText(errorMsg + "\n\n" + getStackTraceAsString(e));
                    progressBar.setString("Error");
                } finally {
                    sendButton.setEnabled(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setVisible(false);
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
            
            // Get timeout from spinner (in seconds)
            int timeoutSeconds = (Integer) timeoutSpinner.getValue();
            int timeoutMs = timeoutSeconds * 1000;
            
            // Set default headers
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) SimpleHttpBrowser/3.2");
            connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            connection.setRequestProperty("Accept-Language", "en-US,en;q=0.9,vi;q=0.8");
            connection.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
            connection.setRequestProperty("Connection", "keep-alive");
            connection.setConnectTimeout(timeoutMs);
            connection.setReadTimeout(timeoutMs);
            
            // Follow redirects
            connection.setInstanceFollowRedirects(followRedirectsCheckBox.isSelected());
            
            // Add cookies if enabled
            if (useCookiesCheckBox.isSelected()) {
                String cookieHeader = getCookiesForUrl(urlString);
                if (!cookieHeader.isEmpty()) {
                    connection.setRequestProperty("Cookie", cookieHeader);
                }
            }
            
            // Parse and set custom headers
            parseAndSetCustomHeaders(connection);
            
            // For POST, PUT, DELETE requests
            if ("POST".equals(method) || "PUT".equals(method) || "DELETE".equals(method)) {
                connection.setDoOutput(true);
                
                // Set content type if not already set by custom headers
                if (connection.getRequestProperty("Content-Type") == null) {
                    connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                }
                
                String postData = postDataArea.getText().trim();
                if (!postData.isEmpty() && !postData.startsWith("#")) {
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
            
            // Store cookies if enabled
            if (useCookiesCheckBox.isSelected()) {
                storeCookiesFromResponse(urlString, response.headers);
            }
            
            // Get content length
            response.contentLength = connection.getContentLength();
            
            // Get content type
            response.contentType = connection.getContentType();
            
            // Read response body (for all methods except HEAD)
            if (!"HEAD".equals(method)) {
                InputStream inputStream;
                if (response.statusCode >= 200 && response.statusCode < 400) {
                    inputStream = connection.getInputStream();
                } else {
                    inputStream = connection.getErrorStream();
                }
                
                if (inputStream != null) {
                    // Check if response is compressed
                    String encoding = connection.getContentEncoding();
                    if ("gzip".equalsIgnoreCase(encoding)) {
                        inputStream = new java.util.zip.GZIPInputStream(inputStream);
                    }
                    
                    response.body = readInputStream(inputStream);
                    
                    // Analyze HTML content
                    if (response.body != null && 
                        (response.contentType != null && 
                         (response.contentType.contains("html") || response.contentType.contains("xml")))) {
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
    
    private void parseAndSetCustomHeaders(HttpURLConnection connection) {
        String headersText = customHeadersArea.getText();
        if (headersText == null || headersText.trim().isEmpty()) {
            return;
        }
        
        String[] lines = headersText.split("\n");
        for (String line : lines) {
            line = line.trim();
            // Skip comments and empty lines
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            
            int colonIndex = line.indexOf(":");
            if (colonIndex > 0 && colonIndex < line.length() - 1) {
                String key = line.substring(0, colonIndex).trim();
                String value = line.substring(colonIndex + 1).trim();
                if (!key.isEmpty() && !value.isEmpty()) {
                    connection.setRequestProperty(key, value);
                }
            }
        }
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
        // Save response for later use (save/copy functions)
        lastResponse = response;
        
        StringBuilder info = new StringBuilder();
        
        // Status information
        info.append("═══════════════════════════════════════════════════════\n");
        info.append("📊 THÔNG TIN PHẢN HỒI HTTP/HTTPS\n");
        info.append("═══════════════════════════════════════════════════════\n\n");
        
        info.append("🔐 Protocol: ").append(response.protocol).append("\n");
        info.append("🔴 Status Code: ").append(response.statusCode).append(" ").append(response.statusMessage).append("\n");
        info.append("📄 Content-Type: ").append(response.contentType != null ? response.contentType : "N/A").append("\n");
        
        if (response.responseTime > 0) {
            info.append("⏱️  Response Time: ").append(response.responseTime).append(" ms\n");
        }
        
        info.append("📏 Content-Length: ");
        if (response.contentLength >= 0) {
            info.append(formatBytes(response.contentLength));
        } else if (response.body != null) {
            info.append(formatBytes(response.body.length())).append(" (actual)");
        } else {
            info.append("Không xác định");
        }
        info.append("\n\n");
        
        // HTML statistics
        if (response.htmlStats != null) {
            info.append("───────────────────────────────────────────────────────\n");
            info.append("📈 THỐNG KÊ HTML TAGS\n");
            info.append("───────────────────────────────────────────────────────\n\n");
            info.append(String.format("  🔹 Thẻ <p>:    %d\n", response.htmlStats.pTags));
            info.append(String.format("  🔹 Thẻ <div>:  %d\n", response.htmlStats.divTags));
            info.append(String.format("  🔹 Thẻ <span>: %d\n", response.htmlStats.spanTags));
            info.append(String.format("  🔹 Thẻ <img>:  %d\n\n", response.htmlStats.imgTags));
            int totalTags = response.htmlStats.pTags + response.htmlStats.divTags + 
                           response.htmlStats.spanTags + response.htmlStats.imgTags;
            info.append(String.format("  📊 Tổng tags: %d\n\n", totalTags));
        }
        
        // Body content
        if (response.body != null && !response.body.isEmpty()) {
            info.append("───────────────────────────────────────────────────────\n");
            info.append("📄 NỘI DUNG PHẢN HỒI\n");
            info.append("───────────────────────────────────────────────────────\n\n");
            
            // Pretty print JSON
            String displayBody = response.body;
            if (response.contentType != null && response.contentType.contains("json")) {
                try {
                    displayBody = prettyPrintJson(response.body);
                } catch (Exception e) {
                    // Keep original if formatting fails
                }
            }
            
            // Limit display length
            if (displayBody.length() > 30000) {
                info.append(displayBody.substring(0, 30000));
                info.append("\n\n... (Nội dung quá dài, chỉ hiển thị 30000 ký tự đầu) ...\n");
                info.append("💡 Tổng độ dài: ").append(formatBytes(displayBody.length())).append("\n");
            } else {
                info.append(displayBody);
            }
        }
        
        responseArea.setText(info.toString());
        responseArea.setCaretPosition(0);
        
        // Display headers
        displayHeaders(response);
        
        // Render HTML if option is enabled
        if (renderHtmlCheckBox.isSelected() && response.body != null && !response.body.isEmpty()) {
            if (response.contentType != null && response.contentType.contains("html")) {
                renderHtml(response.body);
            } else {
                htmlPane.setText("<html><body style='padding:20px;font-family:Arial;'>" +
                               "<h2>Not HTML Content</h2>" +
                               "<p>Content-Type: " + response.contentType + "</p>" +
                               "<p>Chỉ HTML content mới được render.</p>" +
                               "</body></html>");
            }
        } else {
            htmlPane.setText("<html><body style='padding:20px;font-family:Arial;'>" +
                           "<h2>HTML Rendering</h2>" +
                           "<p>✅ Chọn checkbox 'Render HTML' để xem kết quả render.</p>" +
                           "<p>📝 Chỉ áp dụng cho HTML content.</p>" +
                           "</body></html>");
        }
    }
    
    private String prettyPrintJson(String json) {
        // Simple JSON pretty print
        StringBuilder pretty = new StringBuilder();
        int indent = 0;
        boolean inString = false;
        
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            
            if (c == '"' && (i == 0 || json.charAt(i - 1) != '\\')) {
                inString = !inString;
                pretty.append(c);
            } else if (!inString) {
                switch (c) {
                    case '{':
                    case '[':
                        pretty.append(c).append('\n');
                        indent++;
                        pretty.append("  ".repeat(indent));
                        break;
                    case '}':
                    case ']':
                        pretty.append('\n');
                        indent--;
                        pretty.append("  ".repeat(indent)).append(c);
                        break;
                    case ',':
                        pretty.append(c).append('\n');
                        pretty.append("  ".repeat(indent));
                        break;
                    case ':':
                        pretty.append(c).append(' ');
                        break;
                    default:
                        if (!Character.isWhitespace(c)) {
                            pretty.append(c);
                        }
                }
            } else {
                pretty.append(c);
            }
        }
        
        return pretty.toString();
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
            // Set base URL from current request URL for proper resource loading
            String urlText = urlField.getText().trim();
            if (!urlText.isEmpty()) {
                try {
                    URI uri = new URI(urlText);
                    URL baseUrl = uri.toURL();
                    ((javax.swing.text.html.HTMLDocument) htmlPane.getDocument()).setBase(baseUrl);
                } catch (Exception e) {
                    // Ignore if URL is invalid or document is not HTMLDocument
                }
            }
            
            htmlPane.setContentType("text/html; charset=UTF-8");
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
    
    // Cookie management methods
    private String getCookiesForUrl(String urlString) {
        try {
            URI uri = new URI(urlString);
            String domain = uri.getHost();
            
            List<String> cookies = cookieStore.get(domain);
            if (cookies != null && !cookies.isEmpty()) {
                return String.join("; ", cookies);
            }
        } catch (Exception e) {
            // Ignore invalid URLs
        }
        return "";
    }
    
    private void storeCookiesFromResponse(String urlString, Map<String, List<String>> headers) {
        if (headers == null) return;
        
        try {
            URI uri = new URI(urlString);
            String domain = uri.getHost();
            
            List<String> setCookieHeaders = headers.get("Set-Cookie");
            if (setCookieHeaders != null && !setCookieHeaders.isEmpty()) {
                List<String> cookies = cookieStore.computeIfAbsent(domain, k -> new ArrayList<>());
                
                for (String setCookie : setCookieHeaders) {
                    // Extract cookie name=value (ignore attributes)
                    String[] parts = setCookie.split(";");
                    if (parts.length > 0) {
                        String cookie = parts[0].trim();
                        
                        // Update or add cookie
                        String cookieName = cookie.split("=")[0];
                        cookies.removeIf(c -> c.startsWith(cookieName + "="));
                        cookies.add(cookie);
                    }
                }
            }
        } catch (Exception e) {
            // Ignore errors in cookie processing
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
        long responseTime;
    }
    
    static class HtmlStats {
        int pTags;
        int divTags;
        int spanTags;
        int imgTags;
    }
}
