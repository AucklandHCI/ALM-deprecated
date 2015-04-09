package compatibility.GridBagLayout;

import commons.Utils;

import javax.swing.*;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;

public class Test9App extends JPanel
{
    public void addComponentsToPane(Container  pane, LayoutManager lm)
    {
        GridBagConstraints constraints = new GridBagConstraints();
        //JPanel panel = new JPanel(); 
        pane.setLayout(lm);

        constraints.gridx       = 0;
        constraints.gridy       = 0;
        constraints.gridwidth   = 2;  
        constraints.gridheight  = 1;
        constraints.weightx     = 1.0;
        constraints.weighty     = 1.0;
        constraints.anchor      = GridBagConstraints.CENTER;
        constraints.fill        = GridBagConstraints.BOTH;
        constraints.insets      = new Insets(5, 5, 0, 0);
        constraints.ipadx       = 10;  
        constraints.ipady       = 10;
        addButton("(0,0)", constraints, pane);


        // the remaining constraints use the constructor
        constraints = new GridBagConstraints(3, 0, 1, 1, 1.0, 1.0,
                                            GridBagConstraints.NORTHEAST,
                                            GridBagConstraints.NONE,
                                            new Insets(10, 10, 10, 10), 0, 0);
        addButton("(3,0)", constraints, pane);

        constraints = new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,
                                            GridBagConstraints.CENTER,
                                            GridBagConstraints.NONE,
                                           new Insets(0, 0, 0, 0), 0, 0);
        addButton("(0,1)", constraints, pane);

        constraints = new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0,
                                            GridBagConstraints.CENTER,
                                            GridBagConstraints.NONE,
                                            new Insets(0, 0, 0, 0), 0, 0);
        addButton("(1,1)", constraints, pane);


        constraints = new GridBagConstraints(2, 1, 1, 1, 1.0, 1.0,
                                            GridBagConstraints.CENTER,
                                            GridBagConstraints.BOTH,
                                            new Insets(0, 0, 0, 0), 30, 0);
        addButton("(2,1)", constraints, pane);

        constraints = new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0,
                                            GridBagConstraints.SOUTH,
                                            GridBagConstraints.NONE,
                                            new Insets(0, 0, 0, 0), 0, 0);
        addButton("(0,2)", constraints, pane);

        constraints = new GridBagConstraints(0, 3, 1, 1, 1.0, 1.0,
                                            GridBagConstraints.SOUTH,
                                            GridBagConstraints.NONE,
                                            new Insets(0, 0, 0, 0), 0, 0);
        addButton("(0,3)", constraints, pane);
                                        
        constraints = new GridBagConstraints(1, 2, 3, 2, 1.0, 1.0,
                                            GridBagConstraints.SOUTHEAST,
                                            GridBagConstraints.BOTH,
                                            new Insets(5, 7, 1, 8), 0, 0);
        addButton("(1,2)", constraints, pane);
    }
    
	void addButton(String label, GridBagConstraints constraints,
			Container pane) {
		JButton button = new JButton(label);
		button.setName(label);
		pane.add(button, constraints);
	}

    
	public Component[] createAndShowGUI(String title, LayoutManager lm,
			Dimension d, boolean show) {
		// Create and set up the window.
		JFrame frame = new JFrame(title);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		addComponentsToPane(frame.getContentPane(), lm);
		frame.setPreferredSize(d);
		// Display the window.
		frame.pack();
		if (show) {
			frame.setVisible(true);
		}
		//LinkedList <Component> result = commons.utils.getComponentList(((JPanel) (frame.getContentPane().getComponent(0))).getComponents());
		return frame.getContentPane().getComponents();
	}

	public static void main(String[] args) throws InvocationTargetException,
			InterruptedException {
		Test9App app = new Test9App();
		Component c[] = app.createAndShowGUI("GridBagLayout-Test9", new GridBagLayout(),
				new Dimension(500, 500), true);
		Utils.printComponents("GridBagLayout-Test9", c);
		c = app.createAndShowGUI("ALMGridBagLayout-Test9",
				new alm.compatibility.GridBagLayout(), new Dimension(500, 500), true);
		Utils.printComponents("ALMGridBagLayout-Test9", c);
		Utils.generateGBTest("Test9", c);
	}	
}