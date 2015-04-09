package linsolve.performance.experiments;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import linsolve.LinearRelaxationAddingConstraints;
import linsolve.LinearRelaxationRemovingSuspicious;
import linsolve.LinearSolver;
import linsolve.LpSolve;
import linsolve.QRSolver;
import linsolve.RelaxationSolverAdapter;
import linsolve.ResultType;
import linsolve.performance.Result;
import linsolve.performance.TestRunner;
import linsolve.pivots.DeterministicPivotSummandSelector;
import linsolve.pivots.RandomPivotSummandSelector;

import org.junit.Before;
import org.junit.Test;

import alm.LayoutSpec;

/**This is performance test class where we have implemented iteration versus error.
 * @author njam031
 *
 */
public class IterationErrorPerformanceTest {
	
	@Test
	public void test() throws IOException, Exception{
		TestRunner runner = configureTestRunner();
			
		Map<String, LinearSolver> solvers = new HashMap<String, LinearSolver>();
		solvers.put("AddingWithDeterministicPivotSelector",new LinearRelaxationAddingConstraints(new DeterministicPivotSummandSelector()));		
		solvers.put("AddingWithRandomPivotSelector", new LinearRelaxationAddingConstraints(new RandomPivotSummandSelector()));
		solvers.put("RemovingWithRandomPivotSelector", new LinearRelaxationRemovingSuspicious(new RandomPivotSummandSelector()));
		solvers.put("RemovingWithDeterministicPivotSelector", new LinearRelaxationRemovingSuspicious(new DeterministicPivotSummandSelector()));
		
		//repititions and areas (min to max, step)
		runner.configure("./results.csv", 10, 0, 100, 1, solvers);
		runner.start();
	}
	
	private TestRunner configureTestRunner(){
		TestRunner runner = new TestRunner(){ 
			public void compileResult(LinearSolver solver, String name, LayoutSpec ls,int size, int iteration, int problem, int obs){
				Result rslt = this.getResult();
				
				try {
					rslt.addEntry("obs", obs);
					rslt.addEntry("solver", name);
					rslt.addEntry("error", ((RelaxationSolverAdapter)solver).getMaxError());
					rslt.addEntry("iteration", iteration);
					rslt.addEntry("size", size);
					rslt.addEntry("constraints", ls.getConstraints().size());
					rslt.addEntry("problem", problem);
					rslt.addEntry("optimal", solver.getLastSolvingResult() == ResultType.OPTIMAL ? 1 : 0);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}};
			
			Result rslt = new Result();
			rslt.addEntryCategory("obs");
			rslt.addEntryCategory("solver");
			rslt.addEntryCategory("error");
			rslt.addEntryCategory("iteration");
			rslt.addEntryCategory("size");
			rslt.addEntryCategory("constraints");
			rslt.addEntryCategory("problem");
			rslt.addEntryCategory("optimal");
			
			runner.setResultCategories(rslt);
			
			return runner;
	}

}
