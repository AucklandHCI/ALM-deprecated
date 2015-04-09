package alm.examples.old;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.*;

import commons.Utils;
import linsolve.*;
import alm.ALMLayout;
import alm.Area;
import alm.HorizontalAlignment;
import alm.LayoutSpec;
import alm.XTab;
import alm.YTab;

public class TestEdit1 extends JPanel implements ActionListener {
	ALMLayout le = new ALMLayout();
	LayoutSpec ls = new LayoutSpec();


	JButton button1;
	JButton button2;
	JButton button3;
	JButton button4;
	JButton button5;
	JButton button6;
	JButton checkedListBox1;
	//JList checkedListBox1; // C# CheckedListBox
	JTextArea textBox1;
	JTextArea textBox2;
	JButton richTextBox1; // C# RichTextBox
	JButton listView1;
//	JMenuBar listView1; // C# ListView
	JLabel label1;
	JLabel label2;
	JLabel label3;

	public TestEdit1() {
		setLayout(le);

		@SuppressWarnings("unused")
		JMenu listViewItem2 = new JMenu("listView1"); // C# ListViewItem
		button1 = new JButton();
		button2 = new JButton();
		button3 = new JButton();
		button4 = new JButton();
		button5 = new JButton();
		button6 = new JButton();
		checkedListBox1 = new JButton();
		textBox1 = new JTextArea();
		textBox2 = new JTextArea();
		richTextBox1 = new JButton();
		listView1 = new JButton();
		label1 = new JLabel();
		label2 = new JLabel();
		label3 = new JLabel();

		button1.setText("button1");
		button2.setText("button2");
		button3.setText("button3");
		button4.setText("button4");
		button5.setText("button5");
		button6.setText("button6");
		checkedListBox1.setText("checkedListBox1");
		textBox1.setText("textBox1");
		textBox2.setText("textBox2");
		richTextBox1.setText("richTextBox1");
		listView1.setText("listView1");
		label1.setText("label1");
		label2.setText("label2");
		label3.setText("label3");

		button1.setName("button1");
		button2.setName("button2");
		button3.setName("button3");
		button4.setName("button4");
		button5.setName("button5");
		button6.setName("button6");
		textBox1.setName("textBox1");
		textBox2.setName("textBox2");
		checkedListBox1.setName("checkedListBox1");
		richTextBox1.setName("richTextBox1");
		listView1.setName("listView1");
		label1.setName("label1");
		label2.setName("label2");
		label3.setName("label3");

		add(label3);
		add(label2);
		add(label1);
		add(listView1);		
		add(richTextBox1);
		add(textBox2);
		add(textBox1);	
		add(checkedListBox1);
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
		YTab y1 = ls.addYTab();
		YTab y2 = ls.addYTab();
		YTab y3 = ls.addYTab();
		YTab y4 = ls.addYTab();
		YTab y5 = ls.addYTab();

		ls.addArea(ls.getLeft(), ls.getTop(), x1, y1, button1);
		ls.addArea(x1, ls.getTop(), x2, y1, button2);
		ls.addArea(ls.getLeft(), y1, x1, y2, button3);
		ls.addArea(x1, y1, x2, y2, button4);
		ls.addArea(ls.getLeft(), y2, x1, y3, button5);
		ls.addArea(x1, y2, x2, y3, button6);

		// give the buttons the same size
		ls.addConstraint(2, x1, -1, x2, OperatorType.EQ, 0);
		ls.addConstraint(2, y1, -1, y2, OperatorType.EQ, 0);
		ls.addConstraint(1, y1, 1, y2, -1, y3, OperatorType.EQ, 0);

		ls.addArea(ls.getLeft(), y3, x2, y4, checkedListBox1);
		ls.addArea(x2, ls.getTop(), ls.getRight(), y2, textBox1);
		ls.addArea(ls.getLeft(), y4, x3, y5, listView1);
		ls.addArea(x3, y2, ls.getRight(), y5, textBox2);

		Area richTextBox1Area = ls.addArea(x2, y2, x3, y4, richTextBox1);
		richTextBox1Area.setLeftInset(10);
		richTextBox1Area.setTopInset(10);
		richTextBox1Area.setRightInset(10);
		richTextBox1Area.setBottomInset(10);

		Area label1Area = ls.addArea(ls.getLeft(), y5, x2, ls.getBottom(), label1);
		label1Area.setHorizontalAlignment(HorizontalAlignment.LEFT);
		label1Area.setTopInset(4);
		label1Area.setBottomInset(4);

		Area label2Area = ls.addArea(x2, y5, x3, ls.getBottom(), label2);
		label2Area.setHorizontalAlignment(HorizontalAlignment.CENTER);
		label2Area.setTopInset(4);
		label2Area.setBottomInset(4);

		Area label3Area = ls.addArea(x3, y5, ls.getRight(), ls.getBottom(), label3);
		label3Area.setHorizontalAlignment(HorizontalAlignment.RIGHT);
		label3Area.setTopInset(4);
		label3Area.setBottomInset(4);

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

		frame.getContentPane().add(new TestEdit1());

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
		Component [] c = createAndShowGUI("TestEdit1", new Dimension(500, 500), true);		
		Utils.printComponents("TestEdit1", c);
		Utils.generateGBTest("TestEdit1Test", c);
	}
	
	public void actionPerformed(ActionEvent arg0) {
		try {
			le.edit(this);
		} catch (Exception e) {		
			e.printStackTrace();
		}

	}
}
