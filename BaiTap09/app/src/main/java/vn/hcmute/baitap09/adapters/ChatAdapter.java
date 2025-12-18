package vn.hcmute.baitap09.adapters;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import vn.hcmute.baitap09.R;
import vn.hcmute.baitap09.models.ChatMessage;

/**
 * Adapter cho RecyclerView hiển thị danh sách tin nhắn
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {

    private List<ChatMessage> messages;
    private String currentUserId;
    private SimpleDateFormat timeFormat;

    public ChatAdapter(String currentUserId) {
        this.messages = new ArrayList<>();
        this.currentUserId = currentUserId;
        this.timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    /**
     * Thêm tin nhắn mới vào danh sách
     */
    public void addMessage(ChatMessage message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    /**
     * Thêm nhiều tin nhắn (dùng cho load history)
     */
    public void addMessages(List<ChatMessage> newMessages) {
        int startPosition = messages.size();
        messages.addAll(newMessages);
        notifyItemRangeInserted(startPosition, newMessages.size());
    }

    /**
     * Set toàn bộ danh sách tin nhắn (dùng cho load history)
     */
    public void setMessages(List<ChatMessage> newMessages) {
        messages.clear();
        messages.addAll(newMessages);
        notifyDataSetChanged();
    }

    /**
     * Xóa tất cả tin nhắn
     */
    public void clearMessages() {
        messages.clear();
        notifyDataSetChanged();
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout messageContainer;
        private TextView messageText;
        private TextView senderName;
        private TextView timestamp;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageContainer = itemView.findViewById(R.id.messageContainer);
            messageText = itemView.findViewById(R.id.messageText);
            senderName = itemView.findViewById(R.id.senderName);
            timestamp = itemView.findViewById(R.id.timestamp);
        }

        public void bind(ChatMessage message) {
            messageText.setText(message.getMessage());
            senderName.setText(message.getSenderName());
            timestamp.setText(timeFormat.format(new Date(message.getTimestamp())));

            // Căn chỉnh tin nhắn: bên phải cho tin nhắn của mình, bên trái cho tin nhắn của người khác
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) messageContainer.getLayoutParams();

            if (message.isSentByMe(currentUserId)) {
                // Tin nhắn của mình - bên phải, màu xanh
                params.gravity = Gravity.END;
                messageContainer.setBackgroundResource(R.drawable.bg_message_sent);
                senderName.setVisibility(View.GONE);
            } else {
                // Tin nhắn người khác - bên trái, màu xám
                params.gravity = Gravity.START;
                messageContainer.setBackgroundResource(R.drawable.bg_message_received);
                senderName.setVisibility(View.VISIBLE);
            }

            messageContainer.setLayoutParams(params);
        }
    }
}

