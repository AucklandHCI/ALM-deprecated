package alm.examples.old;

import java.awt.Component;
import java.awt.Dimension;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import alm.ALMLayout;
import alm.LayoutSpec;
import alm.XTab;
import alm.YTab;
import commons.Utils;
import linsolve.*;
import alm.editor.ALMPanel;

public class JTableExampleOld extends ALMPanel {
	ALMLayout le = new ALMLayout();
	final boolean debug = true;
	LayoutSpec ls = new LayoutSpec();

	JTable table;
	JScrollPane tableScrollPane;


	public JTableExampleOld() {
		setLayout(le);

		String[] columnNames = {"First Name",
                "Last Name",
                "Sport",
                "# of Years",
                "Vegetarian"};
		
		Object[][] data = {
			    {"Kathy", "Smith",
			     "Snowboarding", new Integer(5), new Boolean(false)},
			    {"John", "Doe",
			     "Rowing", new Integer(3), new Boolean(true)},
			    {"Sue", "Black",
			     "Knitting", new Integer(2), new Boolean(false)},
			    {"Jane", "White",
			     "Speed reading", new Integer(20), new Boolean(true)},
			    {"Joe", "Brown",
			     "Pool", new Integer(10), new Boolean(false)}
			};
		
		table = new JTable(data, columnNames);
		table.setName("table");
		
		tableScrollPane = new JScrollPane(table);
		tableScrollPane.setName("tableScrollPane");
		
		XTab x1 = le.addXTab();
		XTab x2 = le.addXTab();
		YTab y1 = le.addYTab();
		YTab y2 = le.addYTab();
		
		le.addArea(x1, y1, x2, y2, tableScrollPane);
		le.addConstraint(1, x1, -1, le.getLeft(), -1, le.getRight(), 1, x2, OperatorType.EQ, 0);
		le.addConstraint(1, y1, -1, le.getTop(), -1, le.getBottom(), 1, y2, OperatorType.EQ, 0);
	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event dispatch thread.
	 */
	public static Component[] createAndShowGUI(String title, Dimension d, boolean show) {
		// Create and set up the window.
		JFrame frame = new JFrame(title);		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.getContentPane().add(new JTableExampleOld());

		frame.setPreferredSize(d);
		// Display the window.
		frame.pack();
		if (show) {
			frame.setVisible(true);
		}
		LinkedList <Component> result = Utils.getComponentList(((JPanel) (frame.getContentPane().getComponent(0))).getComponents());
		return result.toArray(new Component[result.size()]);
	}
	
	public static void main(String[] args) throws Exception {
		Component [] c = createAndShowGUI("JTableExample", new Dimension(500, 500), true);		
		Utils.printComponents("JTableExample", c);
		Utils.generateGBTest("JTableExample", c);
	}
}