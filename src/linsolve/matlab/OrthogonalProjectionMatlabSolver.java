package linsolve.matlab;

public class OrthogonalProjectionMatlabSolver extends GeneralMatlabSolver {

	/**
	 * creates a string with the matlab command to call a the linprog solver.
	 * It stores the result in a variable "x".
	 */
	protected String createMatlabSolverCommand(){
		StringBuilder matlabCommandBuilder = new StringBuilder();
		
		// TODO add you implementation for the orthogonal projection method
		// here
		
		/*
		 * just some example commands in matlab
		 * 		
		Ae is the coefficient matrix of the equalities
		Ai is the coefficient matrix of the inequaltities
		be and bi respectively
		
		matlabCommandBuilder.append("Ao = vertcat(Ae, Ai);");
		matlabCommandBuilder.append("bo = horzcat(be,bi);");
		matlabCommandBuilder.append("A = Ao'*Ao;");
		matlabCommandBuilder.append("b = Ao'*bo';");
		matlabCommandBuilder.append("x = cgs(A,b);");
		*/
		
		return matlabCommandBuilder.toString();
	}
}
