# Quick Start - Socket.IO Chat Implementation

## ✅ Problem Fixed

**Original Error:**
```
SyntaxError: Unexpected token '}' at line 2
```

**Root Cause:** The entire `server.js` file was written backwards (from bottom to top), causing syntax errors.

**Solution:** ✅ Restructured the file in the correct order with proper JavaScript syntax.

---

## 🚀 Start Server (Required First!)

### Step 1: Open Terminal in Server Directory
```bash
cd C:\Users\Admin\Documents\GitHub\Mobile\BaiTap09\server
```

### Step 2: Install Dependencies (First Time Only)
```bash
npm install
```

This installs:
- `express` - Web framework
- `socket.io` - Real-time communication library

### Step 3: Start the Server
```bash
node server.js
```

**Expected Output:**
```
=================================
Socket.IO Chat Server
Running on port 3000
=================================
HTTP: http://localhost:3000
Android Emulator: http://10.0.2.2:3000
=================================
```

### ⚠️ If Port 3000 is Busy
```powershell
# Find and kill the process
Get-Process -Id (Get-NetTCPConnection -LocalPort 3000).OwningProcess | Stop-Process -Force

# Or change the port in server.js
const PORT = process.env.PORT || 3001;
```

---

## 📱 Android Implementation Steps

### 1. Add Socket.IO Dependency

Add to `app/build.gradle.kts`:
```kotlin
dependencies {
    implementation("io.socket:socket.io-client:2.1.0")
    implementation("com.google.code.gson:gson:2.10.1")
}
```

Then sync Gradle.

### 2. Add Internet Permission

In `AndroidManifest.xml`:
```xml
<uses-permission android:name="android.permission.INTERNET" />
```

### 3. Create SocketManager Class

Create `app/src/main/java/vn/hcmute/baitap09/socket/SocketManager.java`:

```java
package vn.hcmute.baitap09.socket;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.URISyntaxException;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SocketManager {
    private static SocketManager instance;
    private Socket socket;
    
    // Use 10.0.2.2 for Android Emulator (points to host machine)
    // Use your computer's IP (e.g., 192.168.1.100) for real device
    private static final String SERVER_URL = "http://10.0.2.2:3000";
    private static final String TAG = "SocketManager";
    
    private SocketManager() {
        try {
            socket = IO.socket(SERVER_URL);
            setupConnectionListeners();
        } catch (URISyntaxException e) {
            Log.e(TAG, "Socket initialization error", e);
        }
    }
    
    public static synchronized SocketManager getInstance() {
        if (instance == null) {
            instance = new SocketManager();
        }
        return instance;
    }
    
    private void setupConnectionListeners() {
        socket.on(Socket.EVENT_CONNECT, args -> 
            Log.d(TAG, "Connected to server"));
            
        socket.on(Socket.EVENT_DISCONNECT, args -> 
            Log.d(TAG, "Disconnected from server"));
            
        socket.on(Socket.EVENT_CONNECT_ERROR, args -> 
            Log.e(TAG, "Connection error: " + args[0]));
    }
    
    public void connect() {
        if (socket != null && !socket.connected()) {
            socket.connect();
        }
    }
    
    public void disconnect() {
        if (socket != null && socket.connected()) {
            socket.disconnect();
        }
    }
    
    public boolean isConnected() {
        return socket != null && socket.connected();
    }
    
    public void joinChat(String userId, String userName, String userType) {
        try {
            JSONObject data = new JSONObject();
            data.put("userId", userId);
            data.put("userName", userName);
            data.put("userType", userType);
            socket.emit("join", data);
            Log.d(TAG, "Joined chat: " + userName);
        } catch (JSONException e) {
            Log.e(TAG, "Join chat error", e);
        }
    }
    
    public void sendMessage(String senderId, String senderName, String message, String senderType) {
        try {
            JSONObject data = new JSONObject();
            data.put("senderId", senderId);
            data.put("senderName", senderName);
            data.put("message", message);
            data.put("senderType", senderType);
            data.put("timestamp", System.currentTimeMillis());
            socket.emit("send_message", data);
            Log.d(TAG, "Message sent: " + message);
        } catch (JSONException e) {
            Log.e(TAG, "Send message error", e);
        }
    }
    
    public void sendTyping(String userId, String userName) {
        try {
            JSONObject data = new JSONObject();
            data.put("userId", userId);
            data.put("userName", userName);
            socket.emit("typing", data);
        } catch (JSONException e) {
            Log.e(TAG, "Send typing error", e);
        }
    }
    
    public void sendStopTyping(String userId) {
        try {
            JSONObject data = new JSONObject();
            data.put("userId", userId);
            socket.emit("stop_typing", data);
        } catch (JSONException e) {
            Log.e(TAG, "Send stop typing error", e);
        }
    }
    
    // Event listeners
    public void onNewMessage(Emitter.Listener listener) {
        socket.on("new_message", listener);
    }
    
    public void onTyping(Emitter.Listener listener) {
        socket.on("typing", listener);
    }
    
    public void onStopTyping(Emitter.Listener listener) {
        socket.on("stop_typing", listener);
    }
    
    public void onUserJoined(Emitter.Listener listener) {
        socket.on("user_joined", listener);
    }
    
    public void onUserLeft(Emitter.Listener listener) {
        socket.on("user_left", listener);
    }
    
    public void onChatHistory(Emitter.Listener listener) {
        socket.on("chat_history", listener);
    }
    
    public void removeAllListeners() {
        socket.off("new_message");
        socket.off("typing");
        socket.off("stop_typing");
        socket.off("user_joined");
        socket.off("user_left");
        socket.off("chat_history");
    }
}
```

### 4. Create ChatMessage Model

Create `app/src/main/java/vn/hcmute/baitap09/models/ChatMessage.java`:

```java
package vn.hcmute.baitap09.models;

public class ChatMessage {
    private String id;
    private String senderId;
    private String senderName;
    private String message;
    private long timestamp;
    private String senderType; // "customer" or "manager"
    private boolean isRead;
    
    public ChatMessage(String senderId, String senderName, String message, 
                      long timestamp, String senderType) {
        this.senderId = senderId;
        this.senderName = senderName;
        this.message = message;
        this.timestamp = timestamp;
        this.senderType = senderType;
        this.isRead = false;
    }
    
    // Getters and Setters
    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }
    
    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    
    public String getSenderType() { return senderType; }
    public void setSenderType(String senderType) { this.senderType = senderType; }
    
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
}
```

### 5. Use in Activity

In your `ChatActivity.java`:

```java
public class ChatActivity extends AppCompatActivity {
    private SocketManager socketManager;
    private String userId;
    private String userName;
    private String userType; // "customer" or "manager"
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        
        // Get user info
        userId = getIntent().getStringExtra("userId");
        userName = getIntent().getStringExtra("userName");
        userType = getIntent().getStringExtra("userType");
        
        // Initialize socket
        socketManager = SocketManager.getInstance();
        setupSocketListeners();
        socketManager.connect();
        socketManager.joinChat(userId, userName, userType);
        
        // Setup UI...
    }
    
    private void setupSocketListeners() {
        // Listen for new messages
        socketManager.onNewMessage(args -> {
            runOnUiThread(() -> {
                try {
                    JSONObject data = (JSONObject) args[0];
                    ChatMessage message = new ChatMessage(
                        data.getString("senderId"),
                        data.getString("senderName"),
                        data.getString("message"),
                        data.getLong("timestamp"),
                        data.getString("senderType")
                    );
                    // Update UI with new message
                    Toast.makeText(this, "New message: " + message.getMessage(), 
                                 Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        });
    }
    
    private void sendMessage() {
        String messageText = editText.getText().toString().trim();
        if (!messageText.isEmpty()) {
            socketManager.sendMessage(userId, userName, messageText, userType);
            editText.setText("");
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        socketManager.removeAllListeners();
        socketManager.disconnect();
    }
}
```

---

## 🧪 Testing

### Test 1: Server Running
1. Start server: `node server.js`
2. Open browser: `http://localhost:3000`
3. Should see: "Socket.IO Chat Server is running!"

### Test 2: Android Connection
1. Run Android app
2. Check Logcat for: `Connected to server`
3. Check server console for: `New client connected: [socket-id]`

### Test 3: Send Message
1. Send a message from Android
2. Check Logcat: `Message sent: [your message]`
3. Check server console: `Message received: { message: '...' }`

### Test 4: Two Clients
1. Run app on 2 devices/emulators
2. One as Customer, one as Manager
3. Send messages back and forth
4. Both should receive messages in real-time

---

## 🔧 Configuration for Real Device

If using a real Android device (not emulator):

### 1. Find Your Computer's IP
```bash
# Windows
ipconfig

# Look for: IPv4 Address. . . . . . . . . . . : 192.168.1.xxx
```

### 2. Update SocketManager
```java
private static final String SERVER_URL = "http://192.168.1.xxx:3000";
// Replace xxx with your actual IP
```

### 3. Ensure Same Network
- Computer and phone must be on the same WiFi network
- Disable firewall or allow port 3000

---

## 📊 Socket.IO Events Reference

### Client → Server (Emit)

| Event | Data | Description |
|-------|------|-------------|
| `join` | `{userId, userName, userType}` | Join chat room |
| `send_message` | `{senderId, senderName, message, senderType, timestamp}` | Send message |
| `typing` | `{userId, userName}` | User is typing |
| `stop_typing` | `{userId}` | User stopped typing |
| `mark_read` | `{messageId, userId}` | Mark message as read |
| `get_history` | `{userId}` | Request chat history |

### Server → Client (On)

| Event | Data | Description |
|-------|------|-------------|
| `new_message` | `{id, senderId, senderName, message, senderType, timestamp, isRead}` | New message received |
| `typing` | `{userId, userName}` | Another user is typing |
| `stop_typing` | `{userId}` | Another user stopped typing |
| `user_joined` | `{userId, userName, userType, isOnline}` | User joined chat |
| `user_left` | `{userId, userName}` | User left chat |
| `chat_history` | `[array of messages]` | Chat history |
| `message_read` | `{messageId, userId}` | Message was read |

---

## ❓ Troubleshooting

### Problem: Cannot connect to server

**Solutions:**
1. ✅ Verify server is running (`node server.js`)
2. ✅ Check `SERVER_URL` matches your setup
3. ✅ For emulator use `10.0.2.2`, for device use computer's IP
4. ✅ Check firewall allows port 3000
5. ✅ Verify `INTERNET` permission in AndroidManifest.xml

### Problem: Messages not appearing

**Solutions:**
1. ✅ Check Logcat for errors
2. ✅ Verify event names match on client and server
3. ✅ Ensure UI updates are on `runOnUiThread()`
4. ✅ Check JSON parsing is correct

### Problem: "Port already in use" error

**Solutions:**
```powershell
# Windows PowerShell
Get-Process -Id (Get-NetTCPConnection -LocalPort 3000).OwningProcess | Stop-Process -Force

# Or change port in server.js
const PORT = 3001; // Use different port
```

### Problem: Connection keeps dropping

**Solutions:**
1. ✅ Check network stability
2. ✅ Socket.IO auto-reconnects by default
3. ✅ Add reconnection event handlers
4. ✅ Check server logs for errors

---

## 📚 Additional Resources

- **Full Implementation Guide**: See `SOCKET_IO_IMPLEMENTATION.md` (761 lines)
- **Project README**: See `README.md`
- **Socket.IO Docs**: https://socket.io/docs/
- **Socket.IO Java Client**: https://github.com/socketio/socket.io-client-java

---

## ✨ Summary

### What Was Fixed:
- ✅ **server.js** syntax error (file was backwards)
- ✅ Restructured entire server code properly
- ✅ Server now starts without errors

### What You Have Now:
- ✅ Working Socket.IO server (Node.js)
- ✅ Complete implementation guide (761 lines)
- ✅ Android client code examples
- ✅ Step-by-step instructions
- ✅ Troubleshooting guide

### Next Steps:
1. Start the server: `cd server && node server.js`
2. Add dependencies to Android project
3. Copy SocketManager and ChatMessage classes
4. Implement chat UI in your Activity
5. Test with 2 devices

**Happy Coding! 🚀**

