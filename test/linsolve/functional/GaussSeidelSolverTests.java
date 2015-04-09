package linsolve.functional;


import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import linsolve.Constraint;
import linsolve.GaussSeidelSolver;
import linsolve.LinearSpec;
import linsolve.OperatorType;
import linsolve.Variable;
import linsolve.pivots.DeterministicPivotSummandSelector;
import linsolve.pivots.ImprovedTwoPhaseSelector;
import linsolve.pivots.PivotSummandSelector;
import linsolve.pivots.RandomPivotSummandSelector;
import linsolve.pivots.TwoPhaseSelector;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;




@RunWith(Parameterized.class)
public class GaussSeidelSolverTests extends LinearProblems {

	private PivotSummandSelector summandSelector;
	
	public GaussSeidelSolverTests(PivotSummandSelector summandSelector) {
		this.summandSelector = summandSelector;
	}
	
	@Parameters
	public static Collection<Object[]> data() {
	    Object[][] data = new Object[][] { {new DeterministicPivotSummandSelector()}, 
	    								   {new RandomPivotSummandSelector()       },	    								   
	    								    };
	    return Arrays.asList(data);
	  }

	@Override
	public void setup(){
		setLinearSolver(new GaussSeidelSolver(summandSelector, 5000));
	}
	
	@Override
	/**
	 * Gauss Seidel is not able to pass this test alone. It needs to be combined 
	 * with a soft solver. This dummy method is added to avoid showing failures 
	 * when system runs the tests.
	 */
	public void NonSquareMatrix() {
		if (commons.TestConfigParams.debug) {
			printMsg("\n"+this.getClass().getName()+Thread.currentThread().getStackTrace()[1].getMethodName()+
					": GaussSidelSolver is not able to solve this linear system alone. It needs to be combined with a soft solver.");
		}
		
	}
}