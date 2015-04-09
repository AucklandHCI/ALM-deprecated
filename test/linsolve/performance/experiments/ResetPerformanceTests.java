package linsolve.performance.experiments;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import linsolve.BinarySearchStrategy;
import linsolve.LinearRelaxationAddingConstraints;
import linsolve.LinearRelaxationRemovingSuspicious;
import linsolve.LinearSolver;
import linsolve.LpSolve;
import linsolve.QRSolver;
import linsolve.RelaxationSolverAdapter;
import linsolve.ResultType;
import linsolve.performance.Result;
import linsolve.performance.TestHelper;
import linsolve.performance.TestRunner;
import linsolve.performance.decorator.LinearSolverMemoryDecorator;
import linsolve.pivots.DeterministicPivotSummandSelector;
import linsolve.pivots.RandomPivotSummandSelector;

import org.junit.Before;
import org.junit.Test;

import alm.LayoutSpec;

/**
 * Test runner to run experiments to test the behavior of warm start strategies.
 * Is related to paper: "Speed up SOR Solvers for Constraint-based GUIs with Warm Start Strategies"
 * presented at ICDIM 2013.
 * 
 * This specific test tests the behavior of bigger changes up to 3000 pixels.
 * 
 * @author jmue933, njam031
 */
public class ResetPerformanceTests {
	
	@Before
	public void setUp() throws Exception {
		// nothing to do here
	}
	
	@Test
	public void performanceTest() throws Exception{
		TestRunner runner = configureTestRunner();
		Map<String, LinearSolver> solvers = new HashMap<String, LinearSolver>();
		//solvers.put("With warm start", 
				//new LinearRelaxationAddingConstraints(new RandomPivotSummandSelector()));
		solvers.put("Without warm start", 
				new LinearRelaxationAddingConstraints(new RandomPivotSummandSelector(), true));

		runner.configure("./results.csv", 20, 10, 0, 202, solvers);
		runner.start();
	}
	
	protected Result configureTestResult(){
		
		Result rslt = new Result();
		rslt.addEntryCategory("obs");
		rslt.addEntryCategory("solver");
		rslt.addEntryCategory("time");
		rslt.addEntryCategory("memory");
		rslt.addEntryCategory("size");
		rslt.addEntryCategory("constraints");
		rslt.addEntryCategory("problem");
		rslt.addEntryCategory("optimal");
		rslt.addEntryCategory("disabledConstraints");
		rslt.addEntryCategory("iota");
		rslt.addEntryCategory("iota_rel");
		rslt.addEntryCategory("iota_ordered");
		
		return rslt;
	}
	
	protected TestRunner configureTestRunner(){
		TestRunner runner = new ResetPerformanceTestRunner();
		runner.setResultCategories(configureTestResult());

		return runner;
	}
	
	public static class ResetPerformanceTestRunner extends TestRunner{ 
		
		private int noResizing;
		private Random rand = new Random();
		
		public Random getRandom(){
			return rand;
		}
		
		/**
		 * configures a PerformanceTestRunner.
		 * 
		 * @param path the path to the csv file which contains the results of a performance measurement
		 * @param noResizing number of times the window is randomly reszied
		 * @param iterations the number of iterations each problem size should be solved
		 * @param maxSize the problem sizes which have to be solved max size is the maximum of the sizes. The sizes start with 1 and end at maxsize
		 * @param solvers the solvers which have to be evaluated
		 */
		public void configure(String path, int noResizing, int iterations, int startSize, int maxSize, Map<String, LinearSolver> solvers){		
			super.configure(path, iterations, startSize, maxSize, 1, solvers);
			this.noResizing = noResizing;
		}
		
		
		public void doPerformanceTest() throws IOException{
			LinearSolverMemoryDecorator memoryMeasurement = new LinearSolverMemoryDecorator();
			writeHeader(this.getResult());
			int obs = 0;
			int problem = 0;
			LayoutSpec ls;
			LinearSolver solver;
			
			for(int i = this.getFrom(); i < this.getTo(); i = i + this.getStep()){
				getTestDataGenerator().configure(this.getIterations(), i);
				while(getTestDataGenerator().hasNext()){
					//System.out.println("The following problem is the testdata for the next runs;");
					//System.out.println(getTestDataGenerator().next());
					for(String name : this.getSolvers().keySet()){
						System.out.println("Test " + name + " with problem size " + i);
						solver = memoryMeasurement.setBody(this.getSolvers().get(name));;
		
						ls = getTestDataGenerator().next();
						
						ls.setSolver(solver);
						// Warm up solve to only compare the resizing behavior
						ls.solve();
						
						for(int j = 0; j < noResizing; j++){
							
							tweakLayoutSpec(ls);
							
							ls.solve();
							//System.out.println("Problem family member " + i);

							
							this.getResult().resetEntries();
							Result rslt = this.getResult();
							try {
								rslt.addEntry("obs", obs++);
								rslt.addEntry("solver", name);
								rslt.addEntry("time", solver.getLastSolvingTime());
								rslt.addEntry("memory", ((LinearSolverMemoryDecorator) solver).getRequiredMemory());
								rslt.addEntry("size", i);
								rslt.addEntry("constraints", ls.getConstraints().size());
								rslt.addEntry("problem", problem);
								rslt.addEntry("optimal", solver.getLastSolvingResult() == ResultType.OPTIMAL ||solver.getLastSolvingResult() == ResultType.SUBOPTIMAL? 1 : 0);
								rslt.addEntry("disabledConstraints", TestHelper.getDisabledConstraintsCount(ls));
								rslt.addEntry("iota", TestHelper.getIota(ls));
								rslt.addEntry("iota_rel", 0);
								rslt.addEntry("iota_ordered", 0);
								
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							writeResult(this.getResult());
						}
					}
					problem++;
				}
			}
		}
		
		protected void tweakLayoutSpec(LayoutSpec ls){
			ls.setRight(rand.nextInt(3000));
			ls.setBottom(rand.nextInt(3000));
		}
		
		@Override
		public void compileResult(LinearSolver solver, String name, LayoutSpec ls,
				int size, int iteration, int problem, int obs) {
			// directly implemented in doPerformanceTest
		}
	};

}
