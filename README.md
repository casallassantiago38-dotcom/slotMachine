README

PROJECT TITLE: SLOTMACHINE

PURPOSE OF PROJECT: The purpose of the project it´s develop a slot machines simulator built for the assignment DOPO,
the proyect has the objective it´s make a functional simulator that allows us eliminate and 
add wheels, the problem was inspired by "Sloth Machine" of the ICPC World finals Baku at 2025
this version does not solve the hole problem it provides the machine that future solver would
operate.

VERSION or DATE: 3.0 - Cycle 3, September 2026

HOW TO START THIS PROJECT: Click an instance of the slotmachine class. The machines starts empty
and invisible, we develop a typical session to make it easier for you.

	makevisible()
	addSymbol(1, "red")
    	addSymbol(2, "blue")
    	addWheel(1)
    	addWheel(2)
    	addWheel(3)
    	spin()

AUTHORS: Azhael Peña y Santiago Casallas Jaramillo

USER INSTRUCTIONS:Symbols can be identified by colour only the seven colors canvas can draw only 
them are accepted and those are: red, black, yellow, green, magenta, white. Note that two simbols
may not share the same color.

The positions start at 1 and a position outside the valid range is clamped to the nearest allowed
value rather than rejected.

About the operations, every operation records whether it succeeded; call ok() right after an operation
to find out. When the simulator is visible, a failed operation also shows a dialog explaining what 
went wrong. When it is invisible, failures are silent and ok() is the only way to detect them.

The machine body turns yellow when the machine reaches a jackpot.


CYCLE 3 (REFACTORING AND EXTENSION)

New requirements:
  13. new SlotMachine(n) creates a machine with n wheels and n different symbols, initialized randomly
      (it never starts as a jackpot). n goes from 1 to 50.
  14. SlotMachineContest.solve(n) solves the marathon problem. It returns the actions {i, j} (rotate
      wheel i by j steps) that make all the wheels show the same symbol. The machine stays invisible.
  15. SlotMachineContest.simulate(n) plays the same solution on a VISIBLE machine, step by step.
      Every step takes a moment to draw, so use small values of n (3 to 6).

Design decisions:
  - TestingTool (new interface) has only spin(wheel, steps) and distinctSymbols(). SlotMachine implements it
    and the solver only sees the machine through it, so the solver cannot use anything else of the machine.
  - Palette (new class) has 50 CSS colors, because a machine of n wheels needs n different symbols.
    Canvas asks Palette for the colors it does not know.

Typical session:
	int[][] actions = SlotMachineContest.solve(5);
	SlotMachineContest.simulate(4);

Tests: SlotMachineTest, WheelTest, SymbolTest, PaletteTest, SlotMachineContestTest, SlotMachineContestCTest
(plus the tests of Cycle 2). They run with the machine invisible; only the tests that must draw something
(makeVisible and simulate) need a screen and are skipped when there is none.
