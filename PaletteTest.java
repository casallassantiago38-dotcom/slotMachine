import static org.junit.Assert.*;
import org.junit.Test;

import java.awt.Color;

/**
 * Unit tests for every method of Palette.
 *
 * @author Jhazael and Santiago
 * @version 1.0 (Cycle 3 - 2026-2)
 */
public class PaletteTest {

    @Test
    public void testSizeShouldBeEnoughForTheBiggestMachine() {
        assertTrue(Palette.size() >= 50);
    }

    @Test
    public void testColorAtShouldReturnDifferentNamesAndNullOutsideTheRange() {
        assertEquals("red", Palette.colorAt(0));
        assertNull(Palette.colorAt(-1));
        assertNull(Palette.colorAt(Palette.size()));

        for (int i = 0; i < Palette.size(); i++) {
            for (int j = i + 1; j < Palette.size(); j++) {
                assertFalse(Palette.colorAt(i).equals(Palette.colorAt(j)));
            }
        }
    }

    @Test
    public void testIsValidShouldAcceptKnownNamesOnly() {
        assertTrue(Palette.isValid("blue"));
        assertTrue(Palette.isValid("orange"));
        assertFalse(Palette.isValid("notacolor"));
        assertFalse(Palette.isValid(null));
    }

    @Test
    public void testToColorShouldGiveDifferentColorsAndBlackForUnknownNames() {
        assertEquals(new Color(0xFFA500), Palette.toColor("orange"));
        assertEquals(Color.black, Palette.toColor("notacolor"));

        for (int i = 0; i < Palette.size(); i++) {
            for (int j = i + 1; j < Palette.size(); j++) {
                assertFalse(Palette.toColor(Palette.colorAt(i)).equals(
                            Palette.toColor(Palette.colorAt(j))));
            }
        }
    }
}
