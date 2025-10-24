# 🌐 Simple HTTP Browser

**Ứng dụng trình duyệt HTTP hoàn chỉnh với Web Client và Web Server tích hợp**

## ✨ Tính năng chính

### 🖥️ **Web Client**
- ✅ Hỗ trợ đầy đủ **HTTP** và **HTTPS**
- ✅ 3 phương thức: **GET**, **POST**, **HEAD**
- ✅ Giao diện GUI thân thiện với Swing
- ✅ Hiển thị chi tiết:
  - 🔴 Mã trạng thái HTTP (Status Code)
  - 📋 Tất cả HTTP Headers
  - 📏 Chiều dài nội dung (Content Length)
  - 📈 Thống kê HTML tags: `<p>`, `<div>`, `<span>`, `<img>`
  - 📄 Nội dung phản hồi đầy đủ
  - 🌐 **Render HTML** với JEditorPane

### 🖧 **Web Server**
- ✅ HTTP Server tích hợp (port 8080)
- ✅ 6 endpoints demo sẵn sàng để test
- ✅ Hỗ trợ GET, POST, HEAD
- ✅ Server Monitor với log realtime
- ✅ Có thể test ngay trên localhost

### 🔐 **Bảo mật**
- ✅ Hỗ trợ HTTPS với SSL/TLS
- ✅ Trust all certificates (cho môi trường test)
- ✅ User-Agent: `SimpleHttpBrowser/1.0`

## 📦 Yêu cầu hệ thống

- **Java**: JDK 11 trở lên (đã test với Java 21)
- **Hệ điều hành**: Windows, Linux, macOS
- **Bộ nhớ**: Tối thiểu 256MB RAM
- **Kết nối**: Internet (cho external requests)

## 🚀 Cách chạy

### Từ Eclipse/IDE:
1. Mở project trong Eclipse
2. Chạy file `SimpleHttpBrowser.java`
3. Ứng dụng sẽ hiển thị 2 tabs:
   - 🖥️ **Web Client**: Gửi HTTP requests
   - 🖧 **Server Monitor**: Theo dõi server

### Từ Command Line:

#### 1. Biên dịch:
```bash
cd d:\eclipse-workspace\SimpleHttpBrowser\src
javac -d ..\bin module-info.java com\httpbrowser\*.java
```

#### 2. Chạy:
```bash
cd d:\eclipse-workspace\SimpleHttpBrowser
java --module-path bin -m SimpleHttpBrowser/com.httpbrowser.SimpleHttpBrowser
```

### Tạo file JAR (Portable):

#### Tạo manifest:
```bash
echo Main-Class: com.httpbrowser.SimpleHttpBrowser > manifest.txt
echo. >> manifest.txt
```

#### Tạo JAR:
```bash
cd d:\eclipse-workspace\SimpleHttpBrowser
jar cvfm SimpleHttpBrowser.jar manifest.txt -C bin .
```

#### Chạy JAR:
```bash
java -jar SimpleHttpBrowser.jar
```

## 📖 Hướng dẫn sử dụng

### 🎯 Scenario 1: Test với Local Server

1. **Khởi động ứng dụng**
   - Server tự động start ở port 8080
   - Chuyển sang tab "🖧 Server Monitor" để xem thông tin

2. **Test Local Server**
   - Nhấn nút **"🧪 Test Server"** trong tab Server Monitor
   - Hoặc nhập URL: `http://localhost:8080/`
   - Chọn method **GET**
   - Nhấn **"🚀 Gửi yêu cầu"**

3. **Xem kết quả**
   - Tab **📊 Thông tin phản hồi**: Status, stats, nội dung
   - Tab **📋 Headers**: Tất cả HTTP headers
   - Tab **🌐 HTML View**: HTML được render

### 🎯 Scenario 2: Test GET request

```
URL: http://localhost:8080/test
Method: GET
☑️ Render HTML: Checked
→ Nhấn "Gửi yêu cầu"

Kết quả:
✅ Status: 200 OK
📈 Thống kê:
   - <p> tags: 15
   - <div> tags: 10
   - <span> tags: 8
   - <img> tags: 5
```

### 🎯 Scenario 3: Test POST request

```
URL: http://localhost:8080/echo
Method: POST
POST Data: 
{
  "message": "Hello from SimpleHttpBrowser",
  "user": "test"
}
→ Nhấn "Gửi yêu cầu"

Kết quả:
✅ Status: 200 OK
📄 Response: Echo của data bạn gửi (JSON format)
```

### 🎯 Scenario 4: Test HEAD request

```
URL: http://localhost:8080/status
Method: HEAD
→ Nhấn "Gửi yêu cầu"

Kết quả:
✅ Status: 200 OK
📋 Headers: Chỉ hiển thị headers, không có body
```

### 🎯 Scenario 5: Test External HTTPS

```
URL: https://api.github.com/users/github
Method: GET
🔒 HTTPS: Checked
☑️ Render HTML: Checked
→ Nhấn "Gửi yêu cầu"

Kết quả:
✅ Status: 200 OK
🔐 Protocol: HTTPS
📄 Response: JSON data từ GitHub API
```

## 🖧 Server Endpoints

Server tự động cung cấp các endpoints sau:

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| **GET** | `/` | Trang chủ với giao diện đẹp |
| **GET** | `/test` | Trang test với nhiều HTML tags |
| **GET** | `/info` | Thông tin server chi tiết |
| **POST** | `/echo` | Echo lại POST data (JSON) |
| **HEAD** | `/status` | Status check (chỉ headers) |
| **GET** | `/api/users` | Demo API trả về JSON |

### Test URLs sẵn có:
```
http://localhost:8080/
http://localhost:8080/test
http://localhost:8080/info
http://localhost:8080/status
http://localhost:8080/api/users
```

## 🎨 Giao diện

```
┌───────────────────────────────────────────────────────────────┐
│ 🌐 Simple HTTP Browser - Client & Server        [_][□][X]    │
├───────────────────────────────────────────────────────────────┤
│ ┌──────────┬────────────────────────────────────────────────┐ │
│ │🖥️ Client│  🖧 Server Monitor                             │ │
│ └──────────┴────────────────────────────────────────────────┘ │
│                                                                │
│ WEB CLIENT TAB:                                                │
│ ┌────────────────────────────────────────────────────────────┐ │
│ │ 🌐 URL: [http://localhost:8080/__________________]         │ │
│ │         Method: [GET ▼] [🚀 Gửi yêu cầu]                  │ │
│ │         ☑️ Render HTML  ☐ 🔒 HTTPS                        │ │
│ │                                                            │ │
│ │ 📝 POST Data: [_________________________________]          │ │
│ ├────────────────────────────────────────────────────────────┤ │
│ │ ┌───┬─────────┬───────────┐                               │ │
│ │ │📊 │   📋    │    🌐     │                               │ │
│ │ └───┴─────────┴───────────┘                               │ │
│ │                                                            │ │
│ │ ═══════════════════════════════════════════════════        │ │
│ │ 📊 THÔNG TIN PHẢN HỒI HTTP                                │ │
│ │ ═══════════════════════════════════════════════════        │ │
│ │                                                            │ │
│ │ 🔐 Protocol: HTTP                                          │ │
│ │ 🔴 Mã trạng thái: 200 OK                                  │ │
│ │ 📏 Chiều dài: 5.23 KB                                     │ │
│ │                                                            │ │
│ │ 📈 THỐNG KÊ HTML TAGS                                     │ │
│ │   🔹 Thẻ <p>:    15                                       │ │
│ │   🔹 Thẻ <div>:  10                                       │ │
│ │   🔹 Thẻ <span>: 8                                        │ │
│ │   🔹 Thẻ <img>:  5                                        │ │
│ └────────────────────────────────────────────────────────────┘ │
│ ✅ Hoàn thành - 200 OK                                         │
└───────────────────────────────────────────────────────────────┘
```

## 📁 Cấu trúc Project

```
SimpleHttpBrowser/
├── src/
│   ├── module-info.java          # Module configuration
│   └── com/
│       └── httpbrowser/
│           ├── SimpleHttpBrowser.java  # Main application
│           ├── WebClient.java          # HTTP/HTTPS Client
│           └── WebServer.java          # HTTP Server
├── bin/                          # Compiled classes
├── README.md                     # Tài liệu này
└── manifest.txt                  # JAR manifest (optional)
```

## 🔧 Tính năng kỹ thuật

### Web Client:
- **GUI**: Java Swing với JTabbedPane
- **HTTP Client**: HttpURLConnection
- **HTTPS Support**: SSLContext với trust all certificates
- **HTML Rendering**: JEditorPane
- **Threading**: SwingWorker (non-blocking UI)
- **Encoding**: UTF-8

### Web Server:
- **Server**: com.sun.net.httpserver.HttpServer
- **Port**: 8080 (HTTP)
- **Handlers**: Custom HttpHandler cho mỗi endpoint
- **Threading**: Default executor
- **Response**: HTML và JSON

### Module System:
- **Java Platform Module System (JPMS)**
- **Modules**: java.desktop, jdk.httpserver

## ⚡ Performance & Optimization

- ✅ Non-blocking UI với SwingWorker
- ✅ Connection timeout: 10 seconds
- ✅ Read timeout: 10 seconds
- ✅ Auto disconnect after request
- ✅ Memory efficient với streaming
- ✅ Display limit: 20,000 characters

## 🐛 Xử lý lỗi

- ✅ Connection errors với thông báo rõ ràng
- ✅ HTTP error codes (4xx, 5xx) handling
- ✅ Stack trace cho debugging
- ✅ Status bar realtime updates
- ✅ Exception handling ở mọi layer

## 🌍 Tính năng Portable

Ứng dụng có thể chạy trên **bất kỳ máy nào** có JDK:

1. **Copy folder project** sang máy khác
2. **Biên dịch** (nếu cần):
   ```bash
   javac -d bin src/module-info.java src/com/httpbrowser/*.java
   ```
3. **Chạy**:
   ```bash
   java --module-path bin -m SimpleHttpBrowser/com.httpbrowser.SimpleHttpBrowser
   ```

Hoặc copy **file JAR** và chạy:
```bash
java -jar SimpleHttpBrowser.jar
```

## 💡 Tips & Tricks

### Test nhiều requests liên tiếp:
1. Dùng local server để test nhanh
2. Thay đổi URL trong ô URL field
3. Nhấn Enter để gửi nhanh
4. Xem kết quả ở các tabs khác nhau

### Debug responses:
1. Xem tab **📋 Headers** để kiểm tra headers
2. Xem tab **📊 Thông tin** để xem status và body
3. Xem tab **🌐 HTML View** để render HTML

### Test POST data:
1. Chọn method **POST**
2. Nhập JSON hoặc form data
3. Server `/echo` sẽ trả về data bạn gửi

### Test HTTPS:
1. Nhập URL bắt đầu với `https://`
2. Hoặc check ☑️ **HTTPS** checkbox
3. Trust all certificates đã được enable

## 🔮 Mở rộng trong tương lai

Có thể thêm:
- 🔧 Support thêm methods: PUT, DELETE, PATCH
- 🍪 Cookie management
- 📜 Request history
- 💾 Save/Load requests
- 🔑 Custom headers editor
- 🔐 Authentication (Basic, Bearer Token)
- 🌐 Proxy settings
- 📊 Response time measurement
- 🎨 Syntax highlighting cho JSON
- 🔒 HTTPS Server (port 8443)

## ❓ FAQ

**Q: Làm sao để test HTTPS?**  
A: Nhập URL với `https://` hoặc check HTTPS checkbox. Ứng dụng tự động trust all certificates.

**Q: Server không khởi động được?**  
A: Kiểm tra port 8080 có bị chiếm không. Đổi port trong code nếu cần.

**Q: HTML không render?**  
A: Check ☑️ "Render HTML" và gửi lại request. JEditorPane có giới hạn với complex CSS.

**Q: Làm sao copy sang máy khác?**  
A: Copy toàn bộ folder hoặc chỉ file JAR. Đảm bảo máy đích có JDK 11+.

**Q: Tại sao thống kê tags không chính xác 100%?**  
A: Do parsing đơn giản. Đủ cho testing cơ bản.

## 📄 License

Free to use and modify.

## 👨‍💻 Author

**Tạo bởi**: GitHub Copilot  
**Version**: 2.0  
**Ngày**: October 2025  
**Features**: Web Client + Web Server + HTTP + HTTPS + Full GUI

---

## 🎉 Kết luận

**SimpleHttpBrowser** là một công cụ hoàn chỉnh để:
- ✅ Test HTTP/HTTPS APIs
- ✅ Debug web services
- ✅ Học về HTTP protocol
- ✅ Demo web communication
- ✅ Portable và dễ sử dụng

**Enjoy coding! 🚀**
