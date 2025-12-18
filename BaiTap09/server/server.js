const express = require('express');
const http = require('http');
const socketIo = require('socket.io');

const app = express();
const server = http.createServer(app);
const io = socketIo(server, {
  cors: {
    origin: "*",
    methods: ["GET", "POST"]
  }
});

const PORT = process.env.PORT || 3000;

// Store connected users: socketId -> user info
const users = new Map();

// Store chat rooms: customerId -> { messages: [], participants: [] }
// Mỗi customer có 1 room riêng để chat với manager
const chatRooms = new Map();

// Middleware
app.use(express.json());

// Basic route
app.get('/', (req, res) => {
  res.send('Socket.IO Chat Server is running!');
});

// Socket.IO connection handling
io.on('connection', (socket) => {
  console.log('New client connected:', socket.id);

  // User joins chat
  socket.on('join', (data) => {
    console.log('User joined:', data);

    const user = {
      socketId: socket.id,
      userId: data.userId,
      userName: data.userName,
      userType: data.userType,
      joinedAt: new Date(),
      roomId: null
    };

    users.set(socket.id, user);

    // Xác định room ID:
    // - Customer: dùng userId của chính họ làm roomId
    // - Manager: join vào room của customer đang chat (có thể mở rộng sau)
    let roomId;

    if (data.userType === 'customer') {
      // Customer tạo room riêng với userId của họ
      roomId = data.userId;
      user.roomId = roomId;

      // Tạo room nếu chưa có
      if (!chatRooms.has(roomId)) {
        chatRooms.set(roomId, {
          customerId: data.userId,
          customerName: data.userName,
          messages: [],
          participants: []
        });
      }

      // Join socket vào room
      socket.join(roomId);

      // Add participant
      const room = chatRooms.get(roomId);
      room.participants.push({
        userId: data.userId,
        userName: data.userName,
        userType: data.userType,
        socketId: socket.id
      });

      console.log(`Customer ${data.userName} joined room: ${roomId}`);

      // Gửi history của room này cho customer
      socket.emit('chat_history', room.messages);

    } else if (data.userType === 'manager') {
      // Manager: Nếu có customerRoomId được gửi kèm, join vào room đó
      // Nếu không, join vào tất cả rooms để xem tổng quan
      if (data.customerRoomId) {
        roomId = data.customerRoomId;
        user.roomId = roomId;
        socket.join(roomId);

        const room = chatRooms.get(roomId);
        if (room) {
          room.participants.push({
            userId: data.userId,
            userName: data.userName,
            userType: data.userType,
            socketId: socket.id
          });

          console.log(`Manager ${data.userName} joined room: ${roomId}`);

          // Gửi history của room này cho manager
          socket.emit('chat_history', room.messages);

          // Notify customer in room that manager joined
          socket.to(roomId).emit('user_joined', {
            userId: data.userId,
            userName: data.userName,
            userType: data.userType,
            isOnline: true
          });
        }
      } else {
        // Manager chưa chọn room cụ thể - có thể mở rộng với danh sách rooms
        console.log(`Manager ${data.userName} connected - waiting for room selection`);

        // Gửi danh sách các rooms hiện có
        const roomList = Array.from(chatRooms.entries()).map(([roomId, room]) => ({
          roomId: roomId,
          customerId: room.customerId,
          customerName: room.customerName,
          lastMessage: room.messages.length > 0 ? room.messages[room.messages.length - 1] : null,
          messageCount: room.messages.length
        }));

        socket.emit('room_list', roomList);
      }
    }

    // Log current users count
    console.log(`Total users online: ${users.size}`);
  });

  // Handle new message
  socket.on('send_message', (data) => {
    console.log('Message received:', data);

    const user = users.get(socket.id);
    if (!user) {
      console.error('User not found for socket:', socket.id);
      return;
    }

    // Xác định roomId từ user hoặc từ data
    let roomId = user.roomId || data.roomId;

    // Nếu là customer, roomId chính là userId của họ
    if (user.userType === 'customer') {
      roomId = user.userId;
    }

    if (!roomId) {
      console.error('No room ID found for message');
      return;
    }

    const message = {
      id: Date.now().toString(),
      senderId: data.senderId,
      senderName: data.senderName,
      message: data.message,
      senderType: data.senderType,
      timestamp: data.timestamp || Date.now(),
      isRead: false,
      roomId: roomId
    };

    // Lưu message vào room cụ thể
    const room = chatRooms.get(roomId);
    if (room) {
      room.messages.push(message);

      // Keep only last 100 messages per room
      if (room.messages.length > 100) {
        room.messages.shift();
      }

      console.log(`Message saved to room ${roomId}, total: ${room.messages.length}`);
    } else {
      console.error(`Room ${roomId} not found`);
      return;
    }

    // Broadcast CHỈ tới room này (không broadcast toàn server)
    io.to(roomId).emit('new_message', message);

    console.log(`Message broadcast to room: ${roomId}`);
  });

  // Handle typing indicator
  socket.on('typing', (data) => {
    const user = users.get(socket.id);
    if (user && user.roomId) {
      console.log('User typing:', data.userName, 'in room:', user.roomId);
      socket.to(user.roomId).emit('typing', {
        userId: data.userId,
        userName: data.userName
      });
    }
  });

  // Handle stop typing
  socket.on('stop_typing', (data) => {
    const user = users.get(socket.id);
    if (user && user.roomId) {
      console.log('User stopped typing:', data.userId, 'in room:', user.roomId);
      socket.to(user.roomId).emit('stop_typing', {
        userId: data.userId
      });
    }
  });

  // Handle mark as read
  socket.on('mark_read', (data) => {
    const user = users.get(socket.id);
    if (!user || !user.roomId) return;

    const room = chatRooms.get(user.roomId);
    if (!room) return;

    console.log('Message marked as read:', data.messageId);
    const message = room.messages.find(m => m.id === data.messageId);
    if (message) {
      message.isRead = true;
      io.to(user.roomId).emit('message_read', {
        messageId: data.messageId,
        userId: data.userId
      });
    }
  });

  // Handle get chat history
  socket.on('get_history', (data) => {
    const user = users.get(socket.id);
    if (!user) return;

    let roomId = user.roomId || data.roomId;
    if (user.userType === 'customer') {
      roomId = user.userId;
    }

    console.log('History requested by:', data.userId, 'for room:', roomId);

    if (roomId) {
      const room = chatRooms.get(roomId);
      if (room) {
        socket.emit('chat_history', room.messages);
      } else {
        socket.emit('chat_history', []);
      }
    }
  });

  // Handle disconnect
  socket.on('disconnect', () => {
    console.log('Client disconnected:', socket.id);

    const user = users.get(socket.id);
    if (user) {
      // Remove user from room participants
      if (user.roomId) {
        const room = chatRooms.get(user.roomId);
        if (room) {
          room.participants = room.participants.filter(p => p.socketId !== socket.id);

          // Notify others in the room about user leaving
          socket.to(user.roomId).emit('user_left', {
            userId: user.userId,
            userName: user.userName
          });

          console.log(`User ${user.userName} left room: ${user.roomId}`);
        }
      }

      users.delete(socket.id);
      console.log(`Total users online: ${users.size}`);
    }
  });

  // Handle errors
  socket.on('error', (error) => {
    console.error('Socket error:', error);
  });
});

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

// Graceful shutdown
process.on('SIGTERM', () => {
  console.log('SIGTERM signal received: closing HTTP server');
  server.close(() => {
    console.log('HTTP server closed');
  });
});
