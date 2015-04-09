package alm.examples.old;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.*;

import alm.*;
import commons.Utils;
import linsolve.*;
import alm.editor.ALMPanel;
public class ThirteenWidgetsOld extends ALMPanel implements ActionListener{
	ALMLayout le = new ALMLayout();
	LayoutSpec ls = new LayoutSpec();
	
	JButton button1;
	JButton button2;
	JButton button3;
	JButton button4;
	JButton button5;
	JButton button6;
	JList checkedListBox1;	// C# CheckedListBox
	JTextArea textBox1;
	JTextArea textBox2;
	JTextArea richTextBox1; // C# RichTextBox
	JMenuBar listView1;		// C# ListView
	JLabel label1;
	JLabel label2;
	JLabel label3;
	
	public ThirteenWidgetsOld() {
		setLayout(le);
		
		JMenu listViewItem2 = new JMenu("listView1");	// C# ListViewItem
        button1 = new JButton();
        button2 = new JButton();
        button3 = new JButton();
        button4 = new JButton();
        button5 = new JButton();
        button6 = new JButton();
        checkedListBox1 = new JList();
        textBox1 = new JTextArea();
        textBox2 = new JTextArea();
        richTextBox1 = new JTextArea();
        listView1 = new JMenuBar();
        label1 = new JLabel();
        label2 = new JLabel();
        label3 = new JLabel();
        // 
        // button1
        // 
        button1.setLocation(new Point(7, 8));
        button1.setName("button1");
        button1.setSize(new Dimension(44, 29));
        // button1.TabIndex = 0;
        button1.setText("button1");
        // button1.UseVisualStyleBackColor = true;
        // 
        // button2
        // 
        button2.setLocation(new Point(63, 10));
        button2.setName("button2");
        button2.setSize(new Dimension(44, 26));
        // button2.TabIndex = 1;
        button2.setText("button2");
        // button2.UseVisualStyleBackColor = true;
        // 
        // button3
        // 
        button3.setLocation(new Point(10, 45));
        button3.setName("button3");
        button3.setSize(new Dimension(40, 30));
        // button3.TabIndex = 2;
        button3.setText("button3");
        // button3.UseVisualStyleBackColor = true;
        // 
        // button4
        // 
        button4.setLocation(new Point(66, 47));
        button4.setName("button4");
        button4.setSize(new Dimension(40, 27));
        // button4.TabIndex = 3;
        button4.setText("button4");
        // button4.UseVisualStyleBackColor = true;
        // 
        // button5
        // 
        button5.setLocation(new Point(13, 85));
        button5.setName("button5");
        button5.setSize(new Dimension(37, 33));
        // button5.TabIndex = 4;
        button5.setText("button5");
        // button5.UseVisualStyleBackColor = true;
        // 
        // button6
        // 
        button6.setLocation(new Point(70, 92));
        button6.setName("button6");
        button6.setSize(new Dimension(36, 25));
        // button6.TabIndex = 5;
        button6.setText("button6");
        // button6.UseVisualStyleBackColor = true;
        // 
        // checkedListBox1
        // 
        // checkedListBox1.FormattingEnabled = true;
        // checkedListBox1.IntegralHeight = false;
        checkedListBox1.setLocation(new Point(16, 137));
        checkedListBox1.setName("checkedListBox1");
        checkedListBox1.setSize(new Dimension(89, 49));
        // checkedListBox1.TabIndex = 6;
        // 
        // textBox1
        // 
        textBox1.setLocation(new Point(135, 12));
        // textBox1.Multiline = true;
        textBox1.setName("textBox1");
        textBox1.setSize(new Dimension(364, 62));
        // textBox1.TabIndex = 7;
        textBox1.setText("textBox1");
        // 
        // textBox2
        // 
        textBox2.setLocation(new Point(352, 94));
        // textBox2.Multiline = true;
        textBox2.setName("textBox2");
        textBox2.setSize(new Dimension(145, 208));
        // textBox2.TabIndex = 9;
        textBox2.setText("textBox2");
        // 
        // richTextBox1
        // 
        richTextBox1.setLocation(new Point(139, 97));
        richTextBox1.setName("richTextBox1");
        richTextBox1.setSize(new Dimension(200, 88));
        // richTextBox1.TabIndex = 10;
        richTextBox1.setText("richTextBox1");
        // 
        // listView1
        // 
        listView1.add(listViewItem2);
        listView1.setLocation(new Point(18, 205));
        listView1.setName("listView1");
        listView1.setSize(new Dimension(320, 96));
        // listView1.TabIndex = 11;
        // listView1.UseCompatibleStateImageBehavior = false;
        // 
        // label1
        // 
        //label1.AutoSize = true;
        label1.setLocation(new Point(19, 316));
        label1.setName("label1");
        label1.setSize(new Dimension(35, 13));
        // label1.TabIndex = 12;
        label1.setText("label1");
        // 
        // label2
        // 
        // label2.AutoSize = true;
        label2.setLocation(new Point(188, 323));
        label2.setName("label2");
        label2.setSize(new Dimension(35, 13));
        // label2.TabIndex = 13;
        label2.setText("label2");
        // 
        // label3
        // 
        // label3.AutoSize = true;
        label3.setLocation(new Point(438, 324));
        label3.setName("label3");
        label3.setSize(new Dimension(35, 13));
        // label3.TabIndex = 14;
        label3.setText("label3");

        
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
//are these necessary constraints
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
//what about these down
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

		frame.getContentPane().add(new ThirteenWidgetsOld());

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
		Component [] c = createAndShowGUI("ThirteenWidgets", new Dimension(500, 500), true);		
		Utils.printComponents("ThirteenWidgets", c);
		Utils.generateGBTest("ThirteenWidgets", c);
	}
	
	public void actionPerformed(ActionEvent arg0) {
		try {
			le.edit(this);
		} catch (Exception e) {		
			e.printStackTrace();
			}
}
	}
	
	
