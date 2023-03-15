package entity;

/**
 * Entita (Prepravka) pro zpravu
 */
public class Message {

    private int id;
    private int sender;
    private int recipient;
    private String room;
    private String time;
    private String message;

    public Message(int id, int sender, int recipient, String message)
    {
        this(id, sender, message, "private");
        this.recipient = recipient;
    }

    public Message(int id, int sender, String message, String room)
    {
        this.id = id;
        this.sender = sender;
        this.message = message;
        this.room = room;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSender() {
        return sender;
    }

    public void setSender(int sender) {
        this.sender = sender;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getRecipient() {
        return recipient;
    }

    public void setRecipient(int recipient) {
        this.recipient = recipient;
    }
}
