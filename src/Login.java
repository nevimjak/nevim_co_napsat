import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

public class Login extends JFrame implements ActionListener {

    JLabel userLabel = new JLabel("Uživatelské jméno:");
    JLabel passLabel = new JLabel("Heslo:");
    JTextField userText = new JTextField(20);
    JPasswordField passText = new JPasswordField(20);
    JButton loginButton = new JButton("Přihlásit se");

    public Login() {
        setTitle("Přihlášení");
        setSize(400, 150);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel loginPanel = new JPanel(new GridLayout(3, 1));
        loginPanel.add(userLabel);
        loginPanel.add(userText);
        loginPanel.add(passLabel);
        loginPanel.add(passText);
        loginPanel.add(loginButton);

        loginButton.addActionListener(this);

        add(loginPanel, BorderLayout.CENTER);
    }

    public void actionPerformed(ActionEvent ae) {
        String username = userText.getText();
        String password = new String(passText.getPassword());

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pst = conn.prepareStatement("SELECT * FROM users WHERE username=? AND password=?");
            pst.setString(1, username);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(null, "Přihlášeno");
                this.dispose();
                ChatGUI chat = new ChatGUI(conn);
            } else {
                JOptionPane.showMessageDialog(null, "Neplatné jméno nebo heslo!");
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Nelze se připojit do databáze: " + ex.getMessage());
        }

    }

    public static void main(String[] args) {
        Login login = new Login();
        login.setVisible(true);
    }
}
