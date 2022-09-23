package Violet;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;
import java.awt.Color;
import java.awt.Font;

import static java.lang.Thread.sleep;

public class ServerThread implements Runnable {
    private Socket client;
    private String name;
    private final LinkedList<String> message;
    private GraphicsGrindset GUI;
    private PlayerHand hand;
    private CardColor topCard;

    /**
     * Main constructor, initializes the global variables.
     * 
     * @param client
     * @param name
     */
    ServerThread(Socket client, String name) {
        this.client = client;
        this.name = name;

        // Makes the user hand for the client
        hand = new PlayerHand();

        // Makes a new message linked list for messages to send to the server
        message = new LinkedList<String>();

        // Makes a new GUI for the user
        GUI = new GraphicsGrindset(hand, topCard, this);
        GUI.setTitle(name);
        GUI.setVisible(true);
    }

    /**
     * If the user enters something, this primes it to get sent to the server
     * 
     * @param new_message the message to be sent
     */
    public void newMessage(String new_message) {
        synchronized (message) {
            // Adds message to the front of the linked list
            message.push(new_message);
        }
    }

    /**
     * Checks the message from the server to decipher it
     * 
     * @param message the message to check
     */
    public void checkMessage(String message) {
        // First we split the message by spaces
        String[] input = message.split(" ");

        // Set the default card color
        Color cardColor = Color.BLACK;

        // If the first token is to process the hand
        if (input[0].equals("ProcessHand")) {
            // Then we make a new player hand
            hand = new PlayerHand();

            // After which we go through everything in the list and add it to the hand
            // correctly
            for (int i = 1; i < input.length - 1; i++) {
                // First we get the color of the card
                switch (input[i]) {
                    case "Green":
                        cardColor = Color.GREEN;
                        break;
                    case "Colorless":
                        cardColor = Color.BLACK;
                        break;
                    case "Red":
                        cardColor = Color.RED;
                        break;
                    case "Blue":
                        cardColor = Color.BLUE;
                        break;
                    case "Yellow":
                        cardColor = Color.YELLOW;
                        break;
                }
                // We increment to the next element as it should be the value of the card
                i++;

                // Then we find what value it is
                for (CardTypes t : CardTypes.values()) {
                    if (t.toString().equals(input[i])) {
                        // Then add it to the hand
                        hand.draw(new CardColor(cardColor, t));
                    }
                }

            }

            // Then we update the cards to reflect what is currently the players hand
            GUI.updateCards(hand);

        }
        // If not the previous then we check if it's the top card
        else if (input[0].equals("ThisTopCard")) {
            // Similar to process above, we find the color of the card
            switch (input[1]) {
                case "Green":
                    cardColor = Color.GREEN;
                    break;
                case "Colorless":
                    cardColor = Color.BLACK;
                    break;
                case "Red":
                    cardColor = Color.RED;
                    break;
                case "Blue":
                    cardColor = Color.BLUE;
                    break;
                case "Yellow":
                    cardColor = Color.YELLOW;
                    break;
            }

            // Then find the value of the card
            for (CardTypes t : CardTypes.values()) {
                if (t.toString().equals(input[2])) {

                    // Set the top card to the top card
                    topCard = new CardColor(cardColor, t);
                }
            }

            // Update the GUI to reflect the new top card
            GUI.updateTop(topCard);
        }
        // If not the previous, then we check if the server has anything to say about
        // what the user did, or to update the player turn
        else if (input[0].equals("ImportantFromServer")) {
            String append = "";

            // If so, then we take everything else in the message and combine it again
            for (String apply : input) {
                if (!apply.equals("ImportantFromServer")) {
                    append = append.concat(apply + " ");
                }
            }

            // Then we update the label on the GUI that is messages from the server
            GUI.setFromServer(append);
            // Change to color to red, as its an imporant message
            GUI.getFromServer().setForeground(Color.RED);
            GUI.getFromServer().setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        }
        // Otherwise we find if a players has won the game
        else if (input[0].equals("PlayerHasWon")) {
            String append = "";

            //If so, we want to change the string to say who has won round
            for (String apply : input) {
                if (!apply.equals("PlayerHasWon")) {
                    append = append.concat(apply + " ");
                }
            }
            //Set the message from the server
            GUI.setFromServer(append);
            GUI.getFromServer().setForeground(Color.GREEN);
            GUI.getFromServer().setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));

        } 
        //Then this is to change the points
        else if (input[0].equals("ScoreSetting")) {
            //First we parse the number of players that are in the game
            int players = Integer.parseInt(input[1]);

            //Then make a dynamic array to accomidate for the name of the player and their score
            String[] names = new String[players];
            int[] points = new int[players];


            for (int i = 2, j = 0; i < input.length - 1; i++, j++) {
                names[j] = input[i];
                i++;
                points[j] = Integer.parseInt(input[i]);
            }

            //Then we update the scores
            GUI.updateScores(names, points);
        }

    }

    @Override
    public void run() {
        try {
            //First we make the stream for which the input and output are put through
            PrintWriter out_stream = new PrintWriter(client.getOutputStream(), false);
            InputStream in_stream = client.getInputStream();
            Reader otherIn = new InputStreamReader(in_stream);
            Scanner in = new Scanner(in_stream);

            out_stream.write(name + "\r\n");
            out_stream.flush();

            //While the client has not closed the connection
            while (!client.isClosed()) {

                //We then check if the input stream has anything inside
                String messageToParse = "";
                int ascii;
                while (otherIn.ready()) {

                    //Then we read character by character
                    //This is done as otherwise messages will not be read properly
                    if ((ascii = otherIn.read()) != -1) {
                        char ch = (char) ascii;
                        if (ascii == '\n') {
                            break;
                        }

                        //Then we make a message with the character
                        messageToParse += ch;
                    }

                }

                //After words, we then send the message to be checked if it is an important message
                checkMessage(messageToParse);

                // Send messages to socket server
                synchronized (message) {
                    if (!message.isEmpty()) {
                        String next = null;
                        next = message.pop();

                        out_stream.write(next + "\r\n");
                        out_stream.flush();

                    }
                }

            }

        } catch (IOException e) {
        }
    }

}
