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
 * TextSamplerDemo.java requires the following files:
 *   TextSamplerDemoHelp.html (which references images/dukeWaveRed.gif)
 *   images/Pig.gif
 *   images/sound.gif
 */

import commons.Utils;

import javax.swing.*;
import javax.swing.text.*;

import java.awt.*; //for layout managers and more
import java.awt.event.*; //for action events

import java.util.LinkedList;
import java.io.IOException;

public class Test2App extends JPanel implements ActionListener {
	private String newline = "\n";
	protected static final String textFieldString = "JTextField";
	protected static final String passwordFieldString = "JPasswordField";
	protected static final String ftfString = "JFormattedTextField";
	protected static final String tempString1 = "TempTextField1";
	protected static final String buttonString = "JButton";

	protected JLabel actionLabel;
	final static boolean shouldWeight = false;

	public Test2App(LM lman) {
		setLayout(new BorderLayout());

		// Create a regular text field.
		JTextField textField = new JTextField(10);
		textField.setActionCommand(textFieldString);
		textField.addActionListener(this);
		textField.setName("textfield-1");

		// Create a password field.
		JPasswordField passwordField = new JPasswordField(10);
		passwordField.setActionCommand(passwordFieldString);
		passwordField.addActionListener(this);
		passwordField.setName("textfield-2");		

		// Create a formatted text field.
		JFormattedTextField ftf = new JFormattedTextField(java.util.Calendar
				.getInstance().getTime());
		ftf.setActionCommand(textFieldString);
		ftf.addActionListener(this);
		ftf.setName("formatted-text-field-1");

		JTextField tempText1 = new JTextField(10);
		tempText1.setName("tempText1");
		
		
		// Create some labels for the fields.
		JLabel textFieldLabel = new JLabel(textFieldString + ": ");
		textFieldLabel.setName("textfield-3");
		textFieldLabel.setLabelFor(textField);
		JLabel passwordFieldLabel = new JLabel(passwordFieldString + ": ");
		passwordFieldLabel.setName("textfield-4");
		passwordFieldLabel.setLabelFor(passwordField);
		JLabel ftfLabel = new JLabel(ftfString + ": ");
		ftfLabel.setName("textfield-5");
		ftfLabel.setLabelFor(ftf);
		JLabel tempLabel1 = new JLabel(tempString1 + ": ");
		tempLabel1.setName("textfield-6");
		tempLabel1.setLabelFor(tempText1);

		// Create a label to put messages during an action event.
		actionLabel = new JLabel("Type text in a field and press Enter.");
		actionLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		actionLabel.setName("actionlabel-1");

		// Lay out the text controls and the labels.
		JPanel textControlsPane = new JPanel();
		textControlsPane.setName("controlpanel-1");
		GridBagConstraints c = new GridBagConstraints();

		if (lman.gbl == null)
			textControlsPane.setLayout(lman.agbl);
		else
			textControlsPane.setLayout(lman.gbl);

		JLabel[] labels = { textFieldLabel, passwordFieldLabel, ftfLabel, tempLabel1 };
		JTextField[] textFields = { textField, passwordField, ftf, tempText1 };
		addLabelTextRows(labels, textFields, textControlsPane);

		c.gridwidth = GridBagConstraints.REMAINDER; // last
		c.anchor = GridBagConstraints.WEST;
		if (shouldWeight) {
			c.weightx = 1.0;
		}
		textControlsPane.add(actionLabel, c);
		textControlsPane.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Text Fields"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		textControlsPane.setPreferredSize(new Dimension(250, 145));


		// Create a text area.
		JTextArea textArea = new JTextArea("This is an editable JTextArea. "
				+ "A text area is a \"plain\" text component, "
				+ "which means that although it can display text "
				+ "in any font, all of the text is in the same font.");
		textArea.setName("textarea-1");
		textArea.setFont(new Font("Serif", Font.ITALIC, 16));
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		JScrollPane areaScrollPane = new JScrollPane(textArea);
		areaScrollPane.setName("scrollpane-1");
		areaScrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		areaScrollPane.setPreferredSize(new Dimension(250, 250));
		areaScrollPane.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createCompoundBorder(
						BorderFactory.createTitledBorder("Plain Text"),
						BorderFactory.createEmptyBorder(5, 5, 5, 5)),
				areaScrollPane.getBorder()));		

		// Create an editor pane.
		JEditorPane editorPane = createEditorPane();
		editorPane.setName("editorpane-1");
		JScrollPane editorScrollPane = new JScrollPane(editorPane);
		editorScrollPane.setName("scrollpane-2");
		editorScrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		editorScrollPane.setPreferredSize(new Dimension(250, 145));
		editorScrollPane.setMinimumSize(new Dimension(10, 10));

		// Create a text pane.
		JTextPane textPane = createTextPane();
		textPane.setName("textpane-1");
		JScrollPane paneScrollPane = new JScrollPane(textPane);
		paneScrollPane.setName("scrollpane-3");
		paneScrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		paneScrollPane.setPreferredSize(new Dimension(250, 155));
		paneScrollPane.setMinimumSize(new Dimension(10, 10));

		// Put the editor pane and the text pane in a split pane.
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				editorScrollPane, paneScrollPane);
		splitPane.setName("splitpane-1");
		splitPane.setOneTouchExpandable(true);
		splitPane.setResizeWeight(0.5);
		JPanel rightPane = new JPanel(new GridLayout(1, 0));
		rightPane.add(splitPane);
		rightPane.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Styled Text"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		rightPane.setName("panel-1");
		// Put everything together.
		JPanel leftPane = new JPanel(new BorderLayout());
		leftPane.setName("panel-2");
		leftPane.add(textControlsPane, BorderLayout.PAGE_START);
		leftPane.add(areaScrollPane, BorderLayout.CENTER);

		add(leftPane, BorderLayout.LINE_START);
		add(rightPane, BorderLayout.LINE_END);
	}


	private void addLabelTextRows(JLabel[] labels, JTextField[] textFields,
			Container container) {
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.EAST;
		int numLabels = labels.length;

		for (int i = 0; i < numLabels; i++) {
			c.gridwidth = GridBagConstraints.RELATIVE; // next-to-last
			c.fill = GridBagConstraints.NONE; // reset to default
			if (shouldWeight) {
				c.weightx = 0.0; // reset to default
			}
			container.add(labels[i], c);

			c.gridwidth = GridBagConstraints.REMAINDER; // end row
			c.fill = GridBagConstraints.HORIZONTAL;
			if (shouldWeight) {
				c.weightx = 1.0;
			}
			container.add(textFields[i], c);
		}
	}

	public void actionPerformed(ActionEvent e) {
		String prefix = "You typed \"";
		if (textFieldString.equals(e.getActionCommand())) {
			JTextField source = (JTextField) e.getSource();
			actionLabel.setText(prefix + source.getText() + "\"");
		} else if (passwordFieldString.equals(e.getActionCommand())) {
			JPasswordField source = (JPasswordField) e.getSource();
			actionLabel.setText(prefix + new String(source.getPassword())
					+ "\"");
		} else if (buttonString.equals(e.getActionCommand())) {
			Toolkit.getDefaultToolkit().beep();
		}
	}

	private JEditorPane createEditorPane() {
		JEditorPane editorPane = new JEditorPane();
		editorPane.setName("editorpane-3");
		editorPane.setEditable(false);
		java.net.URL helpURL = Test2App.class
				.getResource("images/TextSamplerDemoHelp.html");
		if (helpURL != null) {
			try {
				editorPane.setPage(helpURL);
			} catch (IOException e) {
				System.err.println("Attempted to read a bad URL: " + helpURL);
			}
		} else {
			System.err.println("Couldn't find file: TextSampleDemoHelp.html");
		}

		return editorPane;
	}

	private JTextPane createTextPane() {
		String[] initString = {
				"This is an editable JTextPane, ", // regular
				"another ", // italic
				"styled ", // bold
				"text ", // small
				"component, ", // large
				"which supports embedded components..." + newline,// regular
				" " + newline, // button
				"...and embedded icons..." + newline, // regular
				" ", // icon
				newline
						+ "JTextPane is a subclass of JEditorPane that "
						+ "uses a StyledEditorKit and StyledDocument, and provides "
						+ "cover methods for interacting with those objects." };

		String[] initStyles = { "regular", "italic", "bold", "small", "large",
				"regular", "button", "regular", "icon", "regular" };

		JTextPane textPane = new JTextPane();
		StyledDocument doc = textPane.getStyledDocument();
		addStylesToDocument(doc);

		try {
			for (int i = 0; i < initString.length; i++) {
				doc.insertString(doc.getLength(), initString[i],
						doc.getStyle(initStyles[i]));
			}
		} catch (BadLocationException ble) {
			System.err.println("Couldn't insert initial text into text pane.");
		}

		return textPane;
	}

	protected void addStylesToDocument(StyledDocument doc) {
		// Initialize some styles.
		Style def = StyleContext.getDefaultStyleContext().getStyle(
				StyleContext.DEFAULT_STYLE);

		Style regular = doc.addStyle("regular", def);
		StyleConstants.setFontFamily(def, "SansSerif");

		Style s = doc.addStyle("italic", regular);
		StyleConstants.setItalic(s, true);

		s = doc.addStyle("bold", regular);
		StyleConstants.setBold(s, true);

		s = doc.addStyle("small", regular);
		StyleConstants.setFontSize(s, 10);

		s = doc.addStyle("large", regular);
		StyleConstants.setFontSize(s, 16);

		s = doc.addStyle("icon", regular);
		StyleConstants.setAlignment(s, StyleConstants.ALIGN_CENTER);
		ImageIcon pigIcon = createImageIcon("images/Pig.gif", "a cute pig");
		if (pigIcon != null) {
			StyleConstants.setIcon(s, pigIcon);
		}

		s = doc.addStyle("button", regular);
		StyleConstants.setAlignment(s, StyleConstants.ALIGN_CENTER);
		ImageIcon soundIcon = createImageIcon("images/sound.gif", "sound icon");
		JButton button = new JButton();
		if (soundIcon != null) {
			button.setIcon(soundIcon);
		} else {
			button.setText("BEEP");
		}
		button.setCursor(Cursor.getDefaultCursor());
		button.setMargin(new Insets(0, 0, 0, 0));
		button.setActionCommand(buttonString);
		button.addActionListener(this);
		StyleConstants.setComponent(s, button);
	}

	/** Returns an ImageIcon, or null if the path was invalid. */
	protected static ImageIcon createImageIcon(String path, String description) {
		java.net.URL imgURL = Test2App.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL, description);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event dispatch thread.
	 */
	public static Component[] createAndShowGUI(String title, LayoutManager lm,
			Dimension d, boolean show) {
		// Create and set up the window.
		JFrame frame = new JFrame(title);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Add content to the window.
		if (lm instanceof GridBagLayout)
			frame.add(new Test2App(new LM((GridBagLayout) lm)));
		else
			frame.add(new Test2App(new LM((alm.compatibility.GridBagLayout) lm)));

		frame.setPreferredSize(d);
		// Display the window.
		frame.pack();
		if (show) {
			frame.setVisible(true);
		}
		LinkedList <Component> result = Utils.getComponentList(((JPanel) (frame.getContentPane().getComponent(0))).getComponents());
		return result.toArray(new Component[result.size()]);
	}

	public static void main(String[] args) {
		// Schedule a job for the event dispatching thread:
		// creating and showing this application's GUI.
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// Turn off metal's use of bold fonts
				UIManager.put("swing.boldMetal", Boolean.FALSE);
				Component c[] = createAndShowGUI("GridBagLayout-Test2", new GridBagLayout(),
						new Dimension(500, 500), true);
				Utils.printComponents("GridBagLayout-Test2", c);
				c = createAndShowGUI("ALMGridBagLayout-Test2",
						new alm.compatibility.GridBagLayout(), new Dimension(
								500, 500), true);
				Utils.printComponents("ALMGridBagLayout-Test2", c);
				Utils.generateGBTest("Test2", c);
			}
		});
	}	
}