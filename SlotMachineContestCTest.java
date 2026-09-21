import static org.junit.Assert.*;
import org.junit.Assume;
import org.junit.Test;

import java.awt.GraphicsEnvironment;

/**
 * Shared unit tests (published on the course wiki / exchanged between
 * groups). They only use the public methods of the design:
 * SlotMachineContest.solve(n) and SlotMachineContest.simulate(n).
 * All the machines used here are invisible, except the simulate case,
 * which by definition needs a screen and is skipped when there is none.
 *
 * Note: several other tests received from classmates (Grupos
 * GomezB-CarreroC, CortazarJ-MartinezC and BustosL-GomezG) were checked
 * against this design and discarded because they assume behaviour that
 * contradicts the assignment: that symbols().length is n*n instead of
 * n, that all wheels can start showing the same color (the constructor
 * must never start in a jackpot), that solve never proposes more than
 * n actions, or that solve never returns a negative number of steps
 * (TestingTool.spin explicitly allows negative steps). Only the two
 * tests below (Grupo CañonA-PaezP) hold for this implementation.
 *
 * @author Jhazael and Santiago
 * @version 2.0 (Cycle 3 - 2026-2)
 */
public class SlotMachineContestCTest {

    /**
     * For a small machine, every action must be a pair {wheel, steps}
     * with the wheel between 1 and n, and the total must respect the
     * limit of the marathon.
     */
    @Test
    public void accordingCjPmShouldSolveSmallMachineWithValidActions() {
        int n = 4;
        int[][] actions = SlotMachineContest.solve(n);

        assertTrue(actions.length <= 10000);
        for (int i = 0; i < actions.length; i++) {
            assertEquals(2, actions[i].length);
            assertTrue(actions[i][0] >= 1 && actions[i][0] <= n);
        }
    }

    /**
     * The biggest machine of the marathon (50 wheels) must be solved
     * with at most 10 000 actions.
     */
    @Test
    public void accordingCjPmShouldSolveTheBiggestMachineWithinTheLimit() {
        int[][] actions = SlotMachineContest.solve(50);

        assertTrue(actions.length > 0);
        assertTrue(actions.length <= 10000);
    }

    /**
     * Shared by Grupo CañonA-PaezP: every move solve(n) proposes must
     * be a valid {wheel, steps} pair (length 2).
     */
    @Test
    public void accordingCaPpShouldReturnValidMovesStructure() {
        int n = 3;
        int[][] moves = SlotMachineContest.solve(n);

        assertNotNull(moves);
        for (int[] move : moves) {
            assertEquals(2, move.length);
        }
    }

    /**
     * Shared by Grupo CañonA-PaezP: simulate(n) must run without
     * throwing any error. It needs a screen, so it is skipped when the
     * computer running the tests has none (same rule already used in
     * SlotMachineContestTest).
     */
    @Test
    public void accordingCaPpShouldRunSimulationWithoutErrors() {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());

        int n = 3;
        SlotMachineContest.simulate(n);
    }
}
