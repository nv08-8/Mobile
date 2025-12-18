# Socket.IO Chat Server

## Cài đặt

### Bước 1: Cài đặt Node.js
Tải và cài đặt Node.js từ: https://nodejs.org/

### Bước 2: Cài đặt dependencies
```bash
cd server
npm install
```

## Chạy server

### Development mode (với auto-reload)
```bash
npm run dev
```

### Production mode
```bash
npm start
```

Server sẽ chạy trên port 3000 (mặc định)

## API Endpoints

### HTTP
- `GET /` - Health check endpoint

### Socket.IO Events

#### Client → Server (emit)
1. **join** - Join vào chat
   ```json
   {
     "userId": "customer_123",
     "userName": "John Doe",
     "userType": "customer"
   }
   ```

2. **send_message** - Gửi tin nhắn
   ```json
   {
     "senderId": "customer_123",
     "senderName": "John Doe",
     "message": "Hello!",
     "senderType": "customer",
     "timestamp": 1234567890
   }
   ```

3. **typing** - Đang gõ
   ```json
   {
     "userId": "customer_123",
     "userName": "John Doe"
   }
   ```

4. **stop_typing** - Ngừng gõ
   ```json
   {
     "userId": "customer_123"
   }
   ```

5. **mark_read** - Đánh dấu đã đọc
   ```json
   {
     "messageId": "1234567890",
     "userId": "customer_123"
   }
   ```

6. **get_history** - Lấy lịch sử chat
   ```json
   {
     "userId": "customer_123",
     "userType": "customer"
   }
   ```

#### Server → Client (on)
1. **new_message** - Tin nhắn mới
2. **typing** - Ai đó đang gõ
3. **stop_typing** - Ai đó ngừng gõ
4. **user_joined** - Người dùng mới join
5. **user_left** - Người dùng rời đi
6. **chat_history** - Lịch sử chat
7. **message_read** - Tin nhắn đã đọc

## Cấu hình

### Thay đổi port
Trong `server.js`:
```javascript
const PORT = process.env.PORT || 3000;
```

Hoặc set biến môi trường:
```bash
# Windows
set PORT=4000
node server.js

# Linux/Mac
PORT=4000 node server.js
```

### CORS Configuration
Trong `server.js`, bạn có thể giới hạn origins:
```javascript
const io = socketIo(server, {
  cors: {
    origin: "http://localhost:3000", // Chỉ cho phép origin này
    methods: ["GET", "POST"]
  }
});
```

## Testing

### Test với browser
1. Mở http://localhost:3000 trong browser
2. Mở Console (F12)
3. Test kết nối:
```javascript
const socket = io('http://localhost:3000');
socket.on('connect', () => console.log('Connected!'));
socket.emit('join', {userId: 'test1', userName: 'Test User', userType: 'customer'});
```

### Test với Android app
1. Đảm bảo server đang chạy
2. Cập nhật SERVER_URL trong SocketManager.java
3. Build và chạy app

## Logs
Server sẽ log các events:
- Khi client kết nối
- Khi nhận được tin nhắn
- Khi user join/leave
- Số lượng users online

## Production Deployment

### Heroku
```bash
heroku create
git push heroku main
```

### Railway
1. Connect GitHub repo
2. Deploy automatically

### AWS/Azure/Google Cloud
Use PM2 process manager:
```bash
npm install -g pm2
pm2 start server.js
pm2 save
pm2 startup
```

## Troubleshooting

### Port already in use
```bash
# Windows
netstat -ano | findstr :3000
taskkill /PID [PID_NUMBER] /F

# Linux/Mac
lsof -i :3000
kill -9 [PID_NUMBER]
```

### Connection refused từ Android
- Kiểm tra firewall
- Dùng IP thật của máy thay vì localhost
- Cho emulator: dùng 10.0.2.2

