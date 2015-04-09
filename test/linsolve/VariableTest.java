package linsolve;

import static org.junit.Assert.*;
import linsolve.KaczmarzSolver;
import linsolve.LinearSpec;
import linsolve.Variable;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class VariableTest {

	private LinearSpec linearSpec;
	private Variable variable1;
	private Variable variable2;

	private static double TEST_VALUE_1 = 1;
	private static double TEST_VALUE_2 = 2;

	@Before
	public void setUp() throws Exception {
		linearSpec = new LinearSpec();
		linearSpec.setSolver(new KaczmarzSolver());
		variable1 = new Variable(linearSpec);
		variable2 = new Variable(linearSpec);
	}

	@Test
	public void testSetValue() {
		variable1.setValue(TEST_VALUE_1);
		Assert.assertEquals(TEST_VALUE_1, variable1.getValue(), 0.001);

		variable1.setValue(TEST_VALUE_2);
		Assert.assertEquals(TEST_VALUE_2, variable1.getValue(), 0.001);
	}


	// TODO add tests for remaining methods


}
