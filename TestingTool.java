/**
 * The only two operations the marathon solver is allowed to use on a
 * slot machine: rotate one wheel and ask the friend how many different
 * symbols she can see. This is the "testing tool" of the problem.
 *
 * SlotMachine implements this interface, so SlotMachineContest.solve
 * can only see these two methods and can never (even by mistake) use
 * the rest of the simulator to solve the problem. Any other object
 * that offers these two operations can be solved as well.
 *
 * @author Jhazael and Santiago
 * @version 1.0 (Cycle 3 - 2026-2)
 */
public interface TestingTool {

    /**
     * Rotates a wheel a number of steps.
     *
     * @param wheel position of the wheel (starts at 1).
     * @param steps how many symbols the wheel moves; negative values
     *              rotate in the opposite direction.
     */
    void spin(int wheel, int steps);

    /**
     * @return how many different symbols are currently shown by the
     * wheels (the number the friend tells us in the problem).
     */
    int distinctSymbols();
}
