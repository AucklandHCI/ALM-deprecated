package alm.editor;

import alm.ALMLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * This class defines the ALM specific extension of JPanel.
 *
 * @author Brent Whiteley
 */
public class ALMPanel extends JPanel implements MouseListener, HierarchyListener {

    private static final long serialVersionUID = 1L;
    private JPopupMenu rightClickMenu;
    private JMenuItem editMode;
    private ALMLayout le;
    private boolean editing;
    private boolean firstRClick = true;
    private ArrayList<JComponent> rightClickList = new ArrayList<JComponent>();
    private Container jParent;

    /**
     * Initialises ALMPanel as a content panel.
     */
    public ALMPanel() {
        addMouseListener(this);
        addHierarchyListener(this);
        jParent = null;
    }

    /**
     * Initialises ALMPanel as a listener panel only.
     *
     * @param j The content panel to be covered.
     */
    public ALMPanel(Container j) {
        j.addMouseListener(this);
        j.addHierarchyListener(this);
        jParent = j;
    }

    /**
     * Creates the right click menu item
     */
    private void setRightClickMenu() {
        Component[] clist;
        if (jParent != null) {
            clist = jParent.getComponents();
        } else {
            clist = this.getComponents();
        }
        for (Component c : clist) {
            c.addMouseListener(this);
        }
        initializeRightClickMenuItems();
        if (rightClickList.size() > 0) {
            addToRightClickFromList();
        }
        this.editMode.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (!editing) {
                    switchMode();
                }
            }
        });
    }

    /**
     * Reloads the mouse listeners
     */
    public void reloadListeners() {
        Component[] clist;
        if (jParent != null) {
            clist = jParent.getComponents();
            jParent.addMouseListener(this);
        } else {
            clist = this.getComponents();
            this.addMouseListener(this);
        }
        for (Component c : clist) {
            c.addMouseListener(this);
        }
    }

    /**
     * Disables the mouse listeners.
     */
    public void disableMouseListeners() {
        Component[] clist;
        if (jParent != null) {
            clist = jParent.getComponents();
            jParent.removeMouseListener(this);
        } else {
            clist = this.getComponents();
            this.removeMouseListener(this);
        }
        for (Component c : clist) {
            c.removeMouseListener(this);
        }
    }

    /**
     * Resets boolean editing to indicate it is in normal mode
     */
    public void resetEditing() {
        editing = false;
    }

    /**
     * Method to show the right click menu at the right click location.
     */
    public void mouseClicked(MouseEvent arg0) {
        if (!editing && (arg0.getButton() == MouseEvent.BUTTON3)) {
            // if right click show menu
            try {
                if (rightClickMenu != null)
                    rightClickMenu.show(arg0.getComponent(), arg0.getX(), arg0.getY());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Switches the GUI into editing mode
     */
    private void switchMode() {
        try {
            Container c;
            if (jParent != null) {
                c = jParent;
            } else {
                c = this;
            }

            if (c.getLayout() instanceof alm.compatibility.GridBagLayout) {
                JOptionPane.showMessageDialog(this, "Edit mode is not currently supported for layouts defined using GridBag constraints.");
            } else {
                le.edit(c);
                rightClickMenu.setVisible(false);
                editing = true;
                disableMouseListeners();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Initialise Right Click Menu items
     */
    private void initializeRightClickMenuItems() {
        this.editMode = new JMenuItem();
        this.editMode.setName("Switch to edit mode");
        this.editMode.setSize(new Dimension(168, 22));
        this.editMode.setText("Switch to edit mode");
        this.rightClickMenu.add(editMode);
    }

    /**
     * Adds to the right click menu from the right click list.
     */
    private void addToRightClickFromList() {
        if (this.rightClickMenu.getComponentCount() == 1) {
            if (firstRClick) {
                this.rightClickMenu.addSeparator();
                firstRClick = false;
            }
        }
        for (JComponent i : rightClickList) {
            this.rightClickMenu.add(i);
        }
        rightClickList = new ArrayList<JComponent>();
    }

    /**
     * Removes the right click menu item.
     *
     * @param newItem The menu item to remove.
     */
    public void removeRightClickMenuItem(JMenuItem newItem) {
        this.rightClickMenu.remove((JComponent) newItem);
        if (this.rightClickMenu.getComponentCount() == 2) {
            firstRClick = true;
            this.rightClickMenu.removeAll();
            initializeRightClickMenuItems();
        }
    }

    public void mouseEntered(MouseEvent arg0) {
    }

    public void mouseExited(MouseEvent arg0) {
    }

    public void mousePressed(MouseEvent arg0) {
    }

    public void mouseReleased(MouseEvent arg0) {
    }

    /**
     * Once the panel is created create right click menu and retrieve the ALM layout.
     *
     * @param arg0 The HeirarchyEvent object.
     */
    public void hierarchyChanged(HierarchyEvent arg0) {
        if (rightClickMenu == null) {
            rightClickMenu = new JPopupMenu();
            rightClickMenu.setSize(new Dimension(61, 4));
            setRightClickMenu();
        }
        Container c;
        if (jParent != null) {
            c = jParent;
        } else {
            c = this;
        }
        if (this.le == null && c.getLayout() != null) {
            if (c.getLayout() instanceof ALMLayout) {
                this.le = (ALMLayout) c.getLayout();
            } else if (c.getLayout() instanceof alm.compatibility.GridBagLayout) {
                this.le = ((alm.compatibility.GridBagLayout) c.getLayout()).getAlmLayout();
            }
        }
    }


}
