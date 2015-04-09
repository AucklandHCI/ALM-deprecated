package compatibility.GridBagLayout;

import commons.Utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class Test11App extends JPanel {

	
	/**
	 * constructor. 
	 */
	public Test11App(LM lm){
		initGUI(lm);
	}
	
		
	public void initGUI(LM lm) {

		setLayout(lm.agbl == null ? lm.gbl : lm.agbl);
		
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
		
		JTable table = new JTable(data, columnNames);
		table.setName("table");
		
		JScrollPane tableScrollPane = new JScrollPane(table);
		tableScrollPane.setName("tableScrollPane");
	//	tableScrollPane.setPreferredSize(new Dimension(200, 50));
		
		
		JLabel label = label("My Things");
		
		JPanel tableButtonPanel = new JPanel();
		tableButtonPanel.setName("tableButtonPanel");
		
		tableButtonPanel.add(button("Add Thing"));
		tableButtonPanel.add(button("Delete Thing"));
		tableButtonPanel.add(button("Modify Thing"));

		JPanel buttonPanel = new JPanel();
		buttonPanel.setName("buttonPanel");
		
		buttonPanel.add(button("Print"));
		buttonPanel.add(button("History"));
		buttonPanel.add(button("Preferences"));
		buttonPanel.add(button("Another Button"));
		buttonPanel.add(button("Add Another"));
		buttonPanel.add(button("Yet Another"));

		JPanel detailsPanel = new JPanel();
		detailsPanel.setName("detailsPanel");
		
		detailsPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		
		GridBagConstraints gbc = new GridBagConstraints();
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.WEST;
		add(label, gbc);
		

		gbc.anchor = GridBagConstraints.CENTER;
		gbc.gridx = 0;
		gbc.gridy = 1;		
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		add(tableScrollPane, gbc);

		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.weightx = 0;
		gbc.weighty = 0;
		add(tableButtonPanel, gbc);

		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 2;
		add(buttonPanel, gbc);

		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 2;
		gbc.anchor = GridBagConstraints.NORTH;
		
		add(createDetailsPanel(lm), gbc);
	}

	private JPanel createDetailsPanel(LM lm) {
		
		
		JPanel panel = new JPanel();
		panel.setName("panel");
		
		JLabel thingNameLabel = label("Thing Name");
		JLabel anAttributeLabel = label("An Attribute");
		JLabel dateFieldLabel = label("Date Field");
		JLabel anAttLabel = label("An Att");
		JLabel anotherAttLabel = label("Another Att");
		JLabel anotherAtt2Label = label("Another Att2");
		
		JTextField thingNameField = textField("a");
		JTextField anAttributeField = textField("b");
		JTextField dateFieldField = textField("c");
		JTextField anAttField = textField("d");
		
		JTextArea anotherAttField = new JTextArea(3, 1);		
		anotherAttField.setName("anotherAttField");
		
		JTextField anotherAtt2Field = textField("e", 10);
		
		anotherAtt2Field.setMinimumSize(anotherAtt2Field.getPreferredSize());
		

		JCheckBox checkbox1 = new JCheckBox("A Checkbox");
		checkbox1.setName("checkbox1");
		JCheckBox checkbox2 = new JCheckBox("A Checkbox");
		checkbox2.setName("checkbox2");

		
		panel.setLayout(lm.agbl==null ? new GridBagLayout() : new alm.compatibility.GridBagLayout());
	
		GridBagConstraints gbc = new GridBagConstraints();
		
		int i=0;
		
		gbc.insets = new Insets(2,2,2,2);
		gbc.anchor = GridBagConstraints.NORTHEAST;
		
		gbc.gridx = 0;
		gbc.gridy = i;
		panel.add(thingNameLabel,  gbc);
		
		gbc.gridx = 1;
		gbc.gridy = i;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;		
		panel.add(thingNameField,  gbc);		
		
		i++;
		
		gbc.gridx = 1;
		gbc.gridy = i;
		gbc.gridwidth = 2;
		panel.add(checkbox1,  gbc);

		i++;
				
		gbc.gridx = 0;
		gbc.gridy = i;		
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.NONE;
		panel.add(anAttributeLabel,  gbc);
				
		gbc.gridx = 1;
		gbc.gridy = i;		
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel.add(anAttributeField,  gbc);		
		
		i++;
		
		gbc.gridx = 0;
		gbc.gridy = i;
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.NONE;
		panel.add(dateFieldLabel,  gbc);

		gbc.gridx = 1;
		gbc.gridy = i;		
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel.add(dateFieldField,  gbc);		
		
		i++;
		
		gbc.gridx = 0;
		gbc.gridy = i;		
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.NONE;
		panel.add(anAttLabel,  gbc);
				
		gbc.gridx = 1;
		gbc.gridy = i;				
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel.add(anAttField,  gbc);		

		i++;
		
		gbc.gridx = 0;
		gbc.gridy = i;		
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.NONE;
		panel.add(anotherAttLabel,  gbc);
		
		gbc.gridx = 1;
		gbc.gridy = i;				
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		JScrollPane x = new JScrollPane(anotherAttField);
		x.setName("anotherAttField");
		panel.add(x,  gbc);

		i++;
		gbc.gridx = 0;
		gbc.gridy = i;		
		gbc.gridwidth = 1;
		gbc.weightx = 0.0;
		gbc.fill = GridBagConstraints.NONE;
		panel.add(anotherAtt2Label,  gbc);

		gbc.gridx = 1;
		gbc.gridy = i;				
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		panel.add(anotherAtt2Field,  gbc);
		

		gbc.gridx = 2;
		gbc.gridy = i;
		gbc.weightx = 0;
		gbc.fill = GridBagConstraints.NONE;
		panel.add(checkbox2,  gbc);
		
		return panel;
	}
	
	public JButton button(String name) {
		JButton b = new JButton(name);
		b.setName(name);
		return b;
	}

	public JLabel label(String name) {
		JLabel b = new JLabel(name);
		b.setName(name);
		return b;
	}

	public JTextField textField(String name) {
		JTextField b = new JTextField(name);
		b.setName(name);
		return b;
	}

	public JTextField textField(String name, int size) {
		JTextField b = new JTextField(name, size);
		b.setName(name);
		return b;
	}


	public static Component[] createAndShowGUI(String title, LayoutManager lm,
			Dimension d, boolean show) {
		// Create and set up the window.
		JFrame frame = new JFrame(title);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Add content to the window.
		if (lm instanceof GridBagLayout)
			frame.add(new Test11App(new LM((GridBagLayout) lm)));
		else
			frame.add(new Test11App(new LM((alm.compatibility.GridBagLayout) lm)));

		//frame.setPreferredSize(d);
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
		Component c[] = createAndShowGUI("GridBagLayout-Test11", new GridBagLayout(),
				new Dimension(800, 600), true);
		Utils.printComponents("GridBagLayout-Test11", c);
		c = createAndShowGUI("ALMGridBagLayout-Test11",
				new alm.compatibility.GridBagLayout(), new Dimension(800, 600), true);
		Utils.printComponents("ALMGridBagLayout-Test11", c);
		Utils.generateGBTest("Test11", c);
	}	

}