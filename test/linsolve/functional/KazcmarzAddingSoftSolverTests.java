package linsolve.functional;

import linsolve.KaczmarzSolver;
import linsolve.functional.LinearProblems;
import linsolve.softconstraints.AddingSoftSolver;

public class KazcmarzAddingSoftSolverTests extends LinearProblems {
	
	@Override
	public void setup(){
		setLinearSolver(new AddingSoftSolver(new KaczmarzSolver()));
	}
}
