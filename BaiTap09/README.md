# Socket.IO Chat Application - Android

## Tổng quan về Socket.IO

Socket.IO là một thư viện JavaScript cho ứng dụng web real-time. Nó cho phép giao tiếp hai chiều theo thời gian thực giữa client và server.

### Đặc điểm chính:
- **Real-time bidirectional communication**: Giao tiếp hai chiều theo thời gian thực
- **Auto-reconnection**: Tự động kết nối lại khi mất kết nối
- **Event-based**: Sử dụng events để gửi/nhận dữ liệu
- **Cross-platform**: Hỗ trợ nhiều nền tảng (Web, Android, iOS)

## Cấu trúc dự án

```
BaiTap09/
├── app/src/main/java/vn/hcmute/baitap09/
│   ├── models/
│   │   ├── ChatMessage.java      # Model tin nhắn
│   │   └── User.java             # Model người dùng
│   ├── socket/
│   │   └── SocketManager.java    # Quản lý Socket.IO
│   ├── adapters/
│   │   └── ChatAdapter.java      # Adapter cho RecyclerView
│   ├── ChatActivity.java         # Activity cho khách hàng
│   ├── ManagerChatActivity.java  # Activity cho manager
│   └── MainActivity.java         # Activity chính
└── app/src/main/res/
    └── layout/
        ├── activity_main.xml
        ├── activity_chat.xml
        └── item_chat_message.xml
```

## Các thành phần chính

### 1. SocketManager (Singleton)
Quản lý kết nối Socket.IO và xử lý các events:
- `connect()`: Kết nối với server
- `disconnect()`: Ngắt kết nối
- `sendMessage()`: Gửi tin nhắn
- `sendTyping()`: Gửi trạng thái đang gõ
- `joinChat()`: Join vào chat room

### 2. ChatMessage Model
Lưu trữ thông tin tin nhắn:
- senderId: ID người gửi
- senderName: Tên người gửi
- message: Nội dung tin nhắn
- timestamp: Thời gian gửi
- senderType: Loại người dùng (customer/manager)

### 3. Socket Events
**Client gửi (emit):**
- `join`: Join vào chat
- `send_message`: Gửi tin nhắn
- `typing`: Đang gõ
- `stop_typing`: Ngừng gõ
- `mark_read`: Đánh dấu đã đọc

**Client nhận (on):**
- `new_message`: Tin nhắn mới
- `typing`: Người khác đang gõ
- `stop_typing`: Người khác ngừng gõ
- `user_joined`: Người dùng mới join
- `user_left`: Người dùng rời đi

## Cấu hình

### 1. Thay đổi Server URL
Trong `SocketManager.java`, cập nhật `SERVER_URL`:

```java
// Cho Android Emulator
private static final String SERVER_URL = "http://10.0.2.2:3000";

// Cho thiết bị thật (dùng IP máy tính)
private static final String SERVER_URL = "http://192.168.1.100:3000";
```

### 2. Dependencies đã thêm
- `io.socket:socket.io-client:2.1.0` - Socket.IO client
- `com.google.code.gson:gson:2.10.1` - JSON parsing
- `androidx.recyclerview:recyclerview:1.3.2` - RecyclerView

## Hướng dẫn chạy

### Bước 1: Cài đặt Node.js Server
Xem file `server.js` (sẽ tạo tiếp theo)

### Bước 2: Chạy server
```bash
cd server
npm install
node server.js
```

### Bước 3: Build và chạy Android app
1. Sync Gradle
2. Build project
3. Chạy trên emulator hoặc thiết bị thật

### Bước 4: Test
1. Mở app, chọn "Open as Customer"
2. Mở app lần nữa (hoặc trên thiết bị khác), chọn "Open as Manager"
3. Chat giữa 2 thiết bị

## Tính năng

### ✅ Đã implement
- [x] Kết nối Socket.IO real-time
- [x] Gửi/nhận tin nhắn
- [x] Typing indicator (đang gõ)
- [x] Phân biệt tin nhắn customer/manager
- [x] Auto-scroll khi có tin nhắn mới
- [x] Hiển thị trạng thái kết nối
- [x] Auto-reconnection
- [x] User join/leave notifications

### 🔄 Có thể mở rộng
- [ ] Lưu lịch sử chat vào database
- [ ] Push notification
- [ ] Gửi hình ảnh/file
- [ ] Typing timeout
- [ ] Message read receipts
- [ ] Multiple chat rooms
- [ ] User authentication
- [ ] Encryption

## Troubleshooting

### 1. Không kết nối được server
- Kiểm tra server đang chạy
- Kiểm tra firewall
- Kiểm tra SERVER_URL đúng
- Kiểm tra permission INTERNET trong AndroidManifest.xml

### 2. Tin nhắn không hiển thị
- Kiểm tra log trong Logcat
- Kiểm tra format JSON từ server
- Kiểm tra adapter đã được setup đúng

### 3. Emulator không kết nối được
- Dùng IP `10.0.2.2` cho emulator (trỏ đến localhost của máy host)
- Cho thiết bị thật, dùng IP thật của máy tính

## Tài liệu tham khảo
- [Socket.IO Documentation](https://socket.io/docs/)
- [Socket.IO Client Java](https://github.com/socketio/socket.io-client-java)
- [Android RecyclerView](https://developer.android.com/guide/topics/ui/layout/recyclerview)

