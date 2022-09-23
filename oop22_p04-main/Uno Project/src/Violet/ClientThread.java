package Violet;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.awt.Color;

public class ClientThread implements Runnable {
    // All of the variables that are used in this file
    private Server server;
    private Socket client;
    private PrintWriter out;
    private String clientName;
    private PlayerHand hand;
    private final LinkedList<String> message;
    private final LinkedList<String> inputMessages;
    private Table table;

    /**
     * Constructor for a new client thread
     * this is for when the server accepts a new connection to a client
     * 
     * @param server the server of the connection
     * @param client the client that connected to the server
     * @param table  the table in which the game is to be played
     */
    ClientThread(Server server, Socket client, Table table) {
        this.server = server;
        this.client = client;
        clientName = null;

        // Make a new hand for the player
        hand = new PlayerHand();

        // Make a new outgoing message linked list
        message = new LinkedList<>();

        // Make a new incoming message linked list
        inputMessages = new LinkedList<>();
        this.table = table;

    }

    /**
     * Get the hand of the player
     * 
     * @return the hand
     */
    public PlayerHand getHand() {
        return this.hand;
    }

    /**
     * I think unused
     * 
     * @return the out stream of the current client
     */
    public PrintWriter getWriter() {
        return out;
    }

    /**
     * Gets the clients name
     * 
     * @return the name of client
     */
    public String getClientName() {
        return this.clientName;
    }

    /**
     * Used to say the server is sending a message to this client
     * 
     * @param new_message the message to send to the client
     */
    public void messageFromServer(String new_message) {

        // Synchronized to make sure that its updated and recognizes that there is new
        // info
        synchronized (message) {
            // Just sends to another function which formats it
            sendClient(new_message);
        }
    }

    /**
     * Sends a message to the client
     * 
     * @param new_message the message to send
     */
    public void sendClient(String new_message) {
        // the server will print out the message its sending
        System.out.println("Sending message: " + new_message);

        // then write it to the out stream in the correct format
        out.write(new_message + "\r\n");
        out.flush();
    }

    /**
     * Used to determine what the user wants to play
     * 
     * @return the new value of the top card
     */
    public CardColor play() {

        // The boolean is essentially useless, but I couldn't compile without
        boolean done = false;

        // Get the current hand and top card
        PlayerHand player = this.getHand();
        CardColor topCard = table.getTop();

        while (!done) {
            // Set the userinput to null everytime we enter the loop
            String userin = null;

            // Tell the user to enter a card to play
            sendClient(player.toString() + player.getHand().size() + ": Draw");
            sendClient("Enter a card to play: ");

            // while the user has not entered anything
            while (userin == null) {

                // We make sure to update the inputMessages in case the user has sent something
                synchronized (inputMessages) {
                    // If the user has inputted something then we pop it and move on
                    if (!inputMessages.isEmpty()) {

                        userin = inputMessages.pop();
                    }
                }
            }

            // We try to parse the input as an integer as the input should be an index
            try {
                int choice = Integer.parseInt(userin);

                // If the user did input an integer, we check to make sure its within the bounds
                // of the array
                if (choice >= 0 && choice < player.getHand().size()) {

                    // If it is, then we check the card they want to play
                    CardColor pickedCard = player.getHand().get(choice);
                    System.out.println("Playing card: " + pickedCard.toString());

                    // We check if the move is valid with the current top card through the function
                    if (validMove(topCard, pickedCard)) {
                        // If the move is valid, then we play the card
                        player.play(pickedCard);
                        // return the new top card
                        return new CardColor(iswildCard(pickedCard), pickedCard.getColoredCard());
                    }

                    // If the move is not a valid move, ex wrong color and number
                    else {
                        sendClient("ImportantFromServer invalid card ");
                    }

                }
                // Check if the move entered was 69, heh nice, then the player wants to draw
                else if (choice == 69) {
                    // So we take the top card of the deck and put it in the players hand
                    player.draw(table.getDeck().pop());
                    // The top card remains the same, so we just return the current top card
                    return topCard;
                }
                // If the card number is greater than or less than their hand, tell them to
                // enter a valid index
                else {
                    sendClient("ImportantFromServer invalid card index ");
                }

            }
            // If they don't enter a number, we tell them it's not a number
            catch (NumberFormatException n) {
                sendClient("ImportantFromServer input is not a number ");
            }
        }
        // This should never happen if it does something went wrong
        return null;
    }


    

    /**
     * Checks if a card is a wild card
     * 
     * @param card the card to check
     * @return the color to change to
     */
    public Color iswildCard(CardColor card) {
        // Check if the color is a wild card, as its color will be black
        if (card.getColor().equals(Color.BLACK)) {
            // Get the users input
            sendClient("ImportantFromServer Enter the color you want to change to ");
            while (true) {
                String userin = null;
                while (userin == null) {
                    synchronized (inputMessages) {
                        if (!inputMessages.isEmpty()) {
                            userin = inputMessages.pop();
                        }
                    }
                }

                // Based on what they input we check it

                // Loop until a valid choice is entered
                switch (userin.toUpperCase()) {
                    case "RED":
                        return Color.RED;
                    case "BLUE":
                        return Color.BLUE;
                    case "YELLOW":
                        return Color.YELLOW;
                    case "GREEN":
                        return Color.GREEN;
                    default:
                        sendClient("ImportantFromServer Please enter a valid color ");
                }
            }
        }

        // If it is not a wild card just return the color of the card
        return card.getColor();
    }

    /**
     * Checks if a move is valid
     * 
     * @param topCard the current top card
     * @param toPlay  the card the player is trying to play
     * @return true if the move can be played, false if it cannot
     */
    public static boolean validMove(CardColor topCard, CardColor toPlay) {
        // If it's the same card
        if (topCard.equals(toPlay)) {
            return true;
        }
        // If the colors are the same
        else if (topCard.getColor().equals(toPlay.getColor())) {
            return true;
        }
        // If the numbers are the same
        else if (topCard.getColoredCard().getNumber() == toPlay.getColoredCard().getNumber()) {
            return true;
        }
        // If its a wild card
        else if (toPlay.getColor().equals(Color.BLACK)) {
            return true;
        }
        // If none of the above, return false
        return false;
    }

    /**
     * What is ran when the thread is started
     */
    @Override
    public void run() {
        try {
            // Make new input and output streams
            this.out = new PrintWriter(client.getOutputStream(), false);
            Scanner in = new Scanner(client.getInputStream());
            InputStream otherIn = client.getInputStream();

            // Get the name of the user as its the first thing that is sent
            String input = in.nextLine();

            // Just in case we make sure the name is null
            if (clientName == null) {
                // If it is, then we change the name to the input
                clientName = input;
                // And we add it to the server list
                server.addUser(clientName);

            }

            // As longs as the client has not closed
            while (!client.isClosed()) {

                try {
                    // if the input stream has content available
                    if (otherIn.available() > 0) {
                        // if it has a next line
                        if (in.hasNextLine()) {
                            // If it does we take the next line
                            String message = in.nextLine();
                            // Push it to the incomming messages
                            inputMessages.push(message);

                            // Then we send to the table, that the client sent a message
                            // I think this line is useless?
                            table.messageFromAClient(this, message);

                            // Check if the message is ready, if it is just pop it
                            if (message.equals("Ready")) {
                                inputMessages.pop();
                            }

                        }

                    }

                    // If there is message to be send to the client
                    if (!message.isEmpty()) {
                        String next = null;

                        // We update it, and pop the message
                        synchronized (message) {
                            next = message.pop();
                        }
                        // Then send it to the client
                        sendClient(next);
                    }

                    // Everything after this point is to remove the user
                    // I think it only works after a game has concluded
                } catch (NoSuchElementException n) {
                    server.removeUser(clientName);
                }

            }
            server.removeUser(clientName);
        } catch (IOException e) {
            server.removeUser(clientName);
        }
    }

}
