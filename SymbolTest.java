import static org.junit.Assert.*;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.awt.GraphicsEnvironment;

/**
 * Unit tests for every method of Symbol. The symbols are invisible.
 *
 * @author Jhazael and Santiago
 * @version 1.0 (Cycle 3 - 2026-2)
 */
public class SymbolTest {

    private Symbol symbol;

    @Before
    public void setUp() {
        symbol = new Symbol("red");
    }

    @Test
    public void testConstructorShouldKeepTheColor() {
        assertEquals("red", symbol.getColor());
    }

    @Test
    public void testGetColorShouldReturnTheCurrentColor() {
        symbol.changeColor("blue");
        assertEquals("blue", symbol.getColor());
    }

    @Test
    public void testChangeColorShouldUpdateTheColorOfTheSymbol() {
        symbol.changeColor("orange");
        assertEquals("orange", symbol.getColor());
    }

    @Test
    public void testPlaceAtShouldNotChangeTheColor() {
        symbol.placeAt(200, 120);
        assertEquals("red", symbol.getColor());
    }

    @Test
    public void testMoveHorizontalShouldNotChangeTheColor() {
        symbol.moveHorizontal(-30);
        assertEquals("red", symbol.getColor());
    }

    @Test
    public void testMoveVerticalShouldNotChangeTheColor() {
        symbol.moveVertical(45);
        assertEquals("red", symbol.getColor());
    }

    @Test
    public void testMakeVisibleShouldNotChangeTheColor() {
        // drawing needs a screen
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());
        symbol.makeVisible();
        assertEquals("red", symbol.getColor());
        symbol.makeInvisible();
    }

    @Test
    public void testMakeInvisibleShouldNotChangeTheColor() {
        symbol.makeInvisible();
        assertEquals("red", symbol.getColor());
    }
}
