package linsolve.performance.experiments;

import java.awt.Dimension;

import linsolve.performance.TestRunner;
import alm.Area;
import alm.LayoutSpec;

/**
 * Test runner to run experiments to test the behavior of warm start strategies.
 * Is related to paper: "Speed up SOR Solvers for Constraint-based GUIs with Warm Start Strategies"
 * presented at ICDIM 2013.
 * 
 * This specific test tests the behavior of changes at the definitions of areas in a layout spec.
 * 
 * @author jmue933, njam031
 */
public class ResetPerformanceAlterAreasTests extends ResetPerformanceTests {

	protected TestRunner configureTestRunner(){
		TestRunner runner = new ResetPerformanceAlterAreasTestRunner();
		runner.setResultCategories(configureTestResult());

		return runner;
	}
	
	protected static class ResetPerformanceAlterAreasTestRunner extends ResetPerformanceTestRunner{
		
		public static final double CHANGE_RATE = 0.1;
		
		@Override
		protected void tweakLayoutSpec(LayoutSpec ls){			
			int noOfAreasToChange = (int)(ls.getAreas().size() * CHANGE_RATE);
			Area areaToChange;
			while(noOfAreasToChange-- > 0){
				areaToChange = ls.getAreas().get(getRandom().nextInt(ls.getAreas().size() - 1));
				areaToChange.setMinContentSize(new Dimension(getRandom().nextInt(400), getRandom().nextInt(400)));
				if (getRandom().nextDouble() < 0.05){
					areaToChange.setMaxContentSize(new Dimension(
							areaToChange.getMinContentSize().width + getRandom().nextInt(800),
							areaToChange.getMinContentSize().height + getRandom().nextInt(600)));
			 }
			}
		}
	}
}
