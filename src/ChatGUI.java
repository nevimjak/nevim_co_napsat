import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import javax.swing.*;

public class ChatGUI extends JFrame implements ActionListener {

    private JTabbedPane tabbedPane;
    private JComboBox<String> roomComboBox;
    private HashMap<String, JTextArea> roomChatAreas;
    private HashMap<String, ArrayList<String>> privateMessages;
    private HashMap<String, JTextArea> privateChatAreas;
    private Connection connection;

    public ChatGUI(Connection connection) {
        super("Chat");
        this.connection = connection;
        tabbedPane = new JTabbedPane();
        roomComboBox = new JComboBox<>();
        roomChatAreas = new HashMap<>();
        privateMessages = new HashMap<>();
        privateChatAreas = new HashMap<>();

        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT name FROM rooms");
            while (rs.next()) {
                String roomName = rs.getString("name");
                JTextArea roomChatArea = new JTextArea(10, 40);
                roomChatArea.setEditable(false);
                roomChatAreas.put(roomName, roomChatArea);
                tabbedPane.addTab(roomName, new JScrollPane(roomChatArea));
                roomComboBox.addItem(roomName);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        setLayout(new BorderLayout());
        JPanel northPanel = new JPanel(new BorderLayout());
        add(northPanel, BorderLayout.NORTH);
        northPanel.add(roomComboBox, BorderLayout.WEST);
        northPanel.add(tabbedPane, BorderLayout.CENTER);

        add(new JScrollPane(tabbedPane), BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new BorderLayout());
        add(southPanel, BorderLayout.SOUTH);
        JTextField messageField = new JTextField(40);
        southPanel.add(messageField, BorderLayout.CENTER);
        messageField.addActionListener(this);
        JButton sendButton = new JButton("Send");
        southPanel.add(sendButton, BorderLayout.EAST);
        sendButton.addActionListener(this);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        String message = e.getActionCommand();
        String selectedRoom = (String) roomComboBox.getSelectedItem();
        JTextArea selectedRoomChatArea = roomChatAreas.get(selectedRoom);
        JTextArea selectedPrivateChatArea = privateChatAreas.get(selectedRoom);

        if (selectedPrivateChatArea != null) {
            selectedPrivateChatArea.append("You: " + message + "\n");
            ArrayList<String> privateMessageList = privateMessages.get(selectedRoom);
            if (privateMessageList == null) {
                privateMessageList = new ArrayList<>();
                privateMessages.put(selectedRoom, privateMessageList);
            }
            privateMessageList.add("You: " + message);

            // Save the private message to the database
            try {
                PreparedStatement stmt = connection.prepareStatement(
                        "INSERT INTO messages (sender, recipient, message, room) VALUES (?, ?, ?, ?)"
                );
                // Get the sender and recipient usernames from the users table by their IDs
                int senderID = 1; // Replace with the actual ID of the sender
                int recipientID = 2; // Replace with the actual ID of the recipient
                String senderName = User.getUsernameByID(senderID);
                String recipientName = User.getUsernameByID(recipientID);
                stmt.setString(1, senderName);
                stmt.setString(2, recipientName);
                stmt.setString(3, message);
                stmt.setString(4, "private");
                stmt.executeUpdate();

            } catch (SQLException ex) {
                ex.printStackTrace();

            }
        } else {
            // Room tab is selected
            selectedRoomChatArea.append("You: " + message + "\n");
            try {
                PreparedStatement stmt = connection.prepareStatement(
                        "INSERT INTO messages (sender, message, room) VALUES (?, ?, ?)"
                );
                // Get the sender username from the users table by their ID
                int senderID = 1; // Replace with the actual ID of the sender
                String senderName = User.getUsernameByID(senderID);
                stmt.setString(1, senderName);
                stmt.setString(2, message);
                stmt.setString(3, selectedRoom);
                stmt.executeUpdate();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

    }
}
