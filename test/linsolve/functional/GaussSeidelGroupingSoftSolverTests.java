package linsolve.functional;

import java.util.Arrays;
import java.util.Collection;

import linsolve.GaussSeidelSolver;
import linsolve.pivots.DeterministicPivotSummandSelector;
import linsolve.pivots.ImprovedTwoPhaseSelector;
import linsolve.pivots.PivotSummandSelector;
import linsolve.pivots.RandomPivotSummandSelector;
import linsolve.pivots.TwoPhaseSelector;
import linsolve.softconstraints.GroupingSoftSolver;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class GaussSeidelGroupingSoftSolverTests extends LinearProblems {

	private PivotSummandSelector summandSelector;
	
	public GaussSeidelGroupingSoftSolverTests(PivotSummandSelector summandSelector) {
		this.summandSelector = summandSelector;
	}
	
	@Parameters
	public static Collection<Object[]> data() {
	    Object[][] data = new Object[][] { {new DeterministicPivotSummandSelector()}, 
	    								   {new RandomPivotSummandSelector()       }
	    								    };
	    return Arrays.asList(data);
	  }
	@Override
	public void setup(){
		setLinearSolver(new GroupingSoftSolver(new GaussSeidelSolver(summandSelector, 1000)));
	}
}
