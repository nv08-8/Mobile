# KIẾN TRÚC VÀ LUỒNG HOẠT ĐỘNG

## 1. KIẾN TRÚC TỔNG QUAN

```
┌─────────────────────────────────────────────────────────────────┐
│                        ANDROID APP                               │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌──────────────┐         ┌──────────────┐                     │
│  │ MainActivity │         │              │                     │
│  │              │         │  Models:     │                     │
│  │ - Customer   │         │  - User      │                     │
│  │ - Manager    │         │  - ChatMsg   │                     │
│  └──────┬───────┘         └──────────────┘                     │
│         │                                                        │
│    ┌────┴────┐                                                 │
│    │         │                                                 │
│    ▼         ▼                                                 │
│  ┌─────┐  ┌─────┐         ┌──────────────┐                   │
│  │Chat │  │Mgr  │◄───────►│SocketManager │ (Singleton)       │
│  │Act  │  │Chat │         │              │                   │
│  └──┬──┘  └──┬──┘         └──────┬───────┘                   │
│     │        │                    │                            │
│     │        │            ┌───────┴────────┐                  │
│     └────────┴───────────►│  ChatAdapter   │                  │
│                            │  (RecyclerView)│                  │
│                            └────────────────┘                  │
└─────────────────────────────────┬───────────────────────────────┘
                                  │
                                  │ Socket.IO
                                  │ (WebSocket)
                                  │
┌─────────────────────────────────▼───────────────────────────────┐
│                        NODE.JS SERVER                            │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌──────────────┐         ┌──────────────┐                     │
│  │   Express    │         │  Socket.IO   │                     │
│  │   Server     │         │   Server     │                     │
│  └──────────────┘         └──────┬───────┘                     │
│                                   │                              │
│                          ┌────────┴─────────┐                   │
│                          │                  │                   │
│                    ┌─────▼────┐      ┌─────▼────┐              │
│                    │  Users   │      │ Messages │              │
│                    │  Map     │      │  Array   │              │
│                    └──────────┘      └──────────┘              │
└─────────────────────────────────────────────────────────────────┘
```

## 2. LUỒNG DỮ LIỆU - GỬI TIN NHẮN

```
Customer App                Server                Manager App
     │                        │                        │
     │  1. sendMessage()      │                        │
     ├───────────────────────►│                        │
     │   emit("send_message") │                        │
     │                        │                        │
     │                        │ 2. Store message       │
     │                        │    messages.push()     │
     │                        │                        │
     │                        │ 3. Broadcast           │
     │                        │    io.emit()           │
     │                        │                        │
     │  4. Render locally     │                        │
     │     addMessage()       │                        │
     │                        │                        │
     │                        ├───────────────────────►│
     │                        │  on("new_message")     │
     │                        │                        │
     │                        │                  5. Display
     │                        │                     addMessage()
     │                        │                     scrollToBottom()
     │                        │                        │
```

## 3. LUỒNG TYPING INDICATOR

```
Customer                    Server                  Manager
    │                         │                        │
    │ 1. User gõ phím         │                        │
    │    TextWatcher          │                        │
    │         │               │                        │
    │    sendTyping()         │                        │
    ├────────────────────────►│                        │
    │  emit("typing")         │                        │
    │                         │                        │
    │                         │ 2. Broadcast           │
    │                         ├───────────────────────►│
    │                         │  broadcast.emit()      │
    │                         │                        │
    │                         │                  3. Show indicator
    │                         │                     "Customer is
    │                         │                      typing..."
    │                         │                        │
    │ 4. Stop typing (1s)     │                        │
    │    Handler timeout      │                        │
    │         │               │                        │
    │  sendStopTyping()       │                        │
    ├────────────────────────►│                        │
    │  emit("stop_typing")    │                        │
    │                         │                        │
    │                         │ 5. Broadcast           │
    │                         ├───────────────────────►│
    │                         │                        │
    │                         │                  6. Hide indicator
    │                         │                        │
```

## 4. LIFECYCLE - KỊCH BẢN HOÀN CHỈNH

```
┌─────────────────────────────────────────────────────────────┐
│ PHASE 1: APP START                                          │
└─────────────────────────────────────────────────────────────┘

MainActivity
    │
    ├─► onCreate()
    │     └─► Show 2 buttons (Customer/Manager)
    │
    ├─► Click "Customer" button
    │     └─► startActivity(ChatActivity)
    │
    └─► ChatActivity.onCreate()
          ├─► setupUser()
          │     └─► Generate unique userId
          │
          ├─► SocketManager.getInstance()
          │     ├─► Create Socket with IO.socket()
          │     ├─► Setup options (reconnection, timeout)
          │     └─► setupSocketListeners()
          │
          ├─► socketManager.connect()
          │     └─► socket.connect()
          │
          └─► socketManager.joinChat(user)
                └─► emit("join", userData)

┌─────────────────────────────────────────────────────────────┐
│ PHASE 2: CONNECTED                                          │
└─────────────────────────────────────────────────────────────┘

Socket.IO
    │
    ├─► EVENT_CONNECT fired
    │     └─► onConnect()
    │           ├─► Update UI "Connected"
    │           └─► Change color to green
    │
    └─► Server receives "join"
          ├─► Store user in Map
          ├─► broadcast.emit("user_joined")
          └─► emit("chat_history") to new user

┌─────────────────────────────────────────────────────────────┐
│ PHASE 3: CHATTING                                           │
└─────────────────────────────────────────────────────────────┘

User types → TextWatcher
    ├─► onTextChanged()
    │     ├─► Check if typing == false
    │     ├─► Set typing = true
    │     ├─► sendTyping()
    │     └─► Schedule stopTypingRunnable (1s)
    │
    └─► User stops typing (timeout)
          └─► stopTypingRunnable executes
                ├─► Set typing = false
                └─► sendStopTyping()

User clicks Send → sendMessage()
    ├─► Create ChatMessage object
    ├─► socketManager.sendMessage(text)
    │     └─► emit("send_message", jsonData)
    │
    ├─► Add to local adapter immediately
    │     └─► chatAdapter.addMessage()
    │
    └─► Clear input field

Server receives message
    ├─► Store in messages array
    └─► io.emit("new_message") to ALL clients

All clients receive "new_message"
    ├─► Parse JSON to ChatMessage
    ├─► Check if not own message
    ├─► runOnUiThread()
    │     ├─► chatAdapter.addMessage()
    │     └─► scrollToBottom()
    └─► Optional: Play sound/vibrate

┌─────────────────────────────────────────────────────────────┐
│ PHASE 4: DISCONNECT                                         │
└─────────────────────────────────────────────────────────────┘

User closes app
    │
    ├─► onDestroy()
    │     └─► (Optional) socketManager.disconnect()
    │
    └─► Server detects disconnect
          ├─► Remove user from Map
          └─► broadcast.emit("user_left")
```

## 5. CLASS DIAGRAM

```
┌─────────────────────────┐
│   SocketManager         │ (Singleton)
├─────────────────────────┤
│ - socket: Socket        │
│ - currentUser: User     │
│ - gson: Gson            │
│ - socketListener        │
├─────────────────────────┤
│ + getInstance()         │
│ + connect()             │
│ + disconnect()          │
│ + joinChat(User)        │
│ + sendMessage(String)   │
│ + sendTyping()          │
│ + sendStopTyping()      │
└───────────┬─────────────┘
            │ uses
            ▼
┌─────────────────────────┐       ┌─────────────────────────┐
│     ChatMessage         │       │        User             │
├─────────────────────────┤       ├─────────────────────────┤
│ - id: String            │       │ - userId: String        │
│ - senderId: String      │       │ - userName: String      │
│ - senderName: String    │       │ - userType: String      │
│ - message: String       │       │ - isOnline: boolean     │
│ - timestamp: long       │       ├─────────────────────────┤
│ - senderType: String    │       │ + isManager()           │
│ - isRead: boolean       │       │ + isCustomer()          │
├─────────────────────────┤       └─────────────────────────┘
│ + isSentByMe(String)    │
│ + getters/setters       │
└─────────────────────────┘

            │ uses
            ▼
┌─────────────────────────┐
│      ChatAdapter        │
├─────────────────────────┤
│ - messages: List        │
│ - currentUserId         │
├─────────────────────────┤
│ + addMessage()          │
│ + addMessages()         │
│ + clearMessages()       │
└───────────┬─────────────┘
            │ contains
            ▼
┌─────────────────────────┐
│   MessageViewHolder     │
├─────────────────────────┤
│ - messageText           │
│ - senderName            │
│ - timestamp             │
├─────────────────────────┤
│ + bind(ChatMessage)     │
└─────────────────────────┘

┌─────────────────────────┐       ┌─────────────────────────┐
│    ChatActivity         │       │ ManagerChatActivity     │
├─────────────────────────┤       ├─────────────────────────┤
│ - socketManager         │       │ - socketManager         │
│ - chatAdapter           │       │ - chatAdapter           │
│ - currentUser           │       │ - currentUser           │
├─────────────────────────┤       ├─────────────────────────┤
│ + onCreate()            │       │ + onCreate()            │
│ + sendMessage()         │       │ + sendMessage()         │
│ + onNewMessage()        │       │ + onNewMessage()        │
│ + onTyping()            │       │ + onTyping()            │
└─────────────────────────┘       └─────────────────────────┘
      │ implements                       │ implements
      └──────────┬─────────────────────┘
                 ▼
┌─────────────────────────────────────────┐
│   SocketManager.SocketListener          │
├─────────────────────────────────────────┤
│ + onConnect()                           │
│ + onDisconnect()                        │
│ + onNewMessage(ChatMessage)             │
│ + onTyping(String userId, String name)  │
│ + onStopTyping(String userId)           │
│ + onUserJoined(User)                    │
│ + onUserLeft(String userId)             │
│ + onError(String error)                 │
└─────────────────────────────────────────┘
```

## 6. EVENT FLOW DIAGRAM

```
Socket.IO Events Flow:

┌──────────────┐
│   CLIENT A   │
│  (Customer)  │
└──────┬───────┘
       │
       │ emit("join")
       ├──────────────────┐
       │                  │
       │ emit("typing")   │
       ├──────────────────┤
       │                  │         ┌────────────┐
       │ emit("send_msg") ├────────►│   SERVER   │
       ├──────────────────┤         │            │
       │                  │         │ - Store    │
       │◄─────────────────┤         │ - Process  │
       │ on("new_msg")    │         │ - Broadcast│
       │                  │         └──────┬─────┘
       │◄─────────────────┤                │
       │ on("typing")     │                │
       │                  │                │
       │◄─────────────────┤                │
       │ on("user_joined")│                │
       └──────────────────┘                │
                                           │
┌──────────────┐                           │
│   CLIENT B   │◄──────────────────────────┘
│  (Manager)   │
└──────┬───────┘
       │
       │ emit("join")
       ├──────────────────┐
       │                  │
       │ emit("send_msg") │
       ├──────────────────┤
       │                  │
       │◄─────────────────┤
       │ on("new_msg")    │
       │                  │
       │◄─────────────────┤
       │ on("typing")     │
       └──────────────────┘
```

## 7. THREADING MODEL

```
┌─────────────────────────────────────────────┐
│              MAIN THREAD                    │
│              (UI Thread)                    │
├─────────────────────────────────────────────┤
│                                             │
│  - onCreate()                               │
│  - setupViews()                             │
│  - onClick()                                │
│  - updateUI()                               │
│  - chatAdapter operations                   │
│                                             │
└────────────────┬────────────────────────────┘
                 │
                 │ Post to
                 ▼
┌─────────────────────────────────────────────┐
│          BACKGROUND THREAD                  │
│          (Socket.IO Thread)                 │
├─────────────────────────────────────────────┤
│                                             │
│  - Socket connection                        │
│  - Emit events                              │
│  - Receive events                           │
│  - JSON parsing                             │
│                                             │
└────────────────┬────────────────────────────┘
                 │
                 │ runOnUiThread()
                 ▼
┌─────────────────────────────────────────────┐
│         BACK TO MAIN THREAD                 │
├─────────────────────────────────────────────┤
│                                             │
│  runOnUiThread(() -> {                      │
│      chatAdapter.addMessage(message);       │
│      scrollToBottom();                      │
│      updateTypingIndicator();               │
│  });                                        │
│                                             │
└─────────────────────────────────────────────┘
```

---

**Giải thích:**
- Main Thread: Xử lý UI, không được block
- Socket.IO Thread: Xử lý network operations
- runOnUiThread(): Bridge giữa 2 threads để update UI an toàn

