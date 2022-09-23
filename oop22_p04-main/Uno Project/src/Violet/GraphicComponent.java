package Violet;

import javax.swing.JComponent;
import java.awt.*;

/**
 * Handles the making of components for the GUI. Utilizes JComponent, more
 * specifically paintComponent, to draw the different
 * cards the player has.
 */
public class GraphicComponent extends JComponent {
    private Color color;
    private String value;

    /**
     * main constructor, calls the super class, sets up global variables to the
     * given variables.
     * 
     * @param color (color) inputted color
     * @param value (str) the index of the card
     */
    public GraphicComponent(Color color, String value) {
        super();
        this.color = color;
        this.value = value;
        // Makes a new rectangle 125x175
        setPreferredSize(new Dimension(125, 175));
        repaint();
    }

    /**
     * overrides the paintComponent from JComponent. creates the Cards shown on the
     * GUI
     * 
     * @Param g the graphics
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D canvas = (Graphics2D) g;

        // We draw the string, aka the name of the card
        canvas.drawString(this.value, 0, 50);
        // Set the color of the rectangle to the color of the card
        canvas.setColor(this.color);

        // Then fill the rectangle
        canvas.fillRect(0, 50, 75, 150);
        repaint();
    }
}
