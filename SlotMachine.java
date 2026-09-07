import java.util.ArrayList;
import java.util.Random;
import javax.swing.JOptionPane;

/**
 * This class represents the simulator of a slot machine.
 * A slot machine has several wheels (Wheel objects) and a sequence
 * of colored symbols (Symbol objects).
 *
 * Design decision: there is only ONE symbol sequence and it belongs
 * to the machine, not to each wheel. This follows from the
 * assignment: symbols appear in the same order on every wheel, and
 * that is why addSymbol and delSymbol do not receive a wheel number,
 * while placeSymbol does receive one. What distinguishes one wheel
 * from another is not its symbols, but which of them it is showing.
 *
 * @author Jhazael and Santiago
 * @version 2.1 (Cycle 2 - 2026-2)
 */
public class SlotMachine {

    // Symbol sequence of the machine. All wheels share it.
    private ArrayList<Symbol> symbols;

    // List with all the wheels the machine currently has.
    private ArrayList<Wheel> wheels;

    // Indicates whether the simulator is in visible or invisible mode.
    private boolean isVisible;

    // Indicates whether the last operation performed was successful.
    private boolean successfulOperation;

    // Used to rotate wheels a random number of steps.
    private Random random;

    // These constants are only used to compute where to draw each
    // wheel on screen (they have no relation to business logic).
    private static final int SPACE_BETWEEN_WHEELS = 60;
    private static final int INITIAL_X_POSITION = 40;
    private static final int INITIAL_Y_POSITION = 40;

    // Colors the shapes Canvas knows how to paint. Any other CSS
    // name makes the canvas draw black without warning.
    private static final String[] VALID_COLORS = {"red", "black", "blue",
        "yellow", "green", "magenta", "white"};

    // Body of the machine. It is drawn behind the wheels and changes
    // color when the machine reaches a winning configuration.
    private Rectangle body;

    private static final int MARGIN = 15;

    /**
     * Constructor. Creates a machine with no wheels, no symbols and
     * in invisible mode, as required by the assignment.
     */
    public SlotMachine() {
        symbols = new ArrayList<Symbol>();
        wheels = new ArrayList<Wheel>();
        isVisible = false;

        body = new Rectangle();
        body.moveHorizontal(INITIAL_X_POSITION - MARGIN - 70);
        body.moveVertical(INITIAL_Y_POSITION - MARGIN - 15);
        body.changeColor("black");

        successfulOperation = true;
        random = new Random();
    }

    /**
     * Adds a new wheel at the indicated position. The wheel is born
     * showing the first symbol of the machine's sequence. If the
     * position is out of range it is adjusted to the nearest allowed
     * end.
     *
     * @param pos position where the wheel should be added (starts at 1).
     */
    public void addWheel(int pos) {
        int index = adjustPosition(pos, wheels.size() + 1);

        int x = INITIAL_X_POSITION + index * SPACE_BETWEEN_WHEELS;
        Wheel newWheel = new Wheel(symbols, x, INITIAL_Y_POSITION);
        wheels.add(index, newWheel);

        if (isVisible) {
            newWheel.makeVisible();
        }
        repositionWheels();
        updateWinningState();
        successfulOperation = true;
    }

    /**
     * Removes the wheel at the indicated position.
     * @param pos position of the wheel to remove (starts at 1).
     */
    public void delWheel(int pos) {
        if (wheels.isEmpty()) {
            fail("There are no wheels to remove.");
            return;
        }
        int index = adjustPosition(pos, wheels.size());

        wheels.get(index).makeInvisible();
        wheels.remove(index);

        repositionWheels();
        updateWinningState();
        successfulOperation = true;
    }

    /**
     * Locks a wheel.
     * @param wheel position of the wheel to lock (starts at 1).
     */
    public void lock(int wheel) {
        if (wheels.isEmpty()) {
            fail("Cannot lock a wheel when there are no wheels.");
            return;
        }
        int index = adjustPosition(wheel, wheels.size());
        wheels.get(index).lockWheel();
        successfulOperation = true;
    }

    /**
     * Unlocks a wheel.
     * @param wheel position of the wheel to unlock (starts at 1).
     */
    public void unlock(int wheel) {
        if (wheels.isEmpty()) {
            fail("Cannot unlock a wheel when there are no wheels.");
            return;
        }
        int index = adjustPosition(wheel, wheels.size());
        wheels.get(index).unlockWheel();
        successfulOperation = true;
    }

    /**
     * Rotates a specific wheel the indicated number of steps.
     * If the wheel is locked, the operation fails.
     *
     * @param wheel position of the wheel to rotate (starts at 1).
     * @param steps how many steps the wheel rotates; negative values rotate backwards.
     */
    public void spin(int wheel, int steps) {
        if (wheels.isEmpty()) {
            fail("There are no wheels to spin.");
            return;
        }
        if (symbols.isEmpty()) {
            fail("The machine has no symbols configured.");
            return;
        }
        int index = adjustPosition(wheel, wheels.size());

        if (wheels.get(index).isLocked()) {
            fail("The wheel is locked and cannot be spun.");
            return;
        }

        int direction = steps >= 0 ? 1 : -1;
        int stepCount = Math.abs(steps);
        for (int i = 0; i < stepCount; i++) {
            wheels.get(index).rotate(direction);
            if (isVisible) {
                Canvas.getCanvas().wait(150);
            }
        }

        updateWinningState();
        successfulOperation = true;
    }

    /**
     * Leaves the machine in the indicated configuration: each wheel
     * shows the color that corresponds to it in the array.
     * The operation is atomic: if something does not add up, nothing
     * is changed.
     *
     * @param setSymbols colors that each wheel should show, from left to right.
     */
    public void spin(String[] setSymbols) {
        if (setSymbols == null) {
            fail("No configuration was received.");
            return;
        }
        if (setSymbols.length != wheels.size()) {
            fail("The configuration does not have one color per wheel.");
            return;
        }
        for (int i = 0; i < setSymbols.length; i++) {
            if (findSymbol(setSymbols[i]) < 0) {
                fail("The machine has no symbol of color " + setSymbols[i] + ".");
                return;
            }
            if (wheels.get(i).isLocked()
                    && !setSymbols[i].equals(wheels.get(i).showingColor())) {
                fail("A locked wheel would have to change its symbol.");
                return;
            }
        }

        for (int i = 0; i < wheels.size(); i++) {
            wheels.get(i).place(setSymbols[i]);
        }

        updateWinningState();
        successfulOperation = true;
    }

    /**
     * Swap first checks whether there are two or more wheels; if
     * there is less than one wheel it fails; if the positions are
     * equal, or the same wheel is being swapped with itself, it does
     * nothing and fails; negative numbers are not allowed; and it
     * swaps the positions using a temporary variable.
     *
     * @param wheel1 first wheel entered by the user, position starts at 1; if out of range it is adjusted to the nearest end.
     * @param wheel2 second wheel entered by the user; if out of range it is adjusted to the nearest end.
     */
    public void swap(int wheel1, int wheel2) {
        if (wheels.size() <= 1) {
            fail("At least two wheels are needed to perform a swap.");
            return;
        }
        int position1 = adjustPosition(wheel1, wheels.size());
        int position2 = adjustPosition(wheel2, wheels.size());
        if (position1 == position2) {
            fail("Cannot swap two wheels that are in the same position.");
            return;
        }
        Wheel temp = wheels.get(position1);

        wheels.set(position1, wheels.get(position2));
        wheels.set(position2, temp);

        repositionWheels();
        updateWinningState();
        successfulOperation = true;
    }

    /**
     * Adds a symbol of the indicated color to the machine's sequence,
     * at the requested position. The symbol becomes available to all
     * wheels.
     *
     * @param pos   position where the symbol is wanted (starts at 1).
     * @param color CSS color of the symbol, for example "red" or "blue".
     */
    public void addSymbol(int pos, String color) {
        if (!isColorSupported(color)) {
            fail("The color " + color + " cannot be drawn.");
            return;
        }
        if (findSymbol(color) >= 0) {
            fail("A symbol of that color already exists.");
            return;
        }

        int index = adjustPosition(pos, symbols.size() + 1);
        symbols.add(index, new Symbol(color));
        notifyWheels();

        updateWinningState();
        successfulOperation = true;
    }

    /**
     * Removes from the machine's sequence the symbol with the
     * indicated color.
     *
     * @param symbol color of the symbol to remove.
     */
    public void delSymbol(String symbol) {
        int index = findSymbol(symbol);
        if (index < 0) {
            fail("There is no symbol of that color.");
            return;
        }

        symbols.remove(index);
        notifyWheels();

        updateWinningState();
        successfulOperation = true;
    }

    /**
     * Fixes, on the indicated wheel, the symbol of the indicated
     * color as the symbol being shown.
     * If the wheel is locked, the operation fails.
     *
     * @param wheel  position of the wheel (starts at 1).
     * @param symbol color of the symbol to be shown.
     */
    public void placeSymbol(int wheel, String symbol) {
        if (wheels.isEmpty()) {
            fail("There are no wheels configured.");
            return;
        }
        int index = adjustPosition(wheel, wheels.size());

        if (wheels.get(index).isLocked()) {
            fail("The wheel is locked and its symbol cannot be changed.");
            return;
        }

        if (!wheels.get(index).place(symbol)) {
            fail("The machine has no symbol of that color.");
            return;
        }

        updateWinningState();
        successfulOperation = true;
    }

    /**
     * Rotates a single wheel of the machine a random number of steps.
     * If the wheel is locked, the operation fails.
     *
     * @param wheel position of the wheel to rotate (starts at 1).
     */
    public void spin(int wheel) {
        if (wheels.isEmpty()) {
            fail("There are no wheels to spin.");
            return;
        }
        if (symbols.isEmpty()) {
            fail("The machine has no symbols configured.");
            return;
        }
        int index = adjustPosition(wheel, wheels.size());

        if (wheels.get(index).isLocked()) {
            fail("The wheel is locked and cannot be spun.");
            return;
        }

        wheels.get(index).rotate(random.nextInt(symbols.size()));

        updateWinningState();
        successfulOperation = true;
    }

    /**
     * Rotates all the wheels of the machine, one by one.
     * Locked wheels are skipped and the operation still succeeds.
     */
    public void spin() {
        if (wheels.isEmpty()) {
            fail("There are no wheels to spin.");
            return;
        }
        if (symbols.isEmpty()) {
            fail("The machine has no symbols configured.");
            return;
        }

        for (int i = 0; i < wheels.size(); i++) {
            if (wheels.get(i).isLocked()) {
                continue;
            }
            wheels.get(i).rotate(random.nextInt(symbols.size()));
        }

        updateWinningState();
        successfulOperation = true;
    }

    /**
     * @return the colors of the machine's symbols, in the same order
     * they are in the sequence (starting at position 1).
     */
    public String[] symbols() {
        String[] colors = new String[symbols.size()];
        for (int i = 0; i < symbols.size(); i++) {
            colors[i] = symbols.get(i).getColor();
        }
        successfulOperation = true;
        return colors;
    }

    /**
     * @return the number of distinct colors currently being shown on
     * the wheels. It is the k from the original problem: if it is 1,
     * all the wheels are showing the same thing.
     */
    public int distinctSymbols() {
        String[] visible = configuration();
        ArrayList<String> seen = new ArrayList<String>();
        for (int i = 0; i < visible.length; i++) {
            if (visible[i] != null && !seen.contains(visible[i])) {
                seen.add(visible[i]);
            }
        }
        successfulOperation = true;
        return seen.size();
    }

    /**
     * @return the color each wheel is currently showing, ordered
     * from left to right.
     */
    public String[] configuration() {
        String[] visibleColors = new String[wheels.size()];
        for (int i = 0; i < wheels.size(); i++) {
            visibleColors[i] = wheels.get(i).showingColor();
        }
        successfulOperation = true;
        return visibleColors;
    }

    /**
     * @return true if the machine is in a winning configuration, that
     * is, if all the wheels show the same symbol.
     */
    public boolean isJackpot() {
        return !wheels.isEmpty() && distinctSymbols() == 1;
    }

    /**
     * Makes the whole simulator visible.
     */
    public void makeVisible() {
        isVisible = true;
        body.makeVisible();
        for (int i = 0; i < wheels.size(); i++) {
            wheels.get(i).makeVisible();
        }
        successfulOperation = true;
    }

    /**
     * Makes the whole simulator invisible.
     */
    public void makeInvisible() {
        isVisible = false;
        for (int i = 0; i < wheels.size(); i++) {
            wheels.get(i).makeInvisible();
        }
        body.makeInvisible();
        successfulOperation = true;
    }

    /**
     * Ends the simulator. For now it simply hides everything.
     */
    public void exit() {
        makeInvisible();
        successfulOperation = true;
    }

    /**
     * @return true if the last operation invoked could be performed
     * correctly, false otherwise.
     */
    public boolean ok() {
        return successfulOperation;
    }

    /**
     * Adjusts a position given by the user so that it falls within
     * the allowed range, and converts it to the list index.
     * The assignment requires that if the value is out of range, the
     * nearest end is used instead of rejecting the operation.
     *
     * @param pos     position requested by the user (starts at 1).
     * @param maximum largest position that is accepted.
     * @return the equivalent index in the list (starts at 0).
     */
    private int adjustPosition(int pos, int maximum) {
        int validPosition = pos;
        if (validPosition < 1) {
            validPosition = 1;
        }
        if (validPosition > maximum) {
            validPosition = maximum;
        }
        return validPosition - 1;
    }

    /**
     * Looks up, in the sequence, the symbol with the indicated color.
     *
     * @param color color being searched for.
     * @return the position of the symbol in the list, or -1 if it does not exist.
     */
    private int findSymbol(String color) {
        for (int i = 0; i < symbols.size(); i++) {
            if (symbols.get(i).getColor().equals(color)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * @param color color name to be checked.
     * @return true if the canvas knows how to draw that color.
     */
    private boolean isColorSupported(String color) {
        for (int i = 0; i < VALID_COLORS.length; i++) {
            if (VALID_COLORS[i].equals(color)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Notifies all wheels that the symbol sequence changed, so they
     * can adjust what they are showing.
     */
    private void notifyWheels() {
        for (int i = 0; i < wheels.size(); i++) {
            wheels.get(i).refresh();
        }
    }

    /**
     * Redraws the wheels so they stay on top of the body. The shapes
     * canvas paints shapes in the order they were last drawn, so
     * every time the body changes color or size it ends up on top and
     * the wheels need to be brought forward again.
     */
    private void bringWheelsToFront() {
        if (!isVisible) {
            return;
        }
        for (int i = 0; i < wheels.size(); i++) {
            wheels.get(i).makeVisible();
        }
    }

    /**
     * Puts each wheel back in its place from left to right and
     * adjusts the body's size. It is called every time the number of
     * wheels changes, so that no gaps are left.
     */
    private void repositionWheels() {
        for (int i = 0; i < wheels.size(); i++) {
            wheels.get(i).moveTo(INITIAL_X_POSITION + i * SPACE_BETWEEN_WHEELS,
                                 INITIAL_Y_POSITION);
        }
        body.changeSize(30 + 2 * MARGIN, wheels.size() * SPACE_BETWEEN_WHEELS + 10);
        bringWheelsToFront();
    }


    /**
     * Usability requirement: the machine must look different when it
     * reaches the winning state. The body turns yellow.
     */
    private void updateWinningState() {
        if (isJackpot()) {
            body.changeColor("yellow");
        } else {
            body.changeColor("black");
        }
        bringWheelsToFront();
    }

    /**
     * Marks the last operation as failed and warns the user, but ONLY
     * if the simulator is visible. If it is invisible, the operation
     * fails silently (usability requirement number 4).
     *
     * @param message text to be shown to the user.
     */
    private void fail(String message) {
        successfulOperation = false;
        if (isVisible) {
            JOptionPane.showMessageDialog(null, message,
                    "Slot Machine", JOptionPane.WARNING_MESSAGE);
        }
    }
}
