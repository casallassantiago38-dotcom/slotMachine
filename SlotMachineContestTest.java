import static org.junit.Assert.*;
import org.junit.Assume;
import org.junit.Test;

import java.awt.GraphicsEnvironment;

/**
 * Unit tests for SlotMachineContest (solve and simulate).
 * The machines used by the tests are invisible. The only exception is
 * the test of simulate, which by definition needs a visible machine
 * (it is skipped when the computer has no screen).
 *
 * @author Jhazael and Santiago
 * @version 1.0 (Cycle 3 - 2026-2)
 */
public class SlotMachineContestTest {

    // Maximum number of actions accepted by the marathon problem.
    private static final int MAX_ACTIONS = 10000;

    /**
     * A tiny machine that is NOT a SlotMachine. It only offers the two
     * operations of the testing tool and counts how many times each one
     * is used. It proves that the solver only needs those two methods.
     */
    private static class FakeTool implements TestingTool {
        private int[] positions;
        private int symbols;
        private int spinCalls;
        private int distinctCalls;

        FakeTool(int[] positions) {
            this.positions = positions.clone();
            this.symbols = positions.length;
        }

        public void spin(int wheel, int steps) {
            spinCalls++;
            int moved = positions[wheel - 1] + steps;
            positions[wheel - 1] = ((moved % symbols) + symbols) % symbols;
        }

        public int distinctSymbols() {
            distinctCalls++;
            boolean[] seen = new boolean[symbols];
            int count = 0;
            for (int i = 0; i < positions.length; i++) {
                if (!seen[positions[i]]) {
                    seen[positions[i]] = true;
                    count++;
                }
            }
            return count;
        }
    }

    /**
     * Helper: builds a machine of n wheels (n = positions.length) and
     * leaves wheel i showing the symbol number positions[i].
     */
    private SlotMachine buildMachine(int[] positions) {
        SlotMachine machine = new SlotMachine(positions.length);
        String[] colors = machine.symbols();
        String[] configuration = new String[positions.length];
        for (int i = 0; i < positions.length; i++) {
            configuration[i] = colors[positions[i]];
        }
        machine.spin(configuration);
        return machine;
    }

    /**
     * Helper: applies a list of actions to a machine with spin(wheel, steps).
     */
    private void applyActions(SlotMachine machine, int[][] actions) {
        for (int i = 0; i < actions.length; i++) {
            machine.spin(actions[i][0], actions[i][1]);
        }
    }

    /**
     * Helper: tries EVERY initial configuration of a machine of n wheels
     * (except the ones that are already a jackpot) and checks that the
     * solver wins each of them within the limit of actions.
     */
    private void checkAllConfigurations(int n) {
        int total = 1;
        for (int i = 0; i < n; i++) {
            total = total * n;
        }
        for (int code = 0; code < total; code++) {
            // decode the number into the position of each wheel (base n)
            int[] positions = new int[n];
            int rest = code;
            boolean allEqual = true;
            for (int i = 0; i < n; i++) {
                positions[i] = rest % n;
                rest = rest / n;
                if (positions[i] != positions[0]) {
                    allEqual = false;
                }
            }
            if (allEqual) {
                continue;   // the problem never starts already won
            }
            SlotMachine machine = buildMachine(positions);
            int[][] actions = SlotMachineContest.solve(machine, n);
            assertTrue(machine.isJackpot());
            assertTrue(actions.length <= MAX_ACTIONS);
        }
    }

    // ------------------------------------------------------------
    // solve(n)
    // ------------------------------------------------------------

    @Test
    public void testSolveWithThreeWheelsShouldReturnValidSteps() {
        int[][] actions = SlotMachineContest.solve(3);

        assertTrue(actions.length >= 1);
        assertTrue(actions.length <= MAX_ACTIONS);
        for (int i = 0; i < actions.length; i++) {
            assertEquals(2, actions[i].length);
            assertTrue(actions[i][0] >= 1 && actions[i][0] <= 3);
        }
    }

    @Test
    public void testSolveShouldReturnNoActionsWhenNIsOutOfRange() {
        assertEquals(0, SlotMachineContest.solve(0).length);
        assertEquals(0, SlotMachineContest.solve(-4).length);
        assertEquals(0, SlotMachineContest.solve(51).length);
    }

    @Test
    public void testSolveWithOneWheelShouldNeedNoActions() {
        // a machine with a single wheel is always a jackpot
        assertEquals(0, SlotMachineContest.solve(1).length);
    }

    @Test
    public void testSolveShouldWinEveryConfigurationOfTwoWheels() {
        checkAllConfigurations(2);
    }

    @Test
    public void testSolveShouldWinEveryConfigurationOfThreeWheels() {
        checkAllConfigurations(3);
    }

    @Test
    public void testSolveShouldWinEveryConfigurationOfFourWheels() {
        checkAllConfigurations(4);
    }

    @Test
    public void testSolveShouldWinEveryConfigurationOfFiveWheels() {
        checkAllConfigurations(5);
    }

    @Test
    public void testSolveShouldWinRandomMachinesOfManySizes() {
        int[] sizes = {6, 7, 10, 17, 25, 33, 50};
        for (int s = 0; s < sizes.length; s++) {
            SlotMachine machine = new SlotMachine(sizes[s]);
            int[][] actions = SlotMachineContest.solve(machine, sizes[s]);
            assertTrue(machine.isJackpot());
            assertTrue(actions.length <= MAX_ACTIONS);
        }
    }

    @Test
    public void testSolveShouldNotExceedTheLimitWithFiftyWheels() {
        for (int i = 0; i < 5; i++) {
            int[][] actions = SlotMachineContest.solve(50);
            assertTrue(actions.length > 0);
            assertTrue(actions.length <= MAX_ACTIONS);
        }
    }

    @Test
    public void testSolveShouldWinWhenWheelsShowOnlyTwoDifferentSymbols() {
        // hard case: big groups of equal wheels
        int n = 12;
        int[] positions = new int[n];
        for (int i = 0; i < n; i++) {
            positions[i] = (i % 2 == 0) ? 3 : 8;
        }
        SlotMachine machine = buildMachine(positions);

        SlotMachineContest.solve(machine, n);

        assertTrue(machine.isJackpot());
    }

    @Test
    public void testSolveShouldReturnActionsThatWinWhenReplayed() {
        int[] positions = {2, 0, 2, 4, 1, 4};
        SlotMachine solved = buildMachine(positions);
        int[][] actions = SlotMachineContest.solve(solved, 6);

        // an identical machine, played with the returned actions
        SlotMachine replay = buildMachine(positions);
        applyActions(replay, actions);

        assertTrue(replay.isJackpot());
    }

    @Test
    public void testSolveShouldStopAtTheFirstJackpot() {
        int[] positions = {1, 3, 0, 3, 2};
        SlotMachine solved = buildMachine(positions);
        int[][] actions = SlotMachineContest.solve(solved, 5);

        SlotMachine replay = buildMachine(positions);
        for (int i = 0; i < actions.length; i++) {
            assertFalse(replay.isJackpot());   // not won before the last action
            replay.spin(actions[i][0], actions[i][1]);
        }
        assertTrue(replay.isJackpot());
    }

    @Test
    public void testSolveShouldReturnNoActionsWhenTheMachineIsAlreadyWon() {
        SlotMachine machine = buildMachine(new int[]{2, 2, 2, 2});

        int[][] actions = SlotMachineContest.solve(machine, 4);

        assertEquals(0, actions.length);
    }

    @Test
    public void testSolveShouldOnlyNeedTheTwoMethodsOfTheTestingTool() {
        FakeTool tool = new FakeTool(new int[]{0, 4, 4, 1, 2, 2, 0});

        int[][] actions = SlotMachineContest.solve(tool, 7);

        assertEquals(1, tool.distinctSymbols());
        // one spin per action recorded
        assertEquals(actions.length, tool.spinCalls);
        assertTrue(tool.distinctCalls >= actions.length);
    }

    @Test
    public void testSolveShouldUseShortRotations() {
        int[][] actions = SlotMachineContest.solve(new SlotMachine(9), 9);
        for (int i = 0; i < actions.length; i++) {
            assertTrue(Math.abs(actions[i][1]) <= 4);
        }
    }

    // ------------------------------------------------------------
    // simulate(n)
    // ------------------------------------------------------------

    @Test
    public void testSimulateShouldPlayTheSolutionOnAVisibleMachine() {
        // simulate is visual by definition: it needs a screen
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());

        SlotMachineContest.simulate(3);

        // hide the window again so it does not stay open after the test
        Canvas.getCanvas().setVisible(false);
    }

    @Test
    public void testSimulateShouldDoNothingWhenNIsOutOfRange() {
        // no machine is built, so no window is opened
        SlotMachineContest.simulate(0);
        SlotMachineContest.simulate(51);
    }
}
