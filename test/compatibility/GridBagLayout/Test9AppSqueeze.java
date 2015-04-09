package compatibility.GridBagLayout;

import commons.Utils;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;

public class Test9AppSqueeze {
	public static void main(String[] args) throws InvocationTargetException,
			InterruptedException {
		Test9App app = new Test9App();
		Component c[] = app.createAndShowGUI("GridBagLayout-Test9Squeeze",
				new GridBagLayout(), new Dimension(300, 100), true);
		Utils.printComponents("GridBagLayout-Test9Squeeze", c);
		c = app.createAndShowGUI("ALMGridBagLayout-Test9Squeeze",
				new alm.compatibility.GridBagLayout(), new Dimension(300, 100),
				true);
		Utils.printComponents("ALMGridBagLayout-Test9Squeeze", c);
		Utils.generateGBTest("Test9Squeeze", c);
	}
}