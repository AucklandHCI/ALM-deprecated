/*package linsolve;

import static org.junit.Assert.*;

import org.junit.Test;

public class SolverIntermediateResults {
	LinearSolver solver =(new LinearRelaxationAddingConstraints(new RandomPivotSummandSelector()));

	@Test
	public final void simple1() {
		LinearSpec ls = new LinearSpec(solver);
		Variable x1 = ls.addVariable();
		ls.addConstraint(1, x1, OperatorType.EQ, 1);
		ls.solve();
		assertTrue(Constraint.isEqual(x1.getValue(), 1));
		System.out.println(ls.getCurrentSolution());
	}

	@Test
	public final void simple2() {
		LinearSpec ls = new LinearSpec(solver);
		Variable x1 = ls.addVariable();
		Variable x2 = ls.addVariable();
		ls.addConstraint(1, x1, OperatorType.EQ, 1);
		ls.addConstraint(1, x1, 1, x2, OperatorType.EQ, 2);
		ls.solve();
		assertTrue(Constraint.isEqual(x1.getValue(), 1));
		assertTrue(Constraint.isEqual(x2.getValue(), 1));
		System.out.println(ls.getCurrentSolution());
	}

	@Test
	public final void diagonalMatrix1() {
		LinearSpec ls = new LinearSpec(solver);
		Variable x1 = ls.addVariable();
		Variable x2 = ls.addVariable();
		Variable x3 = ls.addVariable();
		Variable x4 = ls.addVariable();
		ls.addConstraint(1, x1, OperatorType.EQ, 1);
		ls.addConstraint(1, x2, OperatorType.EQ, 2);
		ls.addConstraint(1, x3, OperatorType.EQ, 3);
		ls.addConstraint(1, x4, OperatorType.EQ, 4);
		ls.solve();
		assertTrue(Constraint.isEqual(x1.getValue(), 1));
		assertTrue(Constraint.isEqual(x2.getValue(), 2));
		assertTrue(Constraint.isEqual(x3.getValue(), 3));
		assertTrue(Constraint.isEqual(x4.getValue(), 4));
		System.out.println(ls.getCurrentSolution());
	}

	@Test
	public final void orthogonalMatrix1() {
		LinearSpec ls = new LinearSpec(solver);
		Variable x1 = ls.addVariable();
		Variable x2 = ls.addVariable();
		Variable x3 = ls.addVariable();
		ls.addConstraint(-1, x3, OperatorType.EQ, 1);
		ls.addConstraint(1, x2, OperatorType.EQ, 0);
		ls.addConstraint(1, x1, OperatorType.EQ, 1);
		ls.solve();
		assertTrue(Constraint.isEqual(x3.getValue(), -1));
		assertTrue(Constraint.isEqual(x2.getValue(), 0));
		assertTrue(Constraint.isEqual(x1.getValue(), 1));
		System.out.println(ls.getCurrentSolution());
	}
	@Test
	//This test is for TriangularMatrix
	public final void triangularMatrix1() {
		LinearSpec ls = new LinearSpec(solver);
		Variable x1 = ls.addVariable("x1");
		Variable x2 = ls.addVariable("x2");
		Variable x3 = ls.addVariable("x3");
		//Variable x4 = ls.addVariable();
		ls.addConstraint(1, x1, OperatorType.EQ, 106.8);
		ls.addConstraint(2.56, x1,1, x2, OperatorType.EQ,177.2);
		ls.addConstraint(5.76,x1,3.5,x2,1,x3, OperatorType.EQ,279.2);
		ls.solve();
		System.out.println(ls.getCurrentSolution());
		assertTrue(Constraint.isEqual(x1.getValue(), 106.8));
		assertTrue(Constraint.isEqual(x2.getValue(), -96.208));
		assertTrue(Constraint.isEqual(x3.getValue(), 0.76));
	    System.out.println(ls.getCurrentSolution());
	}
	@Test
	//This test is for divergence
	public final void nonConvergenceExamp() {
		LinearSpec ls = new LinearSpec(solver);
		Variable x1 = ls.addVariable("x1");
		Variable x2 = ls.addVariable("x2");
	    Variable x3 = ls.addVariable("x3");
		ls.addConstraint(1, x1, -1, x2,-1,x3, OperatorType.EQ,1);
        ls.addConstraint(-1, x1, 1, x2,0,x3 ,OperatorType.EQ,0);
        ls.addConstraint(-1, x1, 0, x2,1,x3 ,OperatorType.EQ,0);
		ls.solve();
		System.out.println(ls.getCurrentSolution());
	}
	@Test
	//This is square matrix example
	public final void square() {
		LinearSpec ls = new LinearSpec(solver);
		Variable x1 = ls.addVariable("x1");
		Variable x2 = ls.addVariable("x2");
		Variable x3 = ls.addVariable("x3");
		Variable x4 = ls.addVariable("x4");
		Variable x5 = ls.addVariable("x5");
		ls.addConstraint(1, x1,OperatorType.EQ, 0);
		ls.addConstraint(1, x2,OperatorType.EQ,0);
		ls.addConstraint(-1, x5, 1, x3, OperatorType.EQ,77);
		ls.addConstraint(-1, x2, 1, x4, OperatorType.EQ,26);
		ls.addConstraint(-1, x1, 1, x5, OperatorType.EQ,0);
		ls.solve();
		System.out.println(ls.getCurrentSolution());
		assertTrue(Constraint.isEqual(x1.getValue(),0));
		assertTrue(Constraint.isEqual(x2.getValue(), 0));
		assertTrue(Constraint.isEqual(x3.getValue(),77));
		assertTrue(Constraint.isEqual(x4.getValue(), 26));
		assertTrue(Constraint.isEqual(x5.getValue(),0));
		System.out.println(ls.getCurrentSolution());
	}
	@Test
	//This test is for non-convergence
	public final void nonConvergence1() {
		LinearSpec ls = new LinearSpec(solver);
		Variable x1 = ls.addVariable("x1");
		Variable x2 = ls.addVariable("x2");
		//Variable x3 = ls.addVariable();
		ls.addConstraint(1, x1, -5, x2, OperatorType.EQ,-4);
        ls.addConstraint(7, x1, -1, x2, OperatorType.EQ,28);
		ls.solve();
		//System.out.println(ls.getCurrentSolution());
		assertFalse(Constraint.isEqual(x1.getValue(),4.235));
    	assertFalse(Constraint.isEqual(x2.getValue(), 1.647));
		System.out.println(ls.getCurrentSolution());
	}
	@Test
	public final void StrechingMatrix1() {
		LinearSpec ls = new LinearSpec(solver);
		Variable x1 = ls.addVariable();
		Variable x2 = ls.addVariable();
		Variable x3 = ls.addVariable();
		ls.addConstraint(1, x1, OperatorType.EQ, 1);
		ls.addConstraint(1, x2, OperatorType.EQ, 2);
		ls.addConstraint(1, x3, OperatorType.EQ, 1.0);
		ls.solve();
		assertTrue(Constraint.isEqual(x1.getValue(), 1));
		assertTrue(Constraint.isEqual(x2.getValue(), 2));
		assertTrue(Constraint.isEqual(x3.getValue(), 1.0));
		System.out.println(ls.getCurrentSolution());
	}

	@Test
	public final void StrechingMatrix() {
		LinearSpec ls = new LinearSpec(solver);
		Variable x1 = ls.addVariable();
		Variable x2 = ls.addVariable();
		Variable x3 = ls.addVariable();
		ls.addConstraint(3, x1, OperatorType.EQ, 1);
		ls.addConstraint(1, x2, OperatorType.EQ, 2);
		ls.addConstraint(1, x3, OperatorType.EQ, 1.0);
		ls.solve();
		assertTrue(Constraint.isEqual(x1.getValue(), 0.334));
		assertTrue(Constraint.isEqual(x2.getValue(), 2));
		assertTrue(Constraint.isEqual(x3.getValue(), 1.0));
		System.out.println(ls.getCurrentSolution());
	}


	@Test
	public final void NonSquareMatrix() {
		LinearSpec ls = new LinearSpec(solver);
		Variable x1 = ls.addVariable();
		Variable x2 = ls.addVariable();
		Variable x3 = ls.addVariable();
		Variable x4 = ls.addVariable();
		ls.addConstraint(4, x1, -1, x2, -1, x3, OperatorType.EQ, 0.5);
		ls.addConstraint(-1, x1, 4, x2,OperatorType.EQ, 1.3);
		ls.addConstraint(-1, x1, 4, x3, OperatorType.EQ, 1.0);
		ls.addConstraint(-1, x2, -1, x3, OperatorType.EQ, 1.8);
		ls.solve();
		System.out.println(ls.getCurrentSolution());
	}
}*/