package alm.examples.old;

import java.awt.Component;
import java.awt.Dimension;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import alm.ALMLayout;
import alm.LayoutSpec;
import alm.XTab;
import alm.YTab;
import commons.Utils;
import linsolve.*;
import alm.editor.ALMPanel;

public class TwoButtonsAndOnePanel extends ALMPanel {
	ALMLayout le = new ALMLayout();
	final boolean debug = true;
	LayoutSpec ls = new LayoutSpec();

	JButton button1;
	JButton button2;
	JPanel panel1;


	public TwoButtonsAndOnePanel() {
		setLayout(le);
		button1 = new JButton();
		button2 = new JButton();
		panel1 = new JPanel();
		// 
		// button1
		// 
		button1.setName("button1");
		button1.setText("button1");
		button1.setName("button1");
		// 
		// button2
		// 
		button2.setName("button2");
		button2.setText("button2");
		button2.setName("button2");
		// 
		// button3
		// 
		panel1.setName("panel1");

		add(button1);
		add(button2);
		add(panel1);
		
		JLabel label = new JLabel("Child Label");
		label.setName("Child Label");
		JTextField text = new JTextField("Child Text Field");
		text.setName("Child Text Field");
		JButton child = new JButton("Child Button");
		child.setName("Child Button");
		panel1.add(label);
		panel1.add(text);
		panel1.add(child);
		
		setLayoutSpec();
	}

	public void setLayoutSpec() {
		XTab x1 = ls.addXTab("x1");
		YTab y1 = ls.addYTab("y1");
		
		// Three button example
		ls.addArea(ls.getLeft(), ls.getTop(), x1, y1, button1);
		ls.addArea(x1, ls.getTop(), ls.getRight(), y1, button2);
		ls.addArea(ls.getLeft(), y1, ls.getRight(), ls.getBottom(), panel1);
        // give the two columns the same width and the two rows the same height
		ls.addConstraint(2, x1, -1, ls.getRight(), OperatorType.EQ, 0);
        ls.addConstraint(2, y1, -1, ls.getBottom(), OperatorType.EQ, 0);
		le.setLayoutSpec(ls);
		
		 	
	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event dispatch thread.
	 */
	public static Component[] createAndShowGUI(String title, Dimension d, boolean show) {
		// Create and set up the window.
		JFrame frame = new JFrame(title);		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.getContentPane().add(new TwoButtonsAndOnePanel());

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
		Component [] c = createAndShowGUI("TwoButtonsAndOnePanel", new Dimension(500, 500), true);		
		Utils.printComponents("TwoButtonsAndOnePanel", c);
		Utils.generateGBTest("TwoButtonsAndOnePanel", c);
	}
}