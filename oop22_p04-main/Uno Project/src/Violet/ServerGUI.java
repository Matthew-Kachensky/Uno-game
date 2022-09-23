package Violet;

import javax.swing.*;
import java.awt.*;

public class ServerGUI extends JFrame {
    private Server server;
    private JList<String> users;
    private String[] usernames;
    private DefaultListModel<String> model;

    public ServerGUI() {
        //Make a new list
        model = new DefaultListModel<>();
        model.addElement("Users");
        users = new JList<>(model);
        //Makes the list that shows the currently connected users
        users.setVisibleRowCount(50);
        users.setPreferredSize(new Dimension(500, 500));

        add(users);
        pack();
    }

    /**
     * Updates the list to add the user to the field
     * 
     * @param name the name of the person to add
     */
    public void addUser(String name) {
        model.addElement(name);
    }

    /**
     * Updates the list to remove the user from the field
     * 
     * @param name the name of the person to remove
     */
    public void removeUser(String name) {
        model.removeElement(name);
    }

    public String[] getUsernames() {
        return this.usernames;
    }

}
