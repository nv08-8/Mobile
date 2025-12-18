package vn.hcmute.baitap09;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.UUID;

import vn.hcmute.baitap09.adapters.ChatAdapter;
import vn.hcmute.baitap09.models.ChatMessage;
import vn.hcmute.baitap09.models.User;
import vn.hcmute.baitap09.socket.SocketManager;

/**
 * ManagerChatActivity - Activity cho manager chat với khách hàng
 *
 * Chức năng tương tự ChatActivity nhưng với role là manager
 */
public class ManagerChatActivity extends AppCompatActivity implements SocketManager.SocketListener {

    private RecyclerView messagesRecyclerView;
    private EditText messageInput;
    private ImageButton sendButton;
    private TextView connectionStatus;
    private TextView typingIndicator;
    private TextView chatTitle;

    private ChatAdapter chatAdapter;
    private SocketManager socketManager;
    private User currentUser;

    private boolean isTyping = false;
    private Handler typingHandler = new Handler();
    private Runnable stopTypingRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initViews();
        setupUser();
        setupRecyclerView();
        setupSocketManager();
        setupListeners();
    }

    private void initViews() {
        messagesRecyclerView = findViewById(R.id.messagesRecyclerView);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        connectionStatus = findViewById(R.id.connectionStatus);
        typingIndicator = findViewById(R.id.typingIndicator);
        chatTitle = findViewById(R.id.chatTitle);

        chatTitle.setText("Manager Panel");
    }

    private void setupUser() {
        // Tạo user ID cho manager
        // Trong thực tế, bạn sẽ lấy từ authentication system
        String userId = "manager_" + UUID.randomUUID().toString().substring(0, 8);
        String userName = "Manager"; // hoặc lấy tên thật từ profile

        currentUser = new User(userId, userName, "manager");
    }

    private void setupRecyclerView() {
        chatAdapter = new ChatAdapter(currentUser.getUserId());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);

        messagesRecyclerView.setLayoutManager(layoutManager);
        messagesRecyclerView.setAdapter(chatAdapter);
    }

    private void setupSocketManager() {
        socketManager = SocketManager.getInstance();
        socketManager.setSocketListener(this);
        socketManager.connect();
        socketManager.joinChat(currentUser);
    }

    private void setupListeners() {
        sendButton.setOnClickListener(v -> sendMessage());

        messageInput.setOnEditorActionListener((v, actionId, event) -> {
            sendMessage();
            return true;
        });

        messageInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0 && !isTyping) {
                    isTyping = true;
                    socketManager.sendTyping();
                }

                if (stopTypingRunnable != null) {
                    typingHandler.removeCallbacks(stopTypingRunnable);
                }

                stopTypingRunnable = () -> {
                    isTyping = false;
                    socketManager.sendStopTyping();
                };

                typingHandler.postDelayed(stopTypingRunnable, 1000);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void sendMessage() {
        String message = messageInput.getText().toString().trim();

        if (message.isEmpty()) {
            return;
        }

        socketManager.sendMessage(message);

        ChatMessage chatMessage = new ChatMessage(
            currentUser.getUserId(),
            currentUser.getUserName(),
            message,
            currentUser.getUserType()
        );

        chatAdapter.addMessage(chatMessage);
        messageInput.setText("");
        scrollToBottom();

        if (isTyping) {
            isTyping = false;
            socketManager.sendStopTyping();
        }
    }

    private void scrollToBottom() {
        if (chatAdapter.getItemCount() > 0) {
            messagesRecyclerView.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
        }
    }

    @Override
    public void onConnect() {
        runOnUiThread(() -> {
            connectionStatus.setText("Connected");
            connectionStatus.setTextColor(getResources().getColor(android.R.color.holo_green_light));
            Toast.makeText(this, "Connected to server", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onDisconnect() {
        runOnUiThread(() -> {
            connectionStatus.setText("Disconnected");
            connectionStatus.setTextColor(getResources().getColor(android.R.color.holo_red_light));
            Toast.makeText(this, "Disconnected from server", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onNewMessage(ChatMessage message) {
        runOnUiThread(() -> {
            if (!message.isSentByMe(currentUser.getUserId())) {
                chatAdapter.addMessage(message);
                scrollToBottom();

                // Play notification sound or vibrate for manager
                // You can add notification here
            }
        });
    }

    @Override
    public void onTyping(String userId, String userName) {
        runOnUiThread(() -> {
            if (!userId.equals(currentUser.getUserId())) {
                typingIndicator.setText(userName + " is typing...");
                typingIndicator.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onStopTyping(String userId) {
        runOnUiThread(() -> {
            if (!userId.equals(currentUser.getUserId())) {
                typingIndicator.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onChatHistory(java.util.List<ChatMessage> messages) {
        runOnUiThread(() -> {
            if (messages != null && !messages.isEmpty()) {
                chatAdapter.setMessages(messages);
                scrollToBottom();
                Toast.makeText(this, "Loaded " + messages.size() + " previous messages", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onUserJoined(User user) {
        runOnUiThread(() -> {
            if (user.isCustomer()) {
                Toast.makeText(this, "New customer: " + user.getUserName(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onUserLeft(String userId) {
        runOnUiThread(() -> {
            Toast.makeText(this, "User left the chat", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onError(String error) {
        runOnUiThread(() -> {
            Toast.makeText(this, "Error: " + error, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (typingHandler != null && stopTypingRunnable != null) {
            typingHandler.removeCallbacks(stopTypingRunnable);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isTyping) {
            socketManager.sendStopTyping();
            isTyping = false;
        }
    }
}

