package linsolve;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ AbstractLinearSolverTests.class,
		ConstraintComparatorByNumOfVarsTests.class,
		ConstraintComparatorByPenaltyTests.class, ConstraintTests.class,
		KaczmarzSolverTests.class, VariableTest.class })
public class AllTests {

}
