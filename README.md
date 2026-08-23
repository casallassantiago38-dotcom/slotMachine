------------------------------------------------------------------------
This is the project README file. Here, you should describe your project.
Tell the reader (someone who does not know anything about this project)
all they need to know. The comments should usually include at least:
------------------------------------------------------------------------

PROJECT TITLE: SLOTMACHINE

PURPOSE OF PROJECT: The purpose of the project it´s develop a slot machines simulator built for the assignment DOPO,
the proyect has the objective it´s make a functional simulator that allows us eliminate and 
add wheels, the problem was inspired by "Sloth Machine" of the ICPC World finals Baku at 2025
this version does not solve the hole problem it provides the machine that future solver would
operate.

VERSION or DATE: 1.0 - Cicle 1, Agust 2026

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
