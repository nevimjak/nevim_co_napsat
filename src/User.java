import java.sql.*;

public class User {
    private int id;
    private String login;
    private String password;

    public User(int id, String login, String password) {
        this.id = id;
        this.login = login;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public static User authenticate(String login, String password) throws SQLException {
        String sql = "SELECT * FROM user WHERE login = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, login);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                String storedUsername = rs.getString("login");
                String storedPassword = rs.getString("password");
                return new User(id, storedUsername, storedPassword);
            } else {
                return null;
            }
        }
    }

    public static User findByUsername(String login) throws SQLException {
        String sql = "SELECT * FROM user WHERE login = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, login);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                String storedUsername = rs.getString("login");
                String storedPassword = rs.getString("password");
                return new User(id, storedUsername, storedPassword);
            } else {
                return null;
            }
        }
    }

    public static User findById(int id) throws SQLException {
        String sql = "SELECT * FROM user WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String username = rs.getString("username");
                String password = rs.getString("password");
                return new User(id, username, password);
            } else {
                return null;
            }
        }
    }

    public static String getUsernameByID(int id) throws SQLException {
        String sql = "SELECT username FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("username");
            } else {
                return null;
            }
        }
    }


}
