import static org.junit.Assert.*;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.awt.GraphicsEnvironment;
import java.util.ArrayList;

/**
 * Unit tests for every method of Wheel. The wheels are invisible.
 *
 * @author Jhazael and Santiago
 * @version 1.0 (Cycle 3 - 2026-2)
 */
public class WheelTest {

    private ArrayList<Symbol> sequence;
    private Wheel wheel;

    @Before
    public void setUp() {
        sequence = new ArrayList<Symbol>();
        sequence.add(new Symbol("red"));
        sequence.add(new Symbol("blue"));
        sequence.add(new Symbol("green"));
        wheel = new Wheel(sequence, 40, 40);
    }

    @Test
    public void testConstructorShouldStartShowingTheFirstSymbolUnlocked() {
        assertEquals("red", wheel.showingColor());
        assertFalse(wheel.isLocked());
    }

    @Test
    public void testLockWheelShouldMarkTheWheelAsLocked() {
        wheel.lockWheel();
        assertTrue(wheel.isLocked());
    }

    @Test
    public void testUnlockWheelShouldMarkTheWheelAsUnlocked() {
        wheel.lockWheel();
        wheel.unlockWheel();
        assertFalse(wheel.isLocked());
    }

    @Test
    public void testIsLockedShouldBeFalseForANewWheel() {
        assertFalse(new Wheel(sequence, 0, 0).isLocked());
    }

    @Test
    public void testRotateShouldMoveForwardBackwardsAndWrapAround() {
        wheel.rotate(1);
        assertEquals("blue", wheel.showingColor());

        wheel.rotate(2);                   // passes the end: wraps to red
        assertEquals("red", wheel.showingColor());

        wheel.rotate(-1);                  // backwards from the start
        assertEquals("green", wheel.showingColor());
    }

    @Test
    public void testPlaceShouldShowTheSymbolWithThatColor() {
        assertTrue(wheel.place("green"));
        assertEquals("green", wheel.showingColor());
    }

    @Test
    public void testPlaceShouldReturnFalseWhenTheColorDoesNotExist() {
        assertFalse(wheel.place("magenta"));
        assertEquals("red", wheel.showingColor());
    }

    @Test
    public void testShowingColorShouldBeNullWhenThereAreNoSymbols() {
        Wheel empty = new Wheel(new ArrayList<Symbol>(), 0, 0);
        assertNull(empty.showingColor());
    }

    @Test
    public void testRefreshShouldFixTheIndexWhenTheSequenceGetsShorter() {
        wheel.place("green");              // last symbol
        sequence.remove(2);                // the machine deletes it

        wheel.refresh();

        assertEquals("blue", wheel.showingColor());
    }

    @Test
    public void testMoveToShouldNotChangeTheShownSymbol() {
        wheel.place("blue");
        wheel.moveTo(200, 100);
        assertEquals("blue", wheel.showingColor());
    }

    @Test
    public void testMakeVisibleShouldNotChangeTheShownSymbol() {
        // drawing needs a screen
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());
        wheel.makeVisible();
        assertEquals("red", wheel.showingColor());
        wheel.makeInvisible();
    }

    @Test
    public void testMakeInvisibleShouldNotChangeTheShownSymbol() {
        wheel.makeInvisible();
        assertEquals("red", wheel.showingColor());
    }

    @Test
    public void testRefreshShouldKeepShowingTheSameSymbolWhenOneIsInsertedBefore() {
        wheel.place("blue");
        sequence.add(0, new Symbol("black"));   // the machine inserts it

        wheel.refresh();

        assertEquals("blue", wheel.showingColor());
        wheel.rotate(1);                        // next one is green
        assertEquals("green", wheel.showingColor());
    }
}
