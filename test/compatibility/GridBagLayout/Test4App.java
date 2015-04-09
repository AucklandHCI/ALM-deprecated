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

public class Test4App {
	final static boolean shouldFill = true;
	final static boolean shouldWeight = false;
	final static boolean RIGHT_TO_LEFT = false;

	public static void addComponentsToPane(Container pane, LayoutManager lm) {
		if (RIGHT_TO_LEFT) {
			pane.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		}

		pane.setLayout(lm);

		JButton button1 = new JButton();
		JButton button2 = new JButton();
		JButton button3 = new JButton();
		JButton button4 = new JButton();
		
		GridBagConstraints c = new GridBagConstraints();
		// 
		// button1
		// 
		button1.setName("button-1");
		button1.setText("button1");
		// 
		// button2
		// 
		button2.setName("button-2");
		button2.setText("button2");
		// 
		// button3
		// 
		button3.setName("button-3");
		button3.setText("button3");

		button4.setName("button-4");
		button4.setText("button4");
		if (shouldFill) {
			c.fill = GridBagConstraints.BOTH;
		}
		
		if (shouldWeight) {
			c.weightx = 0.25;
			c.weighty = 1;
		}
		
		c.gridx = GridBagConstraints.RELATIVE;
		pane.add(button1, c);
		c.gridwidth = GridBagConstraints.REMAINDER;
		pane.add(button2, c);
		c.gridwidth = 1;
		pane.add(button3, c);
		pane.add(button4, c);
	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event-dispatching thread.
	 * 
	 * @throws InterruptedException
	 */

	public static Component[] createAndShowGUI(String title, LayoutManager lm,
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
		Component c[] = createAndShowGUI("GridBagLayout-Test4", new GridBagLayout(),
				new Dimension(500, 500), true);
		Utils.printComponents("GridBagLayout-Test4", c);
		c = createAndShowGUI("ALMGridBagLayout-Test4",
				new alm.compatibility.GridBagLayout(), new Dimension(500, 500), true);
		Utils.printComponents("ALMGridBagLayout-Test4", c);
		Utils.generateGBTest("Test4", c);
	}	
}