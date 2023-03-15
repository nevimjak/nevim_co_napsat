import entity.User;

import java.util.List;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

/**
 * Uzivatelske rozhrani pro prihlaseni
 */
public class LoginPanel extends JPanel implements ActionListener {

    JLabel userLabel = new JLabel("Uživatelské jméno:");
    JLabel passLabel = new JLabel("Heslo:");
    JTextField userText = new JTextField(17);
    JPasswordField passText = new JPasswordField(17);
    JButton loginButton = new JButton("Přihlásit se");

    Window parent;

    public LoginPanel(Window parent) {

        this.parent = parent;

        add(userLabel);
        add(userText);
        add(passLabel);
        add(passText);
        add(loginButton);

        loginButton.addActionListener(this);
    }

    /**
     * Obsluha udalosti prihlaseni
     * @param ae the event to be processed
     */
    public void actionPerformed(ActionEvent ae) {
        String username = userText.getText();
        String password = new String(passText.getPassword());

        List<User> users;
        try {
            users = Main.getDatabaseModel().getUserByLoginAndPass(username, password);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Vyskytla se chyba", "Chyba", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if(users == null)
        {
            JOptionPane.showMessageDialog(this, "Vyskytla se chyba", "Chyba", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if(users.size() == 0)
        {
            JOptionPane.showMessageDialog(this, "Nesprávné jméno nebo heslo", "Chyba v přihlášení", JOptionPane.WARNING_MESSAGE);
        }

        this.parent.setPanel(new ChatPanel(this.parent, users.get(0)));

    }
}
