package alm;

import alm.editor.ALMPanel;
import alm.editor.PropertiesWindow;
import jaxb.ALMSchema;
import jaxb.ProcessXML;
import linsolve.*;

import javax.swing.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;


/**
 * A GUI layout engine using the ALM.
 *
 * @author Christof
 * @author Ted
 * @author Sarah
 */
public class ALMLayout implements LayoutManager {
// TODO may simplify code to inherit from LinearSpec

    public static String DEFAULT_PDSTORE_NAME = "AVM";
    public static String DEFAULT_AVM_SPEC_NAME = "My AVM Spec";
    private String devAVMName;

    private ALMPanel listener;
    public Container parent;

    private boolean disableEditor = false;
    private boolean recoverlayout = false;
    private boolean activated = true;

    /** The specification used for calculating the layout. */
    private LayoutSpec layoutSpec;

    /**
     * A list of all the components added to the ALM. Will expand when addArea
     * is called and be reduced when layoutComponent is called.
     */
    private List<Component> componentsToAdd;

    /**
     * Temporary list to store invisible controls when propertiesWindow does not
     * currently exist.
     */
    private List<Area> tempEditFormBin;

    /**
     * The manner in which the GUI is dynamically adjusted. The default is to
     * fit the child controls into their parent.
     */
    private LayoutStyleType LayoutStyle = LayoutStyleType.FIT_TO_SIZE;

    /** Creates a new layout engine with an empty specification. */
    public ALMLayout() {
        super();
        initLayout();
    }

    /** Creates a new layout engine with a specific solver. */
    public ALMLayout(AbstractLinearSolver solver) {
        super();
        initLayout(solver);
    }

    /**
     * Creates a new layout engine while defining the editor state.
     *
     * @param disableEditor set whether the editor shall be disabled
     */
    public ALMLayout(boolean disableEditor) {
        this.disableEditor = disableEditor;
        initLayout();
    }

    /**
     * Creates a new layout engine with the given specification.
     *
     * @param ls the layout specification
     */
    public ALMLayout(LayoutSpec ls) {
        this();
        layoutSpec = ls;
    }

    /**
     * Creates a new layout engine to load a file.
     *
     * @param file a reference to the file to load
     */
    public ALMLayout(File file) {
        this();
        load(file);
    }

    /**
     * Creates a new layout engine from a file from a filename.
     *
     * @param filename the filename of given file
     */
    public ALMLayout(String filename) {
        this();
        load(filename);
    }

    /**
     * Initializes the layout.
     */
    private void initLayout() {
        layoutSpec = new LayoutSpec();
        componentsToAdd = new ArrayList<Component>();
        savedControls = new ArrayList<JComponent>();
        tempEditFormBin = new ArrayList<Area>();
    }

    /**
     * Initializes the layout with a specific solver
     */
    private void initLayout(AbstractLinearSolver solver) {
        layoutSpec = new LayoutSpec(solver);
        componentsToAdd = new ArrayList<Component>();
        savedControls = new ArrayList<JComponent>();
        tempEditFormBin = new ArrayList<Area>();
    }

    /**
     * Return the status of DisableEditor
     *
     * @return boolean of the value of DisableEditor
     */
    public boolean isDisableEditor() {
        return disableEditor;
    }


    /**
     * Set the status of DisableEditor
     *
     * @param disableEditor Desired value of DisableEditor
     */
    public void setDisableEditor(boolean disableEditor) {
        this.disableEditor = disableEditor;
    }

    /**
     * Calculate and set the layout. If no layout specification is given, a
     * specification is reverse engineered automatically.
     *
     * @param parent the parent control of the controls in the layout
     */
    public void layout(Container parent) {
        // make sure that layout events occurring during layout are ignored
        // i.e. activated is set to false during layout calculation
        if (!activated)
            return;
        activated = false;

        // reverse engineer a layout specification if requested
        if (recoverlayout) {
            recoverLayout(parent);
            recoverlayout = false;
        }

        //Change the LayoutStyle flag to ADJUST_SIZE if the width or height of the container is 0
        if (parent.getBounds().getWidth() == 0 || parent.getBounds().getHeight() == 0) {
            LayoutStyle = LayoutStyleType.ADJUST_SIZE;
        }

        //Ensure that the inset constraints are set. (Do not allow ALMEditor to reset the contraints.)
//	TODO what is this? shouldn't be necessary
// if(!(parent instanceof ALMEditorCanvas)){
//			layoutSpec.setInsetConstraints(parent.getInsets());
//		}

        // if the layout engine is set to fit the GUI to the given size,
        // then the given size is enforced by setting absolute positions for
        // Right and Bottom
        // (Default)
        if (LayoutStyle == LayoutStyleType.FIT_TO_SIZE) {
            layoutSpec.setRight(parent.getBounds().getWidth());
            layoutSpec.setBottom(parent.getBounds().getHeight());
        }

        layoutSpec.solve();

        // change the size of the GUI according to the calculated size
        // if the layout engine was configured to do so
        if (LayoutStyle == LayoutStyleType.ADJUST_SIZE) {
            parent.setSize(preferredLayoutSize(parent));
            LayoutStyle = LayoutStyleType.FIT_TO_SIZE;
        }
        // set the calculated positions and sizes for every area
        for (Area a : layoutSpec.getAreas()) {
            a.doLayout();
        }
        if (commons.Constants.generateTest) {
            System.out.println(generateTestMethod());
        }
        activated = true; // now layout calculation is allowed to run again
    }

    private String generateTestMethod() {
        int i = 1;
        StringBuffer method = new StringBuffer();
        StringBuffer expected = new StringBuffer();
        method.append("\t@Test\n");
        method.append("\tpublic void YYY() {\n");
        method.append("\t\tVariable [][] components = {\n");
        for (Area a : layoutSpec.getAreas()) {
            expected.append("\t\t\t\"area#" + i++ + ":" +
                            "left: " + a.left.toString() + ":" + Math.round(a.left.getValue()) + "," +
                            "top: " + a.top.toString() + ":" + Math.round(a.top.getValue()) + "," +
                            "right: " + a.right.toString() + ":" + Math.round(a.right.getValue()) + "," +
                            "bottom: " + a.bottom.toString() + ":" + Math.round(a.bottom.getValue()) + " \"+\n"
            );
            method.append("\t\t\t{new Variable(" + "\"" + a.left.toString() + "\", " + Math.round(a.left.getValue()) + "), " +
                            "new Variable(" + "\"" + a.top.toString() + "\", " + Math.round(a.top.getValue()) + "), " +
                            "new Variable(" + "\"" + a.right.toString() + "\", " + Math.round(a.right.getValue()) + "), " +
                            "new Variable(" + "\"" + a.bottom.toString() + "\", " + Math.round(a.bottom.getValue()) + ")},\n"
            );
        }
        expected.deleteCharAt(expected.lastIndexOf("+"));
        method.append("\t\t};\n\n" +
                "\t\tLinearSpec ls = LinearProblemsCollection.XXX(solver);\n" +
                "\t\tls.solve();\n");
        method.append("\t\tprintResult(ls, Thread.currentThread().getStackTrace()[1].getMethodName(),\n" + expected.toString() + "\t\t\t);\n");
        method.append("\t\tcheckResults(components, ls.getVariables());\n" +
                "\t\tprintMsg(\"  :Done\");\n" +
                "\t}");
        return method.toString();
    }

    /** Indicates whether editing mode is on. */
    boolean editing = false;

    /** List that stores original controls while in edit mode. */
    public List<JComponent> savedControls;

    /** Edit form used */
    public PropertiesWindow propertiesWindow = null;

    /**
     * This method changes the gui from normal to edit mode.
     *
     * @param parent - the parent component that is using ALMLayout
     * @throws ALMException
     */
    public void edit(Container parent) {
        layoutContainer(parent); //What is this doing? It seems to work without this?
        this.parent = parent;
        if (listener != null) {
            listener.disableMouseListeners();
        }
        activated = false;
        editing = true;
        // solves layout
        layoutSpec.solve();

        // add components to savedControls list
        for (Component control : parent.getComponents()) {
            JComponent c = (JComponent) control;
            if (!savedControls.contains(c)) {
                savedControls.add(c);
            }
        }

        // creates the property window if not already created
        System.out.println("PropertiesWindow0" + propertiesWindow);
        System.out.println("True?" + (propertiesWindow == null));
        if (propertiesWindow == null) {
            propertiesWindow = new PropertiesWindow(parent, this);
            System.out.println("PropertiesWindow1" + propertiesWindow);
            //propertiesWindow.getAreaPanel().addBinListener(propertiesWindow.aLMEditorCanvas());

            // TODO look at optimizing this
            // Add the items to the bin that we were storing
            // temporarily
            for (Area a : tempEditFormBin) {
                this.propertiesWindow.getAreaPanel().palette().addToBin(a.getContent());
                this.propertiesWindow.getAreaPanel().addControl(a.content.getName());
            }
        } else {
            this.propertiesWindow.restartWindow();
        }

        // open property window
        activated = true;
        propertiesWindow.peer().setVisible(true);
        propertiesWindow.propertiesPanel().update();
    }

    /** Returns the container that uses this layout to arrange its child widgets. */
    public Container getParent() {
        return this.parent;
    }

    /**
     * Exits the edit mode and restores everything to normal operation.
     *
     * @param parent The parent container of the GUI.
     */
    public void quitEdit(Container parent) {
        editing = false;
        parent.removeAll();
        for (JComponent control : savedControls) {
            if (control != null)
                parent.add(control);
        }
        for (JComponent control : propertiesWindow.getAreaPanel().palette().itemsInBin()) {
            control.setVisible(false);
        }
        try {
            ALMPanel p = (ALMPanel) parent;
            p.resetEditing();
            p.reloadListeners();
        } catch (ClassCastException ce) {
            if (listener != null) {
                listener.resetEditing();
                listener.reloadListeners();
            }
        }
    }

    /**
     * Reverse engineers the current GUI and recovers an ALM specification.
     */
    public void recoverLayout() {
        recoverlayout = true;
    }

    /**
     * Reverse engineers a GUI and recovers an ALM specification.
     *
     * @param parent the parent container of the GUI
     */
    public void recoverLayout(Container parent) {
        layoutSpec = new LayoutSpec();
        TreeMap<Integer, XTab> xtabs = new TreeMap<Integer, XTab>();
        TreeMap<Integer, YTab> ytabs = new TreeMap<Integer, YTab>();

        xtabs.put((int) parent.getBounds().getX(), layoutSpec.getLeft());
        ytabs.put((int) parent.getBounds().getY(), layoutSpec.getTop());

        for (Component c : parent.getComponents()) {
            JComponent cJ = (JComponent) c;
            XTab x1, x2;
            YTab y1, y2;
            Constraint con;

            if (xtabs.containsKey((int) cJ.getX()))
                x1 = xtabs.get((int) cJ.getX());
            else {
                xtabs.put((int) cJ.getX(), x1 = new XTab(layoutSpec, false));
                //Add a constraint to ensure the layout matches the original.
                //Named so can be removed easily if required later.
                con = layoutSpec.addConstraint(1, x1, OperatorType.EQ, cJ.getX(), 100);
                con.setName("recoverX1Lock");
            }
            if (xtabs.containsKey((int) (cJ.getX() + cJ.getWidth())))
                x2 = xtabs.get((int) (cJ.getX() + cJ.getWidth()));
            else {
                xtabs.put((int) (cJ.getX() + cJ.getWidth()), x2 = new XTab(layoutSpec, false));
                //Add a constraint to ensure the layout matches the original.
                //Named so can be removed easily if required later.
                con = layoutSpec.addConstraint(1, x2, OperatorType.EQ, cJ.getX() + cJ.getWidth(), 100);
                con.setName("recoverX2Lock");
            }
            if (ytabs.containsKey((int) cJ.getY()))
                y1 = ytabs.get((int) cJ.getY());
            else {
                ytabs.put((int) cJ.getY(), y1 = new YTab(layoutSpec, false));
                //Add a constraint to ensure the layout matches the original.
                //Named so can be removed easily if required later.
                con = layoutSpec.addConstraint(1, y1, OperatorType.EQ, cJ.getY(), 100);
                con.setName("recoverY1Lock");
            }
            if (ytabs.containsKey((int) (cJ.getY() + cJ.getHeight())))
                y2 = ytabs.get((int) (cJ.getY() + cJ.getHeight()));
            else {
                ytabs.put((int) (cJ.getY() + cJ.getHeight()), y2 = new YTab(layoutSpec, false));
                //Add a constraint to ensure the layout matches the original.
                //Named so can be removed easily if required later.
                con = layoutSpec.addConstraint(1, y2, OperatorType.EQ, cJ.getY() + cJ.getHeight(), 100);
                con.setName("recoverY2Lock");
            }

            layoutSpec.addArea(x1, y1, x2, y2, cJ);

            x2.leftLink = true;
            y2.topLink = true;
        }

        // adding additional constraints (links in the PO) for margins between
        // areas
        XTab currentXTab = null;
        XTab previousXTab = null;
        int currentXKey = 0;
        int previousXKey = 0;
        Iterator<XTab> xTabsIterator = xtabs.values().iterator();
        Iterator<Integer> xKeyIterator = xtabs.keySet().iterator();
        while (xTabsIterator.hasNext()) {
            currentXTab = (XTab) xTabsIterator.next();
            currentXKey = Integer.parseInt(xKeyIterator.next().toString());
            if (!currentXTab.leftLink && previousXTab != null)
                layoutSpec.addConstraint(-1, previousXTab, 1, currentXTab,
                        OperatorType.EQ, -previousXKey + currentXKey);
            previousXTab = currentXTab;
            previousXKey = currentXKey;
        }


        YTab currentYTab = null;
        YTab previousYTab = null;
        int currentYKey = 0;
        int previousYKey = 0;
        Iterator<YTab> yTabsIterator = ytabs.values().iterator();
        Iterator<Integer> yKeyIterator = ytabs.keySet().iterator();
        while (yTabsIterator.hasNext()) {
            currentYTab = (YTab) yTabsIterator.next();
            currentYKey = Integer.parseInt(yKeyIterator.next().toString());
            if (!currentYTab.topLink && previousYTab != null)
                layoutSpec.addConstraint(-1, previousYTab, 1, currentYTab,
                        OperatorType.EQ, -previousYKey + currentYKey);
            previousYTab = currentYTab;
            previousYKey = currentYKey;
        }

    }

    // #endregion RE

    /**
     * Method not implemented
     */
    public void removeLayoutComponent(Component comp) {
    }

    /**
     * Initialize the property window
     */
    public void initPropertyWindow() {
        if (this.propertiesWindow == null) {
            propertiesWindow = new PropertiesWindow(parent, this);
        }
    }

    /**
     * This method adds the components and calls the layout method
     */
    private boolean isFromPropertyWindow;

    /**
     * Checking if is from property window.
     *
     * @return true if is from property window, false otherwise
     */
    public boolean isFromPropertyWindow() {
        return isFromPropertyWindow;
    }

    /**
     * Checking if is from property window.
     *
     * @param isFromPropertyWindow if is from property window, false otherwise
     */
    public void setFromPropertyWindow(boolean isFromPropertyWindow) {
        this.isFromPropertyWindow = isFromPropertyWindow;
    }

    /**
     * Implementation of the same method inherited from LayoutManager.
     * Lays out the specified container. It adds the components into the parent,
     * and calls layout() to lay them out.
     *
     * @param parent the container to be laid out
     */
    public void layoutContainer(Container parent) {
        this.parent = (JComponent) parent;

        // TODO what broken logic is this? doesn't belong here...
        if (!(parent instanceof ALMPanel) && listener == null
                && !isFromPropertyWindow) {
            if (!disableEditor) {
                listener = new ALMPanel(this.parent);
            }
        }

        try {
            for (Component c : componentsToAdd) {
                // make sure container's parent is not added to itself
                if (c != parent) {
                    parent.add(c);
                }
            }
            componentsToAdd.clear();
        } catch (NullPointerException ne) {
            System.err.println("Setting layout container failure");
            System.err.println("> Method: LayoutContainer");
            System.err.println("> Class: ALMLayout");
            ne.printStackTrace();
        }
        try {
            layout(parent);
            if (editing && propertiesWindow != null) {
                propertiesWindow.propertiesPanel().update();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method not implemented
     */
    public void addLayoutComponent(String name, Component comp) {
    }

    /**
     * Return the minimum layout - Dimension
     *
     * @param parent the parent container of this layout spec.
     * @return The minimum size dimension.
     */
    /*public Dimension minimumLayoutSize(Container parent) {
		//ensure border constraints are set.
		layoutSpec.setInsetConstraints(parent.getInsets());
		return layoutSpec.getMinSize();
	}*/

    /**
     * Return the prefer layout - Dimension
     *
     * @param parent the parent container of this layout spec.
     * @return The preferred size dimension.
     */
	/*public Dimension preferredLayoutSize(Container parent) {
		//ensure border constraints are set.
		layoutSpec.setInsetConstraints(parent.getInsets());
		return layoutSpec.getPreferredSize();
	}*/

    /**
     * Return the current layout spec
     *
     * @return LayoutSpec
     */
    public LayoutSpec getLayoutSpec() {
        return layoutSpec;
    }

    /**
     * Sets the current layout spec
     *
     * @param ls The layoutspec to be used.
     */
    public void setLayoutSpec(LayoutSpec ls) {
        layoutSpec = ls;
    }

    /**
     * ALMLayout load method loads a XALMON(.xml or .xln) file containing the
     * ALMLayout spec and refreshes the layout.
     *
     * @param file The file to be loaded.
     * @throws ALMException
     */
    public void load(File file) {
        try {
            ProcessXML pro = new ProcessXML(this);
            JAXBContext jc = JAXBContext.newInstance("jaxb");
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            ALMSchema almlayout = (ALMSchema) unmarshaller.unmarshal(file);
            List<ALMSchema.Area> areaList = almlayout.getArea();
            List<ALMSchema.Constraint> constraintList = almlayout
                    .getConstraint();

            // add components to savectrl list
            for (Component control : parent.getComponents()) {
                JComponent c = (JComponent) control;
                if (!this.savedControls.contains(c)) {
                    this.savedControls.add(c);
                }
            }

            // Check that the layout contains some components
            if (savedControls.size() != 0) {
                // load new Areas
                for (int i = 0; i < areaList.size(); i++) {
                    pro.parseArea(areaList.get(i));
                }
            } else {
                System.err.println("The layout does not contain any components");
            }

            // load new constraints
            for (int i = 0; i < constraintList.size(); i++) {
                pro.parseConstraint(constraintList.get(i));
            }
            int index = 0;
            // Loading file through PropertiesWindow load menu button
            if (propertiesWindow != null) {
                // save all the existing bin element.
                List<JComponent> temp = new ArrayList<JComponent>();
                HashMap<JComponent, Insets> tempAreaInsets = new HashMap<JComponent, Insets>();
                HashMap<JComponent, Object[]> tempAlignment = new HashMap<JComponent, Object[]>();
                for (JComponent j : propertiesWindow.getAreaPanel().palette().itemsInBin()) {
                    temp.add(j);
                    tempAlignment.put(j, propertiesWindow.getAreaPanel().getInvisibleAlignments().get(j));
                    if (propertiesWindow.getAreaPanel().getInvisibleAreaInsets().containsKey(j)) {
                        tempAreaInsets.put(j, propertiesWindow.getAreaPanel().getInvisibleAreaInsets().get(j));
                    }
                }
                // refresh the bin
                this.propertiesWindow.getAreaPanel().palette().itemsInBin().removeAll(propertiesWindow.getAreaPanel().palette().itemsInBin());

                while (index < layoutSpec.getAreas().size()) {
                    boolean found = false;
                    Area a = layoutSpec.getAreas().get(index);
                    if (a.getContent() != null) {
                        for (Area b : pro.getNewLayoutSpec().getAreas()) {
                            if (a.getContent().getName().equals(
                                    b.getContent().getName())) {
                                found = true;
                                break;
                            }
                        }
                    }
                    if (a.getContent() != null && !found) {
                        this.propertiesWindow.getAreaPanel().palette().addToBin(a.getContent());
                        this.propertiesWindow.getAreaPanel().addControl(a.content.getName());
                        // Set the content as invisible
                        a.content.setVisible(false);
                        a.remove();
                    } else {
                        index++;
                    }

                }

                for (JComponent d : temp) {
                    boolean found = false;
                    for (Area b : pro.getNewLayoutSpec().getAreas()) {
                        if (b.getContent().getName().equals(d.getName())) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        this.propertiesWindow.getAreaPanel().palette().addToBin(d);
                    }
                }

                this.layoutSpec = pro.getNewLayoutSpec();

                // this.LayoutStyle = LayoutStyle.ADJUST_SIZE;
                // this.layout(this.parent);
                this.layoutSpec.solve();
                // this.parent.invalidate();

                // Loading a file at start up
            } else {
                // TODO Finish else statement

                // We assume no existing elements exist in the bin
                // because no bin currently exists

                while (index < layoutSpec.getAreas().size()) {
                    boolean found = false;
                    Area a = layoutSpec.getAreas().get(index);
                    for (Area b : pro.getNewLayoutSpec().getAreas()) {
                        if (a.getContent().getName().equals(
                                b.getContent().getName())) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        // editForm does not currently exist.
                        // Store the component temporarily.
                        tempEditFormBin.add(a);
                        // Set the content as invisible
                        a.content.setVisible(false);
                        a.remove();
                    } else {
                        index++;
                    }
                }

                this.layoutSpec = pro.getNewLayoutSpec();

                // this.LayoutStyle = LayoutStyle.ADJUST_SIZE;
                // this.layout(this.parent);
                this.layoutSpec.solve();

                // TODO layoutContainer or validate?
//				layoutContainer(parent);
                parent.validate();
            }
        } catch (Exception e) {
//			throw new RuntimeException(
//					"Could not load " + file.getName() + ".", e);
            e.printStackTrace();
        }
    }

    /**
     * Why is this here - this just loads the file from a string file name
     *
     * @param filename The name of the file to be loaded.
     * @throws ALMException
     */
    public void load(String filename) {
        load(new File(filename));
    }


    /**
     * Saves the layout configuration to a file
     *
     * @param file the configuration file to save
     */
    public void save(File file) {
        try {
            OutputStream fout = new FileOutputStream(file);
            OutputStream bout = new BufferedOutputStream(fout);
            OutputStreamWriter out = new OutputStreamWriter(bout, "UTF8");

            out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            out.write("<almlayout>\n");
            out.write("\t<version> 2 </version>\n");

            layoutSpec.writeXML(out);

            out.write("</almlayout>");
            out.flush();
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Adds the component to the componentsToAdd list. The list is used to add
     * the components to the parent component for display later.
     *
     * @param component the component to add.
     */
    public void addToComponentsToAdd(Component component) {
        componentsToAdd.add(component);
    }


    // -----------------------Start of wrapper methods-----------------------

    // Modified wrapper methods for LayoutSpec

    /**
     * Adds a new area to the specification, setting only the necessary minimum
     * size constraints.
     *
     * @param left           left border
     * @param top            top border
     * @param right          right border
     * @param bottom         bottom border
     * @param content        the control which is the area content
     * @param minContentSize minimum content size
     * @return the new area
     */
    public Area addArea(XTab left, YTab top, XTab right, YTab bottom,
                        JComponent content, Dimension minContentSize) {
        componentsToAdd.add(content);
        return layoutSpec.addArea(left, top, right, bottom, content,
                minContentSize);
    }

    /**
     * Adds a new area to the specification, setting only the necessary minimum
     * size constraints.
     *
     * @param row            the row that defines the top and bottom border
     * @param column         the column that defines the left and right border
     * @param content        the control which is the area content
     * @param minContentSize minimum content size
     * @return the new area
     */
    public Area addArea(Row row, Column column, JComponent content,
                        Dimension minContentSize) {
        componentsToAdd.add(content);
        return layoutSpec.addArea(row, column, content, minContentSize);
    }

    /**
     * Adds a new area to the specification, automatically setting preferred
     * size constraints.
     *
     * @param left    left border
     * @param top     top border
     * @param right   right border
     * @param bottom  bottom border
     * @param content the control which is the area content
     * @return the new area
     */
    public Area addArea(XTab left, YTab top, XTab right, YTab bottom,
                        JComponent content) {
        componentsToAdd.add(content);
        return layoutSpec.addArea(left, top, right, bottom, content);
    }

    /**
     * Adds a new area to the specification, automatically setting preferred
     * size constraints.
     *
     * @param row     the row that defines the top and bottom border
     * @param column  the column that defines the left and right border
     * @param content the control which is the area content
     * @return the new area
     */
    public Area addArea(Row row, Column column, JComponent content) {
        componentsToAdd.add(content);
        return layoutSpec.addArea(row, column, content);
    }

    // Wrapper methods for LayoutSpec

    /**
     * Adds a new x-tab to the specification.
     *
     * @return the new x-tab
     */
    public XTab addXTab() {
        return layoutSpec.addXTab();
    }

    /**
     * Adds a new x-tab to the specification.
     *
     * @param name String define the name of resulting x-tab
     * @return the new x-tab
     */
    public XTab addXTab(String name) {
        return layoutSpec.addXTab(name);
    }

    /**
     * Adds a new y-tab to the specification.
     *
     * @return the new y-tab
     */
    public YTab addYTab() {
        return layoutSpec.addYTab();
    }

    /**
     * Adds a new y-tab to the specification.
     *
     * @param name String define the name of resulting y-tab
     * @return the new y-tab
     */
    public YTab addYTab(String name) {
        return layoutSpec.addYTab(name);
    }

    /**
     * Adds a new row to the specification.
     *
     * @return the new row
     */
    public Row addRow() {
        return layoutSpec.addRow();
    }

    /**
     * Adds a new row to the specification at an index.
     *
     * @param index The index to place the row.
     * @return the new row
     */
    public Row addRow(int index) {
        return layoutSpec.addRow(index);
    }

    /**
     * Adds a new row to the specification that is glued to the given y-tabs.
     *
     * @param top    The top YTab
     * @param bottom The bottom YTab
     * @return the new row
     */
    public Row addRow(YTab top, YTab bottom) {
        return layoutSpec.addRow(top, bottom);
    }

    /**
     * Adds a new column to the specification.
     *
     * @return the new column
     */
    public Column addColumn() {
        return layoutSpec.addColumn();
    }

    /**
     * Adds a new column to the specification at an index.
     *
     * @return the new column
     */
    public Column addColumn(int index) {
        return layoutSpec.addColumn(index);
    }

    /**
     * Adds a new column to the specification that is glued to the given x-tabs.
     *
     * @param left  The Left XTab
     * @param right The Right XTab
     * @return the new column
     */
    public Column addColumn(XTab left, XTab right) {
        return layoutSpec.addColumn(left, right);
    }

    /**
     * Finds the area that contains the given control.
     *
     * @param c the control to look for
     * @return the area that contains the control
     */
    public Area areaOf(JComponent c) {
        return layoutSpec.areaOf(c);
    }

    /**
     * Retrieve all areas in this Layout
     *
     * @return a list of all areas
     */
    public List<Area> getAreas() {
        return layoutSpec.getAreas();
    }

    /**
     * Retrieve all rows in this Layout
     *
     * @return a list of all rows
     */
    public List<Row> getRows() {
        return layoutSpec.getRows();
    }

    /**
     * Get the X-tab for the left border of the GUI
     *
     * @return X-tab for the left border of the GUI
     */
    public XTab getLeft() {
        return layoutSpec.getLeft();
    }

    /**
     * Get the X-tab for the right border of the GUI
     *
     * @return X-tab for the right border of the GUI
     */
    public XTab getRight() {
        return layoutSpec.getRight();
    }

    /**
     * Get the Y-tab for the top border of the GUI
     *
     * @return Y-tab for the top border of the GUI
     */
    public YTab getTop() {
        return layoutSpec.getTop();
    }

    /**
     * Retrieve all columns in this Layout
     *
     * @return a list of all rows
     */
    public List<Column> getColumns() {
        return layoutSpec.getColumns();
    }

    /**
     * Get the Y-tab for the bottom border of the GUI
     *
     * @return Y-tab for the bottom border of the GUI
     */
    public YTab getBottom() {
        return layoutSpec.getBottom();
    }

    /**
     * Get the X-tab for the left border of the GUI.
     * The difference between this and the left tab defines the border thickness.
     *
     * @return the x-tab.
     */
    public XTab getLeftInset() {
        return layoutSpec.getLeftInset();
    }

    /**
     * Get the X-tab for the right border of the GUI.
     * The difference between this and the right tab defines the border thickness.
     *
     * @return the x-tab.
     */
    public XTab getRightInset() {
        return layoutSpec.getRightInset();
    }

    /**
     * Get the Y-tab for the top border of the GUI.
     * The difference between this and the top tab defines the border thickness.
     *
     * @return the y-tab.
     */
    public YTab getTopInset() {
        return layoutSpec.getTopInset();
    }

    /**
     * Get the Y-tab for the top border of the GUI.
     * The difference between this and the bottom tab defines the border thickness.
     *
     * @return the y-tab.
     */
    public YTab getBottomInset() {
        return layoutSpec.getBottomInset();
    }

    /**
     * Get the minimal size of the GUI
     * May not consider any borders. If possible use minimumLayoutSize(Container).
     *
     * @return Dimension defining the minimal size of the GUI
     */
    public Dimension getMinSize() {
        return layoutSpec.getMinSize();
    }

    /**
     * Get the maximal size of the GUI
     *
     * @return Dimension defining the maximal size of the GUI
     */
    public Dimension getMaxSize() {
        return layoutSpec.getMaxSize();
    }

    /**
     * Get the preferred size of the GUI
     * May not consider any borders. If possible use preferredLayoutSize(Container).
     *
     * @return Dimension defining the preferred size of the GUI
     */
    public Dimension getPreferredSize() {
        return layoutSpec.getPreferredSize();
    }

    /**
     * Set the minimal size of the GUI
     *
     * @param minSize defining the minimal size of the GUI
     */
    public void setMinSize(Dimension minSize) {
        layoutSpec.setMinSize(minSize);
    }

    /**
     * Set the maximal size of the GUI
     *
     * @param maxSize defining the maximal size of the GUI
     */
    public void setMaxSize(Dimension maxSize) {
        layoutSpec.setMaxSize(maxSize);
    }

    /**
     * Set the preferred size of the GUI
     *
     * @param preferredSize defining the preferred size of the GUI
     */
    public void setPreferredSize(Dimension preferredSize) {
        layoutSpec.setPreferredSize(preferredSize);
    }

    /**
     * Sets the constraints which govern the border end tabs.
     * @param insets The borders present on the container managed by ALM.
     */
	/*public void setInsetConstraints(Insets insets){
		layoutSpec.setInsetConstraints(insets);
	}*/

    // Wrapper methods for LinearSpec

    /**
     * Adds a new hard linear constraint to the specification.
     *
     * @param summands  the constraint's summands
     * @param op        the constraint's operand
     * @param rightSide the constant value on the constraint's right side
     * @return the new constraint
     */
    public Constraint addConstraint(Summand[] summands, OperatorType op,
                                    double rightSide) {
        return layoutSpec.addConstraint(summands, op, rightSide);
    }

    /**
     * Adds a new hard linear constraint to the specification with a single
     * summand.
     *
     * @param coeff1    the constraint's first coefficient
     * @param var1      the constraint's first variable
     * @param op        the constraint's operand
     * @param rightSide the constant value on the constraint's right side
     * @return the new constraint
     */
    public Constraint addConstraint(double coeff1, Variable var1,
                                    OperatorType op, double rightSide) {

        return layoutSpec.addConstraint(coeff1, var1, op, rightSide);
    }

    /**
     * Adds a new hard linear constraint to the specification with two summands.
     *
     * @param coeff1    the constraint's first coefficient
     * @param var1      the constraint's first variable
     * @param coeff2    the constraint's second coefficient
     * @param var2      the constraint's second variable
     * @param op        the constraint's operand
     * @param rightSide the constant value on the constraint's right side
     * @return the new constraint
     */
    public Constraint addConstraint(double coeff1, Variable var1,
                                    double coeff2, Variable var2, OperatorType op, double rightSide) {

        return layoutSpec.addConstraint(coeff1, var1, coeff2, var2, op,
                rightSide);
    }

    /**
     * Adds a new hard linear constraint to the specification with three
     * summands.
     *
     * @param coeff1    the constraint's first coefficient
     * @param var1      the constraint's first variable
     * @param coeff2    the constraint's second coefficient
     * @param var2      the constraint's second variable
     * @param coeff3    the constraint's third coefficient
     * @param var3      the constraint's third variable
     * @param op        the constraint's operand
     * @param rightSide the constant value on the constraint's right side
     * @return the new constraint
     */
    public Constraint addConstraint(double coeff1, Variable var1,
                                    double coeff2, Variable var2, double coeff3, Variable var3,
                                    OperatorType op, double rightSide) {
        return layoutSpec.addConstraint(coeff1, var1, coeff2, var2, coeff3,
                var3, op, rightSide);
    }

    /**
     * Adds a new hard linear constraint to the specification with four
     * summands.
     *
     * @param coeff1    the constraint's first coefficient
     * @param var1      the constraint's first variable
     * @param coeff2    the constraint's second coefficient
     * @param var2      the constraint's second variable
     * @param coeff3    the constraint's third coefficient
     * @param var3      the constraint's third variable
     * @param coeff4    the constraint's fourth coefficient
     * @param var4      the constraint's fourth variable
     * @param op        the constraint's operand
     * @param rightSide the constant value on the constraint's right side
     * @return the new constraint
     */
    public Constraint addConstraint(double coeff1, Variable var1,
                                    double coeff2, Variable var2, double coeff3, Variable var3,
                                    double coeff4, Variable var4, OperatorType op, double rightSide) {
        return layoutSpec.addConstraint(coeff1, var1, coeff2, var2, coeff3,
                var3, coeff4, var4, op, rightSide);
    }

    /**
     * Adds a new soft linear constraint to the specification. i.e. a constraint
     * that does not always have to be satisfied.
     *
     * @param summands  the constraint's summands
     * @param op        the constraint's operand
     * @param rightSide the constant value on the constraint's right side
     * @param penalty   the constraint's penalty
     */
    public Constraint addConstraint(Summand[] summands, OperatorType op,
                                    double penalty, double rightSide) {
        return layoutSpec.addConstraint(summands, op, penalty, rightSide);
    }

    /**
     * Adds a new soft linear constraint to the specification with a single
     * summand.
     *
     * @param coeff1    the constraint's first coefficient
     * @param var1      the constraint's first variable
     * @param op        the constraint's operand
     * @param rightSide the constant value on the constraint's right side
     * @param penalty   the coefficient penalizing deviations from the exact
     *                  solution
     */
    public Constraint addConstraint(double coeff1, Variable var1,
                                    OperatorType op, double rightSide, double penalty) {
        return layoutSpec.addConstraint(coeff1, var1, op, rightSide, penalty);
    }

    /**
     * Adds a new soft linear constraint to the specification with two summands.
     *
     * @param coeff1    the constraint's first coefficient
     * @param var1      the constraint's first variable
     * @param coeff2    the constraint's second coefficient
     * @param var2      the constraint's second variable
     * @param op        the constraint's operand
     * @param rightSide the constant value on the constraint's right side
     * @param penalty   the coefficient penalizing deviations from the exact
     *                  solution
     */
    public Constraint addConstraint(double coeff1, Variable var1,
                                    double coeff2, Variable var2, OperatorType op, double rightSide,
                                    double penalty) {

        return layoutSpec.addConstraint(coeff1, var1, coeff2, var2, op,
                rightSide, penalty);
    }

    /**
     * Adds a new soft linear constraint to the specification with three
     * summands.
     *
     * @param coeff1    the constraint's first coefficient
     * @param var1      the constraint's first variable
     * @param coeff2    the constraint's second coefficient
     * @param var2      the constraint's second variable
     * @param coeff3    the constraint's third coefficient
     * @param var3      the constraint's third variable
     * @param op        the constraint's operand
     * @param rightSide the constant value on the constraint's right side
     * @param penalty   the coefficient penalizing deviations from the exact
     *                  solution
     */
    public Constraint addConstraint(double coeff1, Variable var1,
                                    double coeff2, Variable var2, double coeff3, Variable var3,
                                    OperatorType op, double rightSide, double penalty) {
        return layoutSpec.addConstraint(coeff1, var1, coeff2, var2, coeff3,
                var3, op, rightSide, penalty);
    }

    /**
     * Adds a new soft linear constraint to the specification with four
     * summands.
     *
     * @param coeff1    the constraint's first coefficient
     * @param var1      the constraint's first variable
     * @param coeff2    the constraint's second coefficient
     * @param var2      the constraint's second variable
     * @param coeff3    the constraint's third coefficient
     * @param var3      the constraint's third variable
     * @param coeff4    the constraint's fourth coefficient
     * @param var4      the constraint's fourth variable
     * @param op        the constraint's operand
     * @param rightSide the constant value on the constraint's right side
     * @param penalty   the coefficient penalizing deviations from the exact
     *                  solution
     */
    public Constraint addConstraint(double coeff1, Variable var1,
                                    double coeff2, Variable var2, double coeff3, Variable var3,
                                    double coeff4, Variable var4, OperatorType op, double rightSide,
                                    double penalty) {
        return layoutSpec.addConstraint(coeff1, var1, coeff2, var2, coeff3,
                var3, coeff4, var4, op, rightSide, penalty);
    }

    @Override
    public Dimension minimumLayoutSize(Container arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Dimension preferredLayoutSize(Container arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public void setInsetConstraints(Insets insets) {
        // TODO Auto-generated method stub

    }

    // ------------------------End of wrapper methods------------------------
}