package linsolve.functional;

import linsolve.KaczmarzSolver;
import linsolve.softconstraints.GroupingSoftSolver;

public class KazcmarzGroupingSoftSolverTests extends LinearProblems {

	@Override
	public void setup(){
		setLinearSolver(new GroupingSoftSolver(new KaczmarzSolver()));
	}
}
