package coverage;

import org.junit.runner.RunWith;

import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({linsolve.AllTests.class, 
	linsolve.functional.AllTests.class,
	alm.ALMTests.class,
	compatibility.GridBagLayout.GridBagLayoutTests.class,
 })
public class AllTests {

}
