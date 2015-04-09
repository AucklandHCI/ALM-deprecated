package alm.editor;

import java.awt.Dimension;

import javax.swing.JPanel;

import alm.ALMException;
import alm.ALMLayout;
import alm.XTab;

import linsolve.OperatorType;

/**
 * Panel for use as the Row tab in the Properties Window.
 */
public class RowsPanel extends JPanel {

    private PropertiesPanel properties;
    private ALMLayout alm;


    public RowsPanel(PropertiesPanel p) throws ALMException {
        setSize(new Dimension(400, 200));
        
        properties = p;
        
        alm = new ALMLayout();
        alm.setFromPropertyWindow(true);
        
        setLayout(alm);
        
        XTab x1 = alm.addXTab();
        
        //alm.addArea(alm.getLeft(), alm.getTop(), x1, alm.getBottom(), properties.rowSelectionLabel().peer());
        //alm.addArea(x1, alm.getTop(), alm.getRight(), alm.getBottom(), properties.rowBox());
        
        alm.addConstraint(2.0, x1, -1.0, alm.getRight(), OperatorType.EQ, 0.0);
    }
}
