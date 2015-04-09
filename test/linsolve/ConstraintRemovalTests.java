package linsolve;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;

import junit.framework.Assert;
import linsolve.pivots.DeterministicPivotSummandSelector;
import linsolve.pivots.RandomPivotSummandSelector;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import alm.YTab;

@RunWith(Parameterized.class)
public class ConstraintRemovalTests {
	
	private AbstractLinearSolver solver;
	private LinearSpec linearSpec;
	private Constraint toBeRemoved;
	
	
	public ConstraintRemovalTests(AbstractLinearSolver solver){
		this.solver = solver;
		linearSpec = new LinearSpec(solver);

		
		
		toBeRemoved = linearSpec.addConstraint(1, new YTab(linearSpec, false), OperatorType.EQ, 0);
	}
	
	@Parameters
	public static Collection<Object[]> data() {
	    Object[][] data = new Object[][] { /*0*/{new LpSolve()}, /*1*/{new KaczmarzSolver()}
	    								    };
	    return Arrays.asList(data);
	  }
	

	@Test
	public void ConstraintClassRemovalMethod() {
		toBeRemoved.remove();
		assertFalse("The constraint has not been correctly removed using Constraint.remove()", linearSpec.constraints.contains(toBeRemoved));
	}
	
	@Test
	public void LinearSpecClassRemovalMethod() {
		linearSpec.removeConstraint(toBeRemoved);
		assertFalse("The constraint has not been correctly removed using LinearSpec.removeConstraint(Constraint)", linearSpec.constraints.contains(toBeRemoved));	
	}
	
	@Test
	public void SolverClassRemovalMethod() {
		solver.remove(toBeRemoved);
		assertFalse("The constraint has not been correctly removed using Solver.remove(Constraint)", linearSpec.constraints.contains(toBeRemoved));
	}

}
