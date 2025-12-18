# QUICK START GUIDE - SOCKET.IO CHAT

## Bước 1: Setup Server (5 phút)

### 1.1 Cài đặt Node.js
- Tải từ: https://nodejs.org/
- Chọn LTS version
- Install với default settings

### 1.2 Khởi chạy Server
```powershell
# Mở PowerShell/Command Prompt
cd C:\Users\Admin\Documents\GitHub\Mobile\BaiTap09\server

# Cài đặt dependencies
npm install

# Chạy server
npm start
```

**Expected output:**
```
=================================
Socket.IO Chat Server
Running on port 3000
=================================
HTTP: http://localhost:3000
Android Emulator: http://10.0.2.2:3000
=================================
```

## Bước 2: Setup Android App (5 phút)

### 2.1 Sync Project
1. Mở project trong Android Studio
2. Click **File → Sync Project with Gradle Files**
3. Đợi Gradle sync xong

### 2.2 Cấu hình Server URL

Mở `SocketManager.java` và kiểm tra SERVER_URL:

**Cho Android Emulator:**
```java
private static final String SERVER_URL = "http://10.0.2.2:3000";
```

**Cho thiết bị thật:**
```java
// Tìm IP máy tính bằng lệnh: ipconfig
private static final String SERVER_URL = "http://192.168.1.XXX:3000";
```

### 2.3 Build & Run
1. Chọn emulator hoặc thiết bị
2. Click **Run** (Shift+F10)
3. Đợi app install

## Bước 3: Test Chat (2 phút)

### Scenario 1: Test trên 1 thiết bị
1. Mở app
2. Click **"Open as Customer"**
3. Gõ tin nhắn và gửi
4. Back về MainActivity
5. Click **"Open as Manager"**
6. Xem tin nhắn từ Customer
7. Reply lại

### Scenario 2: Test trên 2 thiết bị
1. **Thiết bị 1:** Open as Customer
2. **Thiết bị 2:** Open as Manager
3. Chat qua lại real-time

## Bước 4: Verify Features

### ✓ Checklist
- [ ] Kết nối thành công (hiện "Connected")
- [ ] Gửi tin nhắn từ Customer
- [ ] Nhận tin nhắn ở Manager
- [ ] Typing indicator hoạt động
- [ ] Tin nhắn hiển thị đúng sender (trái/phải)
- [ ] Timestamp hiển thị
- [ ] Auto-scroll khi có tin mới

## Troubleshooting Nhanh

### ❌ "Connecting..." mãi không kết nối
**Fix:**
1. Kiểm tra server đang chạy: mở http://localhost:3000 trên browser
2. Kiểm tra SERVER_URL trong SocketManager.java
3. Tắt firewall tạm thời
4. Restart app và server

### ❌ App crash khi gửi tin nhắn
**Fix:**
1. Check Logcat để xem error
2. Verify permission INTERNET trong AndroidManifest.xml
3. Clean & Rebuild project

### ❌ Tin nhắn không hiển thị
**Fix:**
1. Kiểm tra Logcat có log "New message received" không
2. Verify RecyclerView adapter đã setup
3. Check JSON format từ server

### ❌ Build error: Socket.IO not found
**Fix:**
1. Sync Gradle again
2. Invalidate Caches: **File → Invalidate Caches / Restart**
3. Check internet connection (Gradle download dependencies)

## Server Logs Giải Thích

```
New client connected: abc123
→ Có client mới kết nối

User joined: {userId: "customer_xyz", ...}
→ User đã join vào chat

Message received: {message: "Hello", ...}
→ Server nhận được tin nhắn

User typing: John Doe
→ User đang gõ

Client disconnected: abc123
→ Client ngắt kết nối
```

## Android Logcat Tags

Filter Logcat với các tags sau:
- `SocketManager` - Xem Socket.IO events
- `ChatActivity` - Xem activity lifecycle
- `ChatAdapter` - Xem message rendering

## Lệnh hữu ích

### Server
```powershell
# Xem port đang dùng
netstat -ano | findstr :3000

# Stop process
taskkill /PID [PID] /F

# Clear npm cache (nếu install lỗi)
npm cache clean --force
```

### Android
```powershell
# Xem devices
adb devices

# Xem logs real-time
adb logcat | findstr "SocketManager"

# Clear app data
adb shell pm clear vn.hcmute.baitap09
```

## Next Steps

Sau khi test thành công, bạn có thể:

1. **Tùy chỉnh UI**: Edit layout files
2. **Thêm features**: Xem SOCKET_IO_GUIDE.md
3. **Deploy server**: Heroku, Railway, AWS
4. **Add database**: MongoDB, Firebase
5. **Authentication**: Login/Register system

## Cần trợ giúp?

1. Đọc chi tiết trong `SOCKET_IO_GUIDE.md`
2. Check server logs
3. Check Android Logcat
4. Verify network connectivity
5. Test với browser trước (Socket.IO client test)

---

**Thời gian setup tổng:** ~15 phút
**Difficulty:** Beginner-friendly
**Requirements:** Node.js, Android Studio, Java 11+

