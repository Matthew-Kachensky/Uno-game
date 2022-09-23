package Violet;

import java.util.ArrayList;

/**
 * Playerhand handles the methods used to track the player and their actions.
 * such as their current points and whether they
 * draw/play.
 */
public class PlayerHand {
    // NEEDS TO BE ARRAYLIST, as it's easier to have the user pick one of the cards
    // and remove that index
    private ArrayList<CardColor> hand;
    private int points;

    // Makes a new array list of hand
    PlayerHand() {
        this.points = 0;
        hand = new ArrayList<>();
    }

    /**
     * adds points to the current score
     * 
     * @param points the points to add
     */
    public void addPoints(int points) {
        this.points += points;
    }

    /**
     * gets the current points of the given hand
     * 
     * @return the points
     */
    public int getPoints() {
        return this.points;
    }

    /**
     * Gets the hand of the user
     * 
     * @return the hand
     */
    public ArrayList<CardColor> getHand() {
        return this.hand;
    }

    /**
     * If the player is drawing a card, we add it to the hand
     * 
     * @param card the card to be added to the hand
     */
    public void draw(CardColor card) {
        this.hand.add(card);
    }

    /**
     * If the player is playing a card, we remove it from the hand
     *
     * @param card The card to be removed from the hand
     */
    public void play(CardColor card) {
        this.hand.remove(card);

    }

    /**
     * Sees if the hand contains the Cardtype
     * 
     * @param c the card type to see if in hand
     * @return if the card is in the hand or not
     */
    public boolean contains(CardTypes c) {
        // Check the card color for every hand
        System.out.println(c);
        for (CardColor card : this.hand) {
            System.out.println(card.getColoredCard().getType());
            System.out.println(c.getType().equals(card.getColoredCard().getType()));
            // If the card type is equal to the paramater
            if (c.getType().equals(card.getColoredCard().getType())) {
                // return true if it is in
                return true;
            }
        }
        // return false if not in the hand
        return false;
    }

    /**
     * A string format for the terminal
     * 
     * @return The players hand in string format
     */
    @Override
    public String toString() {
        String toReturn = "";
        for (int i = 0; i < this.getHand().size(); i++) {
            // "Card#: CardColor CardType"
            toReturn = toReturn.concat(i + ": " + hand.get(i).toString() + "\t");
        }
        return toReturn;
    }

    /**
     * A string format that can be tokenized in a way to let the server process the
     * information of the hand
     * 
     * @return the string of the hand to be tokenized
     */
    public String tokenizeToClient() {
        // The special token to identify that it is a hand to be processed
        String toReturn = "ProcessHand ";
        // Then make the string have every card in the string
        for (int i = 0; i < this.getHand().size(); i++) {
            toReturn = toReturn.concat(hand.get(i).toString() + " ");
        }

        return toReturn;
    }

}
