import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Message {
    private int id;
    private int senderId;
    private int receiverId;
    private String text;
    private String createdAt;


    public Message(int id, int senderId, int receiverId, String text, String createdAt) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.text = text;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public int getSenderId() {
        return senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public String getText() {
        return text;
    }

    public String getCreatedAt() {
        return createdAt;
    }
    public String getSenderUsername() throws SQLException {
        User sender = User.findById(senderId);
        if (sender != null) {
            return sender.getLogin();
        } else {
            return null;
        }
    }

    public static List<Message> getSentMessages(int userId) throws SQLException {
        String query = "SELECT id, sender_id, receiver_id, message_text, created_at FROM message WHERE sender_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            List<Message> messages = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("id");
                int senderId = rs.getInt("sender_id");
                int receiverId = rs.getInt("receiver_id");
                String text = rs.getString("message_text");
                String createdAt = rs.getString("created_at");
                messages.add(new Message(id, senderId, receiverId, text, createdAt
                ));
            }
            return messages;
        }
    }

    public static List<Message> getReceivedMessages(int userId) throws SQLException {
        String query = "SELECT id, sender_id, receiver_id, message_text, created_at FROM message WHERE receiver_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            List<Message> messages = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("id");
                int senderId = rs.getInt("sender_id");
                int receiverId = rs.getInt("receiver_id");
                String text = rs.getString("message_text");
                String createdAt = rs.getString("created_at");
                messages.add(new Message(id, senderId, receiverId, text, createdAt));
            }
            return messages;
        }
    }

    public static void saveMessage(int senderId, int receiverId, String text) throws SQLException {
        String query = "INSERT INTO message (sender_id, receiver_id, message_text, created_at) VALUES (?, ?, ?, NOW())";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, senderId);
            stmt.setInt(2, receiverId);
            stmt.setString(3, text);
            stmt.executeUpdate();
        }
    }
}