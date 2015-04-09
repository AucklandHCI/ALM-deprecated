package alm.examples.old;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.*;

import commons.Utils;
import linsolve.*;
import alm.*;
import alm.editor.*;

public class EditorSample extends ALMPanel implements ActionListener{
	private static final long serialVersionUID = -5811314162259713031L;
	ALMLayout le = new ALMLayout();
	LayoutSpec ls = new LayoutSpec();

	JButton button1;
	JButton button2;
	JButton button3;
	JButton button4;
	JButton button5;
	JButton button6;
	JButton button7;
	JButton button8;
	JTextArea textbox1;

	public EditorSample() {
		setLayout(le);
		button1 = new JButton();
		button2 = new JButton();
		button3 = new JButton();
		button4 = new JButton();
		button5 = new JButton();
		button6 = new JButton();
		button7 = new JButton();
		button8 = new JButton();
		textbox1 = new JTextArea();

		button1.setText("Click to edit");
		button2.setText("button2");
		button3.setText("button3");
		button4.setText("button4");
		button5.setText("button5");
		button6.setText("button6");
		button7.setText("button7");
		button8.setText("button8");
		textbox1.setText("textbox1");

		button1.setName("button1");
		button2.setName("button2");
		button3.setName("button3");
		button4.setName("button4");
		button5.setName("button5");
		button6.setName("button6");
		button7.setName("button7");
		button8.setName("button8");
		textbox1.setName("textbox1");

		add(textbox1);
		add(button8);
		add(button7);
		add(button6);
		add(button5);
		add(button4);
		add(button3);
		add(button2);
		add(button1);
		button1.addActionListener(this);
		setLayoutSpec();
	}

	public void setLayoutSpec() {
		XTab x1 = ls.addXTab();
		XTab x2 = ls.addXTab();
		XTab x3 = ls.addXTab();
		XTab x4 = ls.addXTab();
		YTab y1 = ls.addYTab();
		YTab y2 = ls.addYTab();
		YTab y3 = ls.addYTab();
		
		ls.addArea(ls.getLeft(), ls.getTop(), x1, y1, button1);
		ls.addArea(ls.getLeft(), y1, x1, ls.getBottom(), textbox1);
		ls.addArea(x1, ls.getTop(), ls.getRight(), y1, button2);
		ls.addArea(x1, y1, x2, y2, button3);
		ls.addArea(x2, y1, ls.getRight(), y2, button4);
		ls.addArea(x1, y2, x3, y3, button5);
		ls.addArea(x3, y2, ls.getRight(), y3, button6);
		ls.addArea(x1, y3, x4, ls.getBottom(), button7);
		ls.addArea(x4, y3, ls.getRight(), ls.getBottom(), button8);
		
		ls.addConstraint(1, x1, OperatorType.EQ, 150);
		ls.addConstraint(1, y1, OperatorType.EQ, 120);
		//ls.addConstraint(1, y2, OperatorType.EQ, 180);
		ls.addConstraint(1, y3, OperatorType.EQ, 240);
		ls.addConstraint(-1, y2, 0.4, y1, 0.6, y3, OperatorType.EQ, 0);
		ls.addConstraint(-1, x2, 0.4, x1, 0.6, ls.getRight(), OperatorType.EQ, 0);
		ls.addConstraint(0.7, x1, -1, x3, 0.3, ls.getRight(), OperatorType.EQ, 0);
		ls.addConstraint(0.3, x1, 0.7, ls.getRight(), -1, x4, OperatorType.EQ, 0);
		
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

		frame.getContentPane().add(new EditorSample());

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
		Component [] c = createAndShowGUI("EditorSample", new Dimension(500, 500), true);		
		Utils.printComponents("EditorSample", c);
		Utils.generateGBTest("EditorSampleTest", c);
	}
	
	public void actionPerformed(ActionEvent arg0) {
		try {
			le.edit(this);
		} catch (Exception e) {		
			e.printStackTrace();
		}

	}
}
