package linsolve.performance.experiments;

import linsolve.performance.TestRunner;
import linsolve.performance.experiments.ResetPerformanceTests.ResetPerformanceTestRunner;
import alm.LayoutSpec;

/**
 * Test runner to run experiments to test the behavior of warm start strategies.
 * Is related to paper: "Speed up SOR Solvers for Constraint-based GUIs with Warm Start Strategies"
 * presented at ICDIM 2013.
 * 
 * This specific test tests the behavior of small changes up to 3 pixels.
 * 
 * @author jmue933, njam031
 */
public class ResetPerformanceSmallChangesTests extends ResetPerformanceTests {

	protected TestRunner configureTestRunner(){
		TestRunner runner = new ResetPerformanceSmallChangesTestRunner();
		runner.setResultCategories(configureTestResult());

		return runner;
	}
	
	protected static class ResetPerformanceSmallChangesTestRunner extends ResetPerformanceTestRunner{
		
		@Override
		protected void tweakLayoutSpec(LayoutSpec ls){
			ls.setRight(getRandom().nextInt(3));
			ls.setBottom(getRandom().nextInt(3));
		}
	}
}
