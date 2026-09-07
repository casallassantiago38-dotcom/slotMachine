import java.util.ArrayList;

/**
 * This class represents a wheel of the slot machine.
 *
 * Design decision: the wheel does NOT own the symbols, since all
 * wheels share the same sequence, which lives in SlotMachine, because
 * the assignment states that symbols appear in the same order in
 * every wheel, and the wheel only remembers which of those symbols
 * it is currently showing through its window.
 *
 * Even though the sequence is shared, each wheel draws with its own
 * Symbol object. Two wheels can show the same symbol at the same
 * time, and a single rectangle cannot be in two places on the screen
 * at once.
 *
 * @author Jhazael and Santiago
 * @version 1.1 (Cycle 2 - 2026-2)
 */
public class Wheel {

    // Symbol sequence shared with the machine. The wheel only reads
    // it: it never adds nor removes symbols from it.
    private ArrayList<Symbol> sequence;

    // Position, within the sequence, of the symbol being shown.
    private int shownIndex;

    // This wheel's own rectangle, the one seen on screen.
    private Symbol visibleSymbol;

    // Where this wheel is drawn on the canvas.
    private int xPosition;
    private int yPosition;

    // Indicates whether this wheel is in visible mode.
    private boolean isVisible;

    // Indicates whether the wheel is locked.
    private boolean locked;

    /**
     * Creates a wheel that reads the indicated symbol sequence and is
     * drawn at position (x, y) of the canvas.
     *
     * @param sequence symbol list shared with the machine.
     * @param x horizontal position of the wheel in pixels.
     * @param y vertical position of the wheel in pixels.
     */
    public Wheel(ArrayList<Symbol> sequence, int x, int y) {
        this.sequence = sequence;
        this.shownIndex = 0;
        this.locked = false;
        this.xPosition = x;
        this.yPosition = y;
        this.isVisible = false;
        this.visibleSymbol = new Symbol("black");
        this.visibleSymbol.placeAt(x, y);
        updateVisibleSymbol();
    }

    /**
     * Sets the wheel's locked state to false.
     */
    public void unlockWheel() {
        locked = false;
    }

    /**
     * Sets the wheel's locked state to true.
     */
    public void lockWheel() {
        locked = true;
    }

    /**
     * @return true if the wheel is currently locked, false otherwise.
     */
    public boolean isLocked() {
        return locked;
    }

    /**
     * Rotates the wheel the indicated number of steps. When it goes
     * past the end of the sequence it wraps back to the beginning,
     * because the wheel is circular. Negative steps rotate in the
     * opposite direction.
     *
     * @param steps how many positions the shown symbol moves.
     */
    public void rotate(int steps) {
        if (sequence.isEmpty()) {
            return;
        }
        int count = sequence.size();
        // the double modulo makes a negative rotation also fall
        // within the range 0..count-1
        shownIndex = ((shownIndex + steps) % count + count) % count;
        updateVisibleSymbol();
    }

    /**
     * Fixes, as the shown symbol, the one with the indicated color.
     *
     * @param color color of the symbol to be shown.
     * @return true if a symbol of that color exists; false otherwise.
     */
    public boolean place(String color) {
        for (int i = 0; i < sequence.size(); i++) {
            if (sequence.get(i).getColor().equals(color)) {
                shownIndex = i;
                updateVisibleSymbol();
                return true;
            }
        }
        return false;
    }

    /**
     * @return the color this wheel is currently showing, or null if
     * there are no symbols yet in the sequence.
     */
    public String showingColor() {
        if (sequence.isEmpty()) {
            return null;
        }
        return sequence.get(shownIndex).getColor();
    }

    /**
     * The machine calls this method when the symbol sequence changes,
     * so that the wheel adjusts its index if it ended up pointing
     * outside the list, and redraws what corresponds.
     */
    public void refresh() {
        if (sequence.isEmpty()) {
            shownIndex = 0;
        } else if (shownIndex >= sequence.size()) {
            shownIndex = sequence.size() - 1;
        }
        updateVisibleSymbol();
    }

    /**
     * Moves the wheel to another position on the canvas. Used when a
     * wheel is removed and the rest need to be rearranged.
     *
     * @param x new horizontal position in pixels.
     * @param y new vertical position in pixels.
     */
    public void moveTo(int x, int y) {
        xPosition = x;
        yPosition = y;
        visibleSymbol.placeAt(x, y);
    }

    /**
     * Makes this wheel visible.
     */
    public void makeVisible() {
        isVisible = true;
        updateVisibleSymbol();
    }

    /**
     * Makes this wheel invisible.
     */
    public void makeInvisible() {
        isVisible = false;
        visibleSymbol.makeInvisible();
    }

    /**
     * Sets this wheel's rectangle to the color of the symbol it is
     * supposed to show, and draws it only if the wheel is visible.
     * If the sequence is empty there is nothing to show.
     */
    private void updateVisibleSymbol() {
        if (sequence.isEmpty()) {
            visibleSymbol.makeInvisible();
            return;
        }
        visibleSymbol.changeColor(sequence.get(shownIndex).getColor());
        if (isVisible) {
            visibleSymbol.makeVisible();
        } else {
            visibleSymbol.makeInvisible();
        }
    }
}
