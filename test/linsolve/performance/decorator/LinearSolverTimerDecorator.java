package linsolve.performance.decorator;

import linsolve.ResultType;

/**
 * Decorates a LinearSolver with time measurement capabilities
 * 
 */
public class LinearSolverTimerDecorator extends LinearSolverDecorator{
	
	private long start  = -1;
	private long end = -1;
	
	/**
	 * returns the required time to solve the current problem
	 * 
	 * @return
	 */
	public long getSolvingTime(){
		return (long)super.getLastSolvingTime();
	}
	
	public ResultType solve(){
		ResultType result = super.solve();
		end = System.currentTimeMillis();
		return result;
	}
}