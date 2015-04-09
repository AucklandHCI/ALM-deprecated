package commons;

/**
 * This class contains a list of constants which are used in the code as penalty
 * values for soft constraints. Any new value for penalty should be defined here
 * to make the assignment of penalties traceable.  
 *  
 * @author hnad002
 *
 */

public class Penalties {
	public static final int MIN = 0;

	public static final double LEFT = 0.7;
	public static final double TOP = 0.7;
	
	public static final int LEFT_INSET = 100;
	public static final int TOP_INSET = 100;
	public static final int BOTTOM_INSET = 100;
	public static final int RIGHT_INSET = 100;
	
	public static final int DEFAULT_SHRINK_WIDTH = 2;
	public static final int DEFAULT_SHRINK_HEIGHT = 2;
	public static final int DEFAULT_GROW_WIDTH = 1;
	public static final int DEFAULT_GROW_HEIGHT = 1;

	// TYPE1 : JRadioButton, JCheckBox, JLabel, JProgressBar 
	// TYPE2 : editor components
	public static final int SHRINK_WIDTH_TYPE_1 = 4;
	public static final int SHRINK_HEIGHT_TYPE_1 = 4;
	public static final int SHRINK_WIDTH_TYPE_2 = 3;
	public static final int SHRINK_HEIGHT_TYPE_2 = 3;
	
	public static final int GROW_WIDTH_TYPE_1 = 0;
	public static final int GROW_HEIGHT_TYPE_1 = 0;
	public static final int GROW_WIDTH_TYPE_2 = 0;
	public static final int GROW_HEIGHT_TYPE_2 = 0;

	// difference between penalties for strict and relaxed constraints for 
	// component's preferred size.
	public static final int STRICT_RELAX_DIFF = 1;
}
