package com.httpbrowser;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.html.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;
import java.util.List;
import java.io.*;
import javax.net.ssl.*;
import java.security.cert.X509Certificate;

/**
 * Web Browser - Full-featured web browser với navigation, history, bookmarks
 * Enhanced rendering for modern websites
 */
public class WebBrowser extends JPanel {
    private JTextField addressBar;
    private JButton backButton;
    private JButton forwardButton;
    private JButton refreshButton;
    private JButton homeButton;
    private JButton stopButton;
    private JButton bookmarkButton;
    private JEditorPane browserPane;
    private JProgressBar progressBar;
    private JLabel statusLabel;
    private JTabbedPane tabPane;
    
    // Navigation
    private Stack<String> backHistory;
    private Stack<String> forwardHistory;
    private String currentUrl;
    private String homeUrl = "http://localhost:8080/";
    
    // Bookmarks
    private List<Bookmark> bookmarks;
    private JComboBox<Bookmark> bookmarksCombo;
    private DefaultComboBoxModel<Bookmark> bookmarksModel;
    
    // Loading state
    private boolean isLoading = false;
    private SwingWorker<String, Void> currentLoader;
    
    public WebBrowser() {
        backHistory = new Stack<>();
        forwardHistory = new Stack<>();
        bookmarks = new ArrayList<>();
        bookmarksModel = new DefaultComboBoxModel<>();
        
        trustAllCertificates(); // Enable HTTPS support
        initializeUI();
        loadDefaultBookmarks();
        navigateTo(homeUrl);
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Top panel - Navigation bar
        JPanel navPanel = createNavigationPanel();
        add(navPanel, BorderLayout.NORTH);
        
        // Center - Browser pane with enhanced rendering
        browserPane = new JEditorPane();
        browserPane.setEditable(false);
        
        // Use HTMLEditorKit for better HTML support
        HTMLEditorKit kit = new HTMLEditorKit();
        browserPane.setEditorKit(kit);
        
        // Add custom CSS for better rendering
        StyleSheet styleSheet = kit.getStyleSheet();
        styleSheet.addRule("body { font-family: 'Segoe UI', Arial, sans-serif; font-size: 14px; margin: 10px; background-color: white; }");
        styleSheet.addRule("h1 { font-size: 32px; font-weight: bold; margin: 20px 0 10px 0; color: #202124; }");
        styleSheet.addRule("h2 { font-size: 24px; font-weight: bold; margin: 18px 0 10px 0; color: #202124; }");
        styleSheet.addRule("h3 { font-size: 20px; font-weight: bold; margin: 16px 0 10px 0; color: #202124; }");
        styleSheet.addRule("p { margin: 10px 0; line-height: 1.6; color: #3c4043; }");
        styleSheet.addRule("a { color: #1a73e8; text-decoration: none; }");
        styleSheet.addRule("a:hover { text-decoration: underline; }");
        styleSheet.addRule("img { max-width: 100%; height: auto; }");
        styleSheet.addRule("table { border-collapse: collapse; width: 100%; margin: 10px 0; }");
        styleSheet.addRule("td, th { border: 1px solid #dadce0; padding: 8px; text-align: left; }");
        styleSheet.addRule("th { background-color: #f1f3f4; font-weight: bold; }");
        styleSheet.addRule("ul, ol { margin: 10px 0; padding-left: 40px; }");
        styleSheet.addRule("li { margin: 5px 0; line-height: 1.6; }");
        styleSheet.addRule("code { background-color: #f1f3f4; padding: 2px 6px; border-radius: 3px; font-family: 'Consolas', monospace; }");
        styleSheet.addRule("pre { background-color: #f1f3f4; padding: 12px; border-radius: 4px; overflow-x: auto; }");
        styleSheet.addRule("blockquote { border-left: 4px solid #dadce0; padding-left: 16px; margin: 10px 0; color: #5f6368; }");
        styleSheet.addRule("hr { border: none; border-top: 1px solid #dadce0; margin: 20px 0; }");
        styleSheet.addRule("div { margin: 5px 0; }");
        styleSheet.addRule(".container { max-width: 1200px; margin: 0 auto; }");
        
        browserPane.setContentType("text/html; charset=UTF-8");
        
        // Handle hyperlink clicks
        browserPane.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    if (e.getURL() != null) {
                        String url = e.getURL().toString();
                        navigateTo(url);
                    }
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(browserPane);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        add(scrollPane, BorderLayout.CENTER);
        
        // Bottom - Status bar
        JPanel statusPanel = createStatusPanel();
        add(statusPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createNavigationPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 3));
        
        backButton = new JButton("◀");
        backButton.setToolTipText("Back (Alt+Left)");
        backButton.setEnabled(false);
        backButton.addActionListener(e -> goBack());
        buttonPanel.add(backButton);
        
        forwardButton = new JButton("▶");
        forwardButton.setToolTipText("Forward (Alt+Right)");
        forwardButton.setEnabled(false);
        forwardButton.addActionListener(e -> goForward());
        buttonPanel.add(forwardButton);
        
        refreshButton = new JButton("🔄");
        refreshButton.setToolTipText("Refresh (F5)");
        refreshButton.addActionListener(e -> refresh());
        buttonPanel.add(refreshButton);
        
        stopButton = new JButton("⏹");
        stopButton.setToolTipText("Stop (Esc)");
        stopButton.setEnabled(false);
        stopButton.addActionListener(e -> stopLoading());
        buttonPanel.add(stopButton);
        
        homeButton = new JButton("🏠");
        homeButton.setToolTipText("Home");
        homeButton.addActionListener(e -> navigateTo(homeUrl));
        buttonPanel.add(homeButton);
        
        panel.add(buttonPanel, BorderLayout.WEST);
        
        // Address bar panel
        JPanel addressPanel = new JPanel(new BorderLayout(5, 0));
        
        JLabel urlLabel = new JLabel("🔒");
        urlLabel.setToolTipText("Secure connection");
        addressPanel.add(urlLabel, BorderLayout.WEST);
        
        addressBar = new JTextField();
        addressBar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        addressBar.addActionListener(e -> {
            String url = addressBar.getText().trim();
            if (!url.isEmpty()) {
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    url = "http://" + url;
                }
                navigateTo(url);
            }
        });
        addressPanel.add(addressBar, BorderLayout.CENTER);
        
        JButton goButton = new JButton("→");
        goButton.setToolTipText("Go");
        goButton.addActionListener(e -> addressBar.postActionEvent());
        addressPanel.add(goButton, BorderLayout.EAST);
        
        panel.add(addressPanel, BorderLayout.CENTER);
        
        // Bookmarks panel
        JPanel bookmarkPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 3));
        
        bookmarkButton = new JButton("⭐");
        bookmarkButton.setToolTipText("Add Bookmark (Ctrl+D)");
        bookmarkButton.addActionListener(e -> addBookmark());
        bookmarkPanel.add(bookmarkButton);
        
        bookmarksCombo = new JComboBox<>(bookmarksModel);
        bookmarksCombo.setPreferredSize(new Dimension(150, 25));
        bookmarksCombo.setToolTipText("Bookmarks");
        bookmarksCombo.addActionListener(e -> {
            Bookmark selected = (Bookmark) bookmarksCombo.getSelectedItem();
            if (selected != null && selected.url != null) {
                navigateTo(selected.url);
            }
        });
        bookmarkPanel.add(bookmarksCombo);
        
        panel.add(bookmarkPanel, BorderLayout.EAST);
        
        // Progress bar
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(false);
        progressBar.setVisible(false);
        progressBar.setPreferredSize(new Dimension(0, 3));
        
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.add(panel, BorderLayout.CENTER);
        wrapperPanel.add(progressBar, BorderLayout.SOUTH);
        
        return wrapperPanel;
    }
    
    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        
        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        panel.add(statusLabel, BorderLayout.WEST);
        
        return panel;
    }
    
    private void loadDefaultBookmarks() {
        addBookmarkInternal("Local Server", "http://localhost:8080/");
        addBookmarkInternal("Google", "https://www.google.com");
        addBookmarkInternal("GitHub", "https://github.com");
        addBookmarkInternal("Wikipedia", "https://www.wikipedia.org");
        addBookmarkInternal("Stack Overflow", "https://stackoverflow.com");
        addBookmarkInternal("YouTube", "https://www.youtube.com");
        addBookmarkInternal("Reddit", "https://www.reddit.com");
        addBookmarkInternal("Twitter", "https://twitter.com");
    }
    
    private void addBookmarkInternal(String title, String url) {
        Bookmark bookmark = new Bookmark(title, url);
        bookmarks.add(bookmark);
        bookmarksModel.addElement(bookmark);
    }
    
    private void addBookmark() {
        if (currentUrl == null || currentUrl.isEmpty()) {
            return;
        }
        
        String title = JOptionPane.showInputDialog(this, 
            "Enter bookmark name:", 
            "Add Bookmark", 
            JOptionPane.QUESTION_MESSAGE);
        
        if (title != null && !title.trim().isEmpty()) {
            addBookmarkInternal(title.trim(), currentUrl);
            statusLabel.setText("✅ Bookmark added: " + title);
        }
    }
    
    public void navigateTo(String url) {
        if (url == null || url.trim().isEmpty()) {
            return;
        }
        
        url = url.trim();
        
        // Save current URL to back history
        if (currentUrl != null && !currentUrl.equals(url)) {
            backHistory.push(currentUrl);
            forwardHistory.clear();
            updateNavigationButtons();
        }
        
        currentUrl = url;
        addressBar.setText(url);
        loadPage(url);
    }
    
    private void loadPage(String urlString) {
        if (isLoading && currentLoader != null) {
            currentLoader.cancel(true);
        }
        
        isLoading = true;
        stopButton.setEnabled(true);
        progressBar.setVisible(true);
        progressBar.setIndeterminate(true);
        statusLabel.setText("⏳ Loading: " + urlString);
        
        currentLoader = new SwingWorker<String, Void>() {
            private long startTime;
            private URL pageUrl;
            
            @Override
            protected String doInBackground() throws Exception {
                startTime = System.currentTimeMillis();
                URI uri = new URI(urlString);
                pageUrl = uri.toURL();
                return fetchPage(urlString);
            }
            
            @Override
            protected void done() {
                isLoading = false;
                stopButton.setEnabled(false);
                progressBar.setIndeterminate(false);
                progressBar.setVisible(false);
                
                try {
                    if (!isCancelled()) {
                        String content = get();
                        long loadTime = System.currentTimeMillis() - startTime;
                        
                        // Debug: Check if content is empty
                        if (content == null || content.trim().isEmpty()) {
                            browserPane.setText("<html><body><h1>Error</h1><p>Received empty response from server.</p></body></html>");
                            statusLabel.setText("❌ Empty response");
                            return;
                        }
                        
                        // Set base URL for proper resource loading (images, CSS, etc.)
                        try {
                            if (pageUrl != null) {
                                ((HTMLDocument) browserPane.getDocument()).setBase(pageUrl);
                            }
                        } catch (Exception e) {
                            // Ignore if document is not HTMLDocument
                        }
                        
                        // Try to render HTML
                        try {
                            // First try: Clean and prepare HTML for better rendering
                            String cleanHtml = prepareHtmlForRendering(content, urlString);
                            
                            // Set content type first
                            browserPane.setContentType("text/html; charset=UTF-8");
                            
                            // Try to set the cleaned HTML
                            if (cleanHtml != null && !cleanHtml.trim().isEmpty()) {
                                browserPane.setText(cleanHtml);
                                browserPane.setCaretPosition(0);
                            } else {
                                // Fallback: Use original content
                                browserPane.setText(content);
                                browserPane.setCaretPosition(0);
                            }
                            
                            statusLabel.setText(String.format("✅ Done - %dms - %d bytes", 
                                              loadTime, content.length()));
                        } catch (Exception renderEx) {
                            // Last resort: Show raw HTML or error
                            try {
                                browserPane.setContentType("text/plain");
                                browserPane.setText("Failed to render HTML. Showing raw content:\n\n" + 
                                                  content.substring(0, Math.min(content.length(), 10000)));
                            } catch (Exception e2) {
                                browserPane.setText("Error rendering page: " + renderEx.getMessage());
                            }
                            statusLabel.setText("⚠️ Rendering error - showing raw content");
                        }
                    } else {
                        statusLabel.setText("⏹ Stopped");
                    }
                } catch (Exception e) {
                    String errorHtml = generateErrorPage(urlString, e);
                    browserPane.setContentType("text/html");
                    browserPane.setText(errorHtml);
                    statusLabel.setText("❌ Error: " + e.getMessage());
                }
            }
        };
        
        currentLoader.execute();
    }
    
    private String fetchPage(String urlString) throws Exception {
        URI uri = new URI(urlString);
        URL url = uri.toURL();
        
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        
        // Set headers like a real browser
        connection.setRequestProperty("User-Agent", 
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
        connection.setRequestProperty("Accept", 
            "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        connection.setRequestProperty("Accept-Language", "en-US,en;q=0.9,vi;q=0.8");
        connection.setRequestProperty("Accept-Encoding", "gzip, deflate");
        connection.setRequestProperty("Connection", "keep-alive");
        connection.setRequestProperty("Upgrade-Insecure-Requests", "1");
        connection.setConnectTimeout(20000);
        connection.setReadTimeout(20000);
        connection.setInstanceFollowRedirects(true);
        
        int responseCode = connection.getResponseCode();
        System.out.println("Response code: " + responseCode); // Debug
        
        if (responseCode >= 200 && responseCode < 400) {
            InputStream inputStream = connection.getInputStream();
            
            // Handle GZIP
            String encoding = connection.getContentEncoding();
            if ("gzip".equalsIgnoreCase(encoding)) {
                inputStream = new java.util.zip.GZIPInputStream(inputStream);
            }
            
            StringBuilder content = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream, "UTF-8"))) {
                String line;
                int lineCount = 0;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                    lineCount++;
                }
                System.out.println("Fetched " + lineCount + " lines, " + content.length() + " bytes"); // Debug
            }
            
            connection.disconnect();
            
            String result = content.toString();
            if (result.trim().isEmpty()) {
                throw new IOException("Server returned empty content");
            }
            
            return result;
        } else {
            connection.disconnect();
            throw new IOException("HTTP Error: " + responseCode + " " + connection.getResponseMessage());
        }
    }
    
    private String generateErrorPage(String url, Exception e) {
        return "<!DOCTYPE html>" +
               "<html>" +
               "<head>" +
               "<title>Error Loading Page</title>" +
               "<style>" +
               "body { font-family: 'Segoe UI', Arial, sans-serif; padding: 40px; background: #f5f5f5; }" +
               ".error-container { max-width: 600px; margin: 0 auto; background: white; padding: 30px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }" +
               "h1 { color: #e74c3c; margin-top: 0; }" +
               ".error-icon { font-size: 64px; text-align: center; margin-bottom: 20px; }" +
               ".url { background: #ecf0f1; padding: 10px; border-radius: 4px; word-break: break-all; font-family: monospace; }" +
               ".message { color: #7f8c8d; margin: 20px 0; }" +
               ".suggestions { background: #e8f5e9; padding: 15px; border-radius: 4px; border-left: 4px solid #4caf50; }" +
               ".suggestions li { margin: 5px 0; }" +
               "</style>" +
               "</head>" +
               "<body>" +
               "<div class='error-container'>" +
               "<div class='error-icon'>⚠️</div>" +
               "<h1>Cannot Load Page</h1>" +
               "<p class='message'>Failed to load:</p>" +
               "<div class='url'>" + escapeHtml(url) + "</div>" +
               "<p class='message'><strong>Error:</strong> " + escapeHtml(e.getMessage()) + "</p>" +
               "<div class='suggestions'>" +
               "<strong>💡 Suggestions:</strong>" +
               "<ul>" +
               "<li>Check your internet connection</li>" +
               "<li>Verify the URL is correct</li>" +
               "<li>Try refreshing the page</li>" +
               "<li>The website might be down temporarily</li>" +
               "</ul>" +
               "</div>" +
               "</div>" +
               "</body>" +
               "</html>";
    }
    
    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                  .replace("<", "&lt;")
                  .replace(">", "&gt;")
                  .replace("\"", "&quot;")
                  .replace("'", "&#x27;");
    }
    
    private void goBack() {
        if (!backHistory.isEmpty()) {
            forwardHistory.push(currentUrl);
            String url = backHistory.pop();
            currentUrl = url;
            addressBar.setText(url);
            loadPage(url);
            updateNavigationButtons();
        }
    }
    
    private void goForward() {
        if (!forwardHistory.isEmpty()) {
            backHistory.push(currentUrl);
            String url = forwardHistory.pop();
            currentUrl = url;
            addressBar.setText(url);
            loadPage(url);
            updateNavigationButtons();
        }
    }
    
    private void refresh() {
        if (currentUrl != null) {
            loadPage(currentUrl);
        }
    }
    
    private void stopLoading() {
        if (isLoading && currentLoader != null) {
            currentLoader.cancel(true);
            isLoading = false;
            stopButton.setEnabled(false);
            progressBar.setVisible(false);
            statusLabel.setText("⏹ Stopped");
        }
    }
    
    private void updateNavigationButtons() {
        backButton.setEnabled(!backHistory.isEmpty());
        forwardButton.setEnabled(!forwardHistory.isEmpty());
    }
    
    private void trustAllCertificates() {
        try {
            // Create a trust manager that accepts all certificates
            TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
            };
            
            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            
            // Create all-trusting hostname verifier
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };
            
            // Install the all-trusting hostname verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (Exception e) {
            System.err.println("Failed to setup SSL trust: " + e.getMessage());
        }
    }
    
    private String prepareHtmlForRendering(String html, String baseUrl) {
        if (html == null || html.trim().isEmpty()) {
            return "<html><body><p>Empty page</p></body></html>";
        }
        
        try {
            // Keep original for fallback
            String original = html;
            
            // Remove problematic scripts (but keep content visible)
            html = html.replaceAll("(?is)<script[^>]*>.*?</script>", "<!-- script removed -->");
            html = html.replaceAll("(?is)<noscript[^>]*>.*?</noscript>", "");
            
            // Remove inline event handlers (but keep the element)
            html = html.replaceAll("\\son\\w+\\s*=\\s*[\"'][^\"']*[\"']", "");
            
            // Keep basic inline styles, remove only complex <style> blocks
            html = html.replaceAll("(?is)<style[^>]*>.*?@media.*?</style>", "<!-- complex style removed -->");
            
            // Convert relative URLs to absolute for images
            if (baseUrl != null && !baseUrl.isEmpty()) {
                try {
                    URI baseUri = new URI(baseUrl);
                    URL base = baseUri.toURL();
                    String protocol = base.getProtocol();
                    String host = base.getHost();
                    int port = base.getPort();
                    String baseUrlPrefix = protocol + "://" + host + (port > 0 && port != 80 && port != 443 ? ":" + port : "");
                    
                    // Fix relative image paths - more patterns
                    html = html.replaceAll("(?i)(<img[^>]+src\\s*=\\s*[\"'])(/[^\"']+)([\"'])", 
                                          "$1" + baseUrlPrefix + "$2$3");
                    
                    // Fix relative link paths
                    html = html.replaceAll("(?i)(<a[^>]+href\\s*=\\s*[\"'])(/[^\"']+)([\"'])", 
                                          "$1" + baseUrlPrefix + "$2$3");
                    
                    // Fix relative CSS/JS paths
                    html = html.replaceAll("(?i)(<link[^>]+href\\s*=\\s*[\"'])(/[^\"']+)([\"'])", 
                                          "$1" + baseUrlPrefix + "$2$3");
                } catch (Exception e) {
                    System.err.println("URL conversion error: " + e.getMessage());
                }
            }
            
            // Ensure proper structure
            if (!html.toLowerCase().contains("<html")) {
                html = "<html><head><meta charset=\"UTF-8\"></head><body>" + html + "</body></html>";
            } else {
                // Add charset if missing
                if (!html.toLowerCase().contains("charset")) {
                    html = html.replaceFirst("(?i)<head>", 
                        "<head><meta charset=\"UTF-8\">");
                }
            }
            
            // Verify we didn't break the HTML completely
            if (html.length() < 100 && original.length() > 1000) {
                // We removed too much, return simplified version
                return "<html><head><meta charset=\"UTF-8\"></head><body>" +
                       "<h1>Content Loaded</h1>" +
                       "<p>The page was loaded but automatic HTML cleaning removed too much content.</p>" +
                       "<p><strong>URL:</strong> " + escapeHtml(baseUrl) + "</p>" +
                       "<p><strong>Original size:</strong> " + original.length() + " bytes</p>" +
                       "<hr><pre>" + escapeHtml(original.substring(0, Math.min(original.length(), 5000))) + "...</pre>" +
                       "</body></html>";
            }
            
            return html;
            
        } catch (Exception e) {
            System.err.println("HTML preprocessing error: " + e.getMessage());
            e.printStackTrace();
            // Return original HTML if preprocessing fails
            return html;
        }
    }
    
    // Bookmark class
    static class Bookmark {
        String title;
        String url;
        
        Bookmark(String title, String url) {
            this.title = title;
            this.url = url;
        }
        
        @Override
        public String toString() {
            return title;
        }
    }
}
