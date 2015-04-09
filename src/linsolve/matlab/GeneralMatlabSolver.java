/**
 * 
 */
package linsolve.matlab;

import java.util.ArrayList;

import linsolve.Constraint;
import linsolve.OperatorType;

/**
 * This class provides a raw representation of the constraint problem. It only generates Ai, bi and Ae, be.
 * These matrices and vectors have all the coefficients of the equality and inequality constraints.
 * They can be further processed in Matlab to implement several solving algorithms and test them. The matlab
 * commands are implemented as strings in the method createMatlabSolverCommand()
 * 
 * @author Johannes Mueller <jmue933@aucklanduni.ac.nz>
 *
 */
public class GeneralMatlabSolver extends AbstractMatlabSolver {

	@Override
	protected void prepare() {
		// Noting to prepare
	}

	@Override
	protected String debugInfo() {
		// TODO Add some debug info in the future
		return "";
	}

	@Override
	protected Double[] createObjectiveFunction() {
		// No Objective function
		return new Double[0];
	}

	/**
	 * Generates an Constraints object, which represents the inequality
	 * constraints of the problem.  All GE constraints are
	 * transformed into LE constraints.
	 */
	protected linsolve.matlab.AbstractMatlabSolver.Constraints createInequalityConstraints() {
		ArrayList<Double[]> A = new ArrayList<Double[]>();
		ArrayList<Double> B = new ArrayList<Double>();

		// We do not need constraints to force slack variables to be greater
		// zero since we can specify a lower and upper bound

		Constraint c;
		// Create inequality constraints for inequality constraints with penalty
		// infinity
		for (int i = 0; i < getLinearSpec().getConstraints().size(); i++) {
			c = getLinearSpec().getConstraints().get(i);
			if(c.getOp() != OperatorType.EQ){
				Double[] a = createLHS(c, getVariableLookup().size(), getVariableLookup());
				// Make all inequality constraints LE constraints
					if (c.getOp() == OperatorType.GE) {
						for (int j = 0; j < a.length; j++)
							a[j] = -1.0d * a[j];
				}	
				A.add(a);
				B.add(c.getRightSide());
			}
		}

		Double[][] constraints = new Double[0][0];
		Double[] b = new Double[0];

		return new linsolve.matlab.AbstractMatlabSolver.Constraints(A.toArray(constraints), B.toArray(b));
	}
	
	/**
	 * Generates an Constraint object, which represents the equality constraints
	 * of the problem.
	 */
	protected linsolve.matlab.AbstractMatlabSolver.Constraints createEqualityConstraints() {
		ArrayList<Double[]> A = new ArrayList<Double[]>();
		ArrayList<Double> B = new ArrayList<Double>();

		Double[] lhs = null;

		for (Constraint c : getLinearSpec().getConstraints()) {
			if(c.getOp() == OperatorType.EQ){
				lhs = createLHS(c, getVariableLookup().size(), getVariableLookup());
				A.add(lhs);
				B.add(c.getRightSide());
			}
		}

		Double[][] constraints = new Double[0][0];
		Double[] b = new Double[0];

		return new linsolve.matlab.AbstractMatlabSolver.Constraints(A.toArray(constraints), B.toArray(b));
	}

	@Override
	public Double[] createLowerBound() {
		// No boundaries
		return new Double[0];
	}

	@Override
	protected Double[] createUpperBound() {
		// No boundaries
		return new Double[0];
	}

	/**
	 * creates a string with the matlab command to call a the linprog solver.
	 * It stores the result in a variable "x".
	 */
	protected String createMatlabSolverCommand(){
		
		// TODO seems to be added for testing purposes. Has to be removed
		// or added to an extra class for the cgs solver
		StringBuilder matlabCommandBuilder = new StringBuilder();
		matlabCommandBuilder.append("Ao = vertcat(Ae, Ai);");
		matlabCommandBuilder.append("bo = horzcat(be,bi);");
		matlabCommandBuilder.append("A = Ao'*Ao;");
		matlabCommandBuilder.append("b = Ao'*bo';");
		matlabCommandBuilder.append("x = cgs(A,b);");
		return matlabCommandBuilder.toString();
	}

}
