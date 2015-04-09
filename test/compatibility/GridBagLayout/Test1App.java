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

public class Test1App {
	boolean shouldFill = true;
	boolean shouldWeightX = false;
	boolean shouldWeightY = false;
	final static boolean RIGHT_TO_LEFT = false;

	public void addComponentsToPane(Container pane, LayoutManager lm) {
		if (RIGHT_TO_LEFT) {
			pane.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		}

		JButton button;
		pane.setLayout(lm);
		GridBagConstraints c = new GridBagConstraints();
		if (shouldFill) {
			// natural height, maximum width
			c.fill = GridBagConstraints.BOTH;
		}		
		if (shouldWeightX) {
			c.weightx = 0.25;
		}
		if (shouldWeightY) {
			c.weighty = 0.25;
		}

		button = new JButton("Button 1");
		button.setName("button-1");	

		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		pane.add(button, c);

		button = new JButton("Button 2");
		button.setName("button-2");
		

		if (shouldWeightX) {
			c.weightx = 0.0;
		}

		c.gridx = 1;
		c.gridy = 0;
		pane.add(button, c);
		button = new JButton("Button 3");
		button.setName("button-3");
		
		

		if (shouldWeightX) {
			c.weightx = 0.75;
		}
		c.gridx = 2;
		c.gridy = 0;
		pane.add(button, c);

		button = new JButton("Long-Named Button 4");
		button.setName("button-4");
		c.ipady = 40; // make this component tall
		
		if (shouldWeightX) {
			c.weightx = 0.0;
		}
		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = 1;
		pane.add(button, c);

		button = new JButton("5");
		button.setName("button-5");
		c.ipady = 0; // reset to default

		if (shouldWeightY) {
			c.weighty = 0.75; // request any extra vertical space		
		}

		c.anchor = GridBagConstraints.PAGE_END; // bottom of space
		c.insets = new Insets(10, 0, 0, 0); // top padding
		c.gridx = 1; // aligned with button 2
		c.gridwidth = 2; // 2 columns wide
		c.gridy = 2; // third row
		pane.add(button, c);
	}
	
	public void setOptions(boolean shouldWeightx, boolean shouldWeighty, boolean shouldFill) {
		this.shouldWeightX = shouldWeightx;
		this.shouldWeightY = shouldWeighty;
		this.shouldFill = shouldFill;
	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event-dispatching thread.
	 * 
	 * @throws InterruptedException
	 */

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
		Test1App app = new Test1App();
		Component c[] = app.createAndShowGUI("GridBagLayout-Test1", new GridBagLayout(),
				new Dimension(500, 500), true);
		Utils.printComponents("GridBagLayout-Test1", c);
		c = app.createAndShowGUI("ALMGridBagLayout-Test1",
				new alm.compatibility.GridBagLayout(), new Dimension(500, 500), true);
		Utils.printComponents("ALMGridBagLayout-Test1", c);
		Utils.generateGBTest("Test1", c);
	}	
}