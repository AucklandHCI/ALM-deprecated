package commons;

/**
 * configuration parameters for test applications and junit tests.
 * @author hnad002
 *
 */
public class TestConfigParams {
	public static boolean debug = true; // flag to print debug messages in tests.
	public static boolean generateGBTest = false; // If set to true, running the test application prints a junit test in the console.
	public static double epsilon = 2.0; // error interval for test results.

}
