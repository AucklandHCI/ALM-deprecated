package alm.examples.old;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.LinkedList;

import javax.swing.*;

import commons.Utils;
import linsolve.OperatorType;

import alm.ALMLayout;
import alm.editor.ALMPanel;
import alm.LayoutSpec;
import alm.XTab;
import alm.YTab;


public class PinWheel extends ALMPanel implements ActionListener {
	private String AVMSpecName = "Pinwheel Button Spec";
	private static String dbName = "AVM";
	private LayoutSpec ls = new LayoutSpec();

	private ALMPanel panel;
	private ALMLayout le;

	public PinWheel() {
		// Set the layout specification
		le = new ALMLayout(ls);
		setLayout(le);
		

		// Try load the layout from the xml file,
		// else use the default layout method
		File layoutSpec = new File("Pinwheel_layout.xml");
		if (layoutSpec.exists()) {
			le.load(layoutSpec);
		} else {
			setDefaultLayoutSpec();
		}
		
		setVisible(true);
	}

	private void setDefaultLayoutSpec() {
		XTab x1 = ls.addXTab();
		XTab x2 = ls.addXTab();
		YTab y1 = ls.addYTab();
		YTab y2 = ls.addYTab();

		JButton button1 = new JButton("Button 1");
		button1.addActionListener(this);
		button1.setName("Button 1");
		
		JButton button2 = new JButton("Button 2");
		button2.setName("Button 2");
		
		JButton button3 = new JButton("Button 3");
		button3.setName("Button 3");
		
		JButton button4 = new JButton("Button 4");
		button4.setName("Button 4");
		
		JTextArea text1 = new JTextArea("Text 1");
		text1.setName("Text 1");

		le.addArea(ls.getLeft(), ls.getTop(), x2, y1, button1);

		le.addArea(x2, ls.getTop(), ls.getRight(), y2, button2);

		le.addArea(x1, y2, ls.getRight(), ls.getBottom(), button3);

		le.addArea(ls.getLeft(), y1, x1, ls.getBottom(), button4);


		le.addArea(x1, y1, x2, y2, text1);

		// Add constraints to make the Pin wheel shape
		le.addConstraint(3, x1, -2, ls.getLeft(), -1, ls.getRight(),
				OperatorType.EQ, 0);
		le.addConstraint(3, y1, -2, ls.getTop(), -1, ls.getBottom(),
				OperatorType.EQ, 0);
		le.addConstraint(1, x1, -1, ls.getLeft(), -1, ls.getRight(), 1, x2,
				OperatorType.EQ, 0);
		le.addConstraint(1, y1, -1, ls.getTop(), -1, ls.getBottom(), 1, y2,
				OperatorType.EQ, 0);

		// Display the widgets in their new location
		revalidate();
	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event dispatch thread.
	 */
	public static Component[] createAndShowGUI(String title, Dimension d, boolean show) {
		// Create and set up the window.
		JFrame frame = new JFrame(title);		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.getContentPane().add(new PinWheel());

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
		Component [] c = createAndShowGUI("PinWheel", new Dimension(500, 500), true);		
		Utils.printComponents("PinWheel", c);
		Utils.generateGBTest("PinWheelTest", c);
	}
	public void actionPerformed(ActionEvent arg0) {
		try {
			le.edit(panel);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
