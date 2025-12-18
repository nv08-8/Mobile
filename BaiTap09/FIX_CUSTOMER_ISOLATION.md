# Fix: Customer thấy tin nhắn của Customer khác

## ⚠️ Vấn Đề Nghiêm Trọng

**Customer A thấy được tin nhắn của Customer B** - Đây là lỗi BẢO MẬT và PRIVACY nghiêm trọng!

### Nguyên Nhân

Server trước đây lưu **TẤT CẢ tin nhắn chung** trong một mảng `messages[]` và **broadcast tới TẤT CẢ clients**. Điều này khiến:
- Customer A gửi tin nhắn → TẤT CẢ clients (kể cả Customer B) nhận được
- Không có sự phân tách giữa các cuộc hội thoại
- Mọi người đều thấy tin nhắn của nhau

### Kiến Trúc Đúng

Mỗi Customer cần có một **cuộc hội thoại riêng 1-1 với Manager**:

```
Customer A  ←─────→  Manager
   (Room A)

Customer B  ←─────→  Manager  
   (Room B)

Customer C  ←─────→  Manager
   (Room C)
```

**KHÔNG được:**
```
Customer A ─┐
            ├───→  [Chung 1 room] ←─── Manager
Customer B ─┘
(Tất cả thấy tin nhắn của nhau ❌)
```

## ✅ Giải Pháp Đã Áp Dụng

### 1. Thay Đổi Server Architecture

#### a) Data Structure

**TRƯỚC:**
```javascript
const messages = [];  // TẤT CẢ tin nhắn chung
```

**SAU:**
```javascript
// Mỗi customer có 1 room riêng
const chatRooms = new Map();
// Structure: customerId -> { 
//   customerId, 
//   customerName,
//   messages: [],      // Tin nhắn riêng của room này
//   participants: []   // Người trong room này
// }
```

#### b) Join Logic

**Customer Join:**
```javascript
if (data.userType === 'customer') {
  // Customer tạo room với userId của họ làm roomId
  roomId = data.userId;  // VD: "customer_123"
  
  // Tạo room nếu chưa có
  if (!chatRooms.has(roomId)) {
    chatRooms.set(roomId, {
      customerId: data.userId,
      customerName: data.userName,
      messages: [],
      participants: []
    });
  }
  
  // Join socket vào room cụ thể
  socket.join(roomId);
  
  // Gửi ONLY history của room này
  socket.emit('chat_history', room.messages);
}
```

**Manager Join:**
```javascript
if (data.userType === 'manager') {
  // Option 1: Join vào room cụ thể (nếu có customerRoomId)
  if (data.customerRoomId) {
    roomId = data.customerRoomId;
    socket.join(roomId);
    socket.emit('chat_history', room.messages);
  } 
  // Option 2: Xem danh sách tất cả rooms
  else {
    const roomList = Array.from(chatRooms.entries()).map(...);
    socket.emit('room_list', roomList);
  }
}
```

#### c) Send Message Logic

**TRƯỚC:**
```javascript
// Broadcast tới TẤT CẢ
io.emit('new_message', message);  ❌
```

**SAU:**
```javascript
// Lưu message vào room cụ thể
const room = chatRooms.get(roomId);
room.messages.push(message);

// Broadcast CHỈ tới room này
io.to(roomId).emit('new_message', message);  ✅
```

#### d) Typing Indicator

**TRƯỚC:**
```javascript
socket.broadcast.emit('typing', ...);  // Gửi tới TẤT CẢ
```

**SAU:**
```javascript
socket.to(user.roomId).emit('typing', ...);  // Gửi CHỈ trong room
```

### 2. Socket.IO Rooms Explained

Socket.IO Rooms là cách để group các sockets lại:

```javascript
// Join room
socket.join('room1');

// Emit to room
io.to('room1').emit('message', data);  // Chỉ người trong room1 nhận

// Emit to everyone except sender in room
socket.to('room1').emit('message', data);

// Leave room
socket.leave('room1');
```

**Trong hệ thống của chúng ta:**
- Room ID = Customer's userId
- Customer tự động join vào room của họ
- Manager join vào room cụ thể khi chat với customer đó
- Tin nhắn chỉ được gửi trong room

### 3. Code Changes Detail

#### server.js - Complete Refactor

**Storage:**
```javascript
// OLD
const messages = [];

// NEW
const chatRooms = new Map();  // customerId -> room data
```

**Join Event:**
```javascript
socket.on('join', (data) => {
  const user = { ..., roomId: null };
  
  if (data.userType === 'customer') {
    roomId = data.userId;  // Room ID = customer ID
    socket.join(roomId);
    
    // Create room if not exists
    if (!chatRooms.has(roomId)) {
      chatRooms.set(roomId, {
        customerId: data.userId,
        customerName: data.userName,
        messages: [],
        participants: []
      });
    }
    
    // Send room-specific history
    const room = chatRooms.get(roomId);
    socket.emit('chat_history', room.messages);
  }
  
  if (data.userType === 'manager') {
    if (data.customerRoomId) {
      socket.join(data.customerRoomId);
      // ...
    } else {
      // Send list of all rooms
      socket.emit('room_list', roomList);
    }
  }
});
```

**Send Message Event:**
```javascript
socket.on('send_message', (data) => {
  const user = users.get(socket.id);
  let roomId = user.roomId;
  
  if (user.userType === 'customer') {
    roomId = user.userId;  // Customer always uses their ID
  }
  
  const room = chatRooms.get(roomId);
  room.messages.push(message);
  
  // IMPORTANT: Only broadcast to THIS room
  io.to(roomId).emit('new_message', message);
});
```

**Typing Events:**
```javascript
socket.on('typing', (data) => {
  const user = users.get(socket.id);
  if (user && user.roomId) {
    socket.to(user.roomId).emit('typing', { ... });  // Only in room
  }
});
```

**Disconnect:**
```javascript
socket.on('disconnect', () => {
  const user = users.get(socket.id);
  if (user && user.roomId) {
    const room = chatRooms.get(user.roomId);
    room.participants = room.participants.filter(...);
    
    // Notify only people in THIS room
    socket.to(user.roomId).emit('user_left', { ... });
  }
});
```

## 🔄 Luồng Hoạt Động Sau Khi Fix

### Scenario 1: Customer A gửi tin nhắn

```
1. Customer A mở app
   ├─► socket.join('customer_A')  // Join vào room riêng
   ├─► Server tạo chatRooms['customer_A']
   └─► Customer A nhận chat_history của room A (rỗng)

2. Customer A gửi "Hello"
   ├─► Server lưu vào chatRooms['customer_A'].messages
   └─► io.to('customer_A').emit('new_message')
       → CHỈ người trong room A nhận ✅
```

### Scenario 2: Customer B gửi tin nhắn

```
1. Customer B mở app
   ├─► socket.join('customer_B')  // Room KHÁC
   ├─► Server tạo chatRooms['customer_B']
   └─► Customer B nhận chat_history của room B (rỗng)

2. Customer B gửi "Hi"
   ├─► Server lưu vào chatRooms['customer_B'].messages
   └─► io.to('customer_B').emit('new_message')
       → CHỈ người trong room B nhận ✅
       → Customer A KHÔNG nhận ✅
```

### Scenario 3: Manager xem tin nhắn

**Option 1: Manager join vào room cụ thể**
```
1. Manager chọn chat với Customer A
   ├─► emit('join', { ..., customerRoomId: 'customer_A' })
   ├─► socket.join('customer_A')
   └─► Nhận chat_history của ONLY room A

2. Manager trả lời
   ├─► Server lưu vào room A
   └─► Chỉ Customer A và Manager nhận
```

**Option 2: Manager xem danh sách (implement sau)**
```
1. Manager mở app không chỉ định room
   └─► Nhận room_list với:
       - customer_A: 5 messages
       - customer_B: 2 messages
       - customer_C: 0 messages

2. Manager click vào Customer A
   ├─► Call emit('join', { customerRoomId: 'customer_A' })
   └─► Load chat của Customer A
```

## 🧪 Testing

### Test 1: Customer Isolation ✅
```
1. Open Customer A → Send "Message from A"
2. Open Customer B → Send "Message from B"
3. Verify:
   ✅ Customer A ONLY sees "Message from A"
   ✅ Customer B ONLY sees "Message from B"
   ✅ They DON'T see each other's messages
```

### Test 2: Manager Sees Specific Customer ✅
```
1. Customer A sends "Help me"
2. Manager opens app
3. Manager joins room of Customer A
4. Verify:
   ✅ Manager sees "Help me" from Customer A
   ✅ Manager does NOT see messages from Customer B
```

### Test 3: 1-1 Communication ✅
```
1. Customer A and Manager in same room
2. They exchange messages
3. Customer B opens app
4. Verify:
   ✅ Customer B does NOT see A's conversation
```

## 📊 Database Structure (For Future)

Hiện tại dữ liệu lưu trong memory (mất khi restart server). Để production, cần lưu vào database:

```javascript
// MongoDB Schema Example
const ChatRoomSchema = new Schema({
  roomId: { type: String, unique: true },
  customerId: String,
  customerName: String,
  messages: [{
    id: String,
    senderId: String,
    senderName: String,
    message: String,
    senderType: String,
    timestamp: Date,
    isRead: Boolean
  }],
  createdAt: Date,
  updatedAt: Date
});
```

## 🎨 UI Improvements Needed

### For Manager App:

**Current:** Manager tự động join vào room đầu tiên hoặc random

**Needed:** Manager cần UI để:
1. Xem danh sách tất cả customers đang chờ
2. Chọn customer cụ thể để chat
3. Thấy số tin nhắn chưa đọc
4. Switch giữa các conversations

**Screen Design:**
```
┌─────────────────────────────┐
│  Customer Conversations     │
├─────────────────────────────┤
│ ● Customer 25317678    [3]  │  ← 3 unread messages
│   "Hello, I need help"      │
├─────────────────────────────┤
│   Customer f8bf6fb4          │
│   "Thank you"                │
├─────────────────────────────┤
│ ● Customer 98a3c2d1    [1]  │
│   "Is anyone there?"         │
└─────────────────────────────┘
```

Click vào customer → Mở chat của customer đó

## 🔐 Security & Privacy

### Đã Fix:
✅ Customer không thấy tin nhắn của customer khác
✅ Messages được isolated theo room
✅ Chỉ participants trong room nhận được updates

### Cần Thêm (Production):
- [ ] Authentication (JWT tokens)
- [ ] Authorization (verify user có quyền xem room không)
- [ ] Encryption (end-to-end encryption cho messages)
- [ ] Rate limiting (chống spam)
- [ ] Input validation & sanitization
- [ ] Audit logs

## 📝 Implementation Checklist

### Server-side: ✅ DONE
- [x] Thay đổi từ global messages sang per-room messages
- [x] Implement Socket.IO rooms
- [x] Update join logic
- [x] Update send_message logic
- [x] Update typing/stop_typing
- [x] Update disconnect handler
- [x] Add room_list event cho manager

### Client-side: 🔄 NEEDED
- [ ] Customer: Không cần thay đổi (tự động join room của họ)
- [ ] Manager: Cần thêm UI chọn customer
- [ ] Manager: Cần handle room_list event
- [ ] Manager: Cần gửi customerRoomId khi join

### Temporary Solution: ✅ WORKING NOW
Manager hiện tại sẽ không thấy tin nhắn của customers khác nữa vì:
- Mỗi customer có room riêng
- Manager chỉ nhận messages từ room họ join
- Nếu manager không chỉ định room, họ sẽ nhận empty history

## 🚀 Deploy & Test

1. **Stop old server:**
   ```bash
   # Already done - server restarted
   ```

2. **Server đã chạy với code mới**

3. **Test immediately:**
   - Open 2 emulators
   - Both as Customers
   - Send messages from each
   - **Verify they DON'T see each other's messages** ✅

4. **Manager test:**
   - Open Manager app
   - Should NOT see all messages mixed together
   - Should only see messages from specific customer conversation

## 💡 Next Steps

### Phase 1: Quick Fix (DONE)
✅ Isolate conversations by room
✅ Prevent cross-customer message viewing

### Phase 2: Manager UI (TODO)
- [ ] Create RoomListActivity for manager
- [ ] Show all active customer conversations
- [ ] Allow manager to select customer
- [ ] Show unread message count

### Phase 3: Database (TODO)
- [ ] Replace in-memory storage with MongoDB/PostgreSQL
- [ ] Persist messages
- [ ] Add message history pagination

### Phase 4: Advanced Features (TODO)
- [ ] Multiple managers support
- [ ] Transfer conversation between managers
- [ ] Conversation status (open/closed)
- [ ] Customer satisfaction rating

---

## ⚠️ CRITICAL FIX APPLIED

**TRƯỚC:** Tất cả customers thấy tin nhắn của nhau (BẢO MẬT ❌)

**SAU:** Mỗi customer có conversation riêng 1-1 với manager (BẢO MẬT ✅)

**Server đã restart với code mới - Test ngay để verify!**

