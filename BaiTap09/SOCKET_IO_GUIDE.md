# HƯỚNG DẪN HỌC VỀ SOCKET.IO VÀ XÂY DỰNG CHỨC NĂNG CHAT REAL-TIME

## 1. SOCKET.IO LÀ GÌ?

### 1.1 Định nghĩa
Socket.IO là một thư viện JavaScript cho phép giao tiếp hai chiều theo thời gian thực (real-time bidirectional communication) giữa client và server. Nó được xây dựng trên WebSocket protocol nhưng cung cấp nhiều tính năng bổ sung.

### 1.2 Tại sao dùng Socket.IO?
- **Real-time**: Dữ liệu được truyền tải tức thời, không cần polling
- **Bidirectional**: Cả client và server đều có thể gửi data
- **Auto-reconnection**: Tự động kết nối lại khi bị mất kết nối
- **Event-based**: Dễ dàng xử lý các sự kiện khác nhau
- **Cross-platform**: Hỗ trợ Web, iOS, Android, và nhiều platform khác

### 1.3 Sự khác biệt với HTTP truyền thống

**HTTP Request/Response (Traditional):**
```
Client --request--> Server
Client <--response-- Server
```
- Client phải luôn khởi tạo request
- Không thể push data từ server xuống client
- Phải polling để kiểm tra update mới

**Socket.IO (WebSocket-based):**
```
Client <--bidirectional channel--> Server
```
- Kết nối liên tục, hai chiều
- Server có thể push data bất cứ lúc nào
- Real-time, hiệu quả hơn

## 2. KIẾN TRÚC HỆ THỐNG CHAT

### 2.1 Tổng quan
```
┌─────────────┐         ┌─────────────┐         ┌─────────────┐
│  Customer   │◄───────►│   Server    │◄───────►│   Manager   │
│  (Android)  │ Socket  │  (Node.js)  │ Socket  │  (Android)  │
└─────────────┘         └─────────────┘         └─────────────┘
```

### 2.2 Flow hoạt động

**Khi Customer gửi tin nhắn:**
1. Customer nhập tin nhắn và nhấn Send
2. App gọi `socketManager.sendMessage()`
3. Socket.IO emit event `send_message` lên server
4. Server nhận event, lưu message, broadcast cho tất cả clients
5. Manager nhận event `new_message` và hiển thị tin nhắn

**Khi có typing indicator:**
1. Customer gõ phím trong EditText
2. TextWatcher detect change
3. Emit event `typing` lên server
4. Server broadcast cho các clients khác
5. Manager hiển thị "Customer is typing..."

## 3. CẤU TRÚC CODE ANDROID

### 3.1 Model Classes

**ChatMessage.java** - Đại diện cho một tin nhắn
```java
public class ChatMessage {
    private String id;              // Unique ID
    private String senderId;        // ID người gửi
    private String senderName;      // Tên người gửi
    private String message;         // Nội dung
    private long timestamp;         // Thời gian
    private String senderType;      // "customer" hoặc "manager"
    private boolean isRead;         // Đã đọc chưa
}
```

**User.java** - Đại diện cho người dùng
```java
public class User {
    private String userId;
    private String userName;
    private String userType;        // "customer" hoặc "manager"
    private boolean isOnline;
}
```

### 3.2 SocketManager (Singleton Pattern)

**Tại sao dùng Singleton?**
- Chỉ cần 1 kết nối Socket.IO duy nhất trong toàn app
- Share connection giữa nhiều Activities
- Tránh duplicate connections

**Các phương thức chính:**
```java
SocketManager.getInstance()         // Lấy instance duy nhất
connect()                           // Kết nối server
disconnect()                        // Ngắt kết nối
joinChat(User user)                 // Join vào chat room
sendMessage(String message)         // Gửi tin nhắn
sendTyping()                        // Gửi trạng thái đang gõ
sendStopTyping()                    // Gửi trạng thái ngừng gõ
```

**Socket Events:**

*Client gửi (emit):*
```java
socket.emit("join", data);          // Join chat
socket.emit("send_message", data);  // Gửi tin nhắn
socket.emit("typing", data);        // Đang gõ
socket.emit("stop_typing", data);   // Ngừng gõ
```

*Client nhận (on):*
```java
socket.on("new_message", listener);     // Tin nhắn mới
socket.on("typing", listener);          // Ai đó đang gõ
socket.on("user_joined", listener);     // User mới join
socket.on(Socket.EVENT_CONNECT, ...);   // Kết nối thành công
```

### 3.3 ChatAdapter (RecyclerView)

**Chức năng:**
- Hiển thị danh sách tin nhắn
- Phân biệt tin nhắn của mình và người khác
- Format thời gian, tên người gửi

**ViewHolder Pattern:**
```java
class MessageViewHolder {
    TextView messageText;
    TextView senderName;
    TextView timestamp;
    
    void bind(ChatMessage message) {
        // Set data vào views
        // Căn trái/phải dựa trên sender
    }
}
```

### 3.4 ChatActivity / ManagerChatActivity

**Lifecycle:**
```java
onCreate() {
    // 1. Init views
    // 2. Setup user (tạo unique ID)
    // 3. Setup RecyclerView + Adapter
    // 4. Connect Socket.IO
    // 5. Join chat room
}

onDestroy() {
    // Clean up (optional disconnect)
}
```

**Key features:**
- Real-time message receiving
- Typing indicator với timeout
- Auto-scroll khi có tin nhắn mới
- Connection status display

## 4. SERVER NODE.JS

### 4.1 Cấu trúc cơ bản
```javascript
const express = require('express');
const http = require('http');
const socketIo = require('socket.io');

const app = express();
const server = http.createServer(app);
const io = socketIo(server);

io.on('connection', (socket) => {
    // Handle các events từ client
});

server.listen(3000);
```

### 4.2 Event Handlers

**Join event:**
```javascript
socket.on('join', (data) => {
    users.set(socket.id, data);
    socket.broadcast.emit('user_joined', data);
});
```

**Message event:**
```javascript
socket.on('send_message', (data) => {
    messages.push(data);
    io.emit('new_message', data);  // Broadcast to all
});
```

**Disconnect:**
```javascript
socket.on('disconnect', () => {
    const user = users.get(socket.id);
    socket.broadcast.emit('user_left', user);
    users.delete(socket.id);
});
```

## 5. TÍNH NĂNG CHI TIẾT

### 5.1 Typing Indicator

**Flow:**
1. User gõ → TextWatcher detect
2. Emit `typing` event (có debounce)
3. Server broadcast cho clients khác
4. Hiển thị "... is typing"
5. Sau 1s không gõ → emit `stop_typing`

**Code:**
```java
Handler typingHandler = new Handler();
Runnable stopTypingRunnable;

messageInput.addTextChangedListener(new TextWatcher() {
    public void onTextChanged(CharSequence s, ...) {
        if (s.length() > 0 && !isTyping) {
            isTyping = true;
            socketManager.sendTyping();
        }
        
        // Reset timer
        typingHandler.removeCallbacks(stopTypingRunnable);
        stopTypingRunnable = () -> {
            isTyping = false;
            socketManager.sendStopTyping();
        };
        typingHandler.postDelayed(stopTypingRunnable, 1000);
    }
});
```

### 5.2 Auto-Reconnection

Socket.IO tự động reconnect khi mất kết nối:
```java
IO.Options options = new IO.Options();
options.reconnection = true;
options.reconnectionAttempts = Integer.MAX_VALUE;
options.reconnectionDelay = 1000;
```

### 5.3 Message Threading (UI Thread)

Vì Socket callbacks chạy trên background thread, phải chuyển về UI thread:
```java
@Override
public void onNewMessage(ChatMessage message) {
    runOnUiThread(() -> {
        chatAdapter.addMessage(message);
        scrollToBottom();
    });
}
```

## 6. DEBUGGING & TESTING

### 6.1 Kiểm tra kết nối
```java
// Trong SocketManager
Log.d("Socket", "Connected: " + socket.connected());
Log.d("Socket", "ID: " + socket.id());
```

### 6.2 Test trên Emulator
- Server URL: `http://10.0.2.2:3000`
- `10.0.2.2` = localhost của máy host

### 6.3 Test trên thiết bị thật
- Tìm IP máy tính: `ipconfig` (Windows) hoặc `ifconfig` (Mac/Linux)
- Dùng IP thật: `http://192.168.1.100:3000`
- Đảm bảo cùng WiFi network

### 6.4 Common Issues

**Không kết nối được:**
- ✓ Server có đang chạy không?
- ✓ URL đúng chưa?
- ✓ Có permission INTERNET chưa?
- ✓ Firewall block không?

**Tin nhắn không nhận:**
- ✓ Event name đúng chưa? (case-sensitive)
- ✓ JSON format đúng chưa?
- ✓ Có gọi runOnUiThread không?

## 7. BEST PRACTICES

### 7.1 Performance
- Giới hạn số message trong memory (VD: 100 tin gần nhất)
- Lazy load history khi scroll lên
- Optimize RecyclerView với DiffUtil

### 7.2 Security
- Validate input trước khi gửi
- Escape HTML trong message
- Implement authentication
- Rate limiting trên server

### 7.3 User Experience
- Hiển thị loading indicator
- Retry mechanism khi fail
- Offline message queue
- Read receipts
- Notification sounds

### 7.4 Code Organization
```
models/         # Data classes
socket/         # Socket management
adapters/       # RecyclerView adapters
activities/     # UI screens
utils/          # Helper classes
```

## 8. MỞ RỘNG

### 8.1 Các tính năng có thể thêm
- [ ] Send images/files
- [ ] Voice messages
- [ ] Video call
- [ ] Group chat
- [ ] Message reactions
- [ ] Message search
- [ ] Encryption (E2E)
- [ ] Push notifications
- [ ] Chat history pagination
- [ ] User presence (online/offline)

### 8.2 Database Integration
- Save messages to SQLite (local)
- Save to MongoDB (server)
- Sync when reconnect

### 8.3 Authentication
- Login/Register system
- JWT tokens
- OAuth integration

## 9. TÀI LIỆU THAM KHẢO

### Official Docs
- Socket.IO: https://socket.io/docs/
- Socket.IO Client Java: https://github.com/socketio/socket.io-client-java

### Android
- RecyclerView: https://developer.android.com/guide/topics/ui/layout/recyclerview
- Threading: https://developer.android.com/guide/components/processes-and-threads

### Node.js
- Express: https://expressjs.com/
- Socket.IO Server: https://socket.io/docs/v4/server-api/

## 10. KẾT LUẬN

Socket.IO là công nghệ mạnh mẽ cho real-time applications. Với kiến trúc event-driven và auto-reconnection, nó rất phù hợp cho:
- Chat applications
- Live notifications
- Collaborative editing
- Gaming
- Real-time dashboards

Dự án này cung cấp foundation vững chắc để xây dựng các tính năng real-time phức tạp hơn.

