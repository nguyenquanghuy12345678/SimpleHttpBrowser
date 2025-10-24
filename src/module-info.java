/**
 * Simple HTTP Browser Module
 * Ứng dụng trình duyệt HTTP với Web Client và Web Server
 */
module SimpleHttpBrowser {
    // Required modules for Swing GUI (transitive for public API)
    requires transitive java.desktop;
    
    // Required for HTTP Server
    requires jdk.httpserver;
    
    // Export package for accessibility
    exports com.httpbrowser;
}