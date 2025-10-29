package com.httpbrowser;

import javax.swing.*;
import java.awt.*;
import java.awt.Desktop;
import java.net.*;
import java.io.*;
import java.util.*;

/**
 * Modern Web Browser - Hybrid approach
 * Option 1: System Browser Integration
 * Option 2: Simplified Rendering
 * Option 3: Reader Mode
 */
public class ModernWebBrowser extends JPanel {
    private JTextField urlField;
    private JButton goButton;
    private JButton systemBrowserButton;
    private JButton readerModeButton;
    private JTabbedPane contentTabs;
    private JTextArea rawHtmlArea;
    private JEditorPane simpleRenderPane;
    private JEditorPane readerPane;
    private JLabel statusLabel;
    private JComboBox<String> renderModeCombo;
    
    private static final String[] RENDER_MODES = {
        "System Browser (Best Quality)",
        "Simple Render (Basic HTML)",
        "Reader Mode (Text Only)",
        "Raw HTML (Source Code)"
    };
    
    public ModernWebBrowser() {
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Top panel
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        
        // URL panel
        JPanel urlPanel = new JPanel(new BorderLayout(5, 5));
        urlPanel.add(new JLabel("🌐 URL:"), BorderLayout.WEST);
        
        urlField = new JTextField("https://tuoitre.vn");
        urlField.setFont(new Font("Consolas", Font.PLAIN, 12));
        urlField.addActionListener(e -> loadUrl());
        urlPanel.add(urlField, BorderLayout.CENTER);
        
        topPanel.add(urlPanel, BorderLayout.CENTER);
        
        // Control panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        
        renderModeCombo = new JComboBox<>(RENDER_MODES);
        renderModeCombo.setToolTipText("Select render mode");
        controlPanel.add(new JLabel("Mode:"));
        controlPanel.add(renderModeCombo);
        
        goButton = new JButton("🚀 Load");
        goButton.setFont(new Font("Arial", Font.BOLD, 12));
        goButton.addActionListener(e -> loadUrl());
        controlPanel.add(goButton);
        
        systemBrowserButton = new JButton("🌍 Open in System Browser");
        systemBrowserButton.addActionListener(e -> openInSystemBrowser());
        controlPanel.add(systemBrowserButton);
        
        readerModeButton = new JButton("📖 Reader Mode");
        readerModeButton.addActionListener(e -> switchToReaderMode());
        controlPanel.add(readerModeButton);
        
        topPanel.add(controlPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);
        
        // Center - Tabbed pane
        contentTabs = new JTabbedPane();
        
        // Tab 1: Simple Render
        simpleRenderPane = new JEditorPane();
        simpleRenderPane.setEditable(false);
        simpleRenderPane.setContentType("text/html");
        JScrollPane renderScroll = new JScrollPane(simpleRenderPane);
        contentTabs.addTab("🎨 Rendered View", renderScroll);
        
        // Tab 2: Reader Mode
        readerPane = new JEditorPane();
        readerPane.setEditable(false);
        readerPane.setContentType("text/html");
        JScrollPane readerScroll = new JScrollPane(readerPane);
        contentTabs.addTab("📖 Reader Mode", readerScroll);
        
        // Tab 3: Raw HTML
        rawHtmlArea = new JTextArea();
        rawHtmlArea.setEditable(false);
        rawHtmlArea.setFont(new Font("Consolas", Font.PLAIN, 11));
        rawHtmlArea.setLineWrap(true);
        JScrollPane rawScroll = new JScrollPane(rawHtmlArea);
        contentTabs.addTab("📄 HTML Source", rawScroll);
        
        add(contentTabs, BorderLayout.CENTER);
        
        // Bottom - Status
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
        statusLabel = new JLabel("✅ Ready - Select mode and enter URL");
        statusPanel.add(statusLabel, BorderLayout.WEST);
        add(statusPanel, BorderLayout.SOUTH);
        
        // Show info panel
        showWelcomeMessage();
    }
    
    private void showWelcomeMessage() {
        String welcome = """
        <html>
        <head>
            <style>
                body { font-family: 'Segoe UI', Arial, sans-serif; padding: 40px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; }
                .container { background: rgba(255,255,255,0.1); backdrop-filter: blur(10px); padding: 30px; border-radius: 15px; }
                h1 { font-size: 32px; margin: 0 0 20px 0; }
                .mode { background: rgba(255,255,255,0.2); padding: 15px; margin: 10px 0; border-radius: 8px; }
                .mode h3 { margin: 0 0 10px 0; font-size: 18px; }
                .mode p { margin: 5px 0; font-size: 14px; line-height: 1.6; }
                .best { border: 2px solid #4CAF50; background: rgba(76,175,80,0.2); }
                .recommended { color: #4CAF50; font-weight: bold; }
            </style>
        </head>
        <body>
            <div class="container">
                <h1>🌐 Modern Web Browser</h1>
                <p style="font-size: 16px; margin-bottom: 30px;">Choose your browsing mode for best experience:</p>
                
                <div class="mode best">
                    <h3>🌍 System Browser <span class="recommended">(RECOMMENDED)</span></h3>
                    <p>✅ Full HTML5, CSS3, JavaScript support</p>
                    <p>✅ Perfect rendering quality</p>
                    <p>✅ Best for: tuoitre.vn, vnexpress.vn, any modern website</p>
                    <p>⚡ Click "Open in System Browser" button</p>
                </div>
                
                <div class="mode">
                    <h3>📖 Reader Mode</h3>
                    <p>✅ Clean, distraction-free reading</p>
                    <p>✅ Extracts main content only</p>
                    <p>✅ Best for: News articles, blog posts</p>
                    <p>⚡ Good for tuoitre.vn articles</p>
                </div>
                
                <div class="mode">
                    <h3>🎨 Simple Render</h3>
                    <p>⚠️ Basic HTML 3.2 support only</p>
                    <p>⚠️ No JavaScript, limited CSS</p>
                    <p>✅ Best for: Simple static pages</p>
                </div>
                
                <div class="mode">
                    <h3>📄 HTML Source</h3>
                    <p>✅ View raw HTML code</p>
                    <p>✅ Good for developers</p>
                    <p>✅ See what server sent</p>
                </div>
                
                <hr style="border: 1px solid rgba(255,255,255,0.3); margin: 30px 0;">
                
                <h3>🚀 Quick Start:</h3>
                <p>1. Enter URL (e.g., https://tuoitre.vn)</p>
                <p>2. Choose mode from dropdown</p>
                <p>3. Click "🚀 Load" or press Enter</p>
                <p>4. Or click "🌍 Open in System Browser" for best quality!</p>
            </div>
        </body>
        </html>
        """;
        
        simpleRenderPane.setText(welcome);
    }
    
    private void loadUrl() {
        String url = urlField.getText().trim();
        if (url.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter URL", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String mode = (String) renderModeCombo.getSelectedItem();
        
        if (mode.startsWith("System Browser")) {
            openInSystemBrowser();
            return;
        }
        
        statusLabel.setText("⏳ Loading: " + url);
        goButton.setEnabled(false);
        
        SwingWorker<String, Void> worker = new SwingWorker<>() {
            private long startTime;
            
            @Override
            protected String doInBackground() throws Exception {
                startTime = System.currentTimeMillis();
                return fetchPage(url);
            }
            
            @Override
            protected void done() {
                try {
                    String html = get();
                    long loadTime = System.currentTimeMillis() - startTime;
                    
                    // Show in all tabs
                    rawHtmlArea.setText(html);
                    rawHtmlArea.setCaretPosition(0);
                    
                    if (mode.startsWith("Reader Mode")) {
                        String reader = extractReaderContent(html, url);
                        readerPane.setText(reader);
                        simpleRenderPane.setText(reader);
                        contentTabs.setSelectedIndex(1); // Reader tab
                    } else if (mode.startsWith("Simple Render")) {
                        String cleaned = cleanHtmlForBasicRender(html);
                        simpleRenderPane.setText(cleaned);
                        contentTabs.setSelectedIndex(0); // Render tab
                    } else if (mode.startsWith("Raw HTML")) {
                        contentTabs.setSelectedIndex(2); // Source tab
                    }
                    
                    statusLabel.setText(String.format("✅ Done - %dms - %d KB", 
                                      loadTime, html.length() / 1024));
                    
                } catch (Exception e) {
                    String error = createErrorPage(url, e);
                    simpleRenderPane.setText(error);
                    statusLabel.setText("❌ Error: " + e.getMessage());
                } finally {
                    goButton.setEnabled(true);
                }
            }
        };
        
        worker.execute();
    }
    
    private void openInSystemBrowser() {
        String url = urlField.getText().trim();
        if (url.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter URL", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
                statusLabel.setText("✅ Opened in system browser: " + url);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "System browser not supported on this platform", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error opening browser: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void switchToReaderMode() {
        renderModeCombo.setSelectedIndex(2); // Reader Mode
        if (!rawHtmlArea.getText().trim().isEmpty()) {
            String reader = extractReaderContent(rawHtmlArea.getText(), urlField.getText());
            readerPane.setText(reader);
            simpleRenderPane.setText(reader);
            contentTabs.setSelectedIndex(1);
        }
    }
    
    private String fetchPage(String urlString) throws Exception {
        URI uri = new URI(urlString);
        URL url = uri.toURL();
        
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", 
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
        connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        connection.setRequestProperty("Accept-Language", "vi-VN,vi;q=0.9,en-US;q=0.8,en;q=0.7");
        connection.setConnectTimeout(20000);
        connection.setReadTimeout(20000);
        connection.setInstanceFollowRedirects(true);
        
        // Handle HTTPS
        if (connection instanceof javax.net.ssl.HttpsURLConnection) {
            javax.net.ssl.HttpsURLConnection https = (javax.net.ssl.HttpsURLConnection) connection;
            https.setHostnameVerifier((hostname, session) -> true);
        }
        
        int code = connection.getResponseCode();
        if (code != 200) {
            throw new IOException("HTTP " + code + ": " + connection.getResponseMessage());
        }
        
        InputStream in = connection.getInputStream();
        if ("gzip".equals(connection.getContentEncoding())) {
            in = new java.util.zip.GZIPInputStream(in);
        }
        
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        
        connection.disconnect();
        return content.toString();
    }
    
    private String extractReaderContent(String html, String url) {
        // Extract title
        String title = "Article";
        java.util.regex.Pattern titlePattern = java.util.regex.Pattern.compile("<title>(.*?)</title>", 
                                                  java.util.regex.Pattern.CASE_INSENSITIVE | java.util.regex.Pattern.DOTALL);
        java.util.regex.Matcher titleMatcher = titlePattern.matcher(html);
        if (titleMatcher.find()) {
            title = titleMatcher.group(1).trim();
            title = title.replaceAll("<[^>]+>", ""); // Remove HTML tags from title
        }
        
        // Extract ALL text content, preserve structure
        StringBuilder extracted = new StringBuilder();
        
        // Remove unwanted elements first
        String cleaned = html;
        cleaned = cleaned.replaceAll("(?is)<script[^>]*>.*?</script>", "");
        cleaned = cleaned.replaceAll("(?is)<style[^>]*>.*?</style>", "");
        cleaned = cleaned.replaceAll("(?is)<noscript[^>]*>.*?</noscript>", "");
        
        // Extract headings (h1-h6)
        java.util.regex.Pattern headingPattern = java.util.regex.Pattern.compile(
            "<h[1-6][^>]*>(.*?)</h[1-6]>", 
            java.util.regex.Pattern.CASE_INSENSITIVE | java.util.regex.Pattern.DOTALL);
        java.util.regex.Matcher headingMatcher = headingPattern.matcher(cleaned);
        java.util.List<String> headings = new java.util.ArrayList<>();
        while (headingMatcher.find()) {
            String heading = headingMatcher.group(1).replaceAll("<[^>]+>", "").trim();
            if (!heading.isEmpty() && heading.length() > 2) {
                headings.add(heading);
            }
        }
        
        // Extract paragraphs
        java.util.regex.Pattern pPattern = java.util.regex.Pattern.compile(
            "<p[^>]*>(.*?)</p>", 
            java.util.regex.Pattern.CASE_INSENSITIVE | java.util.regex.Pattern.DOTALL);
        java.util.regex.Matcher pMatcher = pPattern.matcher(cleaned);
        java.util.List<String> paragraphs = new java.util.ArrayList<>();
        while (pMatcher.find()) {
            String p = pMatcher.group(1).replaceAll("<[^>]+>", "").trim();
            if (!p.isEmpty() && p.length() > 20) {
                paragraphs.add(p);
            }
        }
        
        // Extract links
        java.util.regex.Pattern linkPattern = java.util.regex.Pattern.compile(
            "<a[^>]+href\\s*=\\s*[\"']([^\"']+)[\"'][^>]*>(.*?)</a>", 
            java.util.regex.Pattern.CASE_INSENSITIVE | java.util.regex.Pattern.DOTALL);
        java.util.regex.Matcher linkMatcher = linkPattern.matcher(cleaned);
        java.util.List<String[]> links = new java.util.ArrayList<>();
        while (linkMatcher.find()) {
            String href = linkMatcher.group(1);
            String text = linkMatcher.group(2).replaceAll("<[^>]+>", "").trim();
            if (!text.isEmpty() && text.length() > 3) {
                // Make absolute URL
                if (href.startsWith("/")) {
                    try {
                        URI uri = new URI(url);
                        href = uri.getScheme() + "://" + uri.getHost() + href;
                    } catch (Exception e) {}
                }
                links.add(new String[]{text, href});
            }
        }
        
        // Extract divs with text
        java.util.regex.Pattern divPattern = java.util.regex.Pattern.compile(
            "<div[^>]*>(.*?)</div>", 
            java.util.regex.Pattern.CASE_INSENSITIVE | java.util.regex.Pattern.DOTALL);
        java.util.regex.Matcher divMatcher = divPattern.matcher(cleaned);
        java.util.List<String> divTexts = new java.util.ArrayList<>();
        while (divMatcher.find()) {
            String divText = divMatcher.group(1).replaceAll("<[^>]+>", "").trim();
            if (!divText.isEmpty() && divText.length() > 30 && !divText.contains("cookie") && !divText.contains("script")) {
                divTexts.add(divText);
            }
        }
        
        // Build comprehensive content
        extracted.append("<h1>").append(escapeHtml(title)).append("</h1>\n");
        extracted.append("<p class='source'>Source: <a href='").append(url).append("'>").append(url).append("</a></p>\n");
        extracted.append("<hr>\n");
        
        // Add all headings
        if (!headings.isEmpty()) {
            extracted.append("<div class='section'>\n");
            extracted.append("<h2>📰 Tiêu đề chính</h2>\n");
            for (String heading : headings) {
                if (heading.length() > 5) {
                    extracted.append("<h3>• ").append(escapeHtml(heading)).append("</h3>\n");
                }
            }
            extracted.append("</div>\n");
        }
        
        // Add paragraphs
        if (!paragraphs.isEmpty()) {
            extracted.append("<div class='section'>\n");
            extracted.append("<h2>📝 Nội dung</h2>\n");
            int count = 0;
            for (String p : paragraphs) {
                if (count++ < 50) { // Limit to 50 paragraphs
                    extracted.append("<p>").append(escapeHtml(p)).append("</p>\n");
                }
            }
            extracted.append("</div>\n");
        }
        
        // Add div texts
        if (!divTexts.isEmpty()) {
            extracted.append("<div class='section'>\n");
            extracted.append("<h2>📄 Nội dung khác</h2>\n");
            int count = 0;
            for (String divText : divTexts) {
                if (count++ < 30 && divText.length() < 500) {
                    extracted.append("<p>").append(escapeHtml(divText)).append("</p>\n");
                }
            }
            extracted.append("</div>\n");
        }
        
        // Add links
        if (!links.isEmpty()) {
            extracted.append("<div class='section'>\n");
            extracted.append("<h2>🔗 Links (").append(Math.min(links.size(), 100)).append(" links)</h2>\n");
            extracted.append("<ul>\n");
            int count = 0;
            for (String[] link : links) {
                if (count++ < 100) { // Limit to 100 links
                    extracted.append("<li><a href='").append(link[1]).append("'>")
                             .append(escapeHtml(link[0])).append("</a></li>\n");
                }
            }
            extracted.append("</ul>\n");
            extracted.append("</div>\n");
        }
        
        // Build final HTML with better styling
        return String.format("""
        <html>
        <head>
            <meta charset="UTF-8">
            <style>
                body { 
                    font-family: 'Segoe UI', Georgia, serif; 
                    max-width: 900px; 
                    margin: 20px auto; 
                    padding: 20px;
                    background: #f5f5f5;
                    line-height: 1.8;
                    color: #333;
                }
                .container {
                    background: white;
                    padding: 40px;
                    border-radius: 8px;
                    box-shadow: 0 2px 10px rgba(0,0,0,0.1);
                }
                h1 { 
                    font-size: 28px; 
                    color: #c8102e; 
                    margin-bottom: 10px;
                    border-bottom: 3px solid #c8102e;
                    padding-bottom: 10px;
                }
                h2 { 
                    font-size: 22px; 
                    color: #0066cc; 
                    margin: 30px 0 15px 0;
                    padding-left: 10px;
                    border-left: 4px solid #0066cc;
                }
                h3 { 
                    font-size: 18px; 
                    color: #333; 
                    margin: 15px 0 10px 0;
                    font-weight: 600;
                }
                p { 
                    font-size: 15px; 
                    color: #444; 
                    margin: 12px 0; 
                    text-align: justify;
                }
                a { 
                    color: #0066cc; 
                    text-decoration: none; 
                    font-weight: 500;
                }
                a:hover { 
                    text-decoration: underline; 
                    color: #c8102e;
                }
                .source { 
                    color: #666; 
                    font-size: 13px; 
                    margin-bottom: 20px;
                    padding: 10px;
                    background: #f8f9fa;
                    border-radius: 4px;
                }
                .section {
                    margin: 30px 0;
                    padding: 20px;
                    background: #fafbfc;
                    border-radius: 6px;
                }
                ul { 
                    list-style-type: none;
                    padding: 0;
                }
                li { 
                    margin: 8px 0; 
                    padding: 8px;
                    background: white;
                    border-left: 3px solid #0066cc;
                    padding-left: 15px;
                }
                li:hover {
                    background: #f0f7ff;
                }
                hr { 
                    border: none; 
                    border-top: 2px solid #e0e0e0; 
                    margin: 20px 0; 
                }
            </style>
        </head>
        <body>
            <div class="container">
                %s
            </div>
        </body>
        </html>
        """, extracted.toString());
    }
    
    private String cleanHtmlForBasicRender(String html) {
        // Remove scripts
        html = html.replaceAll("(?is)<script[^>]*>.*?</script>", "");
        html = html.replaceAll("(?is)<style[^>]*>.*?</style>", "");
        
        // Ensure structure
        if (!html.toLowerCase().contains("<html")) {
            html = "<html><head><meta charset=\"UTF-8\"></head><body>" + html + "</body></html>";
        }
        
        return html;
    }
    
    private String createErrorPage(String url, Exception e) {
        return String.format("""
        <html>
        <head>
            <style>
                body { font-family: Arial; padding: 40px; background: #f5f5f5; }
                .error { background: white; padding: 30px; border-radius: 8px; border-left: 4px solid #e74c3c; }
                h1 { color: #e74c3c; }
                .suggestion { background: #e8f5e9; padding: 15px; margin: 20px 0; border-radius: 4px; }
            </style>
        </head>
        <body>
            <div class="error">
                <h1>⚠️ Failed to Load Page</h1>
                <p><strong>URL:</strong> %s</p>
                <p><strong>Error:</strong> %s</p>
                <div class="suggestion">
                    <strong>💡 Try:</strong>
                    <ul>
                        <li>Click "🌍 Open in System Browser" for best quality</li>
                        <li>Check your internet connection</li>
                        <li>Verify the URL is correct</li>
                    </ul>
                </div>
            </div>
        </body>
        </html>
        """, escapeHtml(url), escapeHtml(e.getMessage()));
    }
    
    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }
    
    public JPanel getPanel() {
        return this;
    }
}
