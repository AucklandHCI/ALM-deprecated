package compatibility.GridBagLayout;

import commons.Utils;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class Test10App extends JPanel {

  JTextField name = new JTextField(20), phone = new JTextField(10), address = new JTextField(20);

  JRadioButton small = new JRadioButton("Small"), medium = new JRadioButton("Medium"),
      large = new JRadioButton("Large"), thick = new JRadioButton("Thick"),
      thin = new JRadioButton("Thin");

  JCheckBox pepperoni = new JCheckBox("Pepperoni"), mushrooms = new JCheckBox("Mushrooms"),
      anchovies = new JCheckBox("Anchovies");

  JButton okButton = new JButton("OK"), closeButton = new JButton("Close");

  public Test10App(LM lm) {    
    setLayout(lm.agbl == null ? lm.gbl : lm.agbl);
    addItem("Name", this, new JLabel("Name:"), 0, 0, 1, 1, GridBagConstraints.EAST);
    addItem("Phone", this, new JLabel("Phone:"), 0, 1, 1, 1, GridBagConstraints.EAST);
    addItem("Address", this, new JLabel("Address:"), 0, 2, 1, 1, GridBagConstraints.EAST);

    addItem("name-field", this, name, 1, 0, 2, 1, GridBagConstraints.WEST);
    addItem("phone-field", this, phone, 1, 1, 1, 1, GridBagConstraints.WEST);
    addItem("address-field", this, address, 1, 2, 2, 1, GridBagConstraints.WEST);

    Box sizeBox = Box.createVerticalBox();
    ButtonGroup sizeGroup = new ButtonGroup();
    sizeGroup.add(small);
    sizeGroup.add(medium);
    sizeGroup.add(large);
    sizeBox.add(small);
    sizeBox.add(medium);
    sizeBox.add(large);
    sizeBox.setBorder(BorderFactory.createTitledBorder("Size"));
    addItem("size-box", this, sizeBox, 0, 3, 1, 1, GridBagConstraints.NORTH);

    Box styleBox = Box.createVerticalBox();

    ButtonGroup styleGroup = new ButtonGroup();
    styleGroup.add(thin);
    styleGroup.add(thick);
    styleBox.add(thin);
    styleBox.add(thick);
    styleBox.setBorder(BorderFactory.

    createTitledBorder("Style"));
    addItem("style-box", this, styleBox, 1, 3, 1, 1, GridBagConstraints.NORTH);

    Box topBox = Box.createVerticalBox();
    ButtonGroup topGroup = new ButtonGroup();
    topGroup.add(pepperoni);
    topGroup.add(mushrooms);
    topGroup.add(anchovies);
    topBox.add(pepperoni);
    topBox.add(mushrooms);
    topBox.add(anchovies);
    topBox.setBorder(BorderFactory.createTitledBorder("Toppings"));
    addItem("topbox", this, topBox, 2, 3, 1, 1, GridBagConstraints.NORTH);

    Box buttonBox = Box.createHorizontalBox();
    buttonBox.add(okButton);
    buttonBox.add(Box.createHorizontalStrut(20));
    buttonBox.add(closeButton);
    addItem("buttonBox", this, buttonBox, 2, 4, 1, 1, GridBagConstraints.NORTH);
  }

  private void addItem(String name, JPanel p, JComponent c, int x, int y, int width, int height, int align) {
    GridBagConstraints gc = new GridBagConstraints();
    gc.gridx = x;
    gc.gridy = y;
    gc.gridwidth = width;
    gc.gridheight = height;
    gc.weightx = 100.0;
    gc.weighty = 100.0;
    gc.insets = new Insets(5, 5, 5, 5);
    gc.anchor = align;
    gc.fill = GridBagConstraints.NONE;
    c.setName(name);
    p.add(c, gc);
  }
  
	public static Component[] createAndShowGUI(String title, LayoutManager lm,
			Dimension d, boolean show) {
		// Create and set up the window.
		JFrame frame = new JFrame(title);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Add content to the window.
		if (lm instanceof GridBagLayout)
			frame.add(new Test10App(new LM((GridBagLayout) lm)));
		else
			frame.add(new Test10App(new LM((alm.compatibility.GridBagLayout) lm)));

		frame.setPreferredSize(d);
		// Display the window.
		frame.pack();
		if (show) {
			frame.setVisible(true);
		}
		LinkedList <Component> result = Utils.getComponentList(((JPanel) (frame.getContentPane().getComponent(0))).getComponents());
		return result.toArray(new Component[result.size()]);
	}

	public static void main(String[] args) throws InvocationTargetException,
			InterruptedException {
		Component c[] = createAndShowGUI("GridBagLayout-Test10", new GridBagLayout(),
				new Dimension(500, 500), true);
		Utils.printComponents("GridBagLayout-Test10", c);
		c = createAndShowGUI("ALMGridBagLayout-Test10",
				new alm.compatibility.GridBagLayout(), new Dimension(500, 500), true);
		Utils.printComponents("ALMGridBagLayout-Test10", c);
		Utils.generateGBTest("Test10", c);
	}	

}