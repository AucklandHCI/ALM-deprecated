package linsolve.functional;

import static org.junit.Assert.*;

import java.util.List;

import linsolve.Constraint;
import linsolve.LinearSolver;
import linsolve.LinearSpec;
import linsolve.OperatorType;
import linsolve.Variable;

import org.junit.Before;
import org.junit.Test;

public abstract class LinearProblems {

	private LinearSolver solver;

	protected final LinearSolver getLinearSolver() {
		return solver;
	}

	protected final void setLinearSolver(LinearSolver solver) {
		this.solver = solver;
	}

	@Before
	public abstract void setup();

	protected void printResult(LinearSpec ls, String methodName, String expected) {
		if (commons.TestConfigParams.debug) {
			System.out.println();
			System.out.print(this.getClass().getName() + "." + methodName
					+ ": " + ls.getCurrentSolution() + ", MaxError: "
					+ ls.computeCurrentMaxError() + "  [Expected: " + expected
					+ "]");
		}
	}

	protected void printMsg(String msg) {
		if (commons.TestConfigParams.debug) {
			System.out.println(msg);
		}

	}

	@Test
	public final void simple1() {
		LinearSpec ls = new LinearSpec(solver);
		Variable x1 = ls.addVariable();
		ls.addConstraint(1, x1, OperatorType.EQ, 1);
		ls.solve();
		printResult(ls,
				Thread.currentThread().getStackTrace()[1].getMethodName(), "1");
		assertTrue(Constraint.equalZero(ls.computeCurrentMaxError()));
		printMsg("  :Done");
	}

	@Test
	public final void simple2() {
		LinearSpec ls = new LinearSpec(solver);
		Variable x1 = ls.addVariable("x1");
		Variable x2 = ls.addVariable("x2");
		ls.addConstraint(1, x1, OperatorType.EQ, 0);
		ls.addConstraint(1, x1, 1, x2, OperatorType.EQ, 0);
		ls.solve();
		printResult(ls,
				Thread.currentThread().getStackTrace()[1].getMethodName(),
				"0, 0");
		assertTrue(Constraint.equalZero(ls.computeCurrentMaxError()));
		printMsg("  :Done");
	}

	@Test
	// This test is for non-convergence
	public final void divergence1() {
		LinearSpec ls = new LinearSpec(solver);
		Variable x1 = ls.addVariable("x1");
		Variable x2 = ls.addVariable("x2");
		Variable x3 = ls.addVariable("x3");
		ls.addConstraint(1, x1, -0.55, x2, -0.5, x3, OperatorType.EQ, 0);
		ls.addConstraint(-0.5, x1, 1, x2, -0.6, x3, OperatorType.EQ, 0);
		ls.addConstraint(-0.4, x1, -0.5, x2, 1, x3, OperatorType.EQ, 0);
		ls.solve();
		printResult(ls,
				Thread.currentThread().getStackTrace()[1].getMethodName(),
				"0, 0");
		assertTrue(Constraint.equalZero(ls.computeCurrentMaxError()));
		printMsg("  :Done");
	}

	@Test
	public final void divergence2() {
		LinearSpec ls = new LinearSpec(solver);
		Variable x1 = ls.addVariable("x1");
		Variable x2 = ls.addVariable("x2");
		ls.addConstraint(1, x1, 1, x2, OperatorType.EQ, 1);
		ls.addConstraint(-1, x1, 1, x2, OperatorType.EQ, 1);
		ls.solve();
		printResult(ls,
				Thread.currentThread().getStackTrace()[1].getMethodName(),
				"MaxError=0");
		assertTrue(Constraint.equalZero(ls.computeCurrentMaxError()));
		printMsg("  :Done");
	}

	@Test
	// This test is designed for diagonalMatrix
	public final void diagonalMatrix1() {
		LinearSpec ls = new LinearSpec(solver);
		Variable x1 = ls.addVariable("x1");
		Variable x2 = ls.addVariable("x2");
		Variable x3 = ls.addVariable("x3");
		Variable x4 = ls.addVariable("x4");
		ls.addConstraint(1, x1, OperatorType.EQ, 1);
		ls.addConstraint(1, x2, OperatorType.EQ, 2);
		ls.addConstraint(1, x3, OperatorType.EQ, 3);
		ls.addConstraint(1, x4, OperatorType.EQ, 4);
		ls.solve();
		printResult(ls,
				Thread.currentThread().getStackTrace()[1].getMethodName(),
				"1, 2, 3, 4");
		assertTrue(Constraint.equalZero(ls.computeCurrentMaxError()));
		printMsg("  :Done");
	}

	@Test
	// This test is an example of orthogonalMatrix.
	public final void orthogonalMatrix1() {
		LinearSpec ls = new LinearSpec(solver);
		Variable x1 = ls.addVariable("x1");
		Variable x2 = ls.addVariable("x2");
		Variable x3 = ls.addVariable("x3");
		ls.addConstraint(-1, x3, OperatorType.EQ, 1);
		ls.addConstraint(1, x2, OperatorType.EQ, 0);
		ls.addConstraint(1, x1, OperatorType.EQ, 1);
		ls.solve();
		printResult(ls,
				Thread.currentThread().getStackTrace()[1].getMethodName(),
				"-1, 0, 1");
		assertTrue(Constraint.equalZero(ls.computeCurrentMaxError()));
		printMsg("  :Done");
	}

	@Test
	// This test is for TriangularMatrix
	public void triangularMatrix1() {
		LinearSpec ls = new LinearSpec(solver);
		Variable x1 = ls.addVariable("x1");
		Variable x2 = ls.addVariable("x2");
		Variable x3 = ls.addVariable("x3");
		ls.addConstraint(1, x1, OperatorType.EQ, 106.8);
		ls.addConstraint(2.56, x1, 1, x2, OperatorType.EQ, 177.2);
		ls.addConstraint(5.76, x1, 3.5, x2, 1, x3, OperatorType.EQ, 279.2);
		ls.solve();
		printResult(ls,
				Thread.currentThread().getStackTrace()[1].getMethodName(),
				"MaxError=0");
		assertTrue(Constraint.equalZero(ls.computeCurrentMaxError()));
		printMsg("  :Done");
	}

	@Test
	// This test is for MixedMatrix
	public final void MixedMatrix() {
		LinearSpec ls = new LinearSpec(solver);
		Variable x1 = ls.addVariable("x1");
		Variable x2 = ls.addVariable("x2");
		Variable x3 = ls.addVariable("x3");
		Variable x4 = ls.addVariable("x4");
		ls.addConstraint(1, x1, OperatorType.EQ, 2);
		ls.addConstraint(1, x2, OperatorType.EQ, 1);
		ls.addConstraint(1, x3, OperatorType.EQ, 2);
		ls.addConstraint(1, x4, OperatorType.EQ, 1);
		ls.solve();
		printResult(ls,
				Thread.currentThread().getStackTrace()[1].getMethodName(),
				"2, 1, 2, 1");
		assertTrue(Constraint.equalZero(ls.computeCurrentMaxError()));
		printMsg("  :Done");
	}

	@Test
	// This prototype is for StrechingMatrix1
	public final void StrechingMatrix1() {
		LinearSpec ls = new LinearSpec(solver);
		Variable x1 = ls.addVariable("x1");
		Variable x2 = ls.addVariable("x2");
		Variable x3 = ls.addVariable("x3");
		ls.addConstraint(1, x1, OperatorType.EQ, 1);
		ls.addConstraint(1, x2, OperatorType.EQ, 2);
		ls.addConstraint(1, x3, OperatorType.EQ, 1.0);
		ls.solve();
		printResult(ls,
				Thread.currentThread().getStackTrace()[1].getMethodName(),
				"1, 2, 1");
		assertTrue(Constraint.equalZero(ls.computeCurrentMaxError()));
		printMsg("  :Done");
	}

	@Test
	// This is an example of just StrechingMatrix
	public final void StrechingMatrix() {
		LinearSpec ls = new LinearSpec(solver);
		Variable x1 = ls.addVariable("x1");
		Variable x2 = ls.addVariable("x2");
		Variable x3 = ls.addVariable("x3");
		ls.addConstraint(3, x1, OperatorType.EQ, 3);
		ls.addConstraint(1, x2, OperatorType.EQ, 2);
		ls.addConstraint(2, x3, OperatorType.EQ, -2);
		ls.solve();
		printResult(ls,
				Thread.currentThread().getStackTrace()[1].getMethodName(),
				"1, 2, -1");
		assertTrue(Constraint.equalZero(ls.computeCurrentMaxError()));
		printMsg("  :Done");
	}

	@Test
	// This is nonsquareMatrix example
	public void NonSquareMatrix() {
		LinearSpec ls = new LinearSpec(solver);
		Variable x1 = ls.addVariable("x1");
		Variable x2 = ls.addVariable("x2");
		ls.addConstraint(2, x1, 1, x2, OperatorType.EQ, -1);
		ls.addConstraint(-3, x1, 1, x2, OperatorType.EQ, -2);
		ls.addConstraint(-1, x1, 1, x2, OperatorType.EQ, 1.0);
		ls.solve();
		printResult(ls,
				Thread.currentThread().getStackTrace()[1].getMethodName(),
				"MaxError=0");
		assertTrue(Constraint.equalZero(ls.computeCurrentMaxError()));
		printMsg("  :Done");
	}
	@Test
	// This is nonsquareMatrix example
	public void Inconsistent() {
		LinearSpec ls = new LinearSpec(solver);
		Variable x1 = ls.addVariable("x1");
		Variable x2 = ls.addVariable("x2");
		ls.addConstraint(1, x1, OperatorType.EQ, 0);
		ls.addConstraint(1, x2, OperatorType.EQ, 0);
		ls.addConstraint(1, x1, 1, x2, OperatorType.EQ, 1.0);
		ls.solve();
		printResult(ls,
				Thread.currentThread().getStackTrace()[1].getMethodName(),
				"MaxError=0");
		assertTrue(Constraint.equalZero(ls.computeCurrentMaxError()));
		printMsg("  :Done");
	}
	@Test
	// This test is for non-convergence
	public final void Kaczmarz() {
		LinearSpec ls = new LinearSpec(solver);
		Variable x1 = ls.addVariable("x1");
		Variable x2 = ls.addVariable("x2");
		ls.addConstraint(1, x1, -1, x2, OperatorType.EQ, 0);
		ls.addConstraint(1, x2, OperatorType.EQ, 0);
		ls.solve();
		printResult(ls,
				Thread.currentThread().getStackTrace()[1].getMethodName(),
				"0, 0");
		assertTrue(Constraint.equalZero(ls.computeCurrentMaxError()));
		printMsg("  :Done");
	}

	@Test
	// This is square matrix example
	public final void SquareMatrix() {
		LinearSpec ls = new LinearSpec(solver);
		Variable x1 = ls.addVariable("x1");
		Variable x2 = ls.addVariable("x2");
		Variable x3 = ls.addVariable("x3");
		Variable x4 = ls.addVariable("x4");
		Variable x5 = ls.addVariable("x5");
		ls.addConstraint(1, x1, OperatorType.EQ, 0);
		ls.addConstraint(1, x2, OperatorType.EQ, 0);
		ls.addConstraint(-1, x5, 1, x3, OperatorType.EQ, 0);
		ls.addConstraint(-1, x2, 1, x4, OperatorType.EQ, 0);
		ls.addConstraint(-1, x1, 1, x5, OperatorType.EQ, 0);
		ls.solve();
		printResult(ls,
				Thread.currentThread().getStackTrace()[1].getMethodName(),
				"0, 0, 0, 0, 0");
		assertTrue(Constraint.equalZero(ls.computeCurrentMaxError()));
		printMsg("  :Done");
	}

	@Test
	// This is InfeasibleMatrix example
	public final void Infeasible1() {
		LinearSpec ls = new LinearSpec(solver);
		Variable x1 = ls.addVariable("x1");
		Variable x2 = ls.addVariable("x2");
		ls.addConstraint(2, x1, OperatorType.EQ, 2);
		ls.addConstraint(4, x1, -4, x2, OperatorType.EQ, 4);
		ls.solve();
		printResult(ls,
				Thread.currentThread().getStackTrace()[1].getMethodName(),
				"MaxError=0");
		assertTrue(Constraint.equalZero(ls.computeCurrentMaxError()));
		printMsg("  :Done");
	}

	@Test
	public void simpleinfeasible() {
		LinearSpec ls = new LinearSpec(solver);
		Variable x1 = ls.addVariable("x1");
		Variable x2 = ls.addVariable("x2");
		Variable x3 = ls.addVariable("x3");
		ls.addConstraint(1, x1, 1, x2, 1, x3, OperatorType.EQ, 0);
		ls.addConstraint(1, x1, 1, x2, 2, x3, OperatorType.EQ, 0);
		ls.solve();
		printResult(ls,
				Thread.currentThread().getStackTrace()[1].getMethodName(),
				"0, 0, 0");
		assertTrue(Constraint.equalZero(ls.computeCurrentMaxError()));
		printMsg("  :Done");
	}
	
	

	

	
	
	// Constraints generated by Test4App.
	/**This example does not work with QR, kACZMARZ and Gauss-Seidel solvers alone because there are conflicting constraints in this example that,s why it needs soft solvers.
	 * 
	 */
	@Test	
	public void ThreeButtonsTest() {
		Variable [][] components = {{new Variable("left",0), new Variable("top",0), new Variable("x1",138), new Variable("y1", 114)},
				   {new Variable("x1",138), new Variable("top",0), new Variable("right",276), new Variable("y1", 114)},
				   {new Variable("left",0), new Variable("y1",114), new Variable("right",276), new Variable("bottom", 228)},
				 };
		
		LinearSpec ls = LinearProblemsCollection.ThreeButtons(solver);
		ls.solve();
		printResult(ls,
				Thread.currentThread().getStackTrace()[1].getMethodName(),
				"(area#1: left: left:0,top: top:0,right: x1:138,bottom: y1:114) "+ 
				"(area#2: left: x1:138,top: top:0,right: right:276,bottom: y1:114) "+
				"(area#3: left: left:0,top: y1:114,right: right:276,bottom: bottom:228)");
		checkResults(components, ls.getVariables());
		printMsg("  :Done");
	}
	
	/**
	 * This example does not work with QR, kACZMARZ and Gauss-Seidel solvers alone because there are conflicting constraints in this example that,s why it needs soft solvers
	 */
	@Test
	public void TableTestTest() {
		Variable [][] components = {
			{new Variable("X9", 0), new Variable("Y11", 0), new Variable("X10", 284), new Variable("Y12", 87)},
			{new Variable("X9", 0), new Variable("Y15", 87), new Variable("X10", 284), new Variable("Y16", 175)},
			{new Variable("X9", 0), new Variable("Y13", 175), new Variable("X10", 284), new Variable("Y14", 262)},
		};

		LinearSpec ls = LinearProblemsCollection.TableTest(solver);
		ls.solve();
		printResult(ls, Thread.currentThread().getStackTrace()[1].getMethodName(),
			"area#1:left: X9:0,top: Y11:0,right: X10:284,bottom: Y12:87 "+
			"area#2:left: X9:0,top: Y15:87,right: X10:284,bottom: Y16:175 "+
			"area#3:left: X9:0,top: Y13:175,right: X10:284,bottom: Y14:262"
			);
		checkResults(components, ls.getVariables());
		printMsg("  :Done");
	}
	
	private void checkResults(Variable [][] expected, List<Variable> actual) {
		for (Variable v:actual) {
			for (int i=0 ; i < expected.length; i++) {
				for (int j=0; j < expected[i].length; j++) {
					if (v.getName() != null && v.getName().equals(expected[i][j].getName())) {
						assertEquals(v.getName(), Math.round(expected[i][j].getValue()), Math.round(v.getValue()));
					}
				}
			}
		}		
	}
	
	
}