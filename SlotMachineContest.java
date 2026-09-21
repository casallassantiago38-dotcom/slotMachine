import java.util.ArrayList;

/**
 * Solver and simulator of Problem I (Slot Machine) of the 2025 ICPC
 * World Finals.
 *
 * The problem: a machine has n wheels and each wheel has the same n
 * symbols in the same circular order. We cannot see the wheels. We can
 * only rotate a wheel any number of steps and, after every action, we
 * are told HOW MANY DIFFERENT symbols are being shown (k). We must make
 * all the wheels show the same symbol (k = 1) using at most 10 000
 * actions.
 *
 * Idea of the solution (the wheels are positions on a circle of n
 * places, and k is the number of different places that are occupied):
 *
 *   Phase 1 - Spread. Move the wheels until every wheel is alone on
 *             its place (k = n). Since there are n wheels and n
 *             places, from that moment EVERY place is occupied.
 *   Phase 2 - Measure. Move wheel 1 one step: it lands on another
 *             wheel and leaves exactly ONE empty place (the place
 *             where wheel 1 was). Now, scanning any other wheel step
 *             by step, k tells us which step makes that wheel land on
 *             the empty place, so we learn where wheel 1 was.
 *   Phase 3 - Gather. With that information, every wheel is rotated
 *             once onto the place where wheel 1 is now.
 *
 * The solver only uses the two operations of the testing tool
 * (see TestingTool). It needs at most n*n + n*n + n actions, about
 * 5 000 when n = 50.
 *
 * @author Jhazael and Santiago
 * @version 1.0 (Cycle 3 - 2026-2)
 */
public class SlotMachineContest {

    // Largest machine of the marathon problem (and of the Palette).
    private static final int MAX_WHEELS = 50;

    /**
     * Solves the marathon problem. It builds a random machine of n
     * wheels and n symbols, keeps it INVISIBLE, and uses it only as the
     * testing tool.
     *
     * @param n number of wheels and symbols (from 1 to 50).
     * @return the actions needed to win, in order. Each action is an
     * array {i, j}: rotate wheel i (starts at 1) j steps, negative
     * values rotate backwards. It is empty if n is out of range.
     */
    public static int[][] solve(int n) {
        if (n < 1 || n > MAX_WHEELS) {
            return new int[0][];
        }
        // the constructor leaves the machine invisible
        SlotMachine machine = new SlotMachine(n);
        return solve(machine, n);
    }

    /**
     * Simulates the solution: builds a random machine of n wheels and
     * n symbols, makes it VISIBLE, and lets the solver play step by
     * step, so every rotation can be watched on the screen.
     *
     * Note: with the machine visible every step of a rotation takes a
     * fraction of a second, so it is recommended to use small values
     * of n (for example between 3 and 6).
     *
     * @param n number of wheels and symbols (from 1 to 50).
     */
    public static void simulate(int n) {
        if (n < 1 || n > MAX_WHEELS) {
            return;
        }
        SlotMachine machine = new SlotMachine(n);
        // the only two SlotMachine methods used here as simulator
        machine.makeVisible();
        // the solver spins the visible machine, so the user sees it
        solve(machine, n);
    }

    /**
     * Solves the problem over any testing tool. This is the method that
     * really contains the algorithm; solve(n) and simulate(n) only
     * decide if the machine is visible or invisible. It is public so
     * the algorithm can be tested with machines whose initial
     * configuration is known.
     *
     * @param tool the machine, seen only through the testing tool.
     * @param n    number of wheels (and symbols) of the machine.
     * @return the actions performed, in order. It stops as soon as all
     * the wheels show the same symbol, like the judge of the marathon.
     */
    public static int[][] solve(TestingTool tool, int n) {
        ArrayList<int[]> actions = new ArrayList<int[]>();

        // first number that the friend tells us
        int k = tool.distinctSymbols();

        if (k > 1) {
            k = spreadWheels(tool, n, k, actions);
        }
        int[] offsets = new int[n + 1];
        if (k > 1) {
            k = measureOffsets(tool, n, actions, offsets);
        }
        if (k > 1) {
            k = gatherWheels(tool, n, actions, offsets);
        }
        return actions.toArray(new int[actions.size()][]);
    }

    /**
     * PHASE 1. Leaves every wheel showing a symbol that no other wheel
     * shows (k = n).
     *
     * For each wheel we rotate it one step at a time and watch k:
     *  - if k grows, the wheel just moved from a symbol shared with
     *    other wheels to a free symbol: we leave it there and go on;
     *  - if we complete a whole turn and k never grew, the wheel was
     *    already alone: one more step puts it back where it was.
     * A wheel only moves to FREE symbols, so a wheel that is alone
     * never stops being alone, and one pass over the wheels is enough.
     *
     * @param tool    the machine.
     * @param n       number of wheels.
     * @param k       current number of distinct symbols.
     * @param actions list where the actions performed are recorded.
     * @return the last value of k received.
     */
    private static int spreadWheels(TestingTool tool, int n, int k,
                                    ArrayList<int[]> actions) {
        for (int wheel = 1; wheel <= n; wheel++) {
            if (k == n) {
                // all the symbols are already different
                return k;
            }
            int kBefore = k;
            boolean moved = false;

            // try the n-1 other symbols of this wheel, one step each
            for (int step = 1; step <= n - 1 && !moved; step++) {
                k = act(tool, actions, n, wheel, 1);
                if (k == 1) {
                    return k;
                }
                if (k > kBefore) {
                    moved = true;
                }
            }
            if (!moved) {
                // the wheel was alone: it is n-1 steps away from its
                // first symbol, one more step closes the circle
                k = act(tool, actions, n, wheel, 1);
                if (k == 1) {
                    return k;
                }
            }
        }
        return k;
    }

    /**
     * PHASE 2. Requires k = n (every wheel alone, no empty symbol).
     *
     * Wheel 1 is moved one step. It lands on the symbol of another
     * wheel, so now k = n - 1 and there is exactly one EMPTY symbol:
     * the one wheel 1 was showing before. For every other wheel b we
     * rotate it one step at a time and record k:
     *  - if b is alone, k = n - 1 while it moves onto the empty
     *    symbol and k = n - 2 when it moves onto any other symbol;
     *  - if b is the wheel that shares its symbol with wheel 1, k = n
     *    when it moves onto the empty symbol and k = n - 1 otherwise.
     * In both cases only one step behaves differently, and it is the
     * one that takes b to the empty symbol. We keep that number of
     * steps and put b back where it was.
     *
     * @param tool    the machine.
     * @param n       number of wheels.
     * @param actions list where the actions performed are recorded.
     * @param offsets output: offsets[b] is how many steps wheel b needs
     *                to reach the empty symbol (wheels 2 to n).
     * @return the last value of k received.
     */
    private static int measureOffsets(TestingTool tool, int n,
                                      ArrayList<int[]> actions,
                                      int[] offsets) {
        // wheel 1 leaves its place and lands on another wheel
        int k = act(tool, actions, n, 1, 1);
        if (k == 1) {
            return k;
        }

        for (int wheel = 2; wheel <= n; wheel++) {
            // k received after each of the n-1 steps (index = step)
            int[] received = new int[n];
            for (int step = 1; step <= n - 1; step++) {
                k = act(tool, actions, n, wheel, 1);
                if (k == 1) {
                    return k;
                }
                received[step] = k;
            }

            // look for the special step
            int special = 0;
            for (int step = 1; step <= n - 1; step++) {
                if (received[step] == n) {
                    // the wheel was sharing with wheel 1
                    special = step;
                }
            }
            if (special == 0) {
                for (int step = 1; step <= n - 1; step++) {
                    if (received[step] == n - 1) {
                        // the wheel was alone
                        special = step;
                    }
                }
            }
            offsets[wheel] = special;

            // the wheel is n-1 steps away from where it started: one
            // more step puts it back
            k = act(tool, actions, n, wheel, 1);
            if (k == 1) {
                return k;
            }
        }
        return k;
    }

    /**
     * PHASE 3. Wheel 1 is one step ahead of the empty symbol, and every
     * other wheel knows how far the empty symbol is (offsets). So each
     * wheel needs offsets[b] + 1 steps to reach wheel 1. If that number
     * is a whole turn the wheel is already there.
     *
     * @param tool    the machine.
     * @param n       number of wheels.
     * @param actions list where the actions performed are recorded.
     * @param offsets steps to the empty symbol, from phase 2.
     * @return the last value of k received (1 when the machine wins).
     */
    private static int gatherWheels(TestingTool tool, int n,
                                    ArrayList<int[]> actions,
                                    int[] offsets) {
        int k = 0;
        for (int wheel = 2; wheel <= n; wheel++) {
            int steps = (offsets[wheel] + 1) % n;
            if (steps != 0) {
                k = act(tool, actions, n, wheel, steps);
                if (k == 1) {
                    return k;
                }
            }
        }
        return k;
    }

    /**
     * Performs ONE action of the problem: rotates a wheel, records the
     * action and asks the friend how many different symbols there are.
     * The rotation is written with the shortest number of steps (for
     * example 9 steps on 10 symbols is -1), which is the same rotation
     * but shorter to watch in the simulation.
     *
     * @param tool    the machine.
     * @param actions list where the action is recorded.
     * @param n       number of symbols of each wheel.
     * @param wheel   wheel to rotate (starts at 1).
     * @param steps   steps to rotate.
     * @return the number of distinct symbols after the action.
     */
    private static int act(TestingTool tool, ArrayList<int[]> actions,
                           int n, int wheel, int steps) {
        int shortest = shortestSteps(steps, n);
        actions.add(new int[]{wheel, shortest});
        tool.spin(wheel, shortest);
        return tool.distinctSymbols();
    }

    /**
     * Rewrites a number of steps as the equivalent number with the
     * smallest absolute value, taking into account that the wheel has n
     * symbols and repeats after n steps.
     *
     * @param steps any number of steps.
     * @param n     number of symbols of the wheel.
     * @return an equivalent number of steps between -n/2 and n/2.
     */
    private static int shortestSteps(int steps, int n) {
        // remainder in the range 0..n-1
        int remainder = ((steps % n) + n) % n;
        if (remainder > n / 2) {
            remainder = remainder - n;
        }
        return remainder;
    }
}
