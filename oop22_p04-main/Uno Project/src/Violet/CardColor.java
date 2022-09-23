package Violet;

import java.awt.*;

/**
 * Takes in a CardType enumeration and a color. we are creating a series of
 * instances of this class to represent the different
 * cards in our "deck".
 * 
 */
public class CardColor {
    private Color color;
    private CardTypes ColoredCard;

    // For making numbered cards
    public CardColor(Color color, int value) {
        this.color = color;
        CardTypes ColoredCard = CardTypes.ZERO;
        ColoredCard = ColoredCard.getCard(value);
        this.ColoredCard = ColoredCard;
    }

    // For making special cards
    public CardColor(Color color, String type) {
        this.color = color;
        CardTypes ColoredCard = CardTypes.SKIP;
        ColoredCard = ColoredCard.getType(type);
        this.ColoredCard = ColoredCard;
    }

    // Used in some cases where we have the card type given to us
    public CardColor(Color color, CardTypes type) {
        this.color = color;
        this.ColoredCard = type;
    }

    // Useless constructor
    public CardColor() {
        this.color = Color.PINK;
        this.ColoredCard = CardTypes.ZERO;
    }

    // Returns the type of card, number or special
    public CardTypes getColoredCard() {
        return ColoredCard;
    }

    // Returns the color of the card
    public Color getColor() {
        return color;
    }

    public void setColor(Color c) {
        this.color = c;
    }

    /**
     * @param none
     * @return A non object color + the name of the card
     */
    @Override
    public String toString() {
        Color theColor = this.getColor();
        String toReturn = "";
        if (theColor.equals(Color.GREEN)) {
            toReturn = toReturn.concat("Green ");
        } else if (theColor.equals(Color.RED)) {
            toReturn = toReturn.concat("Red ");
        } else if (theColor.equals(Color.BLUE)) {
            toReturn = toReturn.concat("Blue ");
        } else if (theColor.equals(Color.YELLOW)) {
            toReturn = toReturn.concat("Yellow ");
        } else {
            toReturn = toReturn.concat("Colorless ");
        }
        toReturn = toReturn.concat(this.getColoredCard().toString());
        return toReturn;
    }

}
