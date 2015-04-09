package linsolve.matlab;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;

import linsolve.Constraint;
import linsolve.ConstraintComparatorByPenalty;
import linsolve.LinearSpec;
import linsolve.OperatorType;
import linsolve.Summand;
import linsolve.Variable;

/**
 * The MatlabSolver class is a linear solver, which uses the Matlab linprog
 * solver to solve constraint-based GUIs. To connect to Matlab it uses the <a
 * href="http://code.google.com/p/matlabcontrol/">matlabcontrol library</a>.
 * Check the documentation of that library to learn more about the requirements
 * to use the MatlabSolver class, with regards to Matlab. Basically it requires
 * Matlab to be installed together with Matlab's Optimization toolbox.
 * 
 * The main contribution of <c>MatlabSolver</c> is the generation of
 * Matlab-commands and reading and writing back the calculated results.
 * 
 * The intended use of the class is to compare the results of editor solvers with
 * the runtime of matlabs linprog solver.
 * 
 * 
 * @author Johannes Mueller <jmue933@aucklanduni.ac.nz>
 * 
 */
public class MatlabLinProgSolver extends AbstractMatlabSolver {

	private static final double MAX_BOUND = 10000.0d;

	private Map<Constraint, Integer> posSlackVariableLookup;

	private Map<Constraint, Integer> negSlackVariableLookup;

	private int noOfVariables;
	
	private double[] variableValues = null;

	
	protected void prepare(){
		posSlackVariableLookup = createSlackVariableLookup();
		negSlackVariableLookup = createNegSlackVariableLookup();

		noOfVariables = getVariableLookup().size()
				+ posSlackVariableLookup.size()
				+ negSlackVariableLookup.size();
	}
	

	// Methods to create lookup tables for the tracking of variable numbers

	/**
	 * Creates a lookup table for the variables of the linear problem
	 * 
	 * @return a lookup table to track the number of the variables in the
	 *         matrices
	 */
	private Map<Constraint, Integer> createSlackVariableLookup() {
		Map<Constraint, Integer> variableLookup = new Hashtable<Constraint, Integer>();
		int i = 0;

		for (Constraint c : getLinearSpec().getConstraints()) {
			if (!Double.isInfinite(c.getPenalty())) {
				variableLookup.put(c, i++);
			}
		}

		return variableLookup;
	}

	/**
	 * Creates a lookup table for the slack variables of the linear problem.
	 * Slack variables are connected to a specific constraint.
	 * 
	 * @return a lookup table to track the number of the slack variables in the
	 *         matrices
	 */
	private Map<Constraint, Integer> createNegSlackVariableLookup() {
		Map<Constraint, Integer> variableLookup = new Hashtable<Constraint, Integer>();
		int i = 0;

		for (Constraint c : getLinearSpec().getConstraints()) {
			if (!Double.isInfinite(c.getPenalty())
					&& c.getOp() == OperatorType.EQ) {
				variableLookup.put(c, i++);
			}
		}

		return variableLookup;
	}

	/**
	 * Generates an array, which represents the objective function for the
	 * problem. All slack variables are summed up in the objective function.
	 * This sum is to be minimized. The coefficients of the slack variables are
	 * the penalty in case of positive slack variables and -1.0d * penalty in
	 * case of negative slack variables.
	 * 
	 */
	protected Double[] createObjectiveFunction() {
		Double[] objective = new Double[noOfVariables];

		for (int i = 0; i < getVariableLookup().size(); i++) {
			objective[i] = 0.0d;
		}

		int variableNo;
		for (Constraint c : posSlackVariableLookup.keySet()) {
			variableNo = getVariableLookup().size() + posSlackVariableLookup.get(c);
			objective[variableNo] = c.getPenalty();
		}

		for (Constraint c : negSlackVariableLookup.keySet()) {
			variableNo = getVariableLookup().size() + posSlackVariableLookup.size()
					+ negSlackVariableLookup.get(c);
			objective[variableNo] = -1.0d * c.getPenalty();
		}

		return objective;
	}

	/**
	 * Generates an Constraints object, which represents the inequality
	 * constraints of the problem. The object only containts those inequality
	 * constraints, which have an penalty infinity. All GE constraints are
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
			if (Double.isInfinite(c.getPenalty())
					&& c.getOp() != OperatorType.EQ) {
				Double[] a = createLHS(c, noOfVariables, getVariableLookup());
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
	 * of the problem. From the linearSpec equality constraints for soft
	 * constraints and already contained hard equality constraints are
	 * generated. A soft equality constraint is augmented by 2 slack variables.
	 * One positive and one negative. Soft inequality constraints are augmented
	 * with one positive slack variable.
	 */
	protected linsolve.matlab.AbstractMatlabSolver.Constraints createEqualityConstraints() {
		ArrayList<Double[]> A = new ArrayList<Double[]>();
		ArrayList<Double> B = new ArrayList<Double>();

		Double[] lhs = null;
		for (Constraint c : posSlackVariableLookup.keySet()) {
			lhs = createLHS(c, noOfVariables, getVariableLookup());
			lhs[getVariableLookup().size() + posSlackVariableLookup.get(c)] = c.getOp() == OperatorType.LE ? -1.0d
					: 1.0d; // Equality constraints need also slack coefficient
							// of 1.0d, which is automatically handled here
			if (negSlackVariableLookup.get(c) != null) {
				lhs[getVariableLookup().size() + posSlackVariableLookup.size()
					+ negSlackVariableLookup.get(c)] = 1.0d; // Only equalities
																// are in the
																// list, whose
																// negative
																// deviations
																// are handled
																// by a separate
																// slack
																// variable
			}

			A.add(lhs);
			B.add(c.getRightSide());
		}

		// The case where we have equality constraints with infinity penalty
		for (Constraint c : getLinearSpec().getConstraints()) {
			if (Double.isInfinite(c.getPenalty())
					&& c.getOp() == OperatorType.EQ) {
				lhs = createLHS(c, noOfVariables, getVariableLookup());
				A.add(lhs);
				B.add(c.getRightSide());
			}
		}

		Double[][] constraints = new Double[0][0];
		Double[] b = new Double[0];

		return new linsolve.matlab.AbstractMatlabSolver.Constraints(A.toArray(constraints), B.toArray(b));
	}

	/**
	 * Creates a vector of values for the lower bound. Original variables have a
	 * lower bound of 0.0d, positive slack variables have a lower bound of 0.0d
	 * and negative slack variables have a lower bound of -MAX_BOUND.
	 * 
	 * @return An array of double values representing the lower bound of the variables of the problem
	 */
	public Double[] createLowerBound() {
		Double[] result = new Double[noOfVariables];
		for (Variable v : getVariableLookup().keySet()) {
			result[getVariableLookup().get(v)] = 0.0d;
		}

		for (Constraint c : posSlackVariableLookup.keySet()) {
			result[getVariableLookup().size() + posSlackVariableLookup.get(c)] = 0.0d;
		}

		for (Constraint c : negSlackVariableLookup.keySet()) {
			result[getVariableLookup().size() + posSlackVariableLookup.size()
					+ negSlackVariableLookup.get(c)] = -MAX_BOUND;
		}

		return result;
	}

	/**
	 * Creates a vector of values for the upper bound. Original variables have a
	 * upper bound of MAX_BOUND, positive slack variables have a upper bound of
	 * MAX_BOUND and negative slack variables have a upper bound of 0.0d.
	 * 
	 * @return An array of double values representing the upper bounds of the variables of the problem
	 * 
	 */
	public Double[] createUpperBound() {
		Double[] result = new Double[noOfVariables];
		for (Variable v : getVariableLookup().keySet()) {
			result[getVariableLookup().get(v)] = MAX_BOUND;
		}

		for (Constraint c : posSlackVariableLookup.keySet()) {
			result[getVariableLookup().size() + posSlackVariableLookup.get(c)] = MAX_BOUND;
		}

		for (Constraint c : negSlackVariableLookup.keySet()) {
			result[getVariableLookup().size() + posSlackVariableLookup.size()
					+ negSlackVariableLookup.get(c)] = 0.0d;
		}

		return result;
	}


	

	
	/**
	 * writes the calculated values <code>x</code> back to the linearSpec
	 * object.
	 * 
	 * @param x
	 *            the results vector
	 */
	protected void setResult(Object x) {
		int i = 0;
		variableValues = (double[]) x; 
		for (Variable v : getVariableLookup().keySet()) {
			i = getVariableLookup().get(v);
			// We do not need the result of the slack variables.
			if (i >= 0 && i < variableValues.length) {
				v.setValue(variableValues[i]);
			}
		}
	}

	/**
	 * Prints the lookup tables for debugging purposes
	 */
	@Override
	protected String debugInfo() {
		StringBuilder sb = new StringBuilder();
		for (Variable v : getVariableLookup().keySet()) {
			sb.append(v.getName() + ": " + getVariableLookup().get(v) + ", ");
		}
		sb.append("\n");
		sb.append("Slackvariable Lookup Table");
		for (Constraint c : posSlackVariableLookup.keySet()) {
			sb.append(c.getName() + "(" + c.getRightSide() + "): "
					+ posSlackVariableLookup.get(c) + ", ");
		}
		sb.append("\n");
		return sb.toString();
	}




}

/* EOF */
