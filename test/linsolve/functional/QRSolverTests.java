package linsolve.functional;


import linsolve.QRSolver;



public class QRSolverTests extends LinearProblems {

	@Override
	public void setup(){
		setLinearSolver(new QRSolver());
	}
	
	@Override
	public void NonSquareMatrix() {
		if (commons.TestConfigParams.debug) {
			printMsg("\n"+this.getClass().getName()+Thread.currentThread().getStackTrace()[1].getMethodName()+
					": QRSolver does not work with this prototype because the system is inconsistent.");
		}		
	}
	
	@Override
	public void simpleinfeasible() {
		if (commons.TestConfigParams.debug) {
			printMsg("\n"+this.getClass().getName()+Thread.currentThread().getStackTrace()[1].getMethodName()+
					": QRSolver does not work with this prototype because the system is inconsistent.");
		}		
	}

}