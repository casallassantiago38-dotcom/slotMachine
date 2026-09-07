/**
 * This class represents a symbol of the slot machine.
 * A symbol is identified by its color, and it is drawn on screen as
 * a rectangle. That is why this class extends (inherits from)
 * Rectangle: a Symbol IS a special kind of Rectangle that also
 * remembers its color and its position on screen.
 *
 * @author Jhazael and Santiago
 * @version 1.1 (Cycle 1 - 2026-2)
 */
public class Symbol extends Rectangle {

    // CSS color that identifies this symbol, for example "red".
    private String color;

    // Rectangle stores the (x, y) position as private attributes, and
    // we cannot read them directly from this class.
    // That is why we keep our own copy of the position here.
    private int currentX;
    private int currentY;

    // Remembers whether this symbol is currently visible.
    private boolean isVisible;

    /**
     * Creates a new symbol of the indicated color. It is born
     * invisible, just like Rectangle does by default.
     *
     * @param color CSS color name, for example "red" or "blue".
     */
    public Symbol(String color) {
        super();
        this.color = color;
        changeColor(color);

        // these values must match the initial position used by the
        // Rectangle constructor (xPosition = 70, yPosition = 15)
        currentX = 70;
        currentY = 15;
        isVisible = false;
    }

    /**
     * Moves this symbol to the indicated (x, y) position, using the
     * moveHorizontal and moveVertical methods already provided by
     * Rectangle.
     *
     * @param x new horizontal position in pixels.
     * @param y new vertical position in pixels.
     */
    public void placeAt(int x, int y) {
        int horizontalDistance = x - currentX;
        int verticalDistance = y - currentY;
        moveHorizontal(horizontalDistance);
        moveVertical(verticalDistance);
    }

    /**
     * @return the CSS color that identifies this symbol.
     */
    public String getColor() {
        return color;
    }

    /**
     * Makes this symbol visible on the canvas. We override this
     * method only to be able to update our isVisible variable.
     */
    @Override
    public void makeVisible() {
        super.makeVisible();
        isVisible = true;
    }

    @Override
    public void changeColor(String newColor) {
        super.changeColor(newColor);
        this.color = newColor;
    }

    /**
     * Makes this symbol invisible. Same as above, we override this
     * method only to keep our isVisible variable up to date.
     */
    @Override
    public void makeInvisible() {
        super.makeInvisible();
        isVisible = false;
    }

    /**
     * Moves the symbol horizontally. We override this method to
     * update our own copy of the position (currentX).
     *
     * @param distance how many pixels it should move (can be negative).
     */
    @Override
    public void moveHorizontal(int distance) {
        super.moveHorizontal(distance);
        currentX = currentX + distance;
    }

    /**
     * Moves the symbol vertically. We override this method to
     * update our own copy of the position (currentY).
     *
     * @param distance how many pixels it should move (can be negative).
     */
    @Override
    public void moveVertical(int distance) {
        super.moveVertical(distance);
        currentY = currentY + distance;
    }
}
