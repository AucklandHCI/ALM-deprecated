package linsolve.functional;

import static org.junit.Assert.*;
import linsolve.LinearSolver;
import linsolve.LinearSpec;
import linsolve.matlab.MatlabLinProgSolver;

import org.junit.Before;
import org.junit.Test;

/**MatlabSolverTest is for the comaprison purpose, we are comparing our solvers with linprog
 * that,s why we need this class.
 * @author njam031
 *
 */
public class MatlabSolverTest {

	LinearSolver solver;
	
	@Before
	public void setUp() throws Exception {
		solver = new MatlabLinProgSolver();
	}
	
	@Test
	public void test() {
		LinearSpec ls = LinearProblemsCollection.problem1(solver);
		solver.solve();
		
	}

}
