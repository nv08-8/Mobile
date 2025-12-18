package vn.hcmute.baitap09.models;

public class ChatMessage {
    private String id;
    private String senderId;
    private String senderName;
    private String message;
    private long timestamp;
    private String senderType;
    private boolean isRead;

    public ChatMessage() {
        this.timestamp = System.currentTimeMillis();
        this.isRead = false;
    }

    public ChatMessage(String senderId, String senderName, String message, String senderType) {
        this.senderId = senderId;
        this.senderName = senderName;
        this.message = message;
        this.senderType = senderType;
        this.timestamp = System.currentTimeMillis();
        this.isRead = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getSenderType() {
        return senderType;
    }

    public void setSenderType(String senderType) {
        this.senderType = senderType;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public boolean isSentByMe(String currentUserId) {
        return senderId != null && senderId.equals(currentUserId);
    }
}

