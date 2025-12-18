package vn.hcmute.baitap09.socket;

import android.util.Log;
import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.URISyntaxException;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import vn.hcmute.baitap09.models.ChatMessage;
import vn.hcmute.baitap09.models.User;

public class SocketManager {
    private static final String TAG = "SocketManager";
    private static final String SERVER_URL = "http://10.0.2.2:3000";

    private static SocketManager instance;
    private Socket socket;
    private Gson gson;
    private User currentUser;
    private SocketListener socketListener;

    public interface SocketListener {
        void onConnect();
        void onDisconnect();
        void onNewMessage(ChatMessage message);
        void onChatHistory(java.util.List<ChatMessage> messages);
        void onTyping(String userId, String userName);
        void onStopTyping(String userId);
        void onUserJoined(User user);
        void onUserLeft(String userId);
        void onError(String error);
    }

    private SocketManager() {
        gson = new Gson();
        try {
            IO.Options options = new IO.Options();
            options.forceNew = true;
            options.reconnection = true;
            options.reconnectionAttempts = Integer.MAX_VALUE;
            options.reconnectionDelay = 1000;
            options.reconnectionDelayMax = 5000;
            options.timeout = 20000;

            // Force polling first, then upgrade to websocket
            options.transports = new String[]{"polling", "websocket"};

            Log.d(TAG, "Initializing socket with URL: " + SERVER_URL);
            socket = IO.socket(SERVER_URL, options);
            setupSocketListeners();
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

    private void setupSocketListeners() {
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d(TAG, "Socket connected");
                if (socketListener != null) {
                    socketListener.onConnect();
                }
            }
        });

        socket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d(TAG, "Socket disconnected");
                if (socketListener != null) {
                    socketListener.onDisconnect();
                }
            }
        });

        socket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.e(TAG, "Socket connection error: " + args[0]);
                if (socketListener != null) {
                    socketListener.onError("Connection error: " + args[0]);
                }
            }
        });

        socket.on("new_message", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    JSONObject data = (JSONObject) args[0];
                    ChatMessage message = gson.fromJson(data.toString(), ChatMessage.class);
                    Log.d(TAG, "New message received: " + message.getMessage());
                    if (socketListener != null) {
                        socketListener.onNewMessage(message);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing message", e);
                }
            }
        });

        socket.on("typing", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    JSONObject data = (JSONObject) args[0];
                    String userId = data.getString("userId");
                    String userName = data.getString("userName");
                    if (socketListener != null) {
                        socketListener.onTyping(userId, userName);
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing typing event", e);
                }
            }
        });

        socket.on("stop_typing", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    JSONObject data = (JSONObject) args[0];
                    String userId = data.getString("userId");
                    if (socketListener != null) {
                        socketListener.onStopTyping(userId);
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing stop typing event", e);
                }
            }
        });

        socket.on("user_joined", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    JSONObject data = (JSONObject) args[0];
                    User user = gson.fromJson(data.toString(), User.class);
                    if (socketListener != null) {
                        socketListener.onUserJoined(user);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing user joined event", e);
                }
            }
        });

        socket.on("user_left", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    JSONObject data = (JSONObject) args[0];
                    String userId = data.getString("userId");
                    if (socketListener != null) {
                        socketListener.onUserLeft(userId);
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing user left event", e);
                }
            }
        });

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
    }

    public void connect() {
        if (socket != null && !socket.connected()) {
            socket.connect();
            Log.d(TAG, "Connecting to server...");
        }
    }

    public void disconnect() {
        if (socket != null && socket.connected()) {
            socket.disconnect();
            Log.d(TAG, "Disconnecting from server...");
        }
    }

    public void joinChat(User user) {
        this.currentUser = user;
        try {
            JSONObject data = new JSONObject();
            data.put("userId", user.getUserId());
            data.put("userName", user.getUserName());
            data.put("userType", user.getUserType());
            socket.emit("join", data);
            Log.d(TAG, "User joined: " + user.getUserName());
        } catch (JSONException e) {
            Log.e(TAG, "Error joining chat", e);
        }
    }

    public void sendMessage(String message) {
        if (currentUser == null) {
            Log.e(TAG, "Cannot send message: user not set");
            return;
        }
        try {
            ChatMessage chatMessage = new ChatMessage(
                currentUser.getUserId(),
                currentUser.getUserName(),
                message,
                currentUser.getUserType()
            );
            JSONObject data = new JSONObject(gson.toJson(chatMessage));
            socket.emit("send_message", data);
            Log.d(TAG, "Message sent: " + message);
        } catch (JSONException e) {
            Log.e(TAG, "Error sending message", e);
        }
    }

    public void sendTyping() {
        if (currentUser == null) return;
        try {
            JSONObject data = new JSONObject();
            data.put("userId", currentUser.getUserId());
            data.put("userName", currentUser.getUserName());
            socket.emit("typing", data);
        } catch (JSONException e) {
            Log.e(TAG, "Error sending typing event", e);
        }
    }

    public void sendStopTyping() {
        if (currentUser == null) return;
        try {
            JSONObject data = new JSONObject();
            data.put("userId", currentUser.getUserId());
            socket.emit("stop_typing", data);
        } catch (JSONException e) {
            Log.e(TAG, "Error sending stop typing event", e);
        }
    }

    public void setSocketListener(SocketListener listener) {
        this.socketListener = listener;
    }

    public boolean isConnected() {
        return socket != null && socket.connected();
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public Socket getSocket() {
        return socket;
    }
}

