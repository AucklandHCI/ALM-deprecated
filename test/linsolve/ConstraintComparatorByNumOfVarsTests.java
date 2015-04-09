package linsolve;

import junit.framework.Assert;
import linsolve.Constraint;
import linsolve.ConstraintComparatorByNumOfVars;
import linsolve.LinearSolver;
import linsolve.LinearSpec;
import linsolve.OperatorType;
import linsolve.Summand;
import linsolve.Variable;
import linsolve.pivots.DeterministicPivotSummandSelector;
import linsolve.pivots.PivotSummandSelector;

import org.junit.Before;
import org.junit.Test;

public class ConstraintComparatorByNumOfVarsTests {

	private ConstraintComparatorByNumOfVars toBeTested;
	private Constraint c1, c2;

	@Before
	public void setup(){
		double RIGHT_SIDE = 100;
		double PENALTY = 100;

		toBeTested = new ConstraintComparatorByNumOfVars();
		
		PivotSummandSelector pivot = new DeterministicPivotSummandSelector();
		LinearSolver solver = new GaussSeidelSolver(pivot, 5000);
		LinearSpec linearSpec = new LinearSpec(solver);
		Variable v1 = new Variable(linearSpec);
		Variable v2 = new Variable(linearSpec);
		Variable v3 = new Variable(linearSpec);
		
		Summand[] summands1 = new Summand[2];
		summands1[0] = new Summand(100, v1);
		summands1[1] = new Summand(50, v2);
		
		Summand[] summands2 = new Summand[2];
		summands2[0] = new Summand(100, v1);
		summands2[1] = new Summand(50, v2);
		summands2[1] = new Summand(50, v3);
		
		c1 = new Constraint(linearSpec, summands1, OperatorType.EQ, RIGHT_SIDE, PENALTY);
		c2 = new Constraint(linearSpec, summands2, OperatorType.EQ, RIGHT_SIDE, PENALTY);
		
		pivot.init(linearSpec);
	}

	@Test
	public void testCompare() {
		Assert.assertEquals(toBeTested.compare(c1, c2), 1);
		Assert.assertEquals(toBeTested.compare(c2, c1), -1);
		Assert.assertEquals(toBeTested.compare(c1, c1), 0);
	}

}
