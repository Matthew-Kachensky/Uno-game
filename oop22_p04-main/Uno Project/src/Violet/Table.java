package Violet;

import javax.swing.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;
import java.awt.Color;

/**
 * Table is a serverthread that handles the server-side logic of the game.
 */
public class Table implements Runnable {
    //The corresponding server for the table
    private Server server;

    //The list of messages the table recieves 
    private final LinkedList<String> messages;

    //The rest is global variables for the game logic
    private ArrayList<PlayerHand> hands;
    private CardColor topCard;
    private Stack<CardColor> Deck;
    private int playerTurn = 0;
    private int turnOrder = 1;
    private int toDraw = 0;
    private boolean skip = false;

    /**
     * Initializes the global variables and game elements such as deck/discard pile.
     * 
     * @param server
     */
    Table(Server server) {
        this.server = server;
        this.hands = new ArrayList<>();

        Deck = new Deck().getCards();
        Collections.shuffle(Deck);
        setTop(Deck.pop());
        checkTop();
        messages = new LinkedList<>();

    }

    /**
     * getter for the player hands.
     * 
     * @return ArrayList<PlayerHand>
     */
    public ArrayList<PlayerHand> getHands() {
        return hands;
    }

    /**
     * When the game should be started
     */
    public void startGame() {

    }

    /**
     * Handles whether or not hte player has to draw cards. Checks what the top card
     * is
     * and if is it a drawtwo/reverse/wildfour/skip. If it is, then it does the
     * designated card action.
     * 
     * @param c ClientThread aka the player
     */
    public void getNextCard(ClientThread c) {
        //First we grab the input of the client whose turn it is
        if (topCard.equals(topCard = c.play())) {
        } else {
            //Then we check the special card cases, wild is already checked
            if (topCard.getColoredCard() == CardTypes.SKIP) {
                //if its a skip, then we just increase the turn order
                playerTurn = playerTurn + turnOrder;
            } else if (topCard.getColoredCard() == CardTypes.DRAWTWO) {
                //If its a draw 2, we add 2 to the next player
                toDraw += 2;

            } else if (topCard.getColoredCard() == CardTypes.REVERSE) {
                //If it's a reverse, we swap the rotation
                turnOrder = turnOrder * -1;
            } else if (topCard.getColoredCard() == CardTypes.WILDFOUR) {
                //If its a wild plus four we add four, the color change is handled in isWild()
                toDraw += 4;
            } else if (toDraw > 0) {
                //Otherwise we force the palyer to draw if they did not play a draw 2 or wild 4
                for (int i = 0; i < toDraw; i++) {
                    c.getHand().draw(Deck.pop());
                    toDraw = 0;
                }
            }
        }
    }


    /**
     * Checks if the player has to draw
     * @param c the current player
     * @return if they drew or not
     */
    public boolean checkDraw(ClientThread c) {
        synchronized (c.getHand()) {
            //Check if they even have to draw
            if (toDraw > 0) {
                //Then check if the top card is a draw 2
                if (topCard.getColoredCard().equals(CardTypes.DRAWTWO)) {
                    //If it is, then the player can play a draw 2 or wild + 4 if they have one
                    if(!c.getHand().contains(CardTypes.DRAWTWO) && !c.getHand().contains(CardTypes.WILDFOUR)){
                        //If they don't we make them force draw, and their turn is skipped
                        for (int i = 0; i < toDraw; i++) {
                            c.getHand().draw(Deck.pop());
                        }
                        toDraw = 0;
                        playerTurn = playerTurn + turnOrder;
                        return true;
                    }
                
                    
                } 
                //Same applies to the wild four, but only wild fours can be played on other wildfours
                else if (topCard.getColoredCard().equals(CardTypes.WILDFOUR)) {
                    System.out.println("Top card is wild 4");
                    if(!c.getHand().contains(CardTypes.WILDFOUR)){
                        for (int i = 0; i < toDraw; i++) {
                            c.getHand().draw(Deck.pop());
                        }
                        toDraw = 0;
                        playerTurn = playerTurn + turnOrder;
                        return true;
                    }
                    
                }
            }
        }
        //If they didn't return false
        return false;
    }

    /**
     * Sets the top card
     * 
     * @param newTop The new top card
     */
    public void setTop(CardColor newTop) {
        this.topCard = newTop;
    }

    public CardColor getTop() {
        return this.topCard;
    }

    /**
     * Sets the/draw the top card. If the card is a wildcard then it randomizes what
     * color
     * the card will be.
     */
    public void checkTop() {
        if (topCard.getColor().equals(Color.BLACK)) {
            Random rand = new Random();
            int random = rand.nextInt(4);
            switch (random) {
                case 0:
                    topCard.setColor(Color.RED);
                    break;
                case 1:
                    topCard.setColor(Color.BLUE);
                    break;
                case 2:
                    topCard.setColor(Color.YELLOW);
                    break;
                case 3:
                    topCard.setColor(Color.GREEN);
                    break;

            }

        }
    }

    /**
     * Makes a new deck
     * 
     * @return the new deck in a stack
     */
    public Stack<CardColor> newDeck() {
        Deck theDeck = new Deck();
        /*
         * System.out.println(deck.cards.peek());
         * System.out.println("Successfully peaked");
         */

        // Get the stack of cards
        Stack<CardColor> Deck = theDeck.getCards();
        // Randomize the deck
        Collections.shuffle(Deck);
        return Deck;
    }

    public Stack<CardColor> getDeck() {
        return this.Deck;
    }

    /**
     * To be done
     * Makes a new game
     */
    public void newGame() {

    }

    @Override
    public void run() {
        boolean isReady = false;

        while (true) {
            while (!isReady) {
                //We wait until 1 player has said they're ready
                String next = "";
                synchronized (messages) {
                    if (!messages.isEmpty()) {
                        System.out.println(messages);

                        next = messages.pop();
                    }
                }
                //If the user enters ready
                if (next.equals("Ready")) {
                    //We then get the number of players in the game
                    System.out.println(server.getClients().size());
                    ArrayList<ClientThread> clients = server.getClients();
                    int numberPlayers = server.getClients().size();
                    //Check if they have too many or too little players
                    if (numberPlayers < 2 || numberPlayers > 4) {
                        clients.get(0).messageFromServer("Invalid player amount");
                        continue;
                    }

                    //Then we set the scores
                    String pointsToSend = "ScoreSetting " + String.valueOf(numberPlayers) + " ";
                    for(ClientThread c : clients){
                        pointsToSend = pointsToSend.concat(c.getClientName() + " " + c.getHand().getPoints() + " ");
                    }

                    //Get each players hand
                    for (ClientThread c : clients) {
                        // c.messageFromServer("Starting game");
                        for (int i = 0; i < 7; i++) {
                            c.getHand().draw(Deck.pop());
                        }
                        c.messageFromServer(pointsToSend);
                        //c.messageFromServer(c.getHand().tokenizeToClient());
                    }
                    boolean hasWon = false;

                    //And while no one has won yet
                    while (!hasWon) {

                        //We check if the deck has almost 0 cards
                        if (Deck.size() <= 1) {
                            Deck = newDeck();
                        }
                        synchronized (clients) {
                            //Then we check to see if our player number is invalid, as if a reverse is played turn order decrements by 1 each time
                            if (playerTurn <= -1) {
                                playerTurn = numberPlayers - 1;
                            }

                            //Then we get a string for whose turn it is
                            int toMod = playerTurn % numberPlayers;
                            ClientThread whoseTurn = clients.get(toMod);

                            //Send whose turn it is to every player, as well as the top card, and check if someone has won the game
                            for (ClientThread c : clients) {
                                c.messageFromServer("ThisTopCard " + topCard.toString() + " ");

                                //See if someone has 0 cards
                                if (c.getHand().getHand().size() == 0) {
                                    hasWon = true;
                                }


                                c.messageFromServer("ImportantFromServer " + whoseTurn.getClientName() + "'s turn");
                                c.messageFromServer(c.getHand().tokenizeToClient());
                            }

                            // Check if anyone has won, if so we go next round
                            if(hasWon == true){
                                continue;
                            }

                            //Then we use a switch statement to get the player in question
                            //This statement could be simplified to just one statement overall, but its good for making sure everything is right
                            switch (toMod) {
                                
                                case 0:
                                    if (checkDraw(clients.get(0))) {
                                        continue;
                                    } else {
                                        getNextCard(clients.get(0));
                                    }
                                    break;
                                case 1:
                                    if (checkDraw(clients.get(1))) {
                                        continue;
                                    } else {
                                        getNextCard(clients.get(1));
                                    }
                                    break;
                                case 2:
                                    if (checkDraw(clients.get(2))) {
                                        continue;
                                    } else {
                                        getNextCard(clients.get(2));
                                    }
                                    break;
                                case 3:
                                    if (checkDraw(clients.get(3))) {
                                        continue;
                                    } else {
                                        getNextCard(clients.get(3));
                                    }
                                    break;

                            }
                            //Increment the turn order
                            playerTurn = playerTurn + turnOrder;
                        }
                    }

                    // If we get here the previous game has concluded
                    // Tally points
                    //If someone won, the loop is broken and the game ends
                    isReady = winner(clients);
                }

            }
            break;

        }
    }

    /**
     * Checks to see if a player has 0 cards in their hand.
     * If so, then it says that player has won and begins the process
     * for accumulating points based on how many cards the other players have left.
     * @param clients (ArrayList<ClientThread> An arraylist of ClientThreads
     * @return boolean
     */
    public boolean winner(ArrayList<ClientThread> clients) {
        ClientThread winner = null;

        //We find out who won the round
        for (ClientThread c : clients) {
            if (c.getHand().getHand().size() == 0) {
                //Tell everyone who won the round
                for (ClientThread d : clients) {
                    d.messageFromServer("PlayerHasWon " + c.getClientName() + " won this round ");
                }
                winner = c;
                break;
            }
        }

        //Then we add their points
        int pointsToAdd = 0;
        for (ClientThread c : clients) {
            //We don't check the winners hand, even though they don't have anything
            if (!c.equals(winner)) {
                for (CardColor card : c.getHand().getHand()) {
                    //Get the point value of the cards in each players hand
                    pointsToAdd += card.getColoredCard().getNumber();
                }
                //Clear the players hand so their hand doesn't go to the next game
                c.getHand().getHand().clear();

                //Reset all globals
                toDraw = 0;
                playerTurn = 0;
                turnOrder = 1;
                skip = false;

            }
        }

        winner.getHand().addPoints(pointsToAdd);

        //Then we send the updated scores to everyone
        int numberPlayers = clients.size();
        String pointsToSend = "ScoreSetting " + String.valueOf(numberPlayers) + " ";
        for(ClientThread c : clients){
            pointsToSend = pointsToSend.concat(c.getClientName() + " " + c.getHand().getPoints() + " ");
        }
        for(ClientThread c: clients){
            c.messageFromServer(pointsToSend);
        }

        //Then we check if anyone has over the threshold required, aka 500 points
        System.out.println(winner.getHand().getPoints());
        if (winner.getHand().getPoints() > 500) {
            //If so, we tell everyone that that player has won
            for (ClientThread d : clients) {
                d.messageFromServer("PlayerHasWon " + winner.getClientName() + " won! ");
            }
            return true;
        }

        return false;
    }

    /**
     * Receives a message from a client
     * pushes that message to the LinkedList
     * @param c (ClientThread) the client
     * @param new_message (str) user's message
     */
    public void messageFromAClient(ClientThread c, String new_message) {

        // System.out.println("Message recieved");
        messages.push(new_message);

        // System.out.println(c.getClientName() + " started the game");
        // System.out.println(new_message);
    }

}