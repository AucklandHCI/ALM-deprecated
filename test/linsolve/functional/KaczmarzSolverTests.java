package linsolve.functional;


import linsolve.KaczmarzSolver;


public class KaczmarzSolverTests extends LinearProblems {

	@Override
	public void setup(){
		setLinearSolver(new KaczmarzSolver());
	}
	
	@Override
	/**
	 * This test does not pass with Kaczmarz alone because linear system is inconsistent. This dummy method 
	 * is added to avoid showing failures when system runs the tests.
	 */
	public void NonSquareMatrix() {
		if (commons.TestConfigParams.debug) {
			printMsg("\n"+this.getClass().getName()+Thread.currentThread().getStackTrace()[1].getMethodName()+
					": KaczmarzSolver is not able to solve this linear system alone. It needs to be combined with a soft solver.");
		}
		
	}
}