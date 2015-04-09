package linsolve.functional;

import java.util.Arrays;
import java.util.Collection;

import linsolve.GaussSeidelSolver;
import linsolve.functional.LinearProblems;
import linsolve.pivots.DeterministicPivotSummandSelector;
import linsolve.pivots.PivotSummandSelector;
import linsolve.pivots.RandomPivotSummandSelector;
import linsolve.softconstraints.AddingSoftSolver;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class GaussSeidelAddingSoftSolverTests extends LinearProblems {
	

	private PivotSummandSelector summandSelector;
	
	public GaussSeidelAddingSoftSolverTests(PivotSummandSelector summandSelector) {
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
		setLinearSolver(new AddingSoftSolver(new GaussSeidelSolver(summandSelector, 1000)));
	}
}
