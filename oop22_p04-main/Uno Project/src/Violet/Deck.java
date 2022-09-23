package Violet;

import java.awt.*;
import java.util.Stack;

/**
 * Class deck creates a Stack of CardColors. CardColors are a class that
 * contains the color of the card
 * and the number of the card (via enumeration)
 */
public class Deck {

    public Stack<CardColor> cards;
    public CardTypes cardTypes;

    /**
     * creates a deck of UNO cards minus the special cards right now. That is 76 in
     * total
     */
    public Deck() {
        CardTypes testCard = CardTypes.ZERO;
        // We make a new stack, cause Stack of Cards heh?
        this.cards = new Stack<>();

        this.cardTypes = testCard.getCard(0);
        CardColor cardColor = new CardColor();

        /**
         * adds a pair of each number per color, except 0.
         */
        for (int z = 0; z < 2; z++) {
            for (int x = 1; x < 10; x++) {
                for (int y = 0; y < 4; y++) {
                    if (y == 0) {
                        Color color = Color.RED;
                        cardColor = new CardColor(color, x);
                        this.cards.add(cardColor);

                    } else if (y == 1) {
                        Color color = Color.YELLOW;
                        cardColor = new CardColor(color, x);
                        this.cards.add(cardColor);

                    } else if (y == 2) {
                        Color color = Color.BLUE;
                        cardColor = new CardColor(color, x);
                        this.cards.add(cardColor);
                    } else {
                        Color color = Color.GREEN;
                        cardColor = new CardColor(color, x);
                        this.cards.add(cardColor);
                    }

                }
            }
        }

        // Adding the special cards for the deck
        Color color;
        for (int i = 0; i < 2; i++) {
            for (int k = 0; k < 4; k++) {
                switch (k) {
                    case 0:
                        color = Color.RED;
                        cardColor = new CardColor(color, "Skip");
                        this.cards.add(cardColor);
                        cardColor = new CardColor(color, "Reverse");
                        this.cards.add(cardColor);
                        cardColor = new CardColor(color, "DrawTwo");
                        this.cards.add(cardColor);
                        break;
                    case 1:
                        color = Color.YELLOW;
                        cardColor = new CardColor(color, "Skip");
                        this.cards.add(cardColor);
                        cardColor = new CardColor(color, "Reverse");
                        this.cards.add(cardColor);
                        cardColor = new CardColor(color, "DrawTwo");
                        this.cards.add(cardColor);
                        break;
                    case 2:
                        color = Color.BLUE;
                        cardColor = new CardColor(color, "Skip");
                        this.cards.add(cardColor);
                        cardColor = new CardColor(color, "Reverse");
                        this.cards.add(cardColor);
                        cardColor = new CardColor(color, "DrawTwo");
                        this.cards.add(cardColor);
                    case 3:
                        color = Color.GREEN;
                        cardColor = new CardColor(color, "Skip");
                        this.cards.add(cardColor);
                        cardColor = new CardColor(color, "Reverse");
                        this.cards.add(cardColor);
                        cardColor = new CardColor(color, "DrawTwo");
                        this.cards.add(cardColor);
                }
            }
        }

        // Adding The non colored cards like wild and wold draw four
        for (int i = 0; i < 4; i++) {
            color = Color.BLACK;
            cardColor = new CardColor(color, "Wild");
            this.cards.add(cardColor);
            cardColor = new CardColor(color, "WildFour");
            this.cards.add(cardColor);
        }

        /**
         * adding the single zeros' for each color
         */

        CardColor redZero = new CardColor(Color.RED, 0);
        CardColor yellowZero = new CardColor(Color.YELLOW, 0);
        CardColor blueZero = new CardColor(Color.blue, 0);
        CardColor greenZero = new CardColor(Color.green, 0);

        this.cards.add(redZero);
        this.cards.add(yellowZero);
        this.cards.add(blueZero);
        this.cards.add(greenZero);

    }

    /**
     * Returns the stack of cards
     * 
     * @return the stack
     */
    public Stack<CardColor> getCards() {
        return cards;
    }

    // Used for debugging
    public static void main(String[] args) {
        CardTypes testCard = CardTypes.ZERO;
        System.out.println(testCard);
        testCard = testCard.getCard(1);
        System.out.println(testCard);

        System.out.println("----------");
        CardColor testcolorcard = new CardColor();
        System.out.println(testcolorcard.getColor());
        System.out.println(testcolorcard.getColoredCard());

        System.out.println("------------");
        Color color = Color.RED;
        CardTypes cardzz = CardTypes.ZERO;
        cardzz = cardzz.getCard(5);

        CardColor color1 = new CardColor(color, 5);

        Stack<CardColor> cards2 = new Stack<>();
        cards2.add(color1);

    }

    // public void setCards(ArrayList<CardTypes> cards) {
    // this.cards = cards;
    // }
    //
    // public ArrayList<CardTypes> getCards() {
    // return cards;
    // }

}
