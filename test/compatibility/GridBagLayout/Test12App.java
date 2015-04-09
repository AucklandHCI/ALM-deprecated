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

public class Test12App {
	final static boolean shouldFill = true;
	final static boolean shouldWeight = false;
	final static boolean RIGHT_TO_LEFT = false;

	public static JButton makeButton(String label) {
		JButton b = new JButton(label);
		b.setName(label);
		return b;
	}
	public static void addComponentsToPane(Container pane, LM lm) {
		if (RIGHT_TO_LEFT) {
			pane.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		}

		pane.setLayout(lm.agbl == null ? lm.gbl : lm.agbl);

	    GridBagConstraints gbc = new GridBagConstraints();
	    
	    JButton component1 = makeButton("1");
	    JButton component2 = makeButton("5");

	    gbc.insets = new Insets(40, 40, 3, 50);
	    lm.setConstraints(component2, gbc);

	    pane.add(makeButton("2"));
	    pane.add(makeButton("4"));
	    pane.add(component2);
	    pane.add(makeButton("6"));
	    pane.add(component1);
	    pane.add(makeButton("7"));
	    pane.add(makeButton("8"));
	    
	    gbc.insets = new Insets(20, 20, 2, 40);
	    gbc.fill = GridBagConstraints.HORIZONTAL;
	    gbc.weightx = 1;

	    lm.setConstraints(component1, gbc);
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
		if (lm instanceof GridBagLayout)			
			addComponentsToPane(frame.getContentPane(), new LM((GridBagLayout)lm));
		else
			addComponentsToPane(frame.getContentPane(), new LM((alm.compatibility.GridBagLayout)lm));
		
		frame.setPreferredSize(d);
		frame.pack();
		if (show) {
			frame.setVisible(true);
		}
		return frame.getContentPane().getComponents();
	}

	public static void main(String[] args) throws InvocationTargetException,
			InterruptedException {
		Component c[] = createAndShowGUI("GridBagLayout-Test12", new GridBagLayout(),
				new Dimension(500, 500), true);
		Utils.printComponents("GridBagLayout-Test12", c);
		c = createAndShowGUI("ALMGridBagLayout-Test12",
				new alm.compatibility.GridBagLayout(), new Dimension(500, 500), true);
		Utils.printComponents("ALMGridBagLayout-Test12", c);
		Utils.generateGBTest("Test12", c);
	}	
}