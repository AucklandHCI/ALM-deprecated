package linsolve.performance.decorator;

import linsolve.ResultType;

/**
 * Decorates a LinearSolver with MemoryMeasurement capabilities.
 * @author jo
 *
 */
public class LinearSolverMemoryDecorator extends LinearSolverDecorator{
	
	private long allocatedMemory = -1;
	
	/**
	 * the required memory in kb
	 * 
	 * @return
	 */
	public long getRequiredMemory(){
		if(allocatedMemory == -1) throw new IllegalArgumentException("Cannot be called before solving.");

		return allocatedMemory;
	}
	
	public ResultType solve(){
		// try to remove all the objects from the memory which are not required for the current problem
		Runtime.getRuntime().gc();
		ResultType result = super.solve();
		allocatedMemory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/1024;
		return result;
	}
}