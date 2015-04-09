package linsolve.functional;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ GaussSeidelAddingSoftSolverTests.class,
		GaussSeidelGroupingSoftSolverTests.class, GaussSeidelSolverTests.class,
		KaczmarzSolverTests.class, KazcmarzAddingSoftSolverTests.class,
		KazcmarzGroupingSoftSolverTests.class, LpSolverTests.class,		
		QRSolverTests.class })
public class AllTests {

}
