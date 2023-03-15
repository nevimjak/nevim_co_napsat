import javax.swing.*;
import java.awt.*;

/**
 * Trida pro vytvoreni okna GUI
 */
public class Window extends JFrame {

    public Window()
    {
        super("Chat");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setPreferredSize(new Dimension(350, 200));
        this.setLocation(500, 300);
        this.setVisible(true);
        this.setPanel(new LoginPanel(this));
    }

    /**
     * Nastavi zobrazovany panel v okne
     * @param panel panel pro zobrazeni
     */
    public void setPanel(JPanel panel)
    {
        this.getContentPane().removeAll();
        this.getContentPane().add(panel);
        this.pack();
        this.setVisible(true);
    }
}
