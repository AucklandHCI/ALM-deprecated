package compatibility.GridBagLayout;

/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or editor materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/*
 * GridBagLayoutDemo.java requires no editor files.
 */

import commons.Utils;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JFrame;

public class JavaIssue2 {
	boolean shouldFill = true;
	boolean shouldWeight = true;
	final static boolean RIGHT_TO_LEFT = false;
	
    protected static JButton makebutton(String name) {
    	JButton button = new JButton(name);
    	button.setName(name);
    	return button;
    }

	
	public void addComponentsToPane(Container pane, LayoutManager lm) {
		if (RIGHT_TO_LEFT) {
			pane.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		}
		GridBagConstraints c = new GridBagConstraints();
		
		pane.setLayout(lm);


		if (shouldFill) {
			c.fill = GridBagConstraints.BOTH;
		}
		if(shouldWeight) {
			c.weightx = 1.0;
			c.weighty = 0.5;
		}
		
        c.gridwidth = 2; 
        pane.add(makebutton("button-1"), c);
        
        c.gridwidth = GridBagConstraints.RELATIVE;
        pane.add(makebutton("button-2"),c);

        c.gridwidth = GridBagConstraints.REMAINDER;
        pane.add(makebutton("button-3"),c);

        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        pane.add(makebutton("button-4"),c);
        
        c.gridx = 1;
        c.gridy = 1;
        c.gridwidth = GridBagConstraints.RELATIVE;
        pane.add(makebutton("button-5"),c);
        
        c.gridx = 2;
        c.gridy = 1;
        c.gridwidth = GridBagConstraints.REMAINDER;
        pane.add(makebutton("button-6"),c);        

	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event-dispatching thread.
	 * 
	 * @throws InterruptedException
	 */

	public void setOptions(boolean shouldWeight, boolean shouldFill) {
		this.shouldWeight = shouldWeight;
		this.shouldFill = shouldFill;
	}

	public Component[] createAndShowGUI(String title, LayoutManager lm,
			Dimension d, boolean show) {
		// Create and set up the window.
		JFrame frame = new JFrame(title);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// Set up the content pane.
		addComponentsToPane(frame.getContentPane(), lm);

		frame.setPreferredSize(d);
		frame.pack();
		if (show) {
			frame.setVisible(true);
		}
		return frame.getContentPane().getComponents();
	}

	public static void main(String[] args) throws InvocationTargetException,
			InterruptedException {
		JavaIssue2 app = new JavaIssue2();
		Component c[] = app.createAndShowGUI("GridBagLayout-Test5", new GridBagLayout(),
				new Dimension(500, 500), true);
		Utils.printComponents("GridBagLayout-Test5", c);
		c = app.createAndShowGUI("ALMGridBagLayout-Test5",
				new alm.compatibility.GridBagLayout(), new Dimension(500, 500), true);
		Utils.printComponents("ALMGridBagLayout-Test5", c);
		Utils.generateGBTest("Test5", c);
	}	
}