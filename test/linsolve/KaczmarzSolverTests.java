package linsolve;


import junit.framework.Assert;
import linsolve.Constraint;
import linsolve.KaczmarzSolver;
import linsolve.LinearSpec;
import linsolve.OperatorType;
import linsolve.Summand;
import linsolve.Variable;

import org.junit.Before;
import org.junit.Test;

public class KaczmarzSolverTests extends KaczmarzSolver{

	KaczmarzSolver solver = null;
	LinearSpec ls = null;
	Variable v1;
	Variable v2;
	Variable v3;
	
	Summand[] summands234;
	Summand[] summands000;
	Summand[] summands111;
	
	@Before
	public void setUp() throws Exception {
		solver = new KaczmarzSolver();
		ls = new LinearSpec();
		ls.setSolver(solver);
		v1 = new Variable(ls);
		v2 = new Variable(ls);
		v3 = new Variable(ls);
		
		summands234 = new Summand[]{
				new Summand(2.0d, v1), 
				new Summand(3.0d, v2), 
				new Summand(4.0d, v3)};
		
		summands000 = new Summand[]{
				new Summand(0.0d, v1), 
				new Summand(0.0d, v2), 
				new Summand(0.0d, v3)};
		
		summands111 = new Summand[]{
				new Summand(1.0d, v1), 
				new Summand(1.0d, v2), 
				new Summand(1.0d, v3)};
		
	}
	
	@Test
	public void testProjectConstraint(){
		double res;
		v1.setValue(1.0d);
		v2.setValue(1.0d);
		v3.setValue(1.0d);
		
		double b = 1.0d;
		Constraint c = new Constraint(ls, summands111, OperatorType.EQ, b, 100.0d);
		solver.projectConstraint(c, DEFAULT_LAMBDA);
		
		Assert.assertEquals("v1", 0.3333d, c.getLeftSide()[0].getVar().getValue(), 0.0001);
		Assert.assertEquals("v2", 0.3333d, c.getLeftSide()[1].getVar().getValue(), 0.0001);
		Assert.assertEquals("v3", 0.3333d, c.getLeftSide()[2].getVar().getValue(), 0.0001);
	}
	
	@Test
	public void testEuclidianNorm(){
		double res;
		res = solver.euclidianNorm(summands234);
		Assert.assertEquals(29.0d, res, 0.0001);
		
		res = solver.euclidianNorm(summands000);
		Assert.assertEquals(0.0d, res, 0.0001);
		
		res = solver.euclidianNorm(summands111);
		Assert.assertEquals(3.0d, res, 0.0001);

	}
	
	@Test
	public void testScalarProduct(){
		double res;
		v1.setValue(1.0d);
		v2.setValue(2.0d);
		v3.setValue(3.0d);
		res = solver.scalarProduct(summands234);
		Assert.assertEquals(20.0d, res, 0.0001);
		
		v1.setValue(1.0d);
		v2.setValue(2.0d);
		v3.setValue(3.0d);
		res = solver.scalarProduct(summands000);
		Assert.assertEquals(0.0d, res, 0.0001);
		
		v1.setValue(1.0d);
		v2.setValue(2.0d);
		v3.setValue(3.0d);
		res = solver.scalarProduct(summands111);

		Assert.assertEquals(6.0d, res, 0.0001);

	}

}
