package com.httpbrowser;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;
import java.util.List;
import java.io.*;

/**
 * Web Browser - Full-featured web browser với navigation, history, bookmarks
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
        
        // Center - Browser pane
        browserPane = new JEditorPane();
        browserPane.setEditable(false);
        browserPane.setContentType("text/html");
        
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
                        
                        // Set base URL for proper resource loading (images, CSS, etc.)
                        try {
                            if (pageUrl != null) {
                                ((javax.swing.text.html.HTMLDocument) browserPane.getDocument()).setBase(pageUrl);
                            }
                        } catch (Exception e) {
                            // Ignore if document is not HTMLDocument
                        }
                        
                        // Set content
                        browserPane.setContentType("text/html; charset=UTF-8");
                        browserPane.setText(content);
                        browserPane.setCaretPosition(0);
                        
                        statusLabel.setText(String.format("✅ Done - %dms - %s", 
                                          loadTime, urlString));
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
        connection.setRequestProperty("Accept-Language", "en-US,en;q=0.9");
        connection.setRequestProperty("Accept-Encoding", "gzip, deflate");
        connection.setRequestProperty("Connection", "keep-alive");
        connection.setConnectTimeout(20000);
        connection.setReadTimeout(20000);
        connection.setInstanceFollowRedirects(true);
        
        int responseCode = connection.getResponseCode();
        
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
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
            }
            
            connection.disconnect();
            return content.toString();
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
