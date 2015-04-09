package linsolve;

import junit.framework.Assert;
import linsolve.AbstractLinearSolver;
import linsolve.KaczmarzSolver;
import linsolve.LinearSpec;

import org.junit.Before;
import org.junit.Test;

public class AbstractLinearSolverTests {

	AbstractLinearSolver solver;
	LinearSpec linearSpec;
	
	@Before
	public void setUp() throws Exception {
		this.solver = new KaczmarzSolver();
		this.linearSpec = new LinearSpec(solver);
	}

	@Test
	public final void testIsDebug() {
		solver.setDebug(true);
		Assert.assertTrue(solver.isDebug());
		solver.setDebug(false);
		Assert.assertFalse(solver.isDebug());
	}

	@Test
	public final void testGetLinearSpec() {
		Assert.assertEquals(solver.getLinearSpec(), linearSpec);
	}

	@Test
	public final void testInitVariableValues() {
		Variable v1 = new Variable(linearSpec);	
		solver.initVariableValues();
		
		Assert.assertEquals(v1.getValue(), 0.0);		
	}

}
