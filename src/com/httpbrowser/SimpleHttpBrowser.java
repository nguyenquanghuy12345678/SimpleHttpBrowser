package com.httpbrowser;

import javax.swing.*;
import java.awt.*;

/**
 * Simple HTTP Browser - Main Application
 * Ứng dụng trình duyệt HTTP với Web Client và Web Server
 */
public class SimpleHttpBrowser extends JFrame {
    private static final long serialVersionUID = 1L;
    
    private WebClient webClient;
    private WebServer webServer;
    private JTabbedPane mainTabbedPane;
    
    public SimpleHttpBrowser() {
        initializeUI();
        startWebServer();
    }
    
    private void initializeUI() {
        setTitle("🌐 Simple HTTP Browser - Full Web Browser & Client");
        setSize(1400, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Main tabbed pane
        mainTabbedPane = new JTabbedPane();
        
        // Modern Web Browser Tab (NEW! - Best Quality)
        ModernWebBrowser modernBrowser = new ModernWebBrowser();
        mainTabbedPane.addTab("🌍 Modern Browser (BEST)", modernBrowser.getPanel());
        
        // Web Browser Tab (NEW!)
        WebBrowser webBrowser = new WebBrowser();
        mainTabbedPane.addTab("🌐 Classic Browser", webBrowser);
        
        // Web Client Tab (Advanced HTTP Client)
        webClient = new WebClient();
        mainTabbedPane.addTab("🔧 Advanced Client", webClient.getPanel());
        
        // Server Monitor Tab
        JPanel serverPanel = createServerMonitorPanel();
        mainTabbedPane.addTab("🖧 Server Monitor", serverPanel);
        
        add(mainTabbedPane);
    }
    
    private void startWebServer() {
        webServer = new WebServer(8080);
        new Thread(() -> {
            try {
                webServer.start();
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this,
                        "Không thể khởi động server: " + e.getMessage(),
                        "Lỗi Server", JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }
    
    private JPanel createServerMonitorPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Info panel
        JPanel infoPanel = new JPanel(new GridLayout(6, 2, 10, 5));
        infoPanel.setBorder(BorderFactory.createTitledBorder("📊 Thông tin Server"));
        
        infoPanel.add(new JLabel("🖧 Status:"));
        JLabel statusLabel = new JLabel("🟢 Running");
        statusLabel.setForeground(new Color(0, 150, 0));
        infoPanel.add(statusLabel);
        
        infoPanel.add(new JLabel("🔌 Port HTTP:"));
        infoPanel.add(new JLabel("8080"));
        
        infoPanel.add(new JLabel("🔒 Port HTTPS:"));
        infoPanel.add(new JLabel("8443 (nếu có SSL)"));
        
        infoPanel.add(new JLabel("🌐 URL HTTP:"));
        infoPanel.add(new JLabel("http://localhost:8080/"));
        
        infoPanel.add(new JLabel("🔐 URL HTTPS:"));
        infoPanel.add(new JLabel("https://localhost:8443/"));
        
        infoPanel.add(new JLabel("📝 Log File:"));
        infoPanel.add(new JLabel("server.log"));
        
        panel.add(infoPanel, BorderLayout.NORTH);
        
        // Log area
        JTextArea logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        logArea.setText(getServerInfo());
        
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("📋 Server Log"));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Control panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton testButton = new JButton("🧪 Test Server");
        testButton.addActionListener(e -> testLocalServer());
        controlPanel.add(testButton);
        
        JButton stopButton = new JButton("⏹️ Stop Server");
        stopButton.addActionListener(e -> {
            webServer.stop();
            statusLabel.setText("🔴 Stopped");
            statusLabel.setForeground(Color.RED);
        });
        controlPanel.add(stopButton);
        
        panel.add(controlPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private String getServerInfo() {
        StringBuilder info = new StringBuilder();
        info.append("═══════════════════════════════════════════════════════\n");
        info.append("🖧 SIMPLE HTTP SERVER\n");
        info.append("═══════════════════════════════════════════════════════\n\n");
        info.append("✅ Server đã khởi động thành công!\n\n");
        info.append("📍 Endpoints có sẵn:\n\n");
        info.append("  🏠 GET  /          - Trang chủ\n");
        info.append("  📄 GET  /test      - Trang test\n");
        info.append("  📝 GET  /info      - Thông tin server\n");
        info.append("  📊 POST /echo      - Echo POST data\n");
        info.append("  🔍 HEAD /status    - Status check\n");
        info.append("  📱 GET  /api/users - Demo API JSON\n\n");
        info.append("🧪 Test URLs:\n");
        info.append("  • http://localhost:8080/\n");
        info.append("  • http://localhost:8080/test\n");
        info.append("  • http://localhost:8080/info\n");
        info.append("  • http://localhost:8080/api/users\n\n");
        info.append("💡 Nhấn 'Test Server' để kiểm tra server!\n");
        info.append("═══════════════════════════════════════════════════════\n");
        return info.toString();
    }
    
    private void testLocalServer() {
        mainTabbedPane.setSelectedIndex(0); // Switch to browser tab
        // Web browser will auto-load home page
    }
    
    public static void main(String[] args) {
        // Set Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Run on EDT
        SwingUtilities.invokeLater(() -> {
            SimpleHttpBrowser browser = new SimpleHttpBrowser();
            browser.setVisible(true);
        });
    }
}
