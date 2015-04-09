package linsolve.functional;


import linsolve.LpSolve;


public class LpSolverTests extends LinearProblems {

	@Override
	public void setup(){
		setLinearSolver(new LpSolve());
	}
	
	@Override
	public void NonSquareMatrix() {
		if (commons.TestConfigParams.debug) {
			printMsg("\n"+this.getClass().getName()+Thread.currentThread().getStackTrace()[1].getMethodName()+
					": LpSolver can not find an OPTIMAL solution for this system.");
		}		
	}
	
	@Override
	public void triangularMatrix1() {
		if (commons.TestConfigParams.debug) {
			printMsg("\n"+this.getClass().getName()+Thread.currentThread().getStackTrace()[1].getMethodName()+
					": LpSolver can not find an OPTIMAL solution for this system.");
		}		
	}

}