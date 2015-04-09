package compatibility.GridBagLayout;

import commons.Utils;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;

//GridBagLayout Copyright � 2000 by Ken Slonneger 5
public class Test7App extends JPanel {

	private static final long serialVersionUID = 1L;

	Test7App(LM layout) {
		setLayout(layout.gbl != null? layout.gbl : layout.agbl);
		GridBagConstraints cons = new GridBagConstraints();
		
		cons.gridx = 0;
		cons.gridy = 0;
		cons.gridwidth = 1;
		cons.gridheight = 2;
		cons.fill = GridBagConstraints.NONE;
		cons.anchor = GridBagConstraints.CENTER;
		cons.weightx = 1;
		cons.weighty = 2;
		addButton("One", cons, layout);
		
		cons.gridx = 1;
		cons.gridy = 0;
		cons.gridwidth = 1;
		cons.gridheight = 2;
		cons.fill = GridBagConstraints.VERTICAL;
		cons.anchor = GridBagConstraints.EAST;
		cons.weightx = 1;
		cons.weighty = 2;
		addButton("Two", cons, layout);
		
		cons.gridx = 2;
		cons.gridy = 0;
		cons.gridwidth = 2;
		cons.gridheight = 2;
		cons.fill = GridBagConstraints.HORIZONTAL;
		cons.anchor = GridBagConstraints.NORTH;
		cons.weightx = 2;
		cons.weighty = 2;
		addButton("Three", cons, layout);
		
		cons.gridx = 0;
		cons.gridy = 2;
		cons.gridwidth = 1;
		cons.gridheight = 1;
		cons.fill = GridBagConstraints.BOTH;
		cons.anchor = GridBagConstraints.CENTER;
		cons.weightx = cons.weighty = 1;
		addButton("Four", cons, layout);
		
		cons.gridx = 1;
		cons.gridy = 2;
		cons.gridwidth = 1;
		cons.gridheight = 1;
		cons.fill = GridBagConstraints.NONE;
		cons.anchor = GridBagConstraints.SOUTHWEST;
		cons.weightx = cons.weighty = 1;
		addButton("Five", cons, layout);
		
		cons.gridx = 2;
		cons.gridy = 2;
		cons.gridwidth = 2;
		cons.gridheight = 1;
		cons.fill = GridBagConstraints.BOTH;
		cons.anchor = GridBagConstraints.CENTER;
		cons.weightx = 2;
		cons.weighty = 1;
		addButton("Six", cons, layout);
	}

	void addButton(String label, GridBagConstraints constraints,
			LM layout) {
		JButton button = new JButton(label);
		button.setName(label);
		layout.setConstraints(button, constraints);
		add(button);
	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event dispatch thread.
	 */
	public static Component[] createAndShowGUI(String title, LayoutManager lm,
			Dimension d, boolean show) {
		// Create and set up the window.
		JFrame frame = new JFrame(title);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Add content to the window.
		if (lm instanceof GridBagLayout)
			frame.add(new Test7App(new LM((GridBagLayout) lm)));
		else
			frame.add(new Test7App(new LM((alm.compatibility.GridBagLayout) lm)));

		frame.setPreferredSize(d);
		// Display the window.
		frame.pack();
		if (show) {
			frame.setVisible(true);
		}
		LinkedList <Component> result = Utils.getComponentList(((JPanel) (frame.getContentPane().getComponent(0))).getComponents());
		return result.toArray(new Component[result.size()]);
	}

	public static void main(String[] args) {
		Component c[] = createAndShowGUI("GridBagLayout-Test7", new GridBagLayout(),
				new Dimension(500, 500), true);
		Utils.printComponents("GridBagLayout-Test7", c);
		c = createAndShowGUI("ALMGridBagLayout-Test7",
				new alm.compatibility.GridBagLayout(), new Dimension(
						500, 500), true);
		Utils.printComponents("ALMGridBagLayout-Test7", c);
		Utils.generateGBTest("Test7", c);
	}
}
