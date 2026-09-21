import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.awt.GraphicsEnvironment;
import java.util.ArrayList;

/**
 * Unit tests for every method of SlotMachine (Cycles 1, 2 and 3).
 * Every test works with the machine invisible, so no dialog or window
 * interrupts the run. The only exception is the test of makeVisible,
 * which shows the machine for an instant and hides it again (it is
 * skipped when the computer has no screen).
 *
 * @author Jhazael and Santiago
 * @version 1.0 (Cycle 3 - 2026-2)
 */
public class SlotMachineTest {

    private SlotMachine machine;

    @Before
    public void setUp() {
        machine = new SlotMachine();
        machine.makeInvisible();
    }

    @After
    public void tearDown() {
        machine.makeInvisible();
    }

    /** Helper: a machine with the symbols red, blue and green. */
    private void addThreeSymbols() {
        machine.addSymbol(1, "red");
        machine.addSymbol(2, "blue");
        machine.addSymbol(3, "green");
    }

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------

    @Test
    public void testDefaultConstructorShouldCreateEmptyMachine() {
        assertEquals(0, machine.symbols().length);
        assertEquals(0, machine.configuration().length);
        assertFalse(machine.isJackpot());
        assertTrue(machine.ok());
    }

    @Test
    public void testConstructorWithNShouldCreateNWheelsAndNDifferentSymbols() {
        SlotMachine big = new SlotMachine(5);
        String[] symbols = big.symbols();
        assertEquals(5, symbols.length);
        assertEquals(5, big.configuration().length);

        // the five symbols must be different from each other
        ArrayList<String> seen = new ArrayList<String>();
        for (int i = 0; i < symbols.length; i++) {
            assertFalse(seen.contains(symbols[i]));
            seen.add(symbols[i]);
        }
        assertTrue(big.ok());
    }

    @Test
    public void testConstructorWithNShouldNeverStartInJackpot() {
        for (int i = 0; i < 200; i++) {
            SlotMachine three = new SlotMachine(3);
            assertFalse(three.isJackpot());
        }
    }

    @Test
    public void testConstructorWithNShouldInitializeRandomly() {
        // 30 machines of 6 wheels cannot all have the same configuration
        ArrayList<String> configurations = new ArrayList<String>();
        for (int i = 0; i < 30; i++) {
            SlotMachine random = new SlotMachine(6);
            String text = String.join(",", random.configuration());
            if (!configurations.contains(text)) {
                configurations.add(text);
            }
        }
        assertTrue(configurations.size() > 1);
    }

    @Test
    public void testConstructorWithNShouldAcceptFiftyWheels() {
        SlotMachine fifty = new SlotMachine(50);
        assertTrue(fifty.ok());
        assertEquals(50, fifty.symbols().length);
        assertEquals(50, fifty.configuration().length);
    }

    @Test
    public void testConstructorWithNShouldFailWhenNIsOutOfRange() {
        SlotMachine none = new SlotMachine(0);
        assertFalse(none.ok());
        assertEquals(0, none.symbols().length);

        SlotMachine tooBig = new SlotMachine(51);
        assertFalse(tooBig.ok());
        assertEquals(0, tooBig.configuration().length);
    }

    // ------------------------------------------------------------
    // Wheels
    // ------------------------------------------------------------

    @Test
    public void testAddWheelShouldCreateWheelShowingFirstSymbol() {
        machine.addSymbol(1, "red");
        machine.addSymbol(2, "blue");
        machine.addWheel(1);

        assertTrue(machine.ok());
        assertArrayEquals(new String[]{"red"}, machine.configuration());
    }

    @Test
    public void testAddWheelShouldAdjustPositionToTheNearestEnd() {
        addThreeSymbols();
        machine.addWheel(1);
        machine.placeSymbol(1, "blue");
        machine.addWheel(99);   // too big: goes to the end
        machine.addWheel(-5);   // too small: goes to the beginning

        assertTrue(machine.ok());
        // new wheels show red, the old one (blue) is now in the middle
        assertArrayEquals(new String[]{"red", "blue", "red"},
                          machine.configuration());
    }

    @Test
    public void testDelWheelShouldRemoveTheIndicatedWheel() {
        addThreeSymbols();
        machine.addWheel(1);
        machine.addWheel(2);
        machine.placeSymbol(1, "blue");

        machine.delWheel(1);

        assertTrue(machine.ok());
        assertArrayEquals(new String[]{"red"}, machine.configuration());
    }

    @Test
    public void testDelWheelShouldFailWhenThereAreNoWheels() {
        machine.delWheel(1);
        assertFalse(machine.ok());
    }

    @Test
    public void testSwapShouldExchangeTwoWheels() {
        addThreeSymbols();
        machine.addWheel(1);
        machine.addWheel(2);
        machine.placeSymbol(1, "blue");
        machine.placeSymbol(2, "green");

        machine.swap(1, 2);

        assertTrue(machine.ok());
        assertArrayEquals(new String[]{"green", "blue"},
                          machine.configuration());
    }

    @Test
    public void testSwapShouldFailWithTheSameWheelOrLessThanTwoWheels() {
        addThreeSymbols();
        machine.addWheel(1);
        machine.swap(1, 1);
        assertFalse(machine.ok());       // only one wheel

        machine.addWheel(2);
        machine.swap(2, 2);
        assertFalse(machine.ok());       // same wheel
    }

    @Test
    public void testLockShouldMakeSpinFailAndKeepTheSymbol() {
        addThreeSymbols();
        machine.addWheel(1);

        machine.lock(1);
        assertTrue(machine.ok());

        machine.spin(1, 1);
        assertFalse(machine.ok());
        assertArrayEquals(new String[]{"red"}, machine.configuration());
    }

    @Test
    public void testLockShouldFailWhenThereAreNoWheels() {
        machine.lock(1);
        assertFalse(machine.ok());
    }

    @Test
    public void testUnlockShouldAllowSpinningAgain() {
        addThreeSymbols();
        machine.addWheel(1);
        machine.lock(1);

        machine.unlock(1);
        assertTrue(machine.ok());

        machine.spin(1, 1);
        assertTrue(machine.ok());
        assertArrayEquals(new String[]{"blue"}, machine.configuration());
    }

    @Test
    public void testUnlockShouldFailWhenThereAreNoWheels() {
        machine.unlock(1);
        assertFalse(machine.ok());
    }

    // ------------------------------------------------------------
    // Symbols
    // ------------------------------------------------------------

    @Test
    public void testAddSymbolShouldInsertTheSymbolAtThePosition() {
        machine.addSymbol(1, "red");
        machine.addSymbol(1, "blue");

        assertTrue(machine.ok());
        assertArrayEquals(new String[]{"blue", "red"}, machine.symbols());
    }

    @Test
    public void testAddSymbolShouldAcceptTheNewColorsOfCycleThree() {
        machine.addSymbol(1, "orange");
        assertTrue(machine.ok());
        assertArrayEquals(new String[]{"orange"}, machine.symbols());
    }

    @Test
    public void testAddSymbolShouldFailWithRepeatedOrUnknownColor() {
        machine.addSymbol(1, "red");

        machine.addSymbol(2, "red");
        assertFalse(machine.ok());

        machine.addSymbol(2, "notacolor");
        assertFalse(machine.ok());

        assertArrayEquals(new String[]{"red"}, machine.symbols());
    }

    @Test
    public void testDelSymbolShouldRemoveTheSymbolOfThatColor() {
        machine.addSymbol(1, "red");
        machine.addSymbol(2, "blue");

        machine.delSymbol("red");

        assertTrue(machine.ok());
        assertArrayEquals(new String[]{"blue"}, machine.symbols());
    }

    @Test
    public void testDelSymbolShouldFailWhenTheColorDoesNotExist() {
        machine.addSymbol(1, "red");
        machine.delSymbol("green");
        assertFalse(machine.ok());
        assertArrayEquals(new String[]{"red"}, machine.symbols());
    }

    @Test
    public void testPlaceSymbolShouldShowThatSymbolOnTheWheel() {
        addThreeSymbols();
        machine.addWheel(1);

        machine.placeSymbol(1, "green");

        assertTrue(machine.ok());
        assertArrayEquals(new String[]{"green"}, machine.configuration());
    }

    @Test
    public void testPlaceSymbolShouldFailWithUnknownSymbolOrLockedWheel() {
        addThreeSymbols();
        machine.addWheel(1);

        machine.placeSymbol(1, "magenta");   // the machine has no magenta
        assertFalse(machine.ok());

        machine.lock(1);
        machine.placeSymbol(1, "blue");
        assertFalse(machine.ok());
        assertArrayEquals(new String[]{"red"}, machine.configuration());
    }

    // ------------------------------------------------------------
    // Spins
    // ------------------------------------------------------------

    @Test
    public void testSpinOneWheelShouldOnlyChangeThatWheel() {
        addThreeSymbols();
        machine.addWheel(1);
        machine.addWheel(2);

        machine.spin(1);

        assertTrue(machine.ok());
        // the second wheel keeps showing the first symbol
        assertEquals("red", machine.configuration()[1]);
    }

    @Test
    public void testSpinOneWheelShouldFailWhenThereAreNoWheels() {
        machine.spin(1);
        assertFalse(machine.ok());
    }

    @Test
    public void testSpinAllWheelsShouldSkipLockedWheels() {
        addThreeSymbols();
        machine.addWheel(1);
        machine.addWheel(2);
        machine.lock(1);

        machine.spin();

        assertTrue(machine.ok());
        assertEquals("red", machine.configuration()[0]);
    }

    @Test
    public void testSpinAllWheelsShouldFailWhenThereAreNoWheels() {
        machine.spin();
        assertFalse(machine.ok());
    }

    @Test
    public void testSpinWithStepsShouldRotateForwardAndBackwards() {
        addThreeSymbols();
        machine.addWheel(1);

        machine.spin(1, 4);            // 4 steps on 3 symbols = 1 step
        assertArrayEquals(new String[]{"blue"}, machine.configuration());

        machine.spin(1, -2);           // blue -> red -> green (wraps around)
        assertArrayEquals(new String[]{"green"}, machine.configuration());

        machine.spin(1, 3);            // a whole turn changes nothing
        assertArrayEquals(new String[]{"green"}, machine.configuration());
        assertTrue(machine.ok());
    }

    @Test
    public void testSpinWithStepsShouldFailWhenTheWheelIsLocked() {
        addThreeSymbols();
        machine.addWheel(1);
        machine.lock(1);

        machine.spin(1, 2);

        assertFalse(machine.ok());
        assertArrayEquals(new String[]{"red"}, machine.configuration());
    }

    @Test
    public void testSpinWithSetSymbolsShouldPlaceEachWheel() {
        addThreeSymbols();
        machine.addWheel(1);
        machine.addWheel(2);

        machine.spin(new String[]{"green", "blue"});

        assertTrue(machine.ok());
        assertArrayEquals(new String[]{"green", "blue"},
                          machine.configuration());
    }

    @Test
    public void testSpinWithSetSymbolsShouldFailWithWrongLength() {
        addThreeSymbols();
        machine.addWheel(1);
        machine.addWheel(2);

        machine.spin(new String[]{"green"});

        assertFalse(machine.ok());
        assertArrayEquals(new String[]{"red", "red"},
                          machine.configuration());
    }

    // ------------------------------------------------------------
    // Queries
    // ------------------------------------------------------------

    @Test
    public void testSymbolsShouldReturnTheColorsInOrder() {
        machine.addSymbol(1, "red");
        machine.addSymbol(2, "blue");
        machine.addSymbol(3, "green");

        assertArrayEquals(new String[]{"red", "blue", "green"},
                          machine.symbols());
        assertTrue(machine.ok());
    }

    @Test
    public void testDistinctSymbolsShouldCountTheDifferentColorsShown() {
        addThreeSymbols();
        machine.addWheel(1);
        machine.addWheel(2);
        machine.addWheel(3);
        machine.placeSymbol(2, "blue");

        assertEquals(2, machine.distinctSymbols());
        assertEquals(0, new SlotMachine().distinctSymbols());
    }

    @Test
    public void testConfigurationShouldReturnTheColorShownByEachWheel() {
        addThreeSymbols();
        machine.addWheel(1);
        machine.addWheel(2);
        machine.placeSymbol(2, "green");

        assertArrayEquals(new String[]{"red", "green"},
                          machine.configuration());
    }

    @Test
    public void testIsJackpotShouldBeTrueOnlyWhenAllWheelsShowTheSameSymbol() {
        assertFalse(machine.isJackpot());          // no wheels

        addThreeSymbols();
        machine.addWheel(1);
        machine.addWheel(2);
        assertTrue(machine.isJackpot());           // red, red

        machine.placeSymbol(2, "blue");
        assertFalse(machine.isJackpot());          // red, blue
    }

    // ------------------------------------------------------------
    // Visibility, exit and ok
    // ------------------------------------------------------------

    @Test
    public void testMakeVisibleShouldShowTheMachineAndMakeInvisibleHideIt() {
        // this is the only test that needs a screen
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());
        addThreeSymbols();
        machine.addWheel(1);

        machine.makeVisible();
        assertTrue(machine.ok());

        machine.makeInvisible();
        assertTrue(machine.ok());
    }

    @Test
    public void testMakeInvisibleShouldKeepTheMachineWorking() {
        addThreeSymbols();
        machine.addWheel(1);

        machine.makeInvisible();
        machine.spin(1, 1);

        assertTrue(machine.ok());
        assertArrayEquals(new String[]{"blue"}, machine.configuration());
    }

    @Test
    public void testExitShouldEndTheSimulatorSuccessfully() {
        addThreeSymbols();
        machine.addWheel(1);

        machine.exit();

        assertTrue(machine.ok());
    }

    @Test
    public void testOkShouldReflectTheResultOfTheLastOperation() {
        machine.delWheel(1);                 // fails: no wheels
        assertFalse(machine.ok());

        machine.addSymbol(1, "red");         // succeeds
        assertTrue(machine.ok());
    }

    // ------------------------------------------------------------
    // Circular rotation of the symbols (Cycle 3 fix)
    // ------------------------------------------------------------

    @Test
    public void testSpinWithStepsShouldFollowTheOrderOfTheSymbolsOnThreeWheels() {
        addThreeSymbols();
        machine.addWheel(1);
        machine.addWheel(2);
        machine.addWheel(3);

        // every wheel advances one symbol at a time: red, blue, green, red...
        String[] expected = {"blue", "green", "red", "blue"};
        for (int turn = 0; turn < expected.length; turn++) {
            machine.spin(1, 1);
            machine.spin(2, 1);
            machine.spin(3, 1);
            assertArrayEquals(new String[]{expected[turn], expected[turn],
                                           expected[turn]},
                              machine.configuration());
        }
    }

    @Test
    public void testSpinAllWheelsShouldAlwaysChangeEveryUnlockedWheel() {
        addThreeSymbols();
        machine.addWheel(1);
        machine.addWheel(2);
        machine.addWheel(3);

        for (int i = 0; i < 100; i++) {
            String[] before = machine.configuration();
            machine.spin();
            String[] after = machine.configuration();
            for (int wheel = 0; wheel < 3; wheel++) {
                assertFalse(before[wheel].equals(after[wheel]));
            }
        }
    }

    @Test
    public void testSpinOneWheelShouldAlwaysChangeItsSymbol() {
        addThreeSymbols();
        machine.addWheel(1);

        for (int i = 0; i < 100; i++) {
            String before = machine.configuration()[0];
            machine.spin(1);
            assertFalse(before.equals(machine.configuration()[0]));
        }
    }

    @Test
    public void testAddSymbolBeforeTheShownSymbolShouldNotChangeTheConfiguration() {
        addThreeSymbols();
        machine.addWheel(1);
        machine.addWheel(2);
        machine.placeSymbol(1, "blue");
        machine.placeSymbol(2, "green");

        machine.addSymbol(1, "black");     // inserted before both

        assertArrayEquals(new String[]{"blue", "green"},
                          machine.configuration());
        machine.spin(1, 1);                // next symbol of blue is green
        assertEquals("green", machine.configuration()[0]);
    }
}
