package Violet;

/**
 * This enum creates a "CardTypes" that ranges from 0-9. Also have the option to
 * just send through an integer
 * and that turns it into it CardTypes.
 */
public enum CardTypes {
    // The enum of the card types
    ZERO("Zero", 0), ONE("One", 1), TWO("Two", 2), THREE("Three", 3), FOUR("Four", 4), FIVE("Five",5), SIX("Six", 6), SEVEN("Seven", 7), EIGHT("Eight", 8), NINE("Nine", 9), SKIP("Skip", 20),
    REVERSE("Reverse", 20), DRAWTWO("DrawTwo", 20), WILD("Wild", 50), WILDFOUR("WildFour", 50);

    private int number;
    private String type;

    // constructor for just the standard numbered cards
    // Each number is its point value
    CardTypes(int number) {
        this.number = number;
    }

    // Special cards are given a number
    // Each number is its point value
    CardTypes(String type, int number) {
        this.type = type;
        this.number = number;
    }

    /**
     * Returns the number value of the card, for special cards it's their point values
     * @return the value
     */
    public int getNumber() {
        return this.number;
    }

    /**
     * Returns the type of card in string format
     * @return
     */
    public String getType(){
        return this.type;
    }

    /**
     * Returns the enum type of the card
     * @param number the number you are trying to get
     * @return the enum type
     */
    public CardTypes getCard(int number) {
        switch (number) {
            case 0:
                return ZERO;
            case 1:
                return ONE;
            case 2:
                return TWO;
            case 3:
                return THREE;
            case 4:
                return FOUR;
            case 5:
                return FIVE;
            case 6:
                return SIX;
            case 7:
                return SEVEN;
            case 8:
                return EIGHT;
            case 9:
                return NINE;
            default:
                return ZERO;
        }
    }

    /**
     * Returns the type for special cards
     * @param type the special card type
     * @return the enum special type
     */
    public CardTypes getType(String type) {
        switch (type) {
            case "Skip":
                return SKIP;
            case "Reverse":
                return REVERSE;
            case "DrawTwo":
                return DRAWTWO;
            case "Wild":
                return WILD;
            case "WildFour":
                return WILDFOUR;
            default:
                return SKIP;
        }
    }

}
