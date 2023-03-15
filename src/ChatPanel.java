import entity.Message;
import entity.User;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Trida grafickeho rozhrani pro chatove mistnosti
 */
public class ChatPanel extends JPanel implements ActionListener {

    private JTabbedPane tabbedPane;
    private JComboBox<String> roomComboBox;
    private HashMap<String, JTextArea> roomChatAreas;
    private HashMap<String, ArrayList<String>> privateMessages;
    private HashMap<String, JTextArea> privateChatAreas;
    private JTextField messageField;

    private HashMap<Integer, String> usernameCache;

    /** Instance okna rodice */
    Window parent;
    /** Instance entity aktualne prihlaseneho uzivatele*/
    User loggedUser;

    public ChatPanel(Window parent, User loggedUser) {
        this.parent = parent;
        this.loggedUser = loggedUser;
        tabbedPane = new JTabbedPane();
        roomComboBox = new JComboBox<>();
        roomChatAreas = new HashMap<>();
        privateMessages = new HashMap<>();
        privateChatAreas = new HashMap<>();
        this.usernameCache = new HashMap<>();

        this.usernameCache.put(this.loggedUser.getId(), this.loggedUser.getUsername());

        List<String> rooms;
        try {
            rooms = Main.getDatabaseModel().getRoomsList();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return;
        }

        /*
        Definice layoutu panelu
         */

        for(String room : rooms)
        {
            JTextArea roomChatArea = new JTextArea(10, 40);
            roomChatArea.setEditable(false);
            roomChatAreas.put(room, roomChatArea);
            tabbedPane.addTab(room, new JScrollPane(roomChatArea));
            roomComboBox.addItem(room);
        }

        setLayout(new BorderLayout());
        JPanel northPanel = new JPanel(new BorderLayout());
        add(northPanel, BorderLayout.NORTH);
        northPanel.add(roomComboBox, BorderLayout.WEST);
        northPanel.add(tabbedPane, BorderLayout.CENTER);

        add(new JScrollPane(tabbedPane), BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new BorderLayout());
        add(southPanel, BorderLayout.SOUTH);
        messageField = new JTextField(40);
        southPanel.add(messageField, BorderLayout.CENTER);
        messageField.addActionListener(this);

        JButton selectbutton = new JButton("Refresh");
        southPanel.add(selectbutton, BorderLayout.EAST);

        setSize(400, 400);
        setVisible(true);


        // Akce refresh tlacitka
        selectbutton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                String room = (String) roomComboBox.getSelectedItem();
                JTextArea selectedRoomChatArea = roomChatAreas.get(room);
                try{
                    // Ziskani zprav z DB
                    List<Message> messages = Main.getDatabaseModel().getMessagesFromRoomChat(room);

                    selectedRoomChatArea.setText("");

                    // Vypsani vsech zprav v refreshovanem chatu
                    for(Message message : messages)
                    {
                        // Ziskani uzivatelskeho jmena autora zpravy
                        String username = getUsernameById(message.getSender());

                        selectedRoomChatArea.append(username + ": " + message.getMessage() + "\n");
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });
    }

    /**
     * Obsluzna metoda pro zpracovani odeslane  zpravy
     * @param e event akce
     */
    public void actionPerformed(ActionEvent e) {
        String messageText = e.getActionCommand();
        String selectedRoom = (String) roomComboBox.getSelectedItem();
        JTextArea selectedRoomChatArea = roomChatAreas.get(selectedRoom);
        JTextArea selectedPrivateChatArea = privateChatAreas.get(selectedRoom);
        if(messageText.length() == 0)
        {
            return;
        }

        if (selectedPrivateChatArea != null) {
            selectedPrivateChatArea.append(this.loggedUser.getUsername() + ": " + messageText + "\n");
            ArrayList<String> privateMessageList = privateMessages.get(selectedRoom);
            if (privateMessageList == null) {
                privateMessageList = new ArrayList<>();
                privateMessages.put(selectedRoom, privateMessageList);
            }
            privateMessageList.add(this.loggedUser.getUsername() + ": " + messageText);

            try {
                int recipientID = 2;
                User recipient = Main.getDatabaseModel().getUserById(recipientID);
                String recipientName = recipient.getUsername();
                Message message = new Message(0, this.loggedUser.getId(), recipientID, messageText);
                Main.getDatabaseModel().savePrivateMessage(message);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

        } else {
            selectedRoomChatArea.append(this.loggedUser.getUsername() + ": " + messageText + "\n");
            try {
                Message message = new Message(0, this.loggedUser.getId(), messageText, selectedRoom);
                Main.getDatabaseModel().saveRoomMessage(message);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        this.messageField.setText("");
    }

    /**
     * Metoda ziska uzivatelske jmeno na zaklade identifikatoru uzivatele.
     * Nejprve je prohledana cache (HashMapa), zda jiz v pameti jmeno neexistuje.
     * Pripadne se nacte z databaze
     * @param id
     * @return
     */
    private String getUsernameById(int id)
    {
        String username;
        if(usernameCache.containsKey(id))
        {
            username = usernameCache.get(id);
        } else {
            User u = null;
            try {
                u = Main.getDatabaseModel().getUserById(id);
            } catch (SQLException e) {
                return "Unknown";
            }
            if(u != null) {
                username = u.getUsername();
                this.usernameCache.put(id, username);
            } else {
                username = "Unknown";
            }
        }

        return username;
    }
}
