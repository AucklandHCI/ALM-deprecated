package linsolve;

import static org.junit.Assert.assertTrue;
import junit.framework.Assert;
import linsolve.Constraint;
import linsolve.LinearRelaxationAddingConstraints;
import linsolve.LinearSolver;
import linsolve.LinearSpec;
import linsolve.OperatorType;
import linsolve.Summand;
import linsolve.Variable;
import linsolve.pivots.TwoPhaseSelector;

import org.junit.Before;
import org.junit.Test;

public class ConstraintTests {

	private Constraint toBeTested;
	private Summand[] summands;
	private Variable v1;
	private Variable v2;
	
	@Before
	public void setup(){
		double RIGHT_SIDE = 100;
		double PENALTY = 100;

		LinearSolver solver = new LpSolve(); 
		LinearSpec linearSpec = new LinearSpec(solver);
		v1 = new Variable(linearSpec);
		v2 = new Variable(linearSpec);
		
		summands = new Summand[2];
		summands[0] = new Summand(100, v1);
		summands[1] = new Summand(50, v2);
		toBeTested = new Constraint(linearSpec, summands, OperatorType.EQ, RIGHT_SIDE, PENALTY);
	}

	@Test
	public final void nameTest() {
		String name = "Constraint1";
		toBeTested.setName(name);
		Assert.assertEquals("Name getting and setting does not work!", name, toBeTested.getName());
	}

	@Test
	public final void enabledTest() {
		toBeTested.setEnabled(true);
		Assert.assertTrue("IsEnabled is not working!", toBeTested.isEnabled());
		toBeTested.setEnabled(false);
		Assert.assertFalse("IsEnabled is not working!", toBeTested.isEnabled());
	}
	
	@Test
	public final void LeftSideTest() {
		Summand[] testSummands = toBeTested.getLeftSide();
		Assert.assertEquals("Returns not the same summands array!", summands, testSummands);

		summands = new Summand[3];
		summands[0] = new Summand(100, v1);
		summands[1] = new Summand(50, v2);
		summands[2] = new Summand(10, v2);
		toBeTested.setLeftSide(summands);
		
		testSummands = toBeTested.getLeftSide();
		Assert.assertEquals("Returns not the same summands array after adding new LeftSide with varargs overloading!", summands, testSummands);

		toBeTested.setLeftSide(summands);
		Assert.assertEquals("Returns not the same summands array after adding new LeftSide with varargs overloading!", summands, testSummands);		
	}
	
	@Test
	public final void operatorTest() {	
		Assert.assertEquals("Operator is not as expected", OperatorType.EQ, toBeTested.getOp());	
		
		toBeTested.setOp(OperatorType.GE);
		Assert.assertEquals("Operator is not as expected", OperatorType.GE, toBeTested.getOp());
	}
	
	@Test
	public final void LeftSideContainsTest() {	
		Assert.assertTrue("LeftSideContains does not answer right.", toBeTested.leftSideContains(v1) && 
				toBeTested.leftSideContains(v2));	
	}
	
	@Test
	public final void sumOfAllAbsoluteCoefficientsTest() {	
		double sum = summands[0].getCoeff() + summands[1].getCoeff();
		Assert.assertEquals("The sum of the magitude is not correct.", sum, toBeTested.sumOfAllAbsoluteCoefficients(), 0.001);
	}
	
	@Test
	public final void residualTest() {	
		v1.setValue(2);
		v2.setValue(4);
		
		double sum = toBeTested.getRightSide() 
		- (summands[0].getCoeff()*summands[0].getVar().getValue()
					+summands[1].getCoeff()*summands[1].getVar().getValue());
		
		Assert.assertEquals("The residuals are not correctly calculated", sum, toBeTested.residual(), 0.001);
		
		toBeTested.setOp(OperatorType.GE);
		Assert.assertEquals("The residuals are not correctly calculated", 0.0d, toBeTested.residual(), 0.001);
		toBeTested.setOp(OperatorType.LE);
		Assert.assertEquals("The residuals are not correctly calculated", sum, toBeTested.residual(), 0.001);
	}
	
	@Test
	public final void errorTest() {	
		v1.setValue(1);
		v2.setValue(1);
		
		double sum = -1*(100 -(100 + 50));
		
		Assert.assertEquals("The error are not correctly calculated", sum, toBeTested.error(), 0.001);
	}
	
	@Test
	public void varUsedTwiceTest() {
		LinearSolver solver = new LpSolve();
		LinearSpec linearSpec = new LinearSpec(solver);
		v1 = new Variable(linearSpec);
		v2 = new Variable(linearSpec);
		
		summands = new Summand[3];
		summands[0] = new Summand(100, v1);
		summands[1] = new Summand(50, v2);
		summands[2] = new Summand(50, v1);
		try {
			toBeTested = new Constraint(linearSpec, summands, OperatorType.EQ, 0, 0);
			assertTrue(false);
		} catch (RuntimeException e) {	
			assertTrue(true);
		}
	}
	
}