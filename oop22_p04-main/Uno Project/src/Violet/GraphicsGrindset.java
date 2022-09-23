package Violet;

import java.awt.Color;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;

import javax.imageio.plugins.jpeg.JPEGHuffmanTable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import Violet.*;

public class GraphicsGrindset extends JFrame {

    private JButton button;
    private JButton send_button;
    private JButton draw;
    private JTextField text;
    private JTextArea textarea;
    private String message;
    private JLabel label;
    private Deck deck;
    private Box box;
    private PlayerHand playerHand;
    private CardColor top;
    private JPanel top_card_jpanel;
    private ServerThread server;
    private JPanel NorthPanel;
    private JLabel FromServer;

    /**
     * Main constructor, initializes the global variables and adds all the
     * buttons/textfields/graphics to the frame
     * shown on the GUI
     * 
     * @param playerHand (PlayerHand) the player's hand. Consists of all the cards
     *                   they hold
     * @param topcard    (CardColor) the top card of the deck/discard pile.
     */
    public GraphicsGrindset(PlayerHand playerHand, CardColor topcard, ServerThread server) {
        this.server = server;
        // We set the whole screen of the window, and default close operation
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(1000, 800));

        // We set the layout to border layout
        setLayout(new BorderLayout());
        this.top = topcard;

        // Make 3 new buttons, one for readying up
        // One for sending what the user wants to do
        // One for drawing
        button = new JButton("Ready");
        send_button = new JButton("Send");
        draw = new JButton("Draw");

        // We make a new Text field for the user to be able to enter
        text = new JTextField(16);

        // A new text area to store the current points of everyone playing
        textarea = new JTextArea(10, 10);
        // Make it non editable by the user
        textarea.setEditable(false);
        this.playerHand = playerHand;

        // Makes a new place for the cards
        box = new Box(BoxLayout.X_AXIS);

        // Makes a new panel for the top card of the deck
        top_card_jpanel = new JPanel();
        top_card_jpanel.setPreferredSize(new Dimension(125, 75));

        // Lets the user know they should enter the index of the card
        label = new JLabel("Enter the index of the card");
        label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));

        /**
         * Anonymous ActionListener for the Ready button.
         * Sends the keyword to the server to make to signal the player being ready
         * which starts the game
         * Also adds the box to the JFrame.
         */
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == button) {
                    // Send to the server that they are ready to start the game
                    server.newMessage("Ready");
                    setVisible(true);

                    // Not sure if this has any value
                    add(box, BorderLayout.SOUTH);

                }
            }
        });

        /**
         * Anonymous ActionListener for the send button. Takes the user inputted index
         * sends it to the server and resets the text back to an empty string
         */
        send_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == send_button) {
                    // First we get the message in the textfield
                    message = text.getText().trim();
                    // Send the server the message in the textfield
                    server.newMessage(message);
                    // Then reset the text of the textfield
                    text.setText("");
                }
            }
        });

        /**
         * Anonymous ActionListener for the draw card button.
         * Sends the server the keyword for the player to draw a card
         */
        draw.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == draw) {
                    // Tells the server they want to do action 69, or draw
                    server.newMessage("69");
                    setVisible(true);

                }
            }
        });

        // Set the size of button
        button.setPreferredSize(new Dimension(75, 50));

        // We want to put the draw, send, text field, and points area in the same area
        JPanel jp2 = new JPanel();
        jp2.add(text);
        jp2.add(send_button);
        jp2.add(textarea);
        jp2.add(draw);

        // Then we have another label where it lists any message from the server
        FromServer = new JLabel();

        // Then we make the northpanel of the layout and add the elements to it
        NorthPanel = new JPanel(new FlowLayout());
        NorthPanel.add(label);
        NorthPanel.add(FromServer);

        // Then we add everything to the frame
        add(top_card_jpanel, BorderLayout.CENTER);
        add(NorthPanel, BorderLayout.NORTH);
        add(jp2, BorderLayout.WEST);
        add(button, BorderLayout.EAST);

        // Pack it and set it as visible
        pack();
        setVisible(true);

    }

    /**
     * Sets the message to what the server says
     * Could be whose turn it is, saying they made an invalid move, etc.
     * 
     * @param content the message to change to
     */
    public void setFromServer(String content) {
        this.FromServer.setText(content);
        repaint();
    }

    /**
     * Gets the label which the server sends messages to that displays on the screen
     * 
     * @return the label
     */
    public JLabel getFromServer() {
        return this.FromServer;
    }

    /**
     * Returns the ready button
     * 
     * @return the button
     */
    public JButton getReadyButton() {
        return this.button;
    }

    /**
     * Given a CardColor updates the existing top card to
     * the given card
     * Does this by setting the global top card to the new CardColor.
     * Replaces existing components from the top_card JPanel with the new one.
     * 
     * @param c the top card to set to
     */
    public void updateTop(CardColor c) {
        this.top = c;
        // Sets the name of the top card
        String top_name = "Topcard: ".concat(top.toString());
        // Removes the content of the old top card
        this.top_card_jpanel.removeAll();
        // Then rewrites the new content of the top panel
        this.top_card_jpanel.add(new GraphicComponent(top.getColor(), top_name));
        revalidate();
        repaint();
    }

    /**
     * Given a new playerhand after a change has been made via play/draw.
     * Updates the global variable playerHand to the given new hand.
     * Removes the existing box containing cards and creates a new empty box
     * 
     * @param newHand (PlayerHand) the new hand of cards
     */
    public void updateCards(PlayerHand newHand) {
        // First we set the hand to the new hand
        this.playerHand = newHand;

        // Remove the old box from the panel
        // For some reason doing this.box.removeAll() does not work?
        remove(box);

        // Make a new box with the layout
        // A box is used to make the cards have no distance between each other
        this.box = new Box(BoxLayout.LINE_AXIS);

        // Then for every card in the players hand
        for (int i = 0; i < playerHand.getHand().size(); i++) {

            // We make a new jPanel for the card
            JPanel jPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));

            // Then we get the value of the card
            String value = playerHand.getHand().get(i).getColoredCard().name();

            // Make the name of the card with the index
            value = value + "<" + String.valueOf(i) + ">";

            // Then add the card to the jpanel
            jPanel.add(new GraphicComponent(playerHand.getHand().get(i).getColor(), value));

            // Then add the jPanel to the box
            box.add(jPanel);

        }

        // Then set the new box to be visible
        this.box.setVisible(true);

        // And update it
        this.box.revalidate();

        // Add the new box to the frame
        add(box, BorderLayout.SOUTH);
        revalidate();
        repaint();
    }

    /**
     * Gets the top card
     * 
     * @return the top card
     */
    public CardColor getTop() {
        return top;
    }

    /**
     * Sets the top card to a new value
     * 
     * @param top the card to set to
     */
    public void setTop(CardColor top) {
        this.top = top;
    }

    /**
     * updateScores changes the JTextArea to show the current scores.
     * Use this method by sending in the playername and the new score.
     * 
     * @param name  (str) the usernames
     * @param score (int) the scores
     */
    public void updateScores(String[] name, int[] score) {
        // Set the area to be blank
        this.textarea.setText("");

        // For each value in the list
        // The length of both should be the same
        for (int i = 0; i < name.length; i++) {
            // We append to the text area the namwe
            this.textarea.append(name[i] + ": " + String.valueOf(score[i]));
            this.textarea.append("\n");
        }
        // Update the screen
        revalidate();
        repaint();

    }

    /**
     * main function
     * 
     * @param args
     */
    public static void main(String[] args) {

        Deck deck = new Deck();
        /*
         * System.out.println(deck.cards.peek());
         * System.out.println("Successfully peaked");
         */

        // Get the stack of cards
        Stack<CardColor> theDeck = deck.getCards();
        // Randomize the deck
        Collections.shuffle(theDeck);
        CardColor topcard = theDeck.pop();
        PlayerHand hand = new PlayerHand();

        for (int i = 0; i < 5; i++) {
            hand.draw(theDeck.pop());

        }
    }

}
