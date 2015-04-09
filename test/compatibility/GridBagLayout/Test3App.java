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
 *     documentation and/or other materials provided with the distribution.
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

package compatibility.GridBagLayout;

/*
 * ContainerEventDemo.java requires no editor files.
 */

import commons.Utils;

import javax.swing.*;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.LayoutManager;

import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Vector;

@SuppressWarnings("serial")
public class Test3App extends JPanel 
                                implements ContainerListener,
                                           ActionListener {
    JTextArea display;
    JPanel buttonPanel;
    JButton addButton, removeButton, clearButton;
    JScrollPane scrollPane;
    Vector<JButton> buttonList;
    static final String ADD = "add";
    static final String REMOVE = "remove";
    static final String CLEAR = "clear";
    static final String newline = "\n";
    static final boolean ShouldWight = false;
    static final boolean ShouldFill = true;
    
    
    private void createComponents(LM lm) {
        //Initialize an empty list of buttons.
        buttonList = new Vector<JButton>(10, 10);

        //Create all the components.
        addButton = new JButton("Add a button");
        addButton.setActionCommand(ADD);
        addButton.addActionListener(this);
        addButton.setName("button-1");

        removeButton = new JButton("Remove a button");
        removeButton.setActionCommand(REMOVE);
        removeButton.addActionListener(this);
        removeButton.setName("button-2");

        //buttonPanel = new JPanel(new GridLayout(1,1));
        if (lm.agbl == null) {
        	buttonPanel = new JPanel(new GridBagLayout());
        } else {
        	buttonPanel = new JPanel(new alm.compatibility.GridBagLayout());
        }
        buttonPanel.setPreferredSize(new Dimension(300, 75));
        buttonPanel.addContainerListener(this);
        buttonPanel.setName("panel-1");

        display = new JTextArea();
        display.setName("textarea-1");
        display.setEditable(false);
        scrollPane = new JScrollPane(display);
        scrollPane.setPreferredSize(new Dimension(200, 75));
        scrollPane.setName("scrollpane-1");

        clearButton = new JButton("Clear text area");
        clearButton.setActionCommand(CLEAR);
        clearButton.addActionListener(this);
        clearButton.setName("button-3");    	
    }
    
    private void setConstraints(LM lm) {
        GridBagConstraints c = new GridBagConstraints();
      
        if (ShouldFill) {
        	c.fill = GridBagConstraints.BOTH; //Fill entire cell.
        }
        if (Test3App.ShouldWight) {
        	c.weighty = 1.0;  //Button area and message area have equal height.
        }
        c.gridwidth = GridBagConstraints.REMAINDER; //end of row
        lm.setConstraints(scrollPane, c);
        add(scrollPane);
  
        if (Test3App.ShouldWight) {
        	c.weighty = 0.0;
        }       
        lm.setConstraints(clearButton, c);
        add(clearButton);
        
        if (Test3App.ShouldWight) {
        	c.weightx = 1.0;  //Add/remove buttons have equal width.
        }
        c.gridwidth = 1;  //NOT end of row
        c.anchor = GridBagConstraints.WEST;
        lm.setConstraints(addButton, c);
        add(addButton);

        c.gridwidth = GridBagConstraints.REMAINDER; //end of row
        lm.setConstraints(removeButton, c);
        add(removeButton);

        if (Test3App.ShouldWight) {
        	c.weighty = 1.0;  //Button area and message area have equal height.
        }
        lm.setConstraints(buttonPanel, c);
        add(buttonPanel);
        
        setPreferredSize(new Dimension(400, 400));
        setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
    }

    public Test3App(LM lman, alm.compatibility.GridBagLayout lm) {
   		super(lm);
     		
        createComponents(lman);
        setConstraints(lman);
    }
    
    public Test3App(LM lman, GridBagLayout lm) {
        super(lm);
        
        createComponents(lman);
        setConstraints(lman);
    }


    public void componentAdded(ContainerEvent e) {
        displayMessage(" added to ", e);
    }

    public void componentRemoved(ContainerEvent e) {
        displayMessage(" removed from ", e);
    }

    void displayMessage(String action, ContainerEvent e) {
        display.append(((JButton)e.getChild()).getText()
                       + " was"
                       + action
                       + e.getContainer().getClass().getName()
                       + newline);
        display.setCaretPosition(display.getDocument().getLength());
    }

    /*
     * This could have been implemented as two or three
     * classes or objects, for clarity.
     */
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if (ADD.equals(command)) {
            JButton newButton = new JButton("JButton #"
                                          + (buttonList.size() + 1));
            buttonList.addElement(newButton);
            GridBagConstraints c = new GridBagConstraints();
            buttonPanel.add(newButton, c);
            buttonPanel.revalidate(); //Make the button show up.

        } else if (REMOVE.equals(command)) {
            int lastIndex = buttonList.size() - 1;
            try {
                JButton nixedButton = buttonList.elementAt(lastIndex);
                buttonPanel.remove(nixedButton);
                buttonList.removeElementAt(lastIndex);
                buttonPanel.revalidate(); //Make the button disappear.
                buttonPanel.repaint(); //Make the button disappear.
            } catch (ArrayIndexOutOfBoundsException exc) {}
        } else if (CLEAR.equals(command)) {
            display.setText("");
        }
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    public static Component[] createAndShowGUI(String title, LayoutManager lm, Dimension d, boolean show) {
        //Create and set up the window.
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        JComponent newContentPane;
        if (lm instanceof GridBagLayout)
        	newContentPane = new Test3App(new LM((GridBagLayout)lm), (GridBagLayout)lm);
        else
        	newContentPane = new Test3App(new LM((alm.compatibility.GridBagLayout)lm),(alm.compatibility.GridBagLayout)lm);
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        frame.setPreferredSize(d);

        //Display the window.
        frame.pack();
        if (show) {
        	frame.setVisible(true);
        }
        
        return frame.getContentPane().getComponents();
    }

    public static void main(String[] args) {
        Component c[] = createAndShowGUI("GridBagLayout-Test3", new GridBagLayout(), new Dimension(500, 500), true);
        Utils.printComponents("GridBagLayout-Test3", c);
        c = createAndShowGUI("ALMGridBagLayout-Test3", new alm.compatibility.GridBagLayout(), new Dimension(500, 500), true);
        Utils.printComponents("ALMGridBagLayout-Test3", c);
		Utils.generateGBTest("Test3", c);
    }    
}