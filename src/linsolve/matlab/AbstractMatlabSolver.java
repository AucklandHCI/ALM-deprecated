package linsolve.matlab;

import java.util.Hashtable;
import java.util.Map;

import linsolve.AbstractLinearSolver;
import linsolve.Constraint;
import linsolve.ResultType;
import linsolve.Summand;
import linsolve.Variable;
import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;
import matlabcontrol.MatlabProxy;
import matlabcontrol.MatlabProxyFactory;

/**
 * The AbstractMatlabSolver class is an abstract base class for solvers, which use Matlab
 * code to solve constraint-based GUIs. To connect to Matlab it uses the <a
 * href="http://code.google.com/p/matlabcontrol/">matlabcontrol library</a>.
 * Check the documentation of that library to learn more about the requirements
 * to use the MatlabSolver class, with regards to Matlab. Basically it requires
 * Matlab to be installed together with Matlab's Optimization toolbox.
 * 
 * The main contribution of <c>MatlabSolver</c> is the generation of
 * Matlab-commands and reading and writing back the calculated results.
 * 
 * The intended use of the class is to compare the results of editor solvers with
 * the runtime of matlab solvers.
 * 
 * 
 * @author Johannes Mueller <jmue933@aucklanduni.ac.nz>
 * 
 */
public abstract class AbstractMatlabSolver extends AbstractLinearSolver {
	
	private static final boolean DEBUG = false;

	/**
	 * The matlab proxy to communicate with matlab
	 */
	private MatlabProxy proxy;

	/**
	 * The factory, which produces proxies to connect with matlab
	 */
	private MatlabProxyFactory factory;

	/**
	 * The last solving time
	 */
	private long solvingTime = 0;
		
	private Map<Variable, Integer> variableLookup;
	
	/**
	 * @see linsolve.AbstractLinearSolver#doSolve()
	 * 
	 */
	@Override
	protected ResultType doSolve() {

		ResultType resultType = ResultType.OPTIMAL;
		connectToMatlab();

		try {
			variableLookup = createVariableLookup();
			prepare();

			// Generate matrices and vectors, which represent the linearSpec in
			// linprogs required form
			

			StringBuilder matlabCommandBuilder = new StringBuilder();
			
			// First all the matrices and vectors
			matlabCommandBuilder.append(createObjective());
			matlabCommandBuilder.append(createInequalityMatrixAndVector());
			matlabCommandBuilder.append(createEqualityMatrixAndVector());
			matlabCommandBuilder.append(createBoundVectors());
			
			matlabCommandBuilder.append(createMatlabCommand());
			String matlabCommand = matlabCommandBuilder.toString();
			
			if (DEBUG) {
				System.out.println(matlabCommand);
				System.out.println(debugInfo());
				System.out.println(getLinearSpec().toString());
			}

			// Run the generated matlab commands
			proxy.eval(matlabCommand);

			// Write the results back to the linearSpec object
			setResult(proxy.getVariable("x"));
			setLastSolvingTime(((double[]) proxy.getVariable("time"))[0]);

			String freeMemoryCommand = createFreeMemoryCommand();
			
			// Run the generated free memory command
			//proxy.eval(freeMemoryCommand);
			
		} catch (MatlabInvocationException e) {
			e.printStackTrace();
			resultType = ResultType.INFEASIBLE;
		} finally {
			disconnectFromMatlab();
		}

		return resultType;
	}
	
	protected abstract void prepare();
	
	protected abstract String debugInfo();
	
	/**
	 * creates a vector for an objective function to solve the problem specified by getLinearSpec()
	 * with a linear error distribution.
	 * 
	 * The vector has to be referenced by a variable 'f'
	 * 
	 * The vector considers the slack variables required to solve the problem with linprog
	 * 
	 * @return string with the matlab command.
	 */
	protected String createObjective(){
		Double[] objective = createObjectiveFunction();
		
		StringBuilder matlabCommandBuilder = new StringBuilder();
		// First all the matrices and vectors
		matlabCommandBuilder.append(createVariableAssignmentString("f",
				objective));
		matlabCommandBuilder.append("\n");
		return matlabCommandBuilder.toString();
	}
	
	/**
	 * Creates the RHS and the LHS of the inequalities of the problem specified by getLinearSpec()
	 * The RHS matrix is stored in a variable "Ai"
	 * The LHS vector is stored in a variable "bi"
	 * 
	 * The matrix considers the slack variables required to solve the problem with linprog
	 * 
	 * @return a string with the matlab command
	 */
	protected String createInequalityMatrixAndVector(){
		Constraints ic = createInequalityConstraints();
		
		StringBuilder matlabCommandBuilder = new StringBuilder();
		
		matlabCommandBuilder.append(createVariableAssignmentString("Ai",
				ic.A));
		matlabCommandBuilder.append("\n");
		matlabCommandBuilder.append(createVariableAssignmentString("bi",
				ic.B));
		matlabCommandBuilder.append("\n");
		return matlabCommandBuilder.toString();
	}
	
	/**
	 * Creates the RHS and the LHS of the equalities of the problem specified by getLinearSpec()
	 * The RHS matrix is stored in a variable "Ae"
	 * The LHS vector is stored in a variable "be"
	 * 
	 * The matrix considers the slack variables required to solve the problem with linprog
	 * 
	 * @return a string with the matlab command
	 */
	protected String createEqualityMatrixAndVector(){
		Constraints ec = createEqualityConstraints();
		
		StringBuilder matlabCommandBuilder = new StringBuilder();
		
		matlabCommandBuilder.append(createVariableAssignmentString("Ae",
				ec.A));
		matlabCommandBuilder.append("\n");
		matlabCommandBuilder.append(createVariableAssignmentString("be",
				ec.B));
		matlabCommandBuilder.append("\n");
		
		return matlabCommandBuilder.toString();
	}
	
	/**
	 * creates two vectors for the lower and upper bound of the problem specified by
	 * getLinearSpec()
	 * 
	 * The vectors consider the slack variables required to solve the problem with linprog
	 * 
	 */
	protected String createBoundVectors(){
		Double[] lb = createLowerBound();
		Double[] ub = createUpperBound();
		
		StringBuilder matlabCommandBuilder = new StringBuilder();
		
		matlabCommandBuilder.append(createVariableAssignmentString("lb", lb));
		matlabCommandBuilder.append("\n");
		matlabCommandBuilder
			.append(createVariableAssignmentString("ub", ub));
		matlabCommandBuilder.append("\n");
		
		return matlabCommandBuilder.toString();
	}
	
	/**
	 * creates the matlab command to solve the problem specified with the matrices Ae and Ai and the vectors
	 * be, bi, f, lb and ub. Uses the command generated bz createMatlabSolverCommand() to create the string
	 * for the solver command.
	 * 
	 */
	protected String createMatlabCommand(){
		// Actually build the matlab commands
		StringBuilder matlabCommandBuilder = new StringBuilder();

		// Then the command to run the solver and to take time
		matlabCommandBuilder.append("tic;");
		matlabCommandBuilder.append("\n");
		matlabCommandBuilder.append(createMatlabSolverCommand());
		matlabCommandBuilder.append("\n");
		matlabCommandBuilder.append("time = toc;");

		return matlabCommandBuilder.toString();
	}
	
	
	/**
	 * creates a string with the matlab command to call a the linprog solver.
	 * It stores the result in a variable "x".
	 */
	protected String createMatlabSolverCommand(){
		StringBuilder matlabCommandBuilder = new StringBuilder();
		matlabCommandBuilder.append("x = linprog(f,Ai,bi,Ae,be,lb, ub);");
		return matlabCommandBuilder.toString();
	}
	
	
	/**
	 * creates the matlab command to solve the problem specified with the matrices Ae and Ai and the vectors
	 * be, bi, f, lb and ub. Uses the command generated bz createMatlabSolverCommand() to create the string
	 * for the solver command.
	 * 
	 */
	protected String createFreeMemoryCommand(){
		// Actually build the matlab commands
		StringBuilder matlabCommandBuilder = new StringBuilder();

		matlabCommandBuilder.append("clear all;");

		return matlabCommandBuilder.toString();
		
	}

	/**
	 * Generates an array, which represents the objective function for the
	 * problem.
	 */
	protected abstract Double[] createObjectiveFunction();

	/**
	 * Generates an Constraints object, which represents the inequality
	 * constraints of the problem. 
	 */
	protected abstract Constraints createInequalityConstraints();

	/**
	 * Generates an Constraint object, which represents the equality constraints
	 * of the problem.
	 */
	protected abstract Constraints createEqualityConstraints();

	/**
	 * Create as vector of values for the lower bound.
	 */
	public abstract Double[] createLowerBound();

	/**
	 * Create as vector of values for the upper bound.
	 */
	protected abstract Double[] createUpperBound();
	
	/**
	 * @see linsolve.AbstractLinearSolver#getLastSolvingTime()
	 */
	@Override
	public long getLastSolvingTime() {
		return solvingTime;
	}

	// Methods to write the calculated results back

	/**
	 * writes the calculated values <code>x</code> back to the linearSpec
	 * object.
	 * 
	 * @param x
	 *            the results vector
	 */
	protected void setResult(Object x) {
		int i = 0;
		double[] xd = (double[]) x; 
		for (Variable v : getVariableLookup().keySet()) {
			i = getVariableLookup().get(v);
			// We do not need the result of the slack variables.
			if (i >= 0 && i < xd.length) {
				v.setValue(xd[i]);
			}
		}
	}

	/**
	 * sets the last solving time
	 * 
	 * @param time
	 *            the time measured in seconds.
	 */
	protected void setLastSolvingTime(double time) {
		solvingTime = (long)time * 1000;
	}

	/**
	 * Creates a lookup table for the slack variables of the linear problem.
	 * Slack variables are connected to a specific constraint. In case of soft
	 * equalities we require negative and positive slack variables. Negative
	 * slack variables for slack variables of equalities are tracked with the
	 * lookup table generated by this method.
	 * 
	 * @return a lookup table to track the number of the slack variables in the
	 *         matrices
	 */
	private Map<Variable, Integer> createVariableLookup() {
		Map<Variable, Integer> variableLookup = new Hashtable<Variable, Integer>();
		for (int i = 0; i < getLinearSpec().getVariables().size(); i++) {
			variableLookup.put(getLinearSpec().getVariables().get(i), i);
		}
		return variableLookup;
	}
	
	protected Map<Variable, Integer> getVariableLookup(){
		return variableLookup;
	}
	
	/**
	 * creates a Double array representing the constraint c. The array is
	 * noOfVariables long and has the coefficients of the summands of c at the
	 * positions, which are stored in variableLookup.
	 * 
	 * @param c
	 * @param noOfVariables
	 * @param variableLookup
	 * @return an array that represents the constraint c. the array has the
	 *         format, which is required by Matlabs linprog. Such arrays can be
	 *         combined to matrices, which represent equalities and inequalities
	 *         of linear problems.
	 */
	protected Double[] createLHS(Constraint c, int noOfVariables,
			Map<Variable, Integer> variableLookup) {
		Double[] a = new Double[noOfVariables];
		initDoubleArray(a);
		Summand[] lhs = c.getLeftSide();

		for (int j = 0; j < lhs.length; j++) {
			a[variableLookup.get(lhs[j].getVar())] = lhs[j].getCoeff();
		}

		return a;
	}

	// Methods to generate strings for matlab out of double arrays.

	/**
	 * Creates a string representing a variable assignment in matlab syntax.
	 * 
	 * @param name
	 *            the name a variable should have in the generated matlab
	 *            commmand.
	 * @param coefficients
	 *            the actual data structure, which is to be referenced by
	 *            <code>name</code>.
	 * @return a string representing the command
	 */
	private String createVariableAssignmentString(String name,
			Double[][] coefficients) {
		StringBuilder sb = new StringBuilder();

		sb.append(name);
		sb.append(" = ");
		sb.append(createMatlabMatrix(coefficients));
		sb.append(";");

		return sb.toString();
	}

	/**
	 * Overloading @see
	 * linsolve.MatlabSolver#getLastSolvingTimecreateVariableAssignmentString
	 * (String name, Double[][] coefficients) to use it also with 1D arrays.
	 * 
	 * @param name
	 *            the name a variable should have in the generated matlab
	 *            commmand.
	 * @param coefficients
	 *            the actual data structure, which is to be referenced by
	 *            <code>name</code>.
	 * @return a string representing the command
	 */
	private String createVariableAssignmentString(String name,
			Double[] coefficients) {
		return createVariableAssignmentString(name,
				new Double[][] { coefficients });
	}

	/**
	 * Creates a string representing a Double array in matlab syntax.
	 * 
	 * @param matrix
	 *            the double array in matlab syntax. If only a vector is to be
	 *            generated a 2D matrix with just one line has to be used.
	 * @return a string representing the command
	 */
	private String createMatlabMatrix(Double[][] matrix) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");

		for (int i = 0; i < matrix.length - 1; i++) {
			sb.append(createMatlabMatrixRow(matrix[i]));
			sb.append(";");
			sb.append("\n");
		}
		if (matrix.length > 0)
			sb.append(createMatlabMatrixRow(matrix[matrix.length - 1]));
		sb.append("]");

		return sb.toString();
	}

	/**
	 * Creates a string representing a row of a matrix in matlab syntax.
	 * 
	 * @param row
	 *            a Double array for which a corresponding string is to be
	 *            generated
	 * @return a string representing row.
	 */
	private String createMatlabMatrixRow(Double[] row) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < row.length - 1; i++) {
			sb.append(row[i]);
			sb.append(" ");
		}
		if (row.length > 0)
			sb.append(row[row.length - 1]);

		return sb.toString();
	}
	
	
	// Methods to communicate with Matlab

	/**
	 * Sets up a connection to an existing matlab installation.
	 */
	private void connectToMatlab() {
		// Create a proxy, which we will use to control MATLAB
		if (factory == null)
			factory = new MatlabProxyFactory();

		try {
			if (proxy == null)
				proxy = factory.getProxy();
		} catch (MatlabConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Disconnects from Matlab.
	 */
	private void disconnectFromMatlab() {
		// Disconnect the proxy from MATLAB
		// TODO Currently it is commented because otherwise each time the solve
		// method is called
		// a new matlab instance is started. Could possibly cause some out of
		// memory issues.
		// proxy.disconnect();
	}

	
	
	// Supporting methods and classes


	/**
	 * Inits the Double array <code>array</code> with 0.0d. Is required since
	 * Double objects are not automatically initialized.
	 * 
	 * @param array
	 *            the array to be initialized.
	 */
	protected void initDoubleArray(Double[] array) {
		for (int i = 0; i < array.length; i++)
			array[i] = 0.0d;

	}

	/**
	 * Container class that holds the LHS and the RHS of a set of constraints.
	 * 
	 * @author jmue933
	 * 
	 */
	protected class Constraints {
		public Constraints(Double[][] A, Double[] B) {
			this.A = A;
			this.B = B;
		}

		/**
		 * The LHS matrix
		 */
		public Double[][] A;

		/**
		 * The RHS vector
		 */
		public Double[] B;
	}
}

/* EOF */
