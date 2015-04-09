package alm.examples.old;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.util.LinkedList;

import javax.swing.*;

import alm.ALMLayout;
import alm.LayoutSpec;
import commons.Utils;
import linsolve.LinearSpec;
import linsolve.LpSolve;

import alm.editor.ALMPanel;

/**
 * 
 *It needs lpsolve otherwise it will be failed
 * @author njam031
 *
 */
public class ReverseEngineering extends ALMPanel {
	ALMLayout le = new ALMLayout();
	LinearSpec ls = new LayoutSpec(new LpSolve());
	
	JButton button1;
	JButton button2;
	JList listBox1;
	JTextArea textBox1;
	
	public ReverseEngineering() {
		
		setLayout(le);
		invalidate();
		
		button1 = new JButton();
        button2 = new JButton();
        textBox1 = new JTextArea();
        // 
        // button1
        // 
        button1.setLocation(new Point(13, 9));
        button1.setName("button1");
        button1.setSize(new Dimension(82, 37));
        // button1.TabIndex = 0;
        button1.setText("button1");
        // button1.UseVisualStyleBackColor = true;
        // 
        // button2
        // 
        button2.setLocation(new Point(13, 52));
        button2.setName("button2");
        button2.setSize(new Dimension(82, 36));
        // button2.TabIndex = 1;
        button2.setText("button2");
        // button2.UseVisualStyleBackColor = true;
        // 
        // listBox1
        // 
        // listBox1.FormattingEnabled = true;
        // listBox1.IntegralHeight = false;
        listBox1 = new JList(new String[]{"item1", "item2"});
        listBox1.setLocation(new Point(13, 94));
        listBox1.setName("listBox1");
        listBox1.setSize(new Dimension(82, 95));
        // listBox1.TabIndex = 2;
        // 
        // textBox1
        // 
        textBox1.setLocation(new Point(101, 9));
        // textBox1.Multiline = true;
        textBox1.setName("textBox1");
        textBox1.setSize(new Dimension(179, 180));
        // textBox1.TabIndex = 3;
        textBox1.setText("hello");
        
        add(textBox1);
        add(listBox1);
        add(button2);
        add(button1);
        
        setLayoutSpec();
        validate();
	}
	
	public void setLayoutSpec() {
		le.recoverLayout(this);
        ls = le.getLayoutSpec();
//        ls.areaOf(button1).setBottom(ls.areaOf(button2).getTop());
	}
	
	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event dispatch thread.
	 */
	public static Component[] createAndShowGUI(String title, Dimension d, boolean show) {
		// Create and set up the window.
		JFrame frame = new JFrame(title);		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.getContentPane().add(new ReverseEngineering());

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
		Component [] c = createAndShowGUI("ReverseEngineering", new Dimension(500, 500), true);		
		Utils.printComponents("ReverseEngineering", c);
		Utils.generateGBTest("ReverseEngineering", c);
	}
}