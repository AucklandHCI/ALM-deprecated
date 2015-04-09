package linsolve.performance.experiments;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import linsolve.BinarySearchStrategy;
import linsolve.LinearRelaxationRemovingSuspicious;
import linsolve.LinearSolver;
import linsolve.RelaxationSolverAdapter;
import linsolve.ResultType;
import linsolve.performance.Result;
import linsolve.performance.TestRunner;
import linsolve.performance.decorator.LinearSolverMemoryDecorator;
import linsolve.pivots.DeterministicPivotSummandSelector;
import linsolve.pivots.RandomPivotSummandSelector;

import org.junit.Test;

import alm.LayoutSpec;

/**Here we implement performance test for relaxation parameter.
 * @author njam031
 *
 */
public class OmegaPerformanceTest {

	@Test
	public void omegaPerformanceTest() throws IOException, Exception{
		OmegaPerformanceTestRunner runner = new OmegaPerformanceTestRunner();
		Map<String, RelaxationSolverAdapter> solvers = new HashMap<String, RelaxationSolverAdapter>();
		//solvers.put("AddingWithDeterministicPivotSelector", 
			//new LinearRelaxationAddingConstraints(new DeterministicPivotSummandSelector()));		
		//solvers.put("AddingWithRandomPivotSelector", 
				//new LinearRelaxationAddingConstraints(new RandomPivotSummandSelector()));
		solvers.put("RemovingWithRandomPivotSelector", 
				new LinearRelaxationRemovingSuspicious(new RandomPivotSummandSelector()));
		solvers.put("RemovingWithDeterministicPivotSelector", 
				new LinearRelaxationRemovingSuspicious(new DeterministicPivotSummandSelector()));
		solvers.put("BinaryWithRandomPivotSelector", 
				new BinarySearchStrategy(new RandomPivotSummandSelector()));
		solvers.put("BinaryWithDeterministicPivotSelector", 
				new BinarySearchStrategy(new DeterministicPivotSummandSelector()));

		//solvers.put("Lp-solve", 
				//new LpSolve());
		//solvers.put("QRSolver", new QRSolver());
		//repititions and areas
		//runner.configure("./results.csv", 10, new int[]{1,2}, solvers);
		runner.configure("./results.csv",0.0d, 2.0d, 0.1d, 10, 80, solvers);
		runner.start();
	}

	public class OmegaPerformanceTestRunner extends TestRunner {
		
		private int iterations;
		private double from;
		private double to;
		private double step;
		private int size;
		private Map<String,RelaxationSolverAdapter> solvers;
		
		
		public OmegaPerformanceTestRunner(){
			super();
			Result rslt = new Result();
			rslt.addEntryCategory("obs");
			rslt.addEntryCategory("solver");
			rslt.addEntryCategory("omega");
			rslt.addEntryCategory("time");
			rslt.addEntryCategory("memory");
			rslt.addEntryCategory("size");
			rslt.addEntryCategory("constraints");
			rslt.addEntryCategory("problem");
			rslt.addEntryCategory("optimal");
			
			this.setResultCategories(rslt);
		}
		
		
		/**
		 * configures a PerformanceTestRunner.
		 * 
		 * @param path the path to the csv file which contains the results of a performance measurement
		 * @param iterations the number of iterations each problem size should be solved
		 * @param sizes the problem sizes which have to be solved
		 * @param solvers the solvers which have to be evaluated
		 */
		public void configure(String path, double from, double to, double step, int iterations, int size, Map<String, RelaxationSolverAdapter> solvers){
			this.configure(path, iterations, 0, 0,0, null);
			
			this.from = from;
			this.to = to;
			this.step = step;
			this.iterations = iterations;
			this.size = size;
			this.solvers = solvers;
		}
		
		public void doPerformanceTest() throws Exception, IOException{

			LinearSolverMemoryDecorator memoryMeasurement = new LinearSolverMemoryDecorator();
			writeHeader(this.getResult());
			int obs = 0;
			int problem = 0;
			getTestDataGenerator().configure(iterations, size);
			while(getTestDataGenerator().hasNext()){
				for(double omega = from; omega <= to; omega += step){
					for(String name : solvers.keySet()){
						RelaxationSolverAdapter solver = solvers.get(name);
						solver.setW(omega);
						memoryMeasurement.setBody(solver);
						
						LayoutSpec ls = getTestDataGenerator().next();
						
						ls.setSolver(memoryMeasurement);
						ls.solve();
						
						this.getResult().resetEntries();
						
						this.getResult().addEntry("obs", obs++);
						this.getResult().addEntry("solver", name);
						this.getResult().addEntry("omega", omega);
						this.getResult().addEntry("time", memoryMeasurement.getLastSolvingTime());
						this.getResult().addEntry("memory", memoryMeasurement.getRequiredMemory());
						this.getResult().addEntry("size", size);
						this.getResult().addEntry("constraints", ls.getConstraints().size());
						this.getResult().addEntry("problem", problem);
						this.getResult().addEntry("optimal", solver.getLastSolvingResult() == ResultType.OPTIMAL ? 1 : 0);
						
						writeResult(this.getResult());
					}
				}
				problem++;
			}
			
		}


		@Override
		public void compileResult(LinearSolver solver, String name, LayoutSpec ls,
				int size, int iteration, int problem, int obs) {
			// directly implemented in doPerformanceTest
		}
		
		
	}
}
