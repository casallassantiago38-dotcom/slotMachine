import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the Cycle 2 methods of SlotMachine.
 * All tests run in invisible mode so no dialogs or graphics
 * interrupt the run.
 *
 * @author Jhazael and Santiago
 * @version 1.0 (Cycle 2 - 2026-2)
 */
public class SlotMachineC2Test {

    private SlotMachine slotMachine;

    @Before
    public void setUp() {
        slotMachine = new SlotMachine();
        slotMachine.makeInvisible();
    }

    @After
    public void tearDown() {
        slotMachine.makeInvisible();
    }

    /**
     * Locking a wheel prevents spin(wheel, steps) from changing
     * configuration(); unlocking it allows the change again.
     */
    @Test
    public void shouldOnlyChangeConfigurationAfterUnlockingWheel() {
        slotMachine.addSymbol(1, "red");
        slotMachine.addSymbol(2, "blue");
        slotMachine.addSymbol(3, "green");
        slotMachine.addWheel(1);

        slotMachine.lock(1);
        String[] lockedConfiguration = slotMachine.configuration();
        slotMachine.spin(1, 1);
        assertFalse(slotMachine.ok());
        assertArrayEquals(lockedConfiguration, slotMachine.configuration());

        slotMachine.unlock(1);
        slotMachine.spin(1, 1);
        assertTrue(slotMachine.ok());
        assertEquals("blue", slotMachine.configuration()[0]);
    }

    /**
     * spin(String[]) can place every wheel in the same color at once,
     * and must fail atomically when the configuration is invalid.
     */
    @Test
    public void shouldReachJackpotWithSpinBySetSymbolsAndFailAtomicallyOtherwise() {
        slotMachine.addSymbol(1, "red");
        slotMachine.addSymbol(2, "blue");
        slotMachine.addWheel(1);
        slotMachine.addWheel(2);
        slotMachine.addWheel(3);

        slotMachine.spin(new String[]{"blue", "blue", "blue"});
        assertTrue(slotMachine.ok());
        assertTrue(slotMachine.isJackpot());

        String[] beforeFailure = slotMachine.configuration();
        slotMachine.spin(new String[]{"red", "red"});
        assertFalse(slotMachine.ok());
        assertArrayEquals(beforeFailure, slotMachine.configuration());
    }
}