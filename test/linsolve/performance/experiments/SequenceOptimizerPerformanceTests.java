package linsolve.performance.experiments;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import linsolve.BinarySearchAndSortingStrategy;
import linsolve.BinarySearchStrategy;
import linsolve.LinearRelaxationAddingConstraints;
import linsolve.LinearRelaxationRemovingSuspicious;
import linsolve.LinearSolver;
import linsolve.LpSolve;
import linsolve.QRSolver;
import linsolve.RelaxationSolverAdapter;
import linsolve.ResultType;
import linsolve.Variable;
import linsolve.heuristics.sequenceoptimization.SequenceOptimizer;
import linsolve.performance.Result;
import linsolve.performance.TestHelper;
import linsolve.performance.TestRunner;
import linsolve.performance.decorator.LinearSolverMemoryDecorator;
import linsolve.pivots.DeterministicPivotSummandSelector;
import linsolve.pivots.RandomPivotSummandSelector;

import org.junit.Before;
import org.junit.Test;

import alm.LayoutSpec;

public class SequenceOptimizerPerformanceTests {

	
	
	@Before
	public void setUp() throws Exception {
		
	}
	
	@Test
	public void performanceTest() throws Exception{
		TestRunner runner = configureTestRunner();
		Map<String, LinearSolver> solvers = new HashMap<String, LinearSolver>();

		runner.configure("./results.csv", 10, 1, 100, 1, solvers);
		runner.start();
	}
	
	private TestRunner configureTestRunner(){
		TestRunner runner = new TestRunner(){ 
			public void doPerformanceTest() throws IOException{

				boolean debug = false;
				LinearSolverMemoryDecorator memoryMeasurement = new LinearSolverMemoryDecorator();
				
				writeHeader(this.getResult());
				
				int obs = 0;
				int problem = 0;
				for(int i = this.getFrom(); i < this.getTo() ; i = i + this.getStep()){
					getTestDataGenerator().configure(this.getIterations(), i);
					while(getTestDataGenerator().hasNext()){

						BinarySearchStrategy softPlain = new BinarySearchStrategy(new DeterministicPivotSummandSelector());
						BinarySearchAndSortingStrategy softSort = new BinarySearchAndSortingStrategy(new DeterministicPivotSummandSelector());

						memoryMeasurement.setBody(softPlain);
						
						LayoutSpec ls = getTestDataGenerator().next();
						
						ls.setSolver(memoryMeasurement);
						ls.solve();
			
						this.getResult().resetEntries();
						Result rslt = this.getResult();
						try {
							rslt.addEntry("obs", obs++);
							rslt.addEntry("solver", "Without Sequence Optimization");
							rslt.addEntry("time", memoryMeasurement.getLastSolvingTime());
							rslt.addEntry("memory", ((LinearSolverMemoryDecorator) memoryMeasurement).getRequiredMemory());
							rslt.addEntry("size", i);
							rslt.addEntry("constraints", ls.getConstraints().size());
							rslt.addEntry("problem", problem);
							rslt.addEntry("optimal", memoryMeasurement.getLastSolvingResult() == ResultType.OPTIMAL ||memoryMeasurement.getLastSolvingResult() == ResultType.SUBOPTIMAL? 1 : 0);
							rslt.addEntry("disabledConstraints", TestHelper.getDisabledConstraintsCount(ls));
							rslt.addEntry("iota", TestHelper.getIota(ls));
							rslt.addEntry("iota_rel", 0);
							rslt.addEntry("iota_ordered", 0);
							
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						writeResult(this.getResult());
						
						for(Variable v : ls.getVariables()) v.setValue(0.0d);
							
						memoryMeasurement.setBody(softSort);
							
						ls.setSolver(memoryMeasurement);
						ls.solve();
						
						this.getResult().resetEntries();
						rslt = this.getResult();
						try {
							rslt.addEntry("obs", obs++);
							rslt.addEntry("solver", "With Sequence Optimization");
							rslt.addEntry("time", memoryMeasurement.getLastSolvingTime());
							rslt.addEntry("memory", ((LinearSolverMemoryDecorator) memoryMeasurement).getRequiredMemory());
							rslt.addEntry("size", i);
							rslt.addEntry("constraints", ls.getConstraints().size());
							rslt.addEntry("problem", problem);
							rslt.addEntry("optimal", memoryMeasurement.getLastSolvingResult() == ResultType.OPTIMAL ||memoryMeasurement.getLastSolvingResult() == ResultType.SUBOPTIMAL? 1 : 0);
							rslt.addEntry("disabledConstraints", TestHelper.getDisabledConstraintsCount(ls));
							rslt.addEntry("iota", TestHelper.getIota(ls));
							rslt.addEntry("iota_rel", 0);
							rslt.addEntry("iota_ordered", 0);
							
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						writeResult(this.getResult());
						
						problem++;
					}
				}
			}
			
			@Override
			public void compileResult(LinearSolver solver, String name, LayoutSpec ls,
					int size, int iteration, int problem, int obs) {
				// directly implemented in doPerformanceTest
			}
		};
		
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
		
		runner.setResultCategories(rslt);
		
		return runner;
			
	}

}
