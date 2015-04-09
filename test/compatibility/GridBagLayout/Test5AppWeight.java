package compatibility.GridBagLayout;

import commons.Utils;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.lang.reflect.InvocationTargetException;

public class Test5AppWeight {

	public static void main(String[] args) throws InvocationTargetException,
	InterruptedException {
		runWithOptions("W_F", true, true);
		runWithOptions("W_noF", true, false);
	}
	
	public static void runWithOptions(String title, boolean shouldWeight, boolean shouldFill) {
		Test5App app = new Test5App();
		app.setOptions(shouldWeight, shouldFill);
		Component c[] = app.createAndShowGUI("GridBagLayout-"+title, new GridBagLayout(),
		new Dimension(500, 500), true);
		Utils.printComponents("GridBagLayout" + title, c);
		c = app.createAndShowGUI("ALMGridBagLayout-"+title,
		new alm.compatibility.GridBagLayout(), new Dimension(500, 500), true);
		Utils.printComponents("ALMGridBagLayout-" + title, c);
		Utils.generateGBTest("test5_" + title, c);

	}
}
