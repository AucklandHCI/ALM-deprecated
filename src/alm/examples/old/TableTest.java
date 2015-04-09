package alm.examples.old;

import java.awt.Component;
import java.awt.Dimension;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import alm.*;
import alm.editor.ALMPanel;
import commons.Utils;

public class TableTest extends ALMPanel {
	ALMLayout le = new ALMLayout();
	LayoutSpec ls = new LayoutSpec();

	public TableTest() {
		setLayout(le);

		Column c1 = ls.addColumn(ls.getLeft(), ls.getRight());
		Row r1 = ls.addRow(ls.getTop(), null);
		Row r3 = ls.addRow(null, ls.getBottom());
		r1.setNext(r3);
		Row r2 = ls.addRow();
		r2.insertAfter(r1);

		JButton b1 = new JButton();
		b1.setText("A1");
		b1.setName("A1");
		Area a1 = ls.addArea(r1, c1, b1);
		a1.setHorizontalAlignment(HorizontalAlignment.LEFT);
		a1.setVerticalAlignment(VerticalAlignment.TOP);

		JButton b2 = new JButton();
		b2.setText("A2");
		b2.setName("A2");
		Area a2 = ls.addArea(r2, c1, b2);
		a2.setHorizontalAlignment(HorizontalAlignment.CENTER);
		a2.setVerticalAlignment(VerticalAlignment.CENTER);

		JButton b3 = new JButton();
		b3.setText("A3");
		b3.setName("A3");
		Area a3 = ls.addArea(r3, c1, b3);
		a3.setHorizontalAlignment(HorizontalAlignment.RIGHT);
		a3.setVerticalAlignment(VerticalAlignment.BOTTOM);

		try {
			r2.hasSameHeightAs(r1);
			r3.hasSameHeightAs(r1);
		} catch (Exception e) {
			e.printStackTrace();
		}

		le.setLayoutSpec(ls);
		add(b1);
		add(b2);
		add(b3);
	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event dispatch thread.
	 */
	public static Component[] createAndShowGUI(String title, Dimension d, boolean show) {
		// Create and set up the window.
		JFrame frame = new JFrame(title);		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.getContentPane().add(new TableTest());

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
		Component [] c = createAndShowGUI("TableTest", new Dimension(500, 500), true);		
		Utils.printComponents("TableTest", c);
		Utils.generateGBTest("TableTest", c);
	}
}
