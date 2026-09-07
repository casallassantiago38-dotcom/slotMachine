import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Shared unit tests contributed to the course wiki.
 * All tests run in invisible mode.
 *
 * @author Jhazael and Santiago
 * @version 1.0 (Cycle 2 - 2026-2)
 */
public class SlotMachineCC2Test {

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
     * Swapping two unlocked wheels, locking and unlocking one, and
     * spinning it afterwards should all succeed.
     */
    @Test
    public void accordingCjPmShouldSwapLockUnlockAndSpinSuccessfully() {
        slotMachine.addSymbol(1, "red");
        slotMachine.addSymbol(2, "blue");
        slotMachine.addWheel(1);
        slotMachine.addWheel(2);

        slotMachine.swap(1, 2);
        assertTrue(slotMachine.ok());

        slotMachine.lock(1);
        slotMachine.unlock(1);
        assertTrue(slotMachine.ok());

        slotMachine.spin(1, 1);
        assertTrue(slotMachine.ok());
        assertEquals("blue", slotMachine.configuration()[0]);
    }

    /**
     * Spinning a locked wheel with spin(wheel, steps) must fail and
     * leave what that wheel shows unchanged.
     */
    @Test
    public void accordingCjPmShouldNotSpinLockedWheel() {
        slotMachine.addSymbol(1, "red");
        slotMachine.addSymbol(2, "blue");
        slotMachine.addWheel(1);

        slotMachine.lock(1);
        String before = slotMachine.configuration()[0];

        slotMachine.spin(1, 3);

        assertFalse(slotMachine.ok());
        assertEquals(before, slotMachine.configuration()[0]);
    }
}