import entity.Message;
import entity.User;

import javax.xml.transform.Result;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Trida databazoveho modelu (vrstvy) ktera definuje zpusob ziskavani dat z databaze
 */
public class DatabaseModel {

    Connection conn;

    private static final String TABLE_ROOMS = "rooms";
    private static final String TABLE_MESSAGES = "messages";
    private static final String TABLE_USERS = "users";

    public DatabaseModel(Connection conn)
    {
        this.conn = conn;
    }

    /**
     * Ziskani seznamu mistnosti
     * @return seznam mistnosti
     * @throws SQLException
     */
    public List<String> getRoomsList() throws SQLException
    {
        PreparedStatement stmt = this.conn.prepareStatement("SELECT * FROM " + DatabaseModel.TABLE_ROOMS);

        ResultSet result = stmt.executeQuery();

        ArrayList<String> list = new ArrayList<String>();
        while(result.next())
        {
            list.add(result.getString("name"));
        }

        return list;
    }

    /**
     * Ziskani seznamu uzivatelu se zadanym uziv. jmenem a heslem
     * @param login uzivatelske jmeno
     * @param pass heslo
     * @return seznam uzivatelu
     * @throws SQLException
     */
    public List<User> getUserByLoginAndPass(String login, String pass) throws SQLException
    {
        PreparedStatement stmt = this.conn.prepareStatement("SELECT * FROM " + TABLE_USERS + " WHERE username = ? AND password = ?");

        stmt.setString(1, login);
        stmt.setString(2, pass);

        ResultSet result = stmt.executeQuery();

        ArrayList<User> users = new ArrayList<User>();

        while(result.next())
        {
            int id = result.getInt("id");
            login = result.getString("username");
            pass = result.getString("password");

            users.add(new User(id, login, pass));
        }

        return users;
    }

    /**
     * Ziskani instance uzivatele dle zadaneho identifikatoru
     * @param id identifikator
     * @return entita uzivatele
     * @throws SQLException
     */
    public User getUserById(int id) throws SQLException
    {
        PreparedStatement stmt = this.conn.prepareStatement("SELECT * FROM " + TABLE_USERS + " WHERE id = ?");

        stmt.setInt(1, id);

        ResultSet result = stmt.executeQuery();

        if(!result.next())
        {
            return null;
        }

        id = result.getInt("id");
        String name = result.getString("username");
        String password = result.getString("password");
        return new User(id, name, password);
    }

    /**
     * Ulozeni zpravy v mistnosti
     * @param message entita zpravy
     * @throws SQLException
     */
    public void saveRoomMessage(Message message) throws SQLException
    {
        PreparedStatement stmt;

        if(message.getId() == 0)
        {
            stmt = conn.prepareStatement("INSERT INTO " + TABLE_MESSAGES + " (sender, room, message) VALUES (?, ?, ?)");
            stmt.setInt(1, message.getSender());
            stmt.setString(2, message.getRoom());
            stmt.setString(3, message.getMessage());
        } else {
            stmt = conn.prepareStatement("UPDATE " + TABLE_MESSAGES + " (sender, room, message) VALUES (?, ?, ?) WHERE id = ?");
            stmt.setInt(1, message.getSender());
            stmt.setString(2, message.getRoom());
            stmt.setString(3, message.getMessage());
            stmt.setInt(4, message.getId());
        }

        stmt.executeUpdate();
    }

    /**
     * Ulozeni prime zpravy
     * @param message entita zpravy
     * @throws SQLException
     */
    public void savePrivateMessage(Message message) throws SQLException
    {
        PreparedStatement stmt;

        if(message.getId() == 0)
        {
            stmt = conn.prepareStatement("INSERT INTO " + TABLE_MESSAGES + " (sender, recipient, room, message) VALUES (?, ?, ?, ?)");
            stmt.setInt(1, message.getSender());
            stmt.setString(2, message.getRoom());
            stmt.setInt(3, message.getRecipient());;
            stmt.setString(4, message.getMessage());
        } else {
            stmt = conn.prepareStatement("UPDATE " + TABLE_MESSAGES + " (sender, recipient, room, message) VALUES (?, ?, ?, ?) WHERE id = ?");
            stmt.setInt(1, message.getSender());
            stmt.setString(2, message.getRoom());
            stmt.setInt(3, message.getRecipient());
            stmt.setString(4, message.getMessage());
            stmt.setInt(5, message.getId());
        }

        stmt.executeUpdate();
    }

    /**
     * Ziska soukrome zpravy zadanych uzivatelu (nezalezi na poradi)
     * @param user1 identifikator uzivatele
     * @param user2 identifikator uzivatele
     * @return seznam zprav
     * @throws SQLException
     */
    public List<Message> getMessagesFromPrivateChat(int user1, int user2) throws SQLException
    {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM " + TABLE_MESSAGES + " WHERE room = ? AND ((sender = ? AND recipient = ?) OR (sender = ? AND recipient = ?))");
        stmt.setString(1, "private");
        stmt.setInt(2, user1);
        stmt.setInt(3, user2);
        stmt.setInt(4, user2);
        stmt.setInt(5, user1);

        ResultSet result = stmt.executeQuery();

        List<Message> messages = new ArrayList<>();

        while(result.next())
        {
            int id = result.getInt("id");
            String messageText = result.getString("message");
            int sender = result.getInt("sender");
            Message message = new Message(id, sender, messageText, "private");
            messages.add(message);
        }

        return messages;
    }

    /**
     * Ziskani zprav v mistnosti
     * @param room identifikator mistnosti
     * @return seznam  zprav
     * @throws SQLException
     */
    public List<Message> getMessagesFromRoomChat(String room) throws SQLException
    {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM " + TABLE_MESSAGES + " WHERE room = ?");
        stmt.setString(1, room);

        ResultSet result = stmt.executeQuery();

        List<Message> messages = new ArrayList<>();

        while(result.next())
        {
            int id = result.getInt("id");
            String messageText = result.getString("message");
            int sender = result.getInt("sender");
            room = result.getString("room");
            Message message = new Message(id, sender, messageText, room);
            messages.add(message);
        }

        return messages;
    }
}
