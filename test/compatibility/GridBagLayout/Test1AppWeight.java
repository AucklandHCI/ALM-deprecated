package compatibility.GridBagLayout;

import commons.Utils;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.lang.reflect.InvocationTargetException;

public class Test1AppWeight {

	public static void main(String[] args) throws InvocationTargetException,
	InterruptedException {
		runWithOptions("Wx_noWy_F", true, false, true);
		runWithOptions("noWx_Wy_F", false, true, true);
		runWithOptions("Wx_Wy_F", true, true, true);
		runWithOptions("Wx_Wy_noF", true, true, false);
	}
	
	public static void runWithOptions(String title, boolean shouldWeightx, boolean shouldWeighty, boolean shouldFill) {
		Test1App app = new Test1App();
		app.setOptions(shouldWeightx, shouldWeighty, shouldFill);
		Component c[] = app.createAndShowGUI("GridBagLayout-"+title, new GridBagLayout(),
		new Dimension(500, 500), true);
		Utils.printComponents("GridBagLayout" + title, c);
		c = app.createAndShowGUI("ALMGridBagLayout-"+title,
		new alm.compatibility.GridBagLayout(), new Dimension(500, 500), true);
		Utils.printComponents("ALMGridBagLayout-" + title, c);
		Utils.generateGBTest("test1_" + title, c);

	}
}
