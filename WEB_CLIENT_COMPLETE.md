# Web Client - Complete Edition (v3.2)

## 🎉 Tổng quan

**Web Client** đã được **hoàn thiện đầy đủ** với tất cả các tính năng cần thiết cho một HTTP/HTTPS client chuyên nghiệp.

---

## ✨ Tính năng đầy đủ

### 1. 🌐 HTTP Methods Support
- ✅ **GET** - Lấy dữ liệu từ server
- ✅ **POST** - Gửi dữ liệu lên server
- ✅ **PUT** - Cập nhật dữ liệu
- ✅ **DELETE** - Xóa dữ liệu
- ✅ **HEAD** - Chỉ lấy headers (không lấy body)

### 2. 🔧 Advanced Request Configuration

#### Custom Headers
```
# Format: Key: Value
User-Agent: CustomBrowser/3.2
Authorization: Bearer your_token_here
Content-Type: application/json
Accept: application/json
X-Custom-Header: custom_value
```

#### Request Body (POST/PUT/DELETE)
```json
{
  "message": "Hello from SimpleHttpBrowser",
  "timestamp": "2025-10-28T10:30:00",
  "data": {
    "key": "value"
  }
}
```

### 3. 🍪 Cookie Management
- ✅ **Tự động lưu cookies** từ server response
- ✅ **Tự động gửi cookies** trong request tiếp theo
- ✅ **Cookie store theo domain** (domain-specific)
- ✅ **Enable/Disable** cookies qua checkbox "Use Cookies"

**Cách hoạt động:**
1. Server gửi `Set-Cookie` header
2. Web Client tự động lưu cookie theo domain
3. Request tiếp theo đến cùng domain sẽ tự động gửi cookie

### 4. ⏱️ Timeout Configuration
- ✅ **Configurable timeout** từ 5-60 giây
- ✅ **Default: 15 seconds**
- ✅ **Spinner control** để điều chỉnh dễ dàng
- ✅ Áp dụng cho cả **Connect Timeout** và **Read Timeout**

### 5. 💾 Save Response to File
- ✅ **Save response body** vào file
- ✅ **Tự động đề xuất extension** (.html, .json, .xml, .txt)
- ✅ **File chooser dialog** để chọn vị trí lưu
- ✅ **Thông báo thành công/lỗi** sau khi lưu

**Các định dạng được hỗ trợ:**
- `.html` - Cho HTML content
- `.json` - Cho JSON content
- `.xml` - Cho XML content
- `.txt` - Cho plain text

### 6. 📋 Copy to Clipboard
- ✅ **Copy Response Body** - Chỉ copy nội dung response
- ✅ **Copy Headers** - Chỉ copy headers
- ✅ **Copy All** - Copy cả headers và body
- ✅ **Status notification** trong 3 giây

### 7. 🎨 HTML Rendering
- ✅ **Automatic Base URL** - Tự động set base URL cho tài nguyên
- ✅ **UTF-8 encoding** - Hỗ trợ Unicode đầy đủ
- ✅ **Clickable links** - Click vào link để navigate
- ✅ **Image loading** - Load hình ảnh từ relative paths
- ✅ **CSS support** - Hỗ trợ basic CSS

### 8. ↪️ Redirect Handling
- ✅ **Follow redirects** tự động (3xx status codes)
- ✅ **Enable/Disable** qua checkbox
- ✅ Hỗ trợ cả **301, 302, 303, 307, 308** redirects

### 9. 📊 Response Analysis

#### Thông tin chi tiết:
- ✅ **Protocol** (HTTP/HTTPS)
- ✅ **Status Code** và **Status Message**
- ✅ **Content-Type**
- ✅ **Response Time** (milliseconds)
- ✅ **Content-Length** (bytes/KB/MB)

#### HTML Statistics:
- ✅ Số lượng thẻ `<p>`
- ✅ Số lượng thẻ `<div>`
- ✅ Số lượng thẻ `<span>`
- ✅ Số lượng thẻ `<img>`
- ✅ Tổng số tags

### 10. 📝 JSON Pretty Print
- ✅ **Tự động format JSON** response
- ✅ **Indentation** rõ ràng
- ✅ **Syntax-aware** formatting

### 11. 🗂️ URL History
- ✅ **Quick access dropdown** với popular URLs
- ✅ **Auto-save history** (tối đa 20 URLs)
- ✅ **Pre-loaded popular URLs**:
  - http://localhost:8080/
  - https://www.google.com
  - https://www.github.com
  - https://api.github.com
  - https://httpbin.org/get
  - https://jsonplaceholder.typicode.com/posts
  - https://www.wikipedia.org
  - https://stackoverflow.com

### 12. 🔐 Security Features
- ✅ **HTTPS support** with SSL/TLS
- ✅ **Trust all certificates** (for testing)
- ✅ **Custom User-Agent** header
- ✅ **GZIP/Deflate/Brotli** decompression

### 13. 📦 Compression Support
- ✅ **GZIP** - Tự động decompress
- ✅ **Deflate** - Supported
- ✅ **Brotli** - Supported
- ✅ Tự động detect từ `Content-Encoding` header

### 14. 🎯 Smart Features
- ✅ **Progress bar** - Hiển thị trạng thái request
- ✅ **Async requests** - Không block UI
- ✅ **Error handling** - Stack trace chi tiết
- ✅ **Status bar** - Thông tin real-time
- ✅ **Response size indicator** - Hiển thị kích thước
- ✅ **Response time meter** - Đo thời gian phản hồi

---

## 🚀 Hướng dẫn sử dụng

### Basic Request
1. Nhập URL vào field "URL"
2. Chọn HTTP Method (GET/POST/PUT/DELETE/HEAD)
3. Click nút "🚀 Gửi yêu cầu" hoặc nhấn Enter

### POST Request với JSON
1. Chọn method **POST**
2. Tab "📝 Request Body", nhập JSON data:
```json
{
  "name": "John Doe",
  "email": "john@example.com"
}
```
3. Click "🚀 Gửi yêu cầu"

### Custom Headers
1. Tab "🔧 Custom Headers", nhập:
```
Authorization: Bearer abc123
Content-Type: application/json
Accept: application/json
```
2. Send request như bình thường

### Sử dụng Cookies
1. ✅ Check "🍪 Use Cookies"
2. Gửi request lần đầu (server set cookies)
3. Request tiếp theo sẽ tự động gửi cookies

### Thay đổi Timeout
1. Điều chỉnh spinner "⏱️ Timeout" (5-60 giây)
2. Timeout mới áp dụng cho request tiếp theo

### Lưu Response
1. Gửi request và nhận response
2. Click nút "💾 Save"
3. Chọn vị trí và tên file
4. File được lưu với extension phù hợp

### Copy to Clipboard
1. Gửi request và nhận response
2. Click nút "📋 Copy"
3. Chọn:
   - **Response Body** - Chỉ body
   - **Headers** - Chỉ headers
   - **All** - Cả hai
4. Data đã copy vào clipboard

---

## 📌 Các Tab trong Web Client

### 1️⃣ Tab "📊 Thông tin phản hồi"
- Protocol (HTTP/HTTPS)
- Status Code và Message
- Content-Type
- Response Time
- Content-Length
- HTML Statistics (nếu là HTML)
- Response Body (với JSON pretty print)

### 2️⃣ Tab "📋 Headers"
- Tất cả HTTP headers từ response
- Format: `Header-Name : Value`
- Dễ đọc và copy

### 3️⃣ Tab "🌐 HTML View"
- Render HTML content
- Hỗ trợ images và CSS
- Clickable links
- Base URL support

---

## 🔧 Configuration Options

### Checkboxes
- **🎨 Render HTML** - Bật/tắt HTML rendering
- **↪️ Follow Redirects** - Tự động follow 3xx redirects
- **🍪 Use Cookies** - Enable cookie management

### Spinners
- **⏱️ Timeout** - 5 đến 60 giây

---

## 💡 Tips & Tricks

### 1. Testing REST APIs
```
Method: POST
URL: https://jsonplaceholder.typicode.com/posts
Body:
{
  "title": "Test Post",
  "body": "This is a test",
  "userId": 1
}
```

### 2. Testing with Headers
```
Method: GET
URL: https://httpbin.org/headers
Custom Headers:
X-Test-Header: TestValue
Authorization: Bearer test123
```

### 3. Testing File Downloads
```
Method: GET
URL: https://example.com/file.pdf
Then: Click "💾 Save" to save to disk
```

### 4. Testing HTML Pages
```
Method: GET
URL: https://www.wikipedia.org
✅ Check "Render HTML"
View in "🌐 HTML View" tab
```

### 5. Using Cookies
```
1. Visit: http://localhost:8080/
2. ✅ Check "Use Cookies"
3. Server sends Set-Cookie
4. Next request automatically includes cookies
```

---

## 🎨 UI Components

### Top Panel
- URL input field với auto-complete
- Quick URLs dropdown
- Method selector (GET/POST/HEAD/PUT/DELETE)
- Send button
- Clear button
- Render HTML checkbox
- Follow Redirects checkbox
- Use Cookies checkbox
- Timeout spinner
- Save button
- Copy button

### Center Panel (Tabs)
1. **Thông tin phản hồi** - Response details
2. **Headers** - HTTP headers
3. **HTML View** - Rendered HTML

### Bottom Panel
- Status label (hiển thị trạng thái)
- Response time label
- Response size label

### Input Tabs
1. **Request Body** - POST/PUT/DELETE data
2. **Custom Headers** - Custom HTTP headers

---

## 🚨 Error Handling

### Connection Errors
```
❌ Lỗi: Connection refused
❌ Lỗi: Connection timeout
❌ Lỗi: Unknown host
```

### Timeout Errors
```
❌ Lỗi: Read timed out
Solution: Increase timeout value
```

### SSL Errors
```
✅ Automatically handled by trust all certificates
```

### Invalid URL
```
❌ Lỗi: Vui lòng nhập URL!
Solution: Enter valid URL (http:// or https://)
```

---

## 📊 Response Format

### Success Response
```
═══════════════════════════════════════════════════════
📊 THÔNG TIN PHẢN HỒI HTTP/HTTPS
═══════════════════════════════════════════════════════

🔐 Protocol: HTTPS
🔴 Status Code: 200 OK
📄 Content-Type: application/json; charset=utf-8
⏱️  Response Time: 245 ms
📏 Content-Length: 1.25 KB

───────────────────────────────────────────────────────
📄 NỘI DUNG PHẢN HỒI
───────────────────────────────────────────────────────

{
  "data": "response data here"
}
```

### Error Response
```
❌ Lỗi: Connection refused

Stack trace:
java.net.ConnectException: Connection refused
    at ...
```

---

## 🔄 Version History

### v3.2 - Complete Edition (Current)
✅ **NEW** Cookie management
✅ **NEW** Configurable timeout (5-60s)
✅ **NEW** Save response to file
✅ **NEW** Copy to clipboard (Body/Headers/All)
✅ **IMPROVED** Better error handling
✅ **IMPROVED** Enhanced UI with more controls
✅ **IMPROVED** Smart file extension detection

### v3.1 - HTML Rendering Update
✅ Base URL support for HTML resources
✅ UTF-8 encoding
✅ Improved image/CSS loading

### v3.0 - Major Update
✅ Custom headers support
✅ URL history
✅ HTML statistics
✅ JSON pretty print

### v2.0 - Enhanced Version
✅ 5 HTTP methods
✅ POST data support
✅ Progress bar
✅ GZIP decompression

### v1.0 - Initial Release
✅ Basic GET requests
✅ HTTPS support
✅ Simple UI

---

## 🎯 Use Cases

### 1. API Development & Testing
- Test REST APIs
- Debug HTTP requests
- Validate JSON responses
- Test authentication

### 2. Web Scraping
- Fetch HTML pages
- Render and view content
- Save pages to disk
- Extract data

### 3. HTTP Learning
- Understand HTTP protocol
- See headers in action
- Test different methods
- Learn about cookies

### 4. Integration Testing
- Test endpoints
- Verify responses
- Check status codes
- Validate headers

### 5. Performance Testing
- Measure response times
- Test timeouts
- Check content sizes
- Monitor redirects

---

## 🌟 Features Summary

| Feature | Status | Description |
|---------|--------|-------------|
| GET Request | ✅ | Lấy dữ liệu từ server |
| POST Request | ✅ | Gửi dữ liệu lên server |
| PUT Request | ✅ | Cập nhật dữ liệu |
| DELETE Request | ✅ | Xóa dữ liệu |
| HEAD Request | ✅ | Chỉ lấy headers |
| Custom Headers | ✅ | Headers tùy chỉnh |
| Cookie Support | ✅ | Tự động lưu và gửi cookies |
| Timeout Config | ✅ | 5-60 giây |
| Save to File | ✅ | Lưu response vào file |
| Copy to Clipboard | ✅ | Copy Body/Headers/All |
| HTML Rendering | ✅ | Render HTML với Base URL |
| Follow Redirects | ✅ | Tự động follow 3xx |
| HTTPS/SSL | ✅ | Full SSL/TLS support |
| GZIP Compression | ✅ | Tự động decompress |
| JSON Pretty Print | ✅ | Format JSON đẹp |
| URL History | ✅ | Lưu 20 URLs gần nhất |
| Response Stats | ✅ | Time, Size, Protocol |
| HTML Stats | ✅ | Đếm tags trong HTML |
| Error Handling | ✅ | Stack trace chi tiết |
| Progress Bar | ✅ | Hiển thị tiến trình |
| Async Requests | ✅ | Không block UI |

---

## 📚 Documentation

Để biết thêm thông tin:
- **README.md** - Tổng quan ứng dụng
- **HTML_RENDERING_IMPROVEMENTS.md** - Chi tiết HTML rendering
- **WEB_BROWSER_GUIDE.md** - Hướng dẫn Web Browser

---

## 💻 Technical Details

### Technologies
- **Java Swing** - GUI framework
- **HttpURLConnection** - HTTP client
- **JEditorPane** - HTML rendering
- **SwingWorker** - Async operations
- **SSL/TLS** - Secure connections

### Performance
- **Async requests** - Non-blocking UI
- **GZIP support** - Reduced bandwidth
- **Connection pooling** - Efficient connections
- **Timeout control** - Prevent hanging

### Compatibility
- **Java 17+** required
- **Windows/Linux/Mac** compatible
- **HTTP/1.0, HTTP/1.1** support
- **HTTPS/TLS 1.2+** support

---

## 🎉 Kết luận

**Web Client v3.2** là một HTTP/HTTPS client **đầy đủ tính năng**, phù hợp cho:
- ✅ API testing và development
- ✅ Web scraping
- ✅ HTTP learning
- ✅ Integration testing
- ✅ Performance monitoring

Với **21+ tính năng chính**, Web Client cung cấp mọi thứ bạn cần để làm việc với HTTP/HTTPS một cách chuyên nghiệp! 🚀
