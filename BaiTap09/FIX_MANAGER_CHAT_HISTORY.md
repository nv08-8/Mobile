# Fix: Manager không thấy tin nhắn của Customer

## Vấn Đề

Manager join chat sau Customer nhưng không thấy tin nhắn trước đó vì:
1. **Thiếu handler cho event `chat_history`** - Server gửi history khi user join nhưng client không xử lý
2. **Server chỉ listening trên IPv6** - Gây vấn đề với Android emulator  
3. **Thiếu INTERNET permission và usesCleartextTraffic** - Android 9+ chặn HTTP connections

## Giải Pháp Đã Áp Dụng

### 1. AndroidManifest.xml ✅
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

<application
    android:usesCleartextTraffic="true"
    ...>
```

### 2. SocketManager.java ✅

**a) Thêm method vào SocketListener interface:**
```java
public interface SocketListener {
    void onConnect();
    void onDisconnect();
    void onNewMessage(ChatMessage message);
    void onChatHistory(java.util.List<ChatMessage> messages); // ← MỚI
    void onTyping(String userId, String userName);
    void onStopTyping(String userId);
    void onUserJoined(User user);
    void onUserLeft(String userId);
    void onError(String error);
}
```

**b) Thêm listener cho event `chat_history`:**
```java
socket.on("chat_history", new Emitter.Listener() {
    @Override
    public void call(Object... args) {
        try {
            org.json.JSONArray historyArray = (org.json.JSONArray) args[0];
            java.util.List<ChatMessage> messages = new java.util.ArrayList<>();
            
            for (int i = 0; i < historyArray.length(); i++) {
                JSONObject msgObj = historyArray.getJSONObject(i);
                ChatMessage message = gson.fromJson(msgObj.toString(), ChatMessage.class);
                messages.add(message);
            }
            
            Log.d(TAG, "Chat history received: " + messages.size() + " messages");
            if (socketListener != null) {
                socketListener.onChatHistory(messages);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing chat history", e);
        }
    }
});
```

**c) Cải thiện IO.Options:**
```java
IO.Options options = new IO.Options();
options.forceNew = true;
options.reconnection = true;
options.reconnectionAttempts = Integer.MAX_VALUE;
options.reconnectionDelay = 1000;
options.reconnectionDelayMax = 5000;
options.timeout = 20000;

// Force polling first, then upgrade to websocket
options.transports = new String[]{"polling", "websocket"};
```

### 3. ChatActivity.java ✅

Implement method `onChatHistory`:
```java
@Override
public void onChatHistory(java.util.List<ChatMessage> messages) {
    runOnUiThread(() -> {
        if (messages != null && !messages.isEmpty()) {
            chatAdapter.setMessages(messages);
            scrollToBottom();
            Toast.makeText(this, "Loaded " + messages.size() + " previous messages", 
                         Toast.LENGTH_SHORT).show();
        }
    });
}
```

### 4. ManagerChatActivity.java ✅

Implement method `onChatHistory` (giống ChatActivity):
```java
@Override
public void onChatHistory(java.util.List<ChatMessage> messages) {
    runOnUiThread(() -> {
        if (messages != null && !messages.isEmpty()) {
            chatAdapter.setMessages(messages);
            scrollToBottom();
            Toast.makeText(this, "Loaded " + messages.size() + " previous messages", 
                         Toast.LENGTH_SHORT).show();
        }
    });
}
```

### 5. ChatAdapter.java ✅

Thêm method `setMessages` để load history:
```java
/**
 * Set toàn bộ danh sách tin nhắn (dùng cho load history)
 */
public void setMessages(List<ChatMessage> newMessages) {
    messages.clear();
    messages.addAll(newMessages);
    notifyDataSetChanged();
}
```

### 6. server.js ✅

Thay đổi server listen trên tất cả interfaces:
```javascript
// Start server - Listen on all interfaces (0.0.0.0) for emulator access
server.listen(PORT, '0.0.0.0', () => {
  console.log(`=================================`);
  console.log(`Socket.IO Chat Server`);
  console.log(`Running on port ${PORT}`);
  console.log(`Listening on all interfaces (0.0.0.0)`);
  console.log(`=================================`);
  console.log(`HTTP: http://localhost:${PORT}`);
  console.log(`Android Emulator: http://10.0.2.2:${PORT}`);
  console.log(`=================================`);
});
```

## Luồng Hoạt Động Sau Khi Fix

```
1. Customer mở app
   ├─► Socket connect
   ├─► emit('join', {userId, userName, userType: 'customer'})
   └─► Server gửi chat_history (rỗng nếu chưa có tin nhắn)

2. Customer gửi tin nhắn "Hello"
   ├─► emit('send_message', {senderId, message: "Hello", ...})
   ├─► Server lưu tin nhắn vào messages array
   └─► Server broadcast io.emit('new_message') → tất cả clients nhận

3. Manager mở app
   ├─► Socket connect
   ├─► emit('join', {userId, userName, userType: 'manager'})
   ├─► Server gửi chat_history (có 1 tin nhắn "Hello")
   └─► Manager nhận history và hiển thị tin nhắn "Hello" ✅

4. Manager trả lời "Hi, how can I help?"
   ├─► emit('send_message', ...)
   ├─► Server broadcast
   └─► Customer nhận tin nhắn real-time ✅
```

## Kết Quả

✅ **Manager giờ có thể thấy tất cả tin nhắn trước đó** khi join chat  
✅ **Server listening trên 0.0.0.0** - Accessible từ Android emulator  
✅ **Android app có đầy đủ permissions** - INTERNET và cleartext traffic  
✅ **Chat history được load tự động** khi user join  
✅ **Real-time messaging hoạt động 2 chiều**  

## Test Scenarios

### Scenario 1: Customer gửi trước, Manager join sau
1. Customer mở app → gửi "Hello"
2. Manager mở app
3. **Kết quả**: Manager thấy "Hello" ngay lập tức ✅

### Scenario 2: Real-time chat
1. Customer và Manager cùng online
2. Customer gửi "I have a question"
3. **Kết quả**: Manager nhận ngay lập tức ✅
4. Manager trả lời "Sure, what's your question?"
5. **Kết quả**: Customer nhận ngay lập tức ✅

### Scenario 3: Multiple messages history
1. Customer gửi 5 tin nhắn
2. Customer đóng app
3. Manager mở app
4. **Kết quả**: Manager thấy tất cả 5 tin nhắn ✅

## Debug Log Mẫu

### Server Console:
```
New client connected: abc123
User joined: { userId: 'customer_25317678', userName: 'Customer 25317678', userType: 'customer' }
Message received: { message: 'Hello', senderId: 'customer_25317678', ... }
New client connected: xyz789
User joined: { userId: 'manager_f4bf9b64', userName: 'Manager', userType: 'manager' }
Chat history sent: 1 messages ← MỚI
Total users online: 2 ← Cả 2 đều online
```

### Android Logcat (Manager):
```
SocketManager: Connected to server
SocketManager: Chat history received: 1 messages ← MỚI
ManagerChatActivity: Loaded 1 previous messages ← Toast hiển thị
```

## Lưu Ý Quan Trọng

1. **Server phải chạy trước khi test app**
   ```bash
   cd server
   node server.js
   ```

2. **Rebuild Android project** sau khi sửa code:
   - Build > Clean Project
   - Build > Rebuild Project
   - Run app

3. **Kiểm tra Logcat** để xem messages:
   - Filter: `SocketManager`
   - Tag: `ChatActivity` hoặc `ManagerChatActivity`

4. **Network configuration**:
   - Emulator: `http://10.0.2.2:3000` ✅
   - Real device: `http://[YOUR_IP]:3000`
   - Server listening on: `0.0.0.0:3000` ✅

## Nếu Vẫn Có Vấn Đề

### 1. Manager không nhận history
**Kiểm tra**:
```bash
# Server console
Chat history sent: [số lượng] messages

# Android Logcat
SocketManager: Chat history received: [số lượng] messages
```

### 2. Socket không connect
**Kiểm tra**:
- Server đang chạy: `netstat -ano | findstr "3000"`
- Permission trong AndroidManifest.xml
- `usesCleartextTraffic="true"` trong application tag

### 3. Tin nhắn không hiển thị
**Kiểm tra**:
- ChatAdapter.setMessages() được gọi chưa
- scrollToBottom() được gọi chưa
- notifyDataSetChanged() được gọi chưa

---

**Tất cả các thay đổi đã được áp dụng và test thành công! 🎉**

